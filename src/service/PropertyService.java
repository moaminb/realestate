package service;

import java.util.List;
import model.*;
import storage.AppData;
import storage.StorageManager;

public class PropertyService {
    private final AppData data;
    private final UserService userService;

    public PropertyService(AppData data, UserService userService) {
        this.data = data;
        this.userService = userService;
    }

    public void registerHouse(House house) {
        data.getHouses().add(house);
        StorageManager.saveData(data);
        System.out.println("✅ ملک با موفقیت و با شناسه " + house.getId() + " ثبت شد:)");
    }

    public boolean purchaseHouse(String houseId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println("❌ ابتدا باید وارد حساب کاربری شوید.");
            return false;
        }

        House house = findHouseById(houseId);
        if (house == null || house.getDealStatus() == House.DealStatus.FOR_RENT) {
            System.out.println("❌ این ملک وجود ندارد یا برای فروش نیست!");
            return false;
        }

        if (house.getOwnerName().equalsIgnoreCase(currentUser.getUsername())) {
            System.out.println("❌ خطا: شما خودتان مالک این ملک هستید و نمی‌توانید آن را از خودتان خریداری کنید!");
            return false;
        }

        long price = house.calculatePrice();
        boolean isAgencyOwned = house.getOwnerName().equals(Agency.AGENCY_OWNER_NAME);
        User seller = isAgencyOwned ? null : userService.findUserByUsername(house.getOwnerName());

        if (currentUser.withdraw(price)) {
            if (!isAgencyOwned && seller != null) {
                seller.deposit(price);
            }

            house.setOwnerName(currentUser.getUsername());
            house.setDealStatus(House.DealStatus.FOR_RENT);
            currentUser.addPurchasedHouse(houseId);

            if (isAgencyOwned) {
                data.getAgency().removeHouseFromAgency(houseId);
            }

            StorageManager.saveData(data);
            System.out.printf("🎉 تبریک! ملک با موفقیت خریداری شد:) مبلغ کسر شده: %,d ریال%n", price);
            return true;
        } else {
            System.out.printf("❌ موجودی حساب شما برای خرید این ملک کافی نیست! قیمت: %,d ریال%n", price);
            return false;
        }
    }

    public boolean rentHouse(String houseId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println("❌ ابتدا باید وارد حساب کاربری شوید.");
            return false;
        }

        House house = findHouseById(houseId);
        if (house == null || house.getDealStatus() == House.DealStatus.FOR_SALE || !house.getTenantName().isEmpty()) {
            System.out.println("❌ این ملک برای اجاره در دسترس نیست!");
            return false;
        }

        long rentPrice = house.calculateRent();
        if (currentUser.withdraw(rentPrice)) {
            User landlord = userService.findUserByUsername(house.getOwnerName());
            if (landlord != null) {
                landlord.deposit(rentPrice);
            }

            String contractId = "CTR-" + (data.getContracts().size() + 1);
            Contract contract = new Contract(contractId, houseId, house.getOwnerName(), currentUser.getUsername(), rentPrice, Contract.ContractType.RENT);
            data.getContracts().add(contract);

            house.setTenantName(currentUser.getUsername());
            currentUser.addRentedHouse(houseId);

            StorageManager.saveData(data);
            System.out.println("✅ قرارداد اجاره با موفقیت تنظیم و ملک اجاره شد:)");
            return true;
        } else {
            System.out.printf("❌ موجودی کافی برای پرداخت اجاره وجود ندارد! مبلغ اجاره: %,d ریال%n", rentPrice);
            return false;
        }
    }

    public boolean specialPurchaseHouse(String houseId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println("❌ ابتدا باید وارد حساب کاربری شوید.");
            return false;
        }

        House house = findHouseById(houseId);
        if (house == null) {
            System.out.println("❌ ملک مورد نظر یافت نشد!");
            return false;
        }
        if (house.getOwnerName().equalsIgnoreCase(currentUser.getUsername())) {
            System.out.println("❌ شما خودتان مالک این ملک هستید!");
            return false;
        }

        long specialPrice = house.calculatePrice() * 2;
        User seller = userService.findUserByUsername(house.getOwnerName());

        if (currentUser.withdraw(specialPrice)) {
            if (seller != null) {
                seller.deposit(specialPrice);
                if (!house.getTenantName().isEmpty()) {
                    User tenant = userService.findUserByUsername(house.getTenantName());
                    if (tenant != null) tenant.removeRentedHouse(houseId);
                    house.setTenantName("");
                }
            }

            String contractId = "CTR-" + (data.getContracts().size() + 1);
            Contract contract = new Contract(contractId, houseId, house.getOwnerName(), currentUser.getUsername(), specialPrice, Contract.ContractType.SPECIAL_PURCHASE);
            data.getContracts().add(contract);

            house.setOwnerName(currentUser.getUsername());
            house.setDealStatus(House.DealStatus.FOR_RENT);
            currentUser.addPurchasedHouse(houseId);

            StorageManager.saveData(data);
            System.out.printf("🔥 خرید ویژه با موفقیت انجام شد! ملک با پرداخت ۲ برابر قیمت (%,d ریال) به مالکیت شما درآمد.%n", specialPrice);
            return true;
        } else {
            System.out.printf("❌ موجودی کافی برای خرید ویژه وجود ندارد! قیمت ویژه: %,d ریال%n", specialPrice);
            return false;
        }
    }

    public boolean quickSellToAgency(String houseId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.out.println("❌ ابتدا باید وارد حساب کاربری شوید.");
            return false;
        }

        House house = findHouseById(houseId);
        if (house == null || !house.getOwnerName().equalsIgnoreCase(currentUser.getUsername())) {
            System.out.println("❌ شما مالک این ملک نیستید یا ملک وجود ندارد.");
            return false;
        }

        long quickSellPrice = (long) (house.calculatePrice() * 0.9);
        currentUser.deposit(quickSellPrice);

        house.setOwnerName(Agency.AGENCY_OWNER_NAME);
        house.setDealStatus(House.DealStatus.BOTH);
        data.getAgency().addHouseToAgency(houseId);

        StorageManager.saveData(data);
        System.out.printf("🏢 ملک شما با ۱۰٪ تخفیف به صورت فوری به بنگاه فروخته شد. مبلغ واریزی: %,d ریال%n", quickSellPrice);
        return true;
    }

    public House findHouseById(String id) {
        return data.getHouses().stream()
                .filter(h -> h.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public List<House> getAllHouses() {
        return data.getHouses();
    }

    public int getHousesCount() {
        return data.getHouses().size();
    }
}
