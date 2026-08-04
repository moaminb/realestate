package model;

public class Apartment extends House {

    private int unitNumber;
    private int totalFloors;
    private int totalUnits;


    public Apartment(String id, double area, int bedrooms, int bathrooms, int floor, int region,
                     String ownerName, DealStatus dealStatus, int unitNumber, int totalFloors, int totalUnits) {
        super(id, area, bedrooms, bathrooms, floor, region, ownerName, dealStatus);
        this.unitNumber = unitNumber;
        this.totalFloors = totalFloors;
        this.totalUnits = totalUnits;
    }


    @Override
    public long calculatePrice() {
        long basePrice = calculateBasePrice();
        double bedroomsBonus = 1 + (0.03 * getBedrooms());
        double floorBonus = 1 + (0.01 * getFloor());

        return (long) (basePrice * bedroomsBonus * floorBonus);
    }


    public int getUnitNumber() { return unitNumber; }
    public void setUnitNumber(int unitNumber) { this.unitNumber = unitNumber; }

    public int getTotalFloors() { return totalFloors; }
    public void setTotalFloors(int totalFloors) { this.totalFloors = totalFloors; }

    public int getTotalUnits() { return totalUnits; }
    public void setTotalUnits(int totalUnits) { this.totalUnits = totalUnits; }
}