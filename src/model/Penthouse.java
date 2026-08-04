package model;

public class Penthouse extends House {
    // --- ثابت‌های اختصاصی پنت‌هاوس ---
    public static final double LUXURY_COEFFICIENT = 1.5;         // ضریب لوکس بودن پنت‌هاوس [cite: 115]
    public static final double TERRACE_PRICE_PER_METER = 5_000_000; // ارزش هر متر مربع تراس (فرض شده) [cite: 117]

    // --- فیلدهای اختصاصی پنت‌هاوس ---
    private double terraceArea;


    public Penthouse(String id, double area, int bedrooms, int bathrooms, int floor, int region,
                     String ownerName, DealStatus dealStatus, double terraceArea) {
        super(id, area, bedrooms, bathrooms, floor, region, ownerName, dealStatus);
        this.terraceArea = terraceArea;
    }


    @Override
    public long calculatePrice() {
        long basePrice = calculateBasePrice();
        double bedroomsBonus = 1 + (0.03 * getBedrooms());
        double floorBonus = 1 + (0.01 * getFloor());

        return (long) (basePrice * bedroomsBonus * floorBonus);
    }


    public double getTerraceArea() { return terraceArea; }
    public void setTerraceArea(double terraceArea) { this.terraceArea = terraceArea; }
}