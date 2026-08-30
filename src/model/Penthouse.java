package model;

public class Penthouse extends House {
    public static final double LUXURY_COEFFICIENT = 1.5;
    public static final double TERRACE_PRICE_PER_METER = 5_000_000;

    private double terraceArea;

    public Penthouse(String id, double area, int bedrooms, int bathrooms, int floor, int region,
                     String ownerName, DealStatus dealStatus, double terraceArea) {
        super(id, area, bedrooms, bathrooms, floor, region, ownerName, dealStatus);
        this.terraceArea = terraceArea;
    }

    @Override
    public long calculatePrice() {
        long basePrice = calculateBasePrice();
        long luxuryValue = (long) (basePrice * LUXURY_COEFFICIENT);
        long terraceValue = (long) (terraceArea * TERRACE_PRICE_PER_METER);
        return luxuryValue + terraceValue;
    }

    public double getTerraceArea() { return terraceArea; }
    public void setTerraceArea(double terraceArea) { this.terraceArea = terraceArea; }
}