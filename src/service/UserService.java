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
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty() || initialBudget < 0) {
            return false;
        }
        String cleanUsername = username.trim();
        if (cleanUsername.equalsIgnoreCase(model.Agency.AGENCY_OWNER_NAME)
                || cleanUsername.equalsIgnoreCase("agency")
                || cleanUsername.equalsIgnoreCase("املاکی")
                || cleanUsername.equalsIgnoreCase("بنگاه")) {
            return false;
        }
        for (User u : data.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(cleanUsername)) {
                return false;
            }
        }
        String id = "USR-" + data.getNextUserSequence();
        String hashedPassword = SecurityUtils.hashPassword(password);
        User newUser = new User(id, cleanUsername, hashedPassword, initialBudget);
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

    public User findUserById(String id) {
        for (User u : data.getUsers()) {
            if (u.getId().equalsIgnoreCase(id)) {
                return u;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return data.getUsers();
    }
}
