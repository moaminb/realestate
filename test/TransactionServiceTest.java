package test;

import model.*;
import service.*;
import storage.AppData;

public class TransactionServiceTest {
    public static void run() {
        System.out.println("\n--- ۶. تست‌های سرویس معاملات و رفع باگ‌ها (TransactionServiceTest) ---");
        AppData data = new AppData();
        AuthService authService = new AuthService(data);
        UserService userService = new UserService(data);
        PropertyService propertyService = new PropertyService(data);
        ContractService contractService = new ContractService(data, userService, propertyService);
        TransactionService transactionService = new TransactionService(data, authService, userService, propertyService, contractService);

        // Register Users
        userService.registerUser("seller", "pass1", 100000000L);
        userService.registerUser("buyer", "pass2", 5000000000L);
        userService.registerUser("poor_buyer", "pass3", 1000000L);

        // Register House
        Apartment apt = new Apartment("HSE-1", 100, 2, 1, 1, 1, "seller", House.DealStatus.FOR_SALE, 1, 3, 6);
        propertyService.registerHouse(apt);
        User sellerObj = userService.findUserByUsername("seller");
        sellerObj.addPurchasedHouse("HSE-1");
        long aptPrice = apt.calculatePrice();

        // 1. Purchase without login
        TransactionResult resNoLogin = transactionService.purchaseHouse("HSE-1");
        TestAssert.assertEquals("تلاش برای خرید بدون لاگین", TransactionResult.NOT_LOGGED_IN, resNoLogin);

        // 2. Self Purchase
        authService.login("seller", "pass1");
        TransactionResult resSelf = transactionService.purchaseHouse("HSE-1");
        TestAssert.assertEquals("تلاش برای خرید ملک متعلق به خود کاربر", TransactionResult.SELF_PURCHASE_FORBIDDEN, resSelf);

        // 3. Purchase with insufficient funds
        authService.login("poor_buyer", "pass3");
        TransactionResult resPoor = transactionService.purchaseHouse("HSE-1");
        TestAssert.assertEquals("خرید با موجودی ناکافی", TransactionResult.INSUFFICIENT_FUNDS, resPoor);

        // 4. Successful Purchase
        authService.login("buyer", "pass2");
        long buyerBudgetBefore = authService.getCurrentUser().getBudget();
        long sellerBudgetBefore = sellerObj.getBudget();

        TransactionResult resSuccess = transactionService.purchaseHouse("HSE-1");
        TestAssert.assertEquals("خرید موفق ملک عادی", TransactionResult.SUCCESS, resSuccess);
        TestAssert.assertEquals("کسر وجه از خریدار", buyerBudgetBefore - aptPrice, authService.getCurrentUser().getBudget());
        TestAssert.assertEquals("واریز وجه به فروشنده", sellerBudgetBefore + aptPrice, sellerObj.getBudget());
        TestAssert.assertEquals("تغییر مالک خانه به خریدار", "buyer", apt.getOwnerName());
        TestAssert.assertEquals("تغییر وضعیت معامله به NOT_LISTED پس از خرید", House.DealStatus.NOT_LISTED, apt.getDealStatus());
        TestAssert.assertTrue("اضافه شدن خانه به لیست خریدهای خریدار", authService.getCurrentUser().getPurchasedHouseIds().contains("HSE-1"));
        TestAssert.assertTrue("حذف خانه از لیست خریدهای فروشنده سابق", !sellerObj.getPurchasedHouseIds().contains("HSE-1"));

        // 5. Relist House
        TransactionResult resRelist = transactionService.relistHouse("HSE-1", House.DealStatus.FOR_RENT);
        TestAssert.assertEquals("بازگذاری مجدد ملک برای اجاره توسط مالک جدید", TransactionResult.SUCCESS, resRelist);
        TestAssert.assertEquals("وضعیت جدید معامله", House.DealStatus.FOR_RENT, apt.getDealStatus());

        // 6. Self Rent Prevention
        TransactionResult resSelfRent = transactionService.rentHouse("HSE-1");
        TestAssert.assertEquals("جلوگیری از اجاره ملک توسط خود مالک", TransactionResult.SELF_RENT_FORBIDDEN, resSelfRent);

        // 7. Rent House by another user
        authService.login("poor_buyer", "pass3");
        userService.chargeAccount(authService.getCurrentUser(), 50000000L);
        long tenantBudgetBefore = authService.getCurrentUser().getBudget();
        long rentPrice = apt.calculateRent();
        long landlordBudgetBefore = userService.findUserByUsername("buyer").getBudget();

        TransactionResult resRent = transactionService.rentHouse("HSE-1");
        TestAssert.assertEquals("اجاره موفق ملک", TransactionResult.SUCCESS, resRent);
        TestAssert.assertEquals("کسر اجاره از مستأجر", tenantBudgetBefore - rentPrice, authService.getCurrentUser().getBudget());
        TestAssert.assertEquals("واریز اجاره به مالک", landlordBudgetBefore + rentPrice, userService.findUserByUsername("buyer").getBudget());
        TestAssert.assertEquals("ثبت نام مستأجر روی خانه", "poor_buyer", apt.getTenantName());
        TestAssert.assertTrue("اضافه شدن به لیست اجاره‌های کاربر", authService.getCurrentUser().getRentedHouseIds().contains("HSE-1"));
        TestAssert.assertEquals("ایجاد قرارداد اجاره در سیستم", 1, data.getContracts().size());

        // 8. Quick Sell to Agency
        Villa villa = new Villa("HSE-2", 150, 2, 1, 1, 1, "seller", House.DealStatus.FOR_SALE, 50, 1);
        propertyService.registerHouse(villa);
        sellerObj.addPurchasedHouse("HSE-2");
        authService.login("seller", "pass1");
        long sellerBudgetBeforeQuick = authService.getCurrentUser().getBudget();
        long quickPrice = (long) (villa.calculatePrice() * 0.9);

        TransactionResult resQuick = transactionService.quickSellToAgency("HSE-2");
        TestAssert.assertEquals("فروش فوری ملک به بنگاه با ۱۰٪ تخفیف", TransactionResult.SUCCESS, resQuick);
        TestAssert.assertEquals("واریز ۹۰٪ قیمت به فروشنده", sellerBudgetBeforeQuick + quickPrice, authService.getCurrentUser().getBudget());
        TestAssert.assertTrue("حذف ملک از لیست خریدهای فروشنده پس از فروش فوری به بنگاه", !sellerObj.getPurchasedHouseIds().contains("HSE-2"));
        TestAssert.assertEquals("مالک جدید بنگاه املاک است", Agency.AGENCY_OWNER_NAME, villa.getOwnerName());
        TestAssert.assertEquals("وضعیت معامله خانه فروخته شده به بنگاه BOTH است", House.DealStatus.BOTH, villa.getDealStatus());
        TestAssert.assertTrue("ثبت شناسه در لیست املاک بنگاه", data.getAgency().getAgencyHouseIds().contains("HSE-2"));

        // 9. Purchase from Agency
        authService.login("buyer", "pass2");
        userService.chargeAccount(authService.getCurrentUser(), 10000000000L);
        TransactionResult resBuyAgency = transactionService.purchaseHouse("HSE-2");
        TestAssert.assertEquals("خرید ملک از بنگاه توسط کاربر", TransactionResult.SUCCESS, resBuyAgency);
        TestAssert.assertEquals("تغییر مالک از بنگاه به خریدار", "buyer", villa.getOwnerName());
        TestAssert.assertTrue("حذف ملک از لیست بنگاه پس از خرید", !data.getAgency().getAgencyHouseIds().contains("HSE-2"));
        TestAssert.assertTrue("اضافه شدن به لیست خریدهای خریدار جدید", authService.getCurrentUser().getPurchasedHouseIds().contains("HSE-2"));

        // 10. Ghost Contract Elimination upon Purchase
        authService.login("seller", "pass1");
        userService.chargeAccount(authService.getCurrentUser(), 5000000000L);
        apt.setDealStatus(House.DealStatus.BOTH);
        TransactionResult buyOccupiedRes = transactionService.purchaseHouse("HSE-1");
        TestAssert.assertEquals("خرید خانه دارای مستأجر", TransactionResult.SUCCESS, buyOccupiedRes);
        TestAssert.assertEquals("تخلیه مستأجر از ملک", "", apt.getTenantName());
        TestAssert.assertEquals("ابطال خودکار قرارداد اجاره فعال", 0, data.getContracts().size());
        TestAssert.assertTrue("حذف از لیست اجاره‌های مستأجر سابق", !userService.findUserByUsername("poor_buyer").getRentedHouseIds().contains("HSE-1"));

        // 11. Special Purchase
        userService.registerUser("rich_buyer", "p", 20000000000L);
        Apartment houseForSpecial = new Apartment("HSE-SPEC", 100, 2, 1, 1, 1, "seller", House.DealStatus.FOR_RENT, 1, 2, 4);
        propertyService.registerHouse(houseForSpecial);
        sellerObj.addPurchasedHouse("HSE-SPEC");

        authService.login("poor_buyer", "pass3");
        transactionService.rentHouse("HSE-SPEC");
        TestAssert.assertEquals("ثبت قرارداد اجاره قبل از خرید ویژه", 1, data.getContracts().size());

        authService.login("rich_buyer", "p");
        TransactionResult specialRes = transactionService.specialPurchaseHouse("HSE-SPEC");
        TestAssert.assertEquals("انجام موفق خرید ویژه", TransactionResult.SUCCESS, specialRes);
        TestAssert.assertTrue("حذف ملک از لیست خریدهای فروشنده سابق", !sellerObj.getPurchasedHouseIds().contains("HSE-SPEC"));
        TestAssert.assertTrue("اضافه شدن ملک به لیست خریدار ویژه", authService.getCurrentUser().getPurchasedHouseIds().contains("HSE-SPEC"));
        TestAssert.assertEquals("قرارداد خرید ویژه جایگزین قرارداد اجاره شد", 1, data.getContracts().size());
        TestAssert.assertEquals("نوع قرارداد جدید ویژه است", Contract.ContractType.SPECIAL_PURCHASE, data.getContracts().get(0).getContractType());
    }
}
