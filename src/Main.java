import java.util.Scanner;
import model.*;
import service.PropertyService;
import service.UserService;
import storage.AppData;
import storage.StorageManager;

public class Main {
    private static AppData appData = StorageManager.loadData();
    private static UserService userService = new UserService(appData);
    private static PropertyService propertyService = new PropertyService(appData, userService);
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("🏢 به سامانه آنلاین بنگاه معاملات ملکی خوش آمدید");

        while (true) {
            if (!userService.isLoggedIn()) {
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
        User user = userService.getCurrentUser();
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
                System.out.print("شناسه (ID) ملک مورد نظر برای خرید را وارد کنید: ");
                String buyId = scanner.nextLine();
                propertyService.purchaseHouse(buyId);
                break;
            case "4":
                System.out.print("شناسه (ID) ملک مورد نظر برای اجاره را وارد کنید: ");
                String rentId = scanner.nextLine();
                propertyService.rentHouse(rentId);
                break;
            case "5":
                System.out.print("⚠️ خرید ویژه قرارداد اجاره را لغو و مالک را عوض می‌کند.\nشناسه ملک را وارد کنید: ");
                String specialId = scanner.nextLine();
                propertyService.specialPurchaseHouse(specialId);
                break;
            case "6":
                System.out.print("شناسه ملکی که مالک آن هستید را برای فروش فوری وارد کنید: ");
                String quickSellId = scanner.nextLine();
                propertyService.quickSellToAgency(quickSellId);
                break;
            case "7":
                handleShowMyProperties();
                break;
            case "8":
                userService.logout();
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

        userService.registerUser(username, password, budget);
    }

    private static void handleLogin() {
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine();

        userService.loginUser(username, password);
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
        String owner = userService.getCurrentUser().getUsername();

        if (type.equals("1")) {
            int unitNo = readInt("شماره واحد: ");
            int totalFloors = readInt("تعداد کل طبقات ساختمان: ");
            int totalUnits = readInt("تعداد کل واحدهای ساختمان: ");

            propertyService.registerHouse(new Apartment(houseId, area, bedrooms, bathrooms, floor, region, owner, status, unitNo, totalFloors, totalUnits));
        } else if (type.equals("2")) {
            double yardArea = readDouble("متراژ حیاط: ");
            int floorsCount = readInt("تعداد طبقات ویلا: ");

            propertyService.registerHouse(new Villa(houseId, area, bedrooms, bathrooms, floor, region, owner, status, yardArea, floorsCount));
        } else if (type.equals("3")) {
            double terraceArea = readDouble("متراژ تراس: ");

            propertyService.registerHouse(new Penthouse(houseId, area, bedrooms, bathrooms, floor, region, owner, status, terraceArea));
        } else {
            System.out.println("❌ نوع ملک نامعتبر است.");
        }
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

    private static void handleShowMyProperties() {
        User user = userService.getCurrentUser();
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
        userService.chargeAccount(amount);
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