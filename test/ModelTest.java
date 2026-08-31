package test;

import model.*;

public class ModelTest {
    public static void run() {
        System.out.println("\n--- ۱. تست‌های مدل‌ها و فرمول‌های قیمت‌گذاری (ModelTest) ---");

        // Apartment Test
        Apartment apt = new Apartment("HSE-1", 100, 2, 1, 3, 1, "ali", House.DealStatus.FOR_SALE, 12, 5, 20);
        TestAssert.assertEquals("محاسبه قیمت پایه آپارتمان", 1800000000L, apt.calculateBasePrice());
        TestAssert.assertEquals("محاسبه قیمت نهایی آپارتمان", 1965240000L, apt.calculatePrice());
        TestAssert.assertEquals("محاسبه اجاره ماهانه آپارتمان", 7860960L, apt.calculateRent());

        // Villa Test
        Villa villa = new Villa("HSE-2", 200, 3, 2, 1, 2, "reza", House.DealStatus.FOR_RENT, 150, 2);
        TestAssert.assertEquals("محاسبه قیمت پایه ویلا", 2800000000L, villa.calculateBasePrice());
        TestAssert.assertEquals("محاسبه قیمت نهایی ویلا", 3280000000L, villa.calculatePrice());
        TestAssert.assertEquals("محاسبه اجاره ماهانه ویلا", 13120000L, villa.calculateRent());

        // Penthouse Test
        Penthouse ph = new Penthouse("HSE-3", 300, 4, 3, 10, 1, "sara", House.DealStatus.BOTH, 50);
        TestAssert.assertEquals("محاسبه قیمت پایه پنت‌هاوس", 5400000000L, ph.calculateBasePrice());
        TestAssert.assertEquals("محاسبه قیمت نهایی پنت‌هاوس", 8350000000L, ph.calculatePrice());
        TestAssert.assertEquals("محاسبه اجاره ماهانه پنت‌هاوس", 33400000L, ph.calculateRent());

        // User Test
        User user = new User("USR-1", "testuser", "pass123", 5000000000L);
        TestAssert.assertEquals("بررسی موجودی اولیه کاربر", 5000000000L, user.getBudget());
        user.deposit(1000000000L);
        TestAssert.assertEquals("واریز به حساب کاربر", 6000000000L, user.getBudget());
        boolean withdrawOk = user.withdraw(2000000000L);
        TestAssert.assertTrue("برداشت موفق از حساب", withdrawOk);
        TestAssert.assertEquals("موجودی پس از برداشت", 4000000000L, user.getBudget());
        boolean withdrawFail = user.withdraw(5000000000L);
        TestAssert.assertTrue("عدم امکان برداشت بیش از موجودی", !withdrawFail);
        TestAssert.assertEquals("عدم تغییر موجودی پس از برداشت ناموفق", 4000000000L, user.getBudget());

        user.addPurchasedHouse("HSE-1");
        TestAssert.assertTrue("اضافه شدن به لیست خانه‌های خریداری شده", user.getPurchasedHouseIds().contains("HSE-1"));
        user.removePurchasedHouse("HSE-1");
        TestAssert.assertTrue("حذف از لیست خانه‌های خریداری شده", !user.getPurchasedHouseIds().contains("HSE-1"));

        // Contract Test
        Contract rentContract = new Contract("CTR-1", "HSE-1", "ali", "reza", 10000000L, Contract.ContractType.RENT);
        TestAssert.assertEquals("محاسبه جریمه لغو قرارداد اجاره (۱.۵ برابر)", 15000000L, rentContract.getCancellationPenalty());
        TestAssert.assertTrue("امکان لغو قرارداد با موجودی کافی", rentContract.canCancel(20000000L));
        TestAssert.assertTrue("عدم امکان لغو قرارداد با موجودی ناکافی", !rentContract.canCancel(10000000L));

        Contract specialContract = new Contract("CTR-2", "HSE-2", "ali", "reza", 5000000000L, Contract.ContractType.SPECIAL_PURCHASE);
        TestAssert.assertEquals("جریمه لغو برای خرید ویژه صفر است", 0L, specialContract.getCancellationPenalty());
        TestAssert.assertTrue("قرارداد خرید ویژه قابل لغو نیست", !specialContract.canCancel(10000000000L));
    }
}
