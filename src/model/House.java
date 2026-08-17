package model;

import java.io.Serializable;

public abstract class House implements Serializable, Sellable, Rentable {
    private static final long serialVersionUID = 1L;

    public static final double BASE_PRICE_PER_METER = 10_000_000;
    public static final double RENT_RATE = 0.004;

    public static final double REGION_1_COEFFICIENT = 1.8;
    public static final double REGION_2_COEFFICIENT = 1.4;
    public static final double REGION_3_COEFFICIENT = 1.1;
    public static final double REGION_4_COEFFICIENT = 0.8;

    public enum DealStatus {
        FOR_SALE,
        FOR_RENT,
        BOTH
    }

    private String id;
    private double area;
    private int bedrooms;
    private int bathrooms;
    private int floor;
    private int region;
    private String ownerName;
    private String tenantName;
    private DealStatus dealStatus;

    public House(String id, double area, int bedrooms, int bathrooms, int floor, int region, String ownerName, DealStatus dealStatus) {
        this.id = id;
        this.area = area;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.floor = floor;
        this.region = region;
        this.ownerName = ownerName;
        this.tenantName = "";
        this.dealStatus = dealStatus;
    }

    public long calculateBasePrice() {
        double regionCoefficient = getRegionCoefficient();
        return (long) (this.area * BASE_PRICE_PER_METER * regionCoefficient);
    }

    @Override
    public abstract long calculatePrice();

    @Override
    public long calculateRent() {
        return (long) (calculatePrice() * RENT_RATE);
    }

    private double getRegionCoefficient() {
        switch (this.region) {
            case 1: return REGION_1_COEFFICIENT;
            case 2: return REGION_2_COEFFICIENT;
            case 3: return REGION_3_COEFFICIENT;
            case 4: return REGION_4_COEFFICIENT;
            default: return 1.0;
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }

    public int getBedrooms() { return bedrooms; }
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }

    public int getBathrooms() { return bathrooms; }
    public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public int getRegion() { return region; }
    public void setRegion(int region) { this.region = region; }

    @Override
    public String getOwnerName() { return ownerName; }

    @Override
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    @Override
    public String getTenantName() { return tenantName; }

    @Override
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public DealStatus getDealStatus() { return dealStatus; }
    public void setDealStatus(DealStatus dealStatus) { this.dealStatus = dealStatus; }
}