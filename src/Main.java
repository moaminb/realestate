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
    private static TransactionService transactionService = new TransactionService(appData, authService, userService, propertyService);
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
        System.out.println("2. مشاهده لیست تمام املاک موجود");
        System.out.println("3. خرید عادی ملک");
        System.out.println("4. اجاره ملک");
        System.out.println("5. خرید ویژه (امتیازی - پرداخت ۲ برابر قیمت)");
        System.out.println("6. فروش فوری ملک خود به بنگاه (۱۰٪ تخفیف)");
        System.out.println("7. مشاهده املاک من (خریداری یا اجاره شده)");
        System.out.println("8. خروج از حساب کاربری");
        System.out.println("9. شارژ حساب (افزایش موجودی)");
        System.out.print("لطفاً یک گزینه را انتخاب کنید: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                handleRegisterHouse();
                break;
            case "2":
                handleShowAllHouses();
                break;
            case "3":
                handlePurchaseHouse();
                break;
            case "4":
                handleRentHouse();
                break;
            case "5":
                handleSpecialPurchaseHouse();
                break;
            case "6":
                handleQuickSellHouse();
                break;
            case "7":
                handleShowMyProperties();
                break;
            case "8":
                authService.logout();
                System.out.println("👋 با موفقیت از حساب کاربری خارج شدید.");
                break;
            case "9":
                handleChargeAccount();
                break;
            default:
                System.out.println("❌ گزینه نامعتبر است.");
        }
    }

    private static void handleRegister() {
        System.out.print("نام کاربری جدید: ");
        String username = scanner.nextLine();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine();
        long budget = readLong("موجودی اولیه (بودجه): ");

        boolean success = userService.registerUser(username, password, budget);
        if (success) {
            System.out.println("✅ کاربر جدید با موفقیت ثبت شد.");
        } else {
            System.out.println("❌ خطا: این نام کاربری قبلاً ثبت شده است.");
        }
    }

    private static void handleLogin() {
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine();
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

        double area = readDouble("متراژ (مساحت به متر مربع): ");
        int bedrooms = readInt("تعداد اتاق خواب: ");
        int bathrooms = readInt("تعداد حمام/سرویس: ");
        int floor = readInt("طبقه: ");
        int region = readInt("منطقه (عددی بین ۱ تا ۴): ");

        System.out.println("وضعیت معامله: 1. فروش | 2. اجاره");
        System.out.print("گزینه: ");
        House.DealStatus status = scanner.nextLine().equals("1") ? House.DealStatus.FOR_SALE : House.DealStatus.FOR_RENT;

        String houseId = "HSE-" + (propertyService.getHousesCount() + 1);
        String owner = authService.getCurrentUser().getUsername();

        House newHouse = null;
        if (type.equals("1")) {
            int unitNo = readInt("شماره واحد: ");
            int totalFloors = readInt("تعداد کل طبقات ساختمان: ");
            int totalUnits = readInt("تعداد کل واحدهای ساختمان: ");
            newHouse = HouseFactory.createApartment(houseId, area, bedrooms, bathrooms, floor, region, owner, status, unitNo, totalFloors, totalUnits);
        } else if (type.equals("2")) {
            double yardArea = readDouble("متراژ حیاط: ");
            int floorsCount = readInt("تعداد طبقات ویلا: ");
            newHouse = HouseFactory.createVilla(houseId, area, bedrooms, bathrooms, floor, region, owner, status, yardArea, floorsCount);
        } else if (type.equals("3")) {
            double terraceArea = readDouble("متراژ تراس: ");
            newHouse = HouseFactory.createPenthouse(houseId, area, bedrooms, bathrooms, floor, region, owner, status, terraceArea);
        } else {
            System.out.println("❌ نوع ملک نامعتبر است.");
            return;
        }

        propertyService.registerHouse(newHouse);
        System.out.println("✅ ملک با موفقیت و با شناسه " + houseId + " ثبت شد:)");
    }

    private static void handleShowAllHouses() {
        System.out.println("\n--- لیست تمام املاک سیستم ---");
        if (propertyService.getAllHouses().isEmpty()) {
            System.out.println("هیچ ملکی ثبت نشده است.");
            return;
        }
        for (House h : propertyService.getAllHouses()) {
            System.out.printf("🆔 شناسه: %s | نوع: %s | مالک: %s | مستأجر: %s | وضعیت: %s | قیمت کل: %,d ریال | اجاره ماهیانه: %,d ریال%n",
                    h.getId(),
                    h.getClass().getSimpleName(),
                    h.getOwnerName(),
                    h.getTenantName().isEmpty() ? "ندارد" : h.getTenantName(),
                    h.getDealStatus(),
                    h.calculatePrice(),
                    h.calculateRent());
        }
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

    private static void handleShowMyProperties() {
        User user = authService.getCurrentUser();
        System.out.println("\n--- املاک تحت مالکیت شما ---");
        boolean hasProperty = false;
        for (House h : propertyService.getAllHouses()) {
            if (h.getOwnerName().equals(user.getUsername())) {
                System.out.printf("🏠 [مالک هستید] شناسه: %s | نوع: %s | وضعیت معامله فعلی: %s%n", h.getId(), h.getClass().getSimpleName(), h.getDealStatus());
                hasProperty = true;
            }
            if (h.getTenantName().equals(user.getUsername())) {
                System.out.printf("🔑 [مستأجر هستید] شناسه: %s | نوع: %s | مالک اصلی: %s%n", h.getId(), h.getClass().getSimpleName(), h.getOwnerName());
                hasProperty = true;
            }
        }
        if (!hasProperty) {
            System.out.println("شما در حال حاضر مالک یا مستأجر هیچ ملکی نیستید.");
        }
    }

    private static void handleChargeAccount() {
        long amount = readLong("مبلغ مورد نظر برای شارژ حساب را وارد کنید (ریال): ");
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
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ خطا: لطفاً یک عدد صحیح معتبر وارد کنید.");
            }
        }
    }

    private static long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ خطا: لطفاً یک عدد معتبر وارد کنید.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ خطا: لطفاً یک عدد معتبر وارد کنید.");
            }
        }
    }
}