package service;

import java.util.List;
import model.User;
import storage.AppData;
import storage.StorageManager;
import util.SecurityUtils;

public class UserService {
    private final AppData data;

    public UserService(AppData data) {
        this.data = data;
    }

    public boolean registerUser(String username, String password, long initialBudget) {
        for (User u : data.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        String id = "USR-" + (data.getUsers().size() + 1);
        String hashedPassword = SecurityUtils.hashPassword(password);
        User newUser = new User(id, username, hashedPassword, initialBudget);
        data.getUsers().add(newUser);
        StorageManager.saveData(data);
        return true;
    }

    public boolean chargeAccount(User user, long amount) {
        if (user == null || amount <= 0) {
            return false;
        }
        user.deposit(amount);
        StorageManager.saveData(data);
        return true;
    }

    public User findUserByUsername(String username) {
        for (User u : data.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return data.getUsers();
    }
}
