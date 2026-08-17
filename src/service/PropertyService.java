package service;

import java.util.List;
import model.House;
import storage.AppData;
import storage.StorageManager;

public class PropertyService {
    private final AppData data;

    public PropertyService(AppData data) {
        this.data = data;
    }

    public void registerHouse(House house) {
        data.getHouses().add(house);
        StorageManager.saveData(data);
    }

    public House findHouseById(String id) {
        for (House h : data.getHouses()) {
            if (h.getId().equalsIgnoreCase(id)) {
                return h;
            }
        }
        return null;
    }

    public List<House> getAllHouses() {
        return data.getHouses();
    }

    public int getHousesCount() {
        return data.getHouses().size();
    }
}
