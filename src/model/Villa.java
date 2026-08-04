package model;

public class Villa extends House {
    //  ثابت‌های اختصاصی ویلا
    public static final double YARD_PRICE_PER_METER = 3_000_000;
    public static final double FLOOR_PREMIUM = 15_000_000;


    private double yardArea;
    private int floorsCount;

    public Villa(String id, double area, int bedrooms, int bathrooms, int floor, int region,
                 String ownerName, DealStatus dealStatus, double yardArea, int floorsCount) {
        // برای ویلا طبقه فعلی را معمولاً همان کف یا ۱ در نظر می‌گیریم اما ساختار پایه حفظ می‌شود
        super(id, area, bedrooms, bathrooms, floor, region, ownerName, dealStatus);
        this.yardArea = yardArea;
        this.floorsCount = floorsCount;
    }


    @Override
    public long calculatePrice() {
        long basePrice = calculateBasePrice();
        double bedroomsBonus = 1 + (0.03 * getBedrooms());
        double floorBonus = 1 + (0.01 * getFloor());

        return (long) (basePrice * bedroomsBonus * floorBonus);
    }


    public double getYardArea() { return yardArea; }
    public void setYardArea(double yardArea) { this.yardArea = yardArea; }

    public int getFloorsCount() { return floorsCount; }
    public void setFloorsCount(int floorsCount) { this.floorsCount = floorsCount; }
}