package factory;

import model.*;

public class HouseFactory {

    public static final String TYPE_APARTMENT = "1";
    public static final String TYPE_VILLA = "2";
    public static final String TYPE_PENTHOUSE = "3";

    public static House create(String typeCode, String id, double area, int bedrooms, int bathrooms,
                               int floor, int region, String owner, House.DealStatus status,
                               HouseTypeSpecificParams params) {
        if (params == null) {
            params = new HouseTypeSpecificParams();
        }
        switch (typeCode) {
            case TYPE_APARTMENT:
                return new Apartment(id, area, bedrooms, bathrooms, floor, region, owner, status,
                        params.getUnitNumber(), params.getTotalFloors(), params.getTotalUnits());
            case TYPE_VILLA:
                return new Villa(id, area, bedrooms, bathrooms, floor, region, owner, status,
                        params.getYardArea(), params.getFloorsCount());
            case TYPE_PENTHOUSE:
                return new Penthouse(id, area, bedrooms, bathrooms, floor, region, owner, status,
                        params.getTerraceArea());
            default:
                return null;
        }
    }

    public static class HouseTypeSpecificParams {
        private int unitNumber;
        private int totalFloors;
        private int totalUnits;
        private double yardArea;
        private int floorsCount;
        private double terraceArea;

        public int getUnitNumber() { return unitNumber; }
        public void setUnitNumber(int unitNumber) { this.unitNumber = unitNumber; }

        public int getTotalFloors() { return totalFloors; }
        public void setTotalFloors(int totalFloors) { this.totalFloors = totalFloors; }

        public int getTotalUnits() { return totalUnits; }
        public void setTotalUnits(int totalUnits) { this.totalUnits = totalUnits; }

        public double getYardArea() { return yardArea; }
        public void setYardArea(double yardArea) { this.yardArea = yardArea; }

        public int getFloorsCount() { return floorsCount; }
        public void setFloorsCount(int floorsCount) { this.floorsCount = floorsCount; }

        public double getTerraceArea() { return terraceArea; }
        public void setTerraceArea(double terraceArea) { this.terraceArea = terraceArea; }
    }
}
