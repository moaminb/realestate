package test;

import model.User;
import service.UserService;
import storage.AppData;

public class UserServiceTest {
    public static void run() {
        System.out.println("\n--- ۳. تست‌های سرویس کاربران (UserServiceTest) ---");
        AppData data = new AppData();
        UserService userService = new UserService(data);

        boolean reg1 = userService.registerUser("user1", "pass1", 1000000L);
        TestAssert.assertTrue("ثبت‌نام کاربر اول موفق", reg1);
        TestAssert.assertEquals("تعداد کاربران در حافظه", 1, data.getUsers().size());

        boolean regEmptyUser = userService.registerUser("   ", "pass1", 1000000L);
        TestAssert.assertTrue("عدم پذیرش نام کاربری خالی", !regEmptyUser);

        boolean regEmptyPass = userService.registerUser("user2", "   ", 1000000L);
        TestAssert.assertTrue("عدم پذیرش رمز عبور خالی", !regEmptyPass);

        boolean regNegBudget = userService.registerUser("user3", "pass", -100L);
        TestAssert.assertTrue("عدم پذیرش بودجه منفی", !regNegBudget);

        boolean regDuplicate = userService.registerUser("user1", "pass2", 2000000L);
        TestAssert.assertTrue("جلوگیری از ثبت‌نام نام کاربری تکراری", !regDuplicate);

        boolean regCaseInsensitive = userService.registerUser("USER1", "pass3", 3000000L);
        TestAssert.assertTrue("جلوگیری از ثبت نام کاربری تکراری با حروف بزرگ", !regCaseInsensitive);

        User found = userService.findUserByUsername("User1");
        TestAssert.assertTrue("یافتن کاربر بدون حساسیت به حروف بزرگ/کوچک", found != null && found.getUsername().equals("user1"));

        User foundById = userService.findUserById("USR-1");
        TestAssert.assertTrue("یافتن کاربر بر اساس شناسه ID", foundById != null && foundById.getUsername().equals("user1"));

        boolean chargeOk = userService.chargeAccount(found, 500000L);
        TestAssert.assertTrue("شارژ حساب کاربر", chargeOk);
        TestAssert.assertEquals("موجودی پس از شارژ", 1500000L, found.getBudget());

        boolean chargeNegative = userService.chargeAccount(found, -100000L);
        TestAssert.assertTrue("عدم پذیرش مبلغ منفی در شارژ حساب", !chargeNegative);
    }
}
