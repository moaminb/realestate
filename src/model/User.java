package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String password;

    // 🔴 ادیت اصلی: تغییر نوع داده بودجه از double به long
    private long budget;

    private List<String> purchasedHouseIds;
    private List<String> rentedHouseIds;

    // 🔴 ادیت: تغییر ورودی سازنده به long
    public User(String id, String username, String password, long initialBudget) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.budget = initialBudget;
        this.purchasedHouseIds = new ArrayList<>();
        this.rentedHouseIds = new ArrayList<>();
    }

    // 🔴 ادیت: تغییر خروجی به long برای هماهنگی با فرمت %,d در منوی اصلی
    public long getBudget() {
        return budget;
    }

    // 🔴 ادیت: تغییر ورودی متد واریز به long
    public void deposit(long amount) {
        if (amount > 0) {
            this.budget += amount;
        }
    }

    // 🔴 ادیت: تغییر ورودی متد برداشت به long
    public boolean withdraw(long amount) {
        if (amount > 0 && this.budget >= amount) {
            this.budget -= amount;
            return true;
        }
        return false;
    }

    // --- سایر متدهای کلاس بدون تغییر ---
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public List<String> getPurchasedHouseIds() { return purchasedHouseIds; }
    public void addPurchasedHouse(String houseId) { this.purchasedHouseIds.add(houseId); }

    public List<String> getRentedHouseIds() { return rentedHouseIds; }
    public void addRentedHouse(String houseId) { this.rentedHouseIds.add(houseId); }
    public void removeRentedHouse(String houseId) { this.rentedHouseIds.remove(houseId); }
}