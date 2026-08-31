package test;

import model.User;
import service.AuthService;
import service.UserService;
import storage.AppData;
import util.SecurityUtils;

public class SecurityAndAuthTest {
    public static void run() {
        System.out.println("\n--- ۲. تست‌های امنیت و احراز هویت (SecurityAndAuthTest) ---");

        // SecurityUtils Tests
        String hash1 = SecurityUtils.hashPassword("mySecretPass123");
        String hash2 = SecurityUtils.hashPassword("mySecretPass123");
        String hash3 = SecurityUtils.hashPassword("differentPass");

        TestAssert.assertTrue("هش یک پسورد یکسان باید همواره یکسان باشد", hash1.equals(hash2));
        TestAssert.assertTrue("هش دو پسورد متفاوت باید متمایز باشد", !hash1.equals(hash3));
        TestAssert.assertEquals("طول هش SHA-256 باید ۶۴ کاراکتر هگزادسیمال باشد", 64, hash1.length());
        TestAssert.assertTrue("هش رمز خالی بدون استثنا تولید می‌شود", SecurityUtils.hashPassword("").length() == 64);
        TestAssert.assertTrue("هش مقدار null بدون استثنا مدیریت می‌شود", SecurityUtils.hashPassword(null).length() == 64);

        // AuthService Tests
        AppData data = new AppData();
        UserService userService = new UserService(data);
        AuthService authService = new AuthService(data);

        userService.registerUser("john_doe", "Secret99", 5000000L);

        TestAssert.assertTrue("قبل از لاگین وضعیت کاربری null است", !authService.isLoggedIn());

        boolean loginWrongPass = authService.login("john_doe", "wrongpass");
        TestAssert.assertTrue("لاگین با پسورد اشتباه ناموفق است", !loginWrongPass);
        TestAssert.assertTrue("همچنان لاگین نشده است", !authService.isLoggedIn());

        boolean loginOk = authService.login("JOHN_DOE", "Secret99");
        TestAssert.assertTrue("لاگین با نام کاربری و پسورد درست موفق است", loginOk);
        TestAssert.assertTrue("وضعیت کاربری پس از لاگین فعال است", authService.isLoggedIn());
        TestAssert.assertEquals("کاربر لاگین شده تطابق دارد", "john_doe", authService.getCurrentUser().getUsername());

        authService.logout();
        TestAssert.assertTrue("پس از خروج وضعیت کاربری لاگین نیست", !authService.isLoggedIn());
        TestAssert.assertTrue("کاربر جاری پس از خروج null است", authService.getCurrentUser() == null);
    }
}
