package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String password;

    private long budget;

    private List<String> purchasedHouseIds;
    private List<String> rentedHouseIds;

    public User(String id, String username, String password, long initialBudget) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.budget = initialBudget;
        this.purchasedHouseIds = new ArrayList<>();
        this.rentedHouseIds = new ArrayList<>();
    }

    public long getBudget() {
        return budget;
    }

    public void deposit(long amount) {
        if (amount > 0) {
            this.budget += amount;
        }
    }

    public boolean withdraw(long amount) {
        if (amount > 0 && this.budget >= amount) {
            this.budget -= amount;
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getPurchasedHouseIds() {
        return purchasedHouseIds;
    }

    public void addPurchasedHouse(String houseId) {
        this.purchasedHouseIds.add(houseId);
    }

    public List<String> getRentedHouseIds() {
        return rentedHouseIds;
    }

    public void addRentedHouse(String houseId) {
        this.rentedHouseIds.add(houseId);
    }

    public void removeRentedHouse(String houseId) {
        this.rentedHouseIds.remove(houseId);
    }
}