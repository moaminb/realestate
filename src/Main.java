import java.util.List;
import java.util.Scanner;
import factory.HouseFactory;
import model.*;
import service.*;
import storage.AppData;
import storage.StorageManager;

public class Main {
    private static AppData appData = StorageManager.loadData();
    private static AuthService authService = new AuthService(appData);
    private static UserService userService = new UserService(appData);
    private static PropertyService propertyService = new PropertyService(appData);
    private static ContractService contractService = new ContractService(appData, userService, propertyService);
    private static TransactionService transactionService = new TransactionService(appData, authService, userService, propertyService, contractService);
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("🏢 به سامانه آنلاین بنگاه معاملات ملکی خوش آمدید");

        while (true) {
            if (!authService.isLoggedIn()) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n--- منوی ورود / ثبت‌نام ---");
        System.out.println("1. ثبت‌نام کاربر جدید");
        System.out.println("2. ورود به حساب کاربری");
        System.out.println("3. خروج از برنامه");
        System.out.print("لطفاً یک گزینه را انتخاب کنید: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                handleRegister();
                break;
            case "2":
                handleLogin();
                break;
            case "3":
                System.out.println("👋 خروج از برنامه. روز خوش!");
                System.exit(0);
                break;
            default:
                System.out.println("❌ گزینه نامعتبر است. مجدداً تلاش کنید.");
        }
    }

    private static void showMainMenu() {
        User user = authService.getCurrentUser();
        System.out.println("\n--- پنل کاربری: " + user.getUsername() + " | موجودی: " + String.format("%,d", user.getBudget()) + " ریال ---");
        System.out.println("1. ثبت ملک جدید برای فروش/اجاره");
        System.out.println("2. مشاهده لیست خانه‌های فروشی");
        System.out.println("3. مشاهده لیست خانه‌های اجاره‌ای");
        System.out.println("4. مشاهده اطلاعات یک خانه با شناسه");
        System.out.println("5. خرید عادی ملک");
        System.out.println("6. اجاره ملک");
        System.out.println("7. خرید ویژه (امتیازی - پرداخت ۲ برابر قیمت)");
        System.out.println("8. فروش فوری ملک خود به بنگاه (۱۰٪ تخفیف)");
        System.out.println("9. بازگذاری ملک من برای فروش/اجاره");
        System.out.println("10. مشاهده خانه‌های خریداری‌شده من");
        System.out.println("11. مشاهده خانه‌های اجاره‌شده من");
        System.out.println("12. مشاهده قراردادهای من");
        System.out.println("13. مشاهده جزئیات قرارداد با شناسه");
        System.out.println("14. لغو قرارداد اجاره");
        System.out.println("15. شارژ حساب (افزایش موجودی)");
        System.out.println("16. خروج از حساب کاربری");
        System.out.print("لطفاً یک گزینه را انتخاب کنید: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                handleRegisterHouse();
                break;
            case "2":
                handleShowForSaleHouses();
                break;
            case "3":
                handleShowForRentHouses();
                break;
            case "4":
                handleShowHouseById();
                break;
            case "5":
                handlePurchaseHouse();
                break;
            case "6":
                handleRentHouse();
                break;
            case "7":
                handleSpecialPurchaseHouse();
                break;
            case "8":
                handleQuickSellHouse();
                break;
            case "9":
                handleRelistHouse();
                break;
            case "10":
                handleShowPurchasedHouses();
                break;
            case "11":
                handleShowRentedHouses();
                break;
            case "12":
                handleShowMyContracts();
                break;
            case "13":
                handleShowContractById();
                break;
            case "14":
                handleCancelContract();
                break;
            case "15":
                handleChargeAccount();
                break;
            case "16":
                authService.logout();
                System.out.println("👋 با موفقیت از حساب کاربری خارج شدید.");
                break;
            default:
                System.out.println("❌ گزینه نامعتبر است.");
        }
    }

    private static void handleRegister() {
        System.out.print("نام کاربری جدید: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("❌ خطا: نام کاربری نمی‌تواند خالی باشد.");
            return;
        }
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine();
        if (password.trim().isEmpty()) {
            System.out.println("❌ خطا: رمز عبور نمی‌تواند خالی باشد.");
            return;
        }
        System.out.print("تکرار رمز عبور: ");
        String passwordConfirm = scanner.nextLine();

        if (!password.equals(passwordConfirm)) {
            System.out.println("❌ خطا: رمز عبور و تکرار آن یکسان نیستند.");
            return;
        }

        long budget = readNonNegativeLong("موجودی اولیه (بودجه): ");

        boolean success = userService.registerUser(username, password, budget);
        if (success) {
            System.out.println("✅ کاربر جدید با موفقیت ثبت شد.");
        } else {
            System.out.println("❌ خطا: این نام کاربری قبلاً ثبت شده است.");
        }
    }

    private static void handleLogin() {
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine().trim();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine();

        boolean success = authService.login(username, password);
        if (success) {
            System.out.println("✅ ورود موفقیت‌آمیز بود. خوش آمدید " + username);
        } else {
            System.out.println("❌ خطا: نام کاربری یا رمز عبور اشتباه است.");
        }
    }

    private static void handleRegisterHouse() {
        System.out.println("\n--- نوع ملک را انتخاب کنید ---");
        System.out.println("1. آپارتمان (Apartment)");
        System.out.println("2. ویلا (Villa)");
        System.out.println("3. پنت‌هاوس (Penthouse)");
        System.out.print("گزینه: ");
        String type = scanner.nextLine();

        double area = readPositiveDouble("متراژ (مساحت به متر مربع): ");
        int bedrooms = readNonNegativeInt("تعداد اتاق خواب: ");
        int bathrooms = readNonNegativeInt("تعداد حمام/سرویس: ");
        int floor = readInt("طبقه: ");
        int region = readRangeInt("منطقه (عددی بین ۱ تا ۴): ", 1, 4);

        System.out.println("وضعیت معامله: 1. فروش | 2. اجاره");
        System.out.print("گزینه: ");
        House.DealStatus status = scanner.nextLine().equals("1") ? House.DealStatus.FOR_SALE : House.DealStatus.FOR_RENT;

        String houseId = propertyService.generateNextHouseId();
        String owner = authService.getCurrentUser().getUsername();

        HouseFactory.HouseTypeSpecificParams params = new HouseFactory.HouseTypeSpecificParams();
        if (type.equals(HouseFactory.TYPE_APARTMENT)) {
            params.setUnitNumber(readPositiveInt("شماره واحد: "));
            params.setTotalFloors(readPositiveInt("تعداد کل طبقات ساختمان: "));
            params.setTotalUnits(readPositiveInt("تعداد کل واحدهای ساختمان: "));
        } else if (type.equals(HouseFactory.TYPE_VILLA)) {
            params.setYardArea(readPositiveDouble("متراژ حیاط: "));
            params.setFloorsCount(readPositiveInt("تعداد طبقات ویلا: "));
        } else if (type.equals(HouseFactory.TYPE_PENTHOUSE)) {
            params.setTerraceArea(readPositiveDouble("متراژ تراس: "));
        } else {
            System.out.println("❌ نوع ملک نامعتبر است.");
            return;
        }

        House newHouse = HouseFactory.create(type, houseId, area, bedrooms, bathrooms, floor, region, owner, status, params);
        propertyService.registerHouse(newHouse);
        authService.getCurrentUser().addPurchasedHouse(houseId);
        StorageManager.saveData(appData);
        System.out.println("✅ ملک با موفقیت و با شناسه " + houseId + " ثبت شد:)");
    }

    private static String getHouseTypeName(House h) {
        if (h instanceof Apartment) return "آپارتمان (Apartment)";
        if (h instanceof Villa) return "ویلا (Villa)";
        if (h instanceof Penthouse) return "پنت‌هاوس (Penthouse)";
        return "نامشخص";
    }

    private static String getDealStatusLabel(House.DealStatus status) {
        if (status == null) return "نامشخص";
        switch (status) {
            case FOR_SALE: return "فقط برای فروش";
            case FOR_RENT: return "فقط برای اجاره";
            case BOTH: return "فروش و اجاره";
            case NOT_LISTED: return "خارج از لیست معامله (ثبت‌نشده)";
            default: return status.name();
        }
    }

    private static void printHouseSummary(House h) {
        System.out.printf("🆔 شناسه: %s | نوع: %s | مالک: %s | مستأجر: %s | وضعیت: %s | قیمت کل: %,d ریال | اجاره ماهیانه: %,d ریال%n",
                h.getId(),
                getHouseTypeName(h),
                h.getOwnerName(),
                h.getTenantName().isEmpty() ? "ندارد" : h.getTenantName(),
                getDealStatusLabel(h.getDealStatus()),
                h.calculatePrice(),
                h.calculateRent());
    }

    private static void printHouseFullDetails(House h) {
        System.out.println("--------------------------------------------------");
        System.out.printf("🏠 شناسه ملک: %s%n", h.getId());
        System.out.printf("🏢 نوع ملک: %s%n", getHouseTypeName(h));
        System.out.printf("📐 متراژ بنا: %.1f متر مربع%n", h.getArea());
        System.out.printf("🛏️ تعداد اتاق خواب: %d%n", h.getBedrooms());
        System.out.printf("🚿 تعداد حمام و سرویس: %d%n", h.getBathrooms());
        System.out.printf("🪜 طبقه: %d%n", h.getFloor());
        System.out.printf("📍 منطقه شهری: %d (ضریب قیمت: %.1f)%n", h.getRegion(), getRegionCoeff(h.getRegion()));
        if (h instanceof Apartment) {
            Apartment a = (Apartment) h;
            System.out.printf("🔢 شماره واحد: %d | تعداد کل طبقات: %d | تعداد کل واحدها: %d%n",
                    a.getUnitNumber(), a.getTotalFloors(), a.getTotalUnits());
        } else if (h instanceof Villa) {
            Villa v = (Villa) h;
            System.out.printf("🌳 متراژ حیاط: %.1f متر مربع | تعداد طبقات ویلا: %d%n",
                    v.getYardArea(), v.getFloorsCount());
        } else if (h instanceof Penthouse) {
            Penthouse p = (Penthouse) h;
            System.out.printf("🌅 متراژ تراس: %.1f متر مربع%n", p.getTerraceArea());
        }
        System.out.printf("👤 مالک فعلی: %s%n", h.getOwnerName());
        System.out.printf("👤 مستأجر: %s%n", h.getTenantName().isEmpty() ? "ندارد" : h.getTenantName());
        System.out.printf("📋 وضعیت معامله: %s%n", getDealStatusLabel(h.getDealStatus()));
        System.out.printf("💰 قیمت محاسبه‌شده برای خرید: %,d ریال%n", h.calculatePrice());
        System.out.printf("💳 اجاره ماهیانه محاسبه‌شده: %,d ریال%n", h.calculateRent());
        System.out.println("--------------------------------------------------");
    }

    private static double getRegionCoeff(int region) {
        switch (region) {
            case 1: return House.REGION_1_COEFFICIENT;
            case 2: return House.REGION_2_COEFFICIENT;
            case 3: return House.REGION_3_COEFFICIENT;
            case 4: return House.REGION_4_COEFFICIENT;
            default: return 1.0;
        }
    }

    private static void handleShowForSaleHouses() {
        System.out.println("\n--- لیست خانه‌های فروشی ---");
        boolean found = false;
        for (House h : propertyService.getAllHouses()) {
            if (h.getDealStatus() == House.DealStatus.FOR_SALE || h.getDealStatus() == House.DealStatus.BOTH) {
                printHouseSummary(h);
                found = true;
            }
        }
        if (!found) {
            System.out.println("هیچ خانه‌ای برای فروش ثبت نشده است.");
        }
    }

    private static void handleShowForRentHouses() {
        System.out.println("\n--- لیست خانه‌های اجاره‌ای ---");
        boolean found = false;
        for (House h : propertyService.getAllHouses()) {
            if (h.getDealStatus() == House.DealStatus.FOR_RENT || h.getDealStatus() == House.DealStatus.BOTH) {
                printHouseSummary(h);
                found = true;
            }
        }
        if (!found) {
            System.out.println("هیچ خانه‌ای برای اجاره ثبت نشده است.");
        }
    }

    private static void handleShowHouseById() {
        System.out.print("شناسه (ID) خانه مورد نظر را وارد کنید: ");
        String id = scanner.nextLine();
        House house = propertyService.findHouseById(id);
        if (house == null) {
            System.out.println("❌ خانه‌ای با این شناسه یافت نشد.");
            return;
        }
        printHouseFullDetails(house);
    }

    private static void handlePurchaseHouse() {
        System.out.print("شناسه (ID) ملک مورد نظر برای خرید را وارد کنید: ");
        String buyId = scanner.nextLine();
        TransactionResult result = transactionService.purchaseHouse(buyId);
        switch (result) {
            case SUCCESS:
                System.out.println("🎉 تبریک! ملک با موفقیت خریداری شد:)");
                break;
            case INVALID_DEAL_STATUS:
                System.out.println("❌ این ملک وجود ندارد یا برای فروش نیست!");
                break;
            case SELF_PURCHASE_FORBIDDEN:
                System.out.println("❌ خطا: شما خودتان مالک این ملک هستید!");
                break;
            case INSUFFICIENT_FUNDS:
                System.out.println("❌ موجودی حساب شما برای خرید این ملک کافی نیست!");
                break;
            default:
                System.out.println("❌ انجام معامله با خطا مواجه شد.");
        }
    }

    private static void handleRentHouse() {
        System.out.print("شناسه (ID) ملک مورد نظر برای اجاره را وارد کنید: ");
        String rentId = scanner.nextLine();
        TransactionResult result = transactionService.rentHouse(rentId);
        switch (result) {
            case SUCCESS:
                System.out.println("✅ قرارداد اجاره با موفقیت تنظیم و ملک اجاره شد:)");
                break;
            case INVALID_DEAL_STATUS:
                System.out.println("❌ این ملک برای اجاره در دسترس نیست!");
                break;
            case SELF_RENT_FORBIDDEN:
                System.out.println("❌ خطا: شما خودتان مالک این ملک هستید و نمی‌توانید آن را به خودتان اجاره دهید!");
                break;
            case INSUFFICIENT_FUNDS:
                System.out.println("❌ موجودی کافی برای پرداخت اجاره وجود ندارد!");
                break;
            default:
                System.out.println("❌ انجام معامله اجاره با خطا مواجه شد.");
        }
    }

    private static void handleSpecialPurchaseHouse() {
        System.out.print("⚠️ خرید ویژه قرارداد اجاره را لغو و مالک را عوض می‌کند.\nشناسه ملک را وارد کنید: ");
        String specialId = scanner.nextLine();
        TransactionResult result = transactionService.specialPurchaseHouse(specialId);
        switch (result) {
            case SUCCESS:
                System.out.println("🔥 خرید ویژه با موفقیت انجام شد! ملک با پرداخت ۲ برابر قیمت به مالکیت شما درآمد.");
                break;
            case HOUSE_NOT_FOUND:
                System.out.println("❌ ملک مورد نظر یافت نشد!");
                break;
            case SELF_PURCHASE_FORBIDDEN:
                System.out.println("❌ شما خودتان مالک این ملک هستید!");
                break;
            case INSUFFICIENT_FUNDS:
                System.out.println("❌ موجودی کافی برای خرید ویژه وجود ندارد!");
                break;
            default:
                System.out.println("❌ انجام خرید ویژه با خطا مواجه شد.");
        }
    }

    private static void handleQuickSellHouse() {
        System.out.print("شناسه ملکی که مالک آن هستید را برای فروش فوری وارد کنید: ");
        String quickSellId = scanner.nextLine();
        TransactionResult result = transactionService.quickSellToAgency(quickSellId);
        switch (result) {
            case SUCCESS:
                System.out.println("🏢 ملک شما با ۱۰٪ تخفیف به صورت فوری به بنگاه فروخته شد.");
                break;
            case NOT_THE_OWNER:
                System.out.println("❌ شما مالک این ملک نیستید یا ملک وجود ندارد.");
                break;
            default:
                System.out.println("❌ انجام فروش فوری با خطا مواجه شد.");
        }
    }

    private static void handleRelistHouse() {
        System.out.print("شناسه ملکی که مالک آن هستید را وارد کنید: ");
        String houseId = scanner.nextLine();
        System.out.println("وضعیت جدید: 1. فروش | 2. اجاره | 3. فروش و اجاره");
        System.out.print("گزینه: ");
        String choice = scanner.nextLine();
        House.DealStatus newStatus;
        switch (choice) {
            case "1":
                newStatus = House.DealStatus.FOR_SALE;
                break;
            case "2":
                newStatus = House.DealStatus.FOR_RENT;
                break;
            case "3":
                newStatus = House.DealStatus.BOTH;
                break;
            default:
                System.out.println("❌ گزینه نامعتبر است.");
                return;
        }

        TransactionResult result = transactionService.relistHouse(houseId, newStatus);
        switch (result) {
            case SUCCESS:
                System.out.println("✅ وضعیت ملک با موفقیت به‌روزرسانی شد.");
                break;
            case NOT_THE_OWNER:
                System.out.println("❌ شما مالک این ملک نیستید یا ملک وجود ندارد.");
                break;
            default:
                System.out.println("❌ به‌روزرسانی وضعیت ملک با خطا مواجه شد.");
        }
    }

    private static void showHouseIdsThenDetail(List<String> houseIds) {
        if (houseIds.isEmpty()) {
            System.out.println("موردی برای نمایش وجود ندارد.");
            return;
        }
        System.out.println("شناسه‌های خانه:");
        for (String id : houseIds) {
            System.out.println("🆔 " + id);
        }
        System.out.print("برای مشاهده جزئیات کامل، شناسه یک خانه را وارد کنید (یا خالی بگذارید): ");
        String chosenId = scanner.nextLine();
        if (chosenId.isEmpty()) {
            return;
        }
        House house = propertyService.findHouseById(chosenId);
        if (house == null) {
            System.out.println("❌ خانه‌ای با این شناسه یافت نشد.");
            return;
        }
        printHouseFullDetails(house);
    }

    private static void handleShowPurchasedHouses() {
        User user = authService.getCurrentUser();
        System.out.println("\n--- خانه‌های خریداری‌شده من ---");
        showHouseIdsThenDetail(user.getPurchasedHouseIds());
    }

    private static void handleShowRentedHouses() {
        User user = authService.getCurrentUser();
        System.out.println("\n--- خانه‌های اجاره‌شده من ---");
        showHouseIdsThenDetail(user.getRentedHouseIds());
    }

    private static void printContractSummary(Contract c, String currentUsername) {
        String oppositeParty = c.getLandlordName().equalsIgnoreCase(currentUsername)
                ? c.getTenantOrBuyerName()
                : c.getLandlordName();
        System.out.printf("🆔 قرارداد: %s | خانه: %s | نوع: %s | طرف مقابل: %s | قیمت: %,d ریال%n",
                c.getId(), c.getHouseId(), c.getContractType(),
                oppositeParty, c.getPrice());
    }

    private static void handleShowMyContracts() {
        User user = authService.getCurrentUser();
        System.out.println("\n--- قراردادهای من ---");
        List<Contract> contracts = contractService.getContractsForUser(user.getUsername());
        if (contracts.isEmpty()) {
            System.out.println("شما هیچ قراردادی ندارید.");
            return;
        }
        for (Contract c : contracts) {
            printContractSummary(c, user.getUsername());
        }
    }

    private static void handleShowContractById() {
        System.out.print("شناسه (ID) قرارداد مورد نظر را وارد کنید: ");
        String id = scanner.nextLine();
        Contract contract = contractService.findContractById(id);
        if (contract == null) {
            System.out.println("❌ قراردادی با این شناسه یافت نشد.");
            return;
        }
        System.out.printf("🆔 شناسه قرارداد: %s%n", contract.getId());
        System.out.printf("🏠 شناسه خانه: %s%n", contract.getHouseId());
        System.out.printf("📄 نوع قرارداد: %s%n", contract.getContractType());
        System.out.printf("👤 طرف مقابل (مالک/موجر): %s%n", contract.getLandlordName());
        System.out.printf("👤 مستأجر/خریدار: %s%n", contract.getTenantOrBuyerName());
        System.out.printf("💰 قیمت قرارداد: %,d ریال%n", contract.getPrice());
        System.out.printf("⚠️ جریمه لغو قرارداد: %,d ریال%n", contract.getCancellationPenalty());
    }

    private static void handleCancelContract() {
        System.out.print("شناسه (ID) قرارداد اجاره‌ای که می‌خواهید لغو کنید را وارد کنید: ");
        String id = scanner.nextLine();
        TransactionResult result = contractService.cancelContract(id, authService.getCurrentUser());
        switch (result) {
            case SUCCESS:
                System.out.println("✅ قرارداد با موفقیت لغو شد و جریمه پرداخت گردید.");
                break;
            case CONTRACT_NOT_FOUND:
                System.out.println("❌ قراردادی با این شناسه یافت نشد.");
                break;
            case NOT_CANCELLABLE:
                System.out.println("❌ این قرارداد قابل لغو نیست (قرارداد اجاره نیست).");
                break;
            case NOT_THE_TENANT:
                System.out.println("❌ شما مستأجر این قرارداد نیستید.");
                break;
            case INSUFFICIENT_FUNDS:
                System.out.println("❌ موجودی حساب شما برای پرداخت جریمه لغو کافی نیست!");
                break;
            default:
                System.out.println("❌ لغو قرارداد با خطا مواجه شد.");
        }
    }

    private static void handleChargeAccount() {
        long amount = readPositiveLong("مبلغ مورد نظر برای شارژ حساب را وارد کنید (ریال): ");
        boolean success = userService.chargeAccount(authService.getCurrentUser(), amount);
        if (success) {
            System.out.printf("✅ موجودی حساب شما با موفقیت %,d ریال افزایش یافت.%n", amount);
        } else {
            System.out.println("❌ خطا در شارژ حساب. مبلغ نامعتبر است.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ خطا: لطفاً یک عدد صحیح معتبر وارد کنید.");
            }
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            int val = readInt(prompt);
            if (val > 0) {
                return val;
            }
            System.out.println("❌ خطا: مقدار وارد شده باید یک عدد مثبت (بزرگتر از صفر) باشد.");
        }
    }

    private static int readNonNegativeInt(String prompt) {
        while (true) {
            int val = readInt(prompt);
            if (val >= 0) {
                return val;
            }
            System.out.println("❌ خطا: مقدار وارد شده نمی‌تواند منفی باشد.");
        }
    }

    private static int readRangeInt(String prompt, int min, int max) {
        while (true) {
            int val = readInt(prompt);
            if (val >= min && val <= max) {
                return val;
            }
            System.out.println("❌ خطا: مقدار وارد شده باید بین " + min + " و " + max + " باشد.");
        }
    }

    private static long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ خطا: لطفاً یک عدد معتبر وارد کنید.");
            }
        }
    }

    private static long readPositiveLong(String prompt) {
        while (true) {
            long val = readLong(prompt);
            if (val > 0) {
                return val;
            }
            System.out.println("❌ خطا: مقدار وارد شده باید یک عدد مثبت (بزرگتر از صفر) باشد.");
        }
    }

    private static long readNonNegativeLong(String prompt) {
        while (true) {
            long val = readLong(prompt);
            if (val >= 0) {
                return val;
            }
            System.out.println("❌ خطا: مقدار وارد شده نمی‌تواند منفی باشد.");
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ خطا: لطفاً یک عدد معتبر وارد کنید.");
            }
        }
    }

    private static double readPositiveDouble(String prompt) {
        while (true) {
            double val = readDouble(prompt);
            if (val > 0) {
                return val;
            }
            System.out.println("❌ خطا: مقدار وارد شده باید عددی بزرگتر از صفر باشد.");
        }
    }
}
