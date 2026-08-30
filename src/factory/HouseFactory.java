package factory;

import model.*;

public class HouseFactory {

    public static final String TYPE_APARTMENT = "1";
    public static final String TYPE_VILLA = "2";
    public static final String TYPE_PENTHOUSE = "3";

    public static House create(String typeCode, String id, double area, int bedrooms, int bathrooms,
                               int floor, int region, String owner, House.DealStatus status,
                               HouseTypeSpecificParams params) {
        switch (typeCode) {
            case TYPE_APARTMENT:
                return new Apartment(id, area, bedrooms, bathrooms, floor, region, owner, status,
                        params.unitNumber, params.totalFloors, params.totalUnits);
            case TYPE_VILLA:
                return new Villa(id, area, bedrooms, bathrooms, floor, region, owner, status,
                        params.yardArea, params.floorsCount);
            case TYPE_PENTHOUSE:
                return new Penthouse(id, area, bedrooms, bathrooms, floor, region, owner, status,
                        params.terraceArea);
            default:
                return null;
        }
    }

    public static class HouseTypeSpecificParams {
        public int unitNumber;
        public int totalFloors;
        public int totalUnits;
        public double yardArea;
        public int floorsCount;
        public double terraceArea;
    }
}
