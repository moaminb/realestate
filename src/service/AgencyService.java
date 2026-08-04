package service;

import java.util.List;
import model.*;
import storage.*;
import util.SecurityUtils;

public class AgencyService {
    private AppData data;
    private User currentUser;

    public AgencyService() {
        // بارگذاری داده‌ها از فایل در بدو شروع برنامه
        this.data = StorageManager.loadData();
        this.currentUser = null;
    }



    public boolean registerUser(String username, String password, long initialBudget) {
        // بررسی تکراری نبودن نام کاربری
        for (User u : data.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                System.out.println("❌ این نام کاربری قبلاً ثبت شده است.");
                return false;
            }
        }
        String id = "USR-" + (data.getUsers().size() + 1);
        String hashedPassword = SecurityUtils.hashPassword(password); // هش کردن رمز عبور
        User newUser = new User(id, username, hashedPassword, initialBudget);
        data.getUsers().add(newUser);
        StorageManager.saveData(data); // ذخیره در فایل
        return true;
    }

    public boolean loginUser(String username, String password) {
        String hashedPassword = SecurityUtils.hashPassword(password);
        for (User u : data.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(hashedPassword)) {
                this.currentUser = u;
                System.out.println(" ورود موفقیت‌آمیز بود. خوش آمدید " + username);
                return true;
            }
        }
        System.out.println(" نام کاربری یا رمز عبور اشتباه است.");
        return false;
    }

    public void logout() {
        this.currentUser = null;
        System.out.println(" از حساب کاربری خود خارج شدید.");
    }

    // --- ۲. مدیریت املاک ---

    public void registerHouse(House house) {
        data.getHouses().add(house);
        StorageManager.saveData(data);
        System.out.println(" ملک با موفقیت و با شناسه " + house.getId() + " ثبت شد:)");
    }

    // --- ۳. فرآیند خرید عادی ملک ---
    public boolean purchaseHouse(String houseId) {
        if (currentUser == null) return false;
        House house = findHouseById(houseId);

        if (house == null || house.getDealStatus() == House.DealStatus.FOR_RENT) {
            System.out.println(" این ملک وجود ندارد یا برای فروش نیست!");
            return false;
        }

        // 🌟 تغییر جدید: بررسی و جلوگیری از خرید ملک توسط خودِ مالک
        if (house.getOwnerName().equalsIgnoreCase(currentUser.getUsername())) {
            System.out.println(" خطا: شما خودتان مالک این ملک هستید و نمی‌توانید آن را از خودتان خریداری کنید!");
            return false;
        }

        long price = house.calculatePrice();


        boolean isAgencyOwned = house.getOwnerName().equals(Agency.AGENCY_OWNER_NAME);
        User seller = isAgencyOwned ? null : findUserByUsername(house.getOwnerName());

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
            System.out.printf(" تبریک! ملک با موفقیت خریداری شد:) مبلغ کسر شده: %,d ریال%n", price);
            return true;
        } else {
            System.out.printf(" موجودی حساب شما برای خرید این ملک کافی نیست! قیمت: %,d ریال%n", price);
            return false;
        }
    }


    public boolean rentHouse(String houseId) {
        if (currentUser == null) return false;
        House house = findHouseById(houseId);

        if (house == null || house.getDealStatus() == House.DealStatus.FOR_SALE || !house.getTenantName().isEmpty()) {
            System.out.println(" این ملک برای اجاره در دسترس نیست!");
            return false;
        }

        long rentPrice = house.calculateRent();
        if (currentUser.withdraw(rentPrice)) {
            // واریز به مالک (در صورت وجود)
            User landlord = findUserByUsername(house.getOwnerName());
            if (landlord != null) {
                landlord.deposit(rentPrice);
            }

            // ثبت قرارداد اجاره
            String contractId = "CTR-" + (data.getContracts().size() + 1);
            Contract contract = new Contract(contractId, houseId, house.getOwnerName(), currentUser.getUsername(), rentPrice, Contract.ContractType.RENT);
            data.getContracts().add(contract);

            // بروزرسانی وضعیت ملک و کاربر
            house.setTenantName(currentUser.getUsername());
            currentUser.addRentedHouse(houseId);

            StorageManager.saveData(data);
            System.out.println(" قرارداد اجاره با موفقیت تنظیم و ملک اجاره شد:)");
            return true;
        } else {
            System.out.printf(" موجودی کافی برای پرداخت اجاره وجود ندارد! مبلغ اجاره: %,d ریال%n", rentPrice);
            return false;
        }
    }

    //  بخش امتیازی: خرید ویژه
    public boolean specialPurchaseHouse(String houseId) {
        if (currentUser == null) return false;
        House house = findHouseById(houseId);

        if (house == null) {
            System.out.println(" ملک مورد نظر یافت نشد!");
            return false;
        }
        if (house.getOwnerName().equals(currentUser.getUsername())) {
            System.out.println(" شما خودتان مالک این ملک هستید!");
            return false;
        }

        long specialPrice = house.calculatePrice() * 2;
        User seller = findUserByUsername(house.getOwnerName());

        if (currentUser.withdraw(specialPrice)) {
            if (seller != null) {
                seller.deposit(specialPrice);
                // اگر دست مستأجر بود، قرارداد اجاره قبلی ملغی و مستأجر تخلیه می‌شود
                if (!house.getTenantName().isEmpty()) {
                    User tenant = findUserByUsername(house.getTenantName());
                    if (tenant != null) tenant.removeRentedHouse(houseId);
                    house.setTenantName("");
                }
            }

            // ثبت در قراردادها به عنوان خرید ویژه امتیازی
            String contractId = "CTR-" + (data.getContracts().size() + 1);
            Contract contract = new Contract(contractId, houseId, house.getOwnerName(), currentUser.getUsername(), specialPrice, Contract.ContractType.SPECIAL_PURCHASE);
            data.getContracts().add(contract);

            // انتقال مالکیت
            house.setOwnerName(currentUser.getUsername());
            house.setDealStatus(House.DealStatus.FOR_RENT);
            currentUser.addPurchasedHouse(houseId);

            StorageManager.saveData(data);
            System.out.printf(" خرید ویژه با موفقیت انجام شد! ملک با پرداخت ۲ برابر قیمت (%,d ریال) به مالکیت شما درآمد.%n", specialPrice);
            return true;
        } else {
            System.out.printf(" موجودی کافی برای خرید ویژه وجود ندارد! قیمت ویژه: %,d ریال%n", specialPrice);
            return false;
        }
    }

    //  فرآیند فروش فوری به بنگاه
    public boolean quickSellToAgency(String houseId) {
        if (currentUser == null) return false;
        House house = findHouseById(houseId);

        if (house == null || !house.getOwnerName().equals(currentUser.getUsername())) {
            System.out.println("❌ شما مالک این ملک نیستید یا ملک وجود ندارد.");
            return false;
        }

        long quickSellPrice = (long) (house.calculatePrice() * 0.9);

        currentUser.deposit(quickSellPrice);

        house.setOwnerName(Agency.AGENCY_OWNER_NAME);
        house.setDealStatus(House.DealStatus.BOTH); // بنگاه می‌تواند آن را هم اجاره دهد هم بفروشد
        data.getAgency().addHouseToAgency(houseId);

        StorageManager.saveData(data);
        System.out.printf(" ملک شما با ۱۰٪ تخفیف به صورت فوری به بنگاه فروخته شد. مبلغ واریزی: %,d ریال%n", quickSellPrice);
        return true;
    }


    public boolean chargeAccount(long amount) {
        if (currentUser == null) return false;
        if (amount > 0) {
            currentUser.deposit(amount);
            StorageManager.saveData(data);
            System.out.printf(" موجودی حساب شما با موفقیت %,d ریال افزایش یافت.%n", amount);
            return true;
        } else {
            System.out.println("❌ خطا در شارژ حساب. مبلغ نامعتبر است.");
            return false;
        }
    }

    public House findHouseById(String id) {
        return data.getHouses().stream().filter(h -> h.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    private User findUserByUsername(String username) {
        return data.getUsers().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }


    public User getCurrentUser() { return currentUser; }
    public AppData getData() { return data; }
}