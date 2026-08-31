package test;

import model.Apartment;
import model.House;
import model.User;
import storage.AppData;
import storage.StorageManager;

import java.io.File;

public class StorageManagerTest {
    public static void run() {
        System.out.println("\n--- ۷. تست‌های ذخیره‌سازی داده‌ها (StorageManagerTest) ---");
        AppData data = new AppData();
        User u = new User("USR-1", "savedUser", "hashedPass", 123456L);
        data.getUsers().add(u);
        Apartment apt = new Apartment("HSE-99", 80, 1, 1, 2, 3, "savedUser", House.DealStatus.FOR_SALE, 3, 4, 8);
        data.getHouses().add(apt);

        StorageManager.saveData(data);

        AppData loaded = StorageManager.loadData();
        TestAssert.assertEquals("تعداد کاربران بازیابی شده", 1, loaded.getUsers().size());
        TestAssert.assertEquals("نام کاربر بازیابی شده", "savedUser", loaded.getUsers().get(0).getUsername());
        TestAssert.assertEquals("تعداد املاک بازیابی شده", 1, loaded.getHouses().size());
        TestAssert.assertEquals("شناسه ملک بازیابی شده", "HSE-99", loaded.getHouses().get(0).getId());

        // Clean up test file
        File file = new File("database.dat");
        if (file.exists()) {
            file.delete();
        }
    }
}
