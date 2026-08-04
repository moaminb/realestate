package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Agency implements Serializable {

    public static final String AGENCY_OWNER_NAME = "بنگاه املاک ";

    private List<String> agencyHouseIds;


    public Agency() {
        this.agencyHouseIds = new ArrayList<>();
    }


    public void addHouseToAgency(String houseId) {
        if (!agencyHouseIds.contains(houseId)) {
            agencyHouseIds.add(houseId);
        }
    }


    public void removeHouseFromAgency(String houseId) {
        agencyHouseIds.remove(houseId);
    }


    public List<String> getAgencyHouseIds() {
        return agencyHouseIds;
    }
}