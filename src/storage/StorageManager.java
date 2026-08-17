package storage;

import java.io.*;

public class StorageManager {
    private static final String FILE_NAME = "database.dat";

    public static void saveData(AppData data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("❌ خطا در ذخیره‌سازی داده‌ها: " + e.getMessage());
        }
    }

    public static AppData loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new AppData();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (AppData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new AppData();
        }
    }
}