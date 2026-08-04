package storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class AppData implements Serializable {
    private static final long serialVersionUID = 1L; // برای هماهنگی در بازخوانی فایل‌ها

    private List<User> users;
    private List<House> houses;
    private List<Contract> contracts;
    private Agency agency;

    public AppData() {
        this.users = new ArrayList<>();
        this.houses = new ArrayList<>();
        this.contracts = new ArrayList<>();
        this.agency = new Agency();
    }


    public List<User> getUsers() { return users; }
    public List<House> getHouses() { return houses; }
    public List<Contract> getContracts() { return contracts; }
    public Agency getAgency() { return agency; }
}