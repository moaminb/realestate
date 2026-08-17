package service;

import model.User;
import storage.AppData;
import util.SecurityUtils;

public class AuthService {
    private final AppData data;
    private User currentUser;

    public AuthService(AppData data) {
        this.data = data;
        this.currentUser = null;
    }

    public boolean login(String username, String password) {
        String hashedPassword = SecurityUtils.hashPassword(password);
        for (User u : data.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(hashedPassword)) {
                this.currentUser = u;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
