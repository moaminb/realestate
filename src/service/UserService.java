package service;

import model.User;
import storage.AppData;
import storage.StorageManager;
import util.SecurityUtils;

public class UserService {
    private final AppData data;
    private User currentUser;

    public UserService(AppData data) {
        this.data = data;
        this.currentUser = null;
    }

    public boolean registerUser(String username, String password, long initialBudget) {
        for (User u : data.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                System.out.println("❌ این نام کاربری قبلاً ثبت شده است.");
                return false;
            }
        }
        String id = "USR-" + (data.getUsers().size() + 1);
        String hashedPassword = SecurityUtils.hashPassword(password);
        User newUser = new User(id, username, hashedPassword, initialBudget);
        data.getUsers().add(newUser);
        StorageManager.saveData(data);
        System.out.println("✅ کاربر جدید با موفقیت ثبت شد.");
        return true;
    }

    public boolean loginUser(String username, String password) {
        String hashedPassword = SecurityUtils.hashPassword(password);
        for (User u : data.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(hashedPassword)) {
                this.currentUser = u;
                System.out.println("✅ ورود موفقیت‌آمیز بود. خوش آمدید " + username);
                return true;
            }
        }
        System.out.println("❌ نام کاربری یا رمز عبور اشتباه است.");
        return false;
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("👋 کاربر " + currentUser.getUsername() + " از حساب کاربری خود خارج شد.");
            this.currentUser = null;
        }
    }

    public boolean chargeAccount(long amount) {
        if (currentUser == null) {
            System.out.println("❌ هیچ کاربری وارد سیستم نشده است.");
            return false;
        }
        if (amount > 0) {
            currentUser.deposit(amount);
            StorageManager.saveData(data);
            System.out.printf("✅ موجودی حساب شما با موفقیت %,d ریال افزایش یافت.%n", amount);
            return true;
        } else {
            System.out.println("❌ خطا در شارژ حساب. مبلغ نامعتبر است.");
            return false;
        }
    }

    public User findUserByUsername(String username) {
        return data.getUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
