package storage;

import java.io.*;

public class StorageManager {
    private static final String FILE_NAME = "database.dat";

    // متد ذخیره کردن کل داده‌های برنامه در فایل
    public static void saveData(AppData data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(data);
            System.out.println(" داده‌های برنامه با موفقیت ذخیره شدند.");
        } catch (IOException e) {
            System.err.println("❌ خطا در ذخیره‌سازی داده‌ها: " + e.getMessage());
        }
    }

    // متد بازیابی داده‌ها از فایل (اگر فایل وجود نداشته باشد، یک دیتابیس خالی می‌سازد)
    public static AppData loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("ℹ️ فایل داده‌ها یافت نشد. یک پایگاه داده جدید ایجاد شد.");
            return new AppData(); // برگشت دیتابیس خالی برای شروع کار
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (AppData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ خطا در بارگذاری داده‌ها (احتمالاً فایل آسیب دیده است): " + e.getMessage());
            // در صورت بروز خطا برای جلوگیری از کرش، یک دیتابیس جدید و خالی برمی‌گردانیم
            return new AppData();
        }
    }
}