package factory;

import model.*;

public class HouseFactory {
    public static Apartment createApartment(String id, double area, int bedrooms, int bathrooms,
                                            int floor, int region, String owner, House.DealStatus status,
                                            int unitNo, int totalFloors, int totalUnits) {
        return new Apartment(id, area, bedrooms, bathrooms, floor, region, owner, status, unitNo, totalFloors, totalUnits);
    }

    public static Villa createVilla(String id, double area, int bedrooms, int bathrooms,
                                    int floor, int region, String owner, House.DealStatus status,
                                    double yardArea, int floorsCount) {
        return new Villa(id, area, bedrooms, bathrooms, floor, region, owner, status, yardArea, floorsCount);
    }

    public static Penthouse createPenthouse(String id, double area, int bedrooms, int bathrooms,
                                            int floor, int region, String owner, House.DealStatus status,
                                            double terraceArea) {
        return new Penthouse(id, area, bedrooms, bathrooms, floor, region, owner, status, terraceArea);
    }
}
