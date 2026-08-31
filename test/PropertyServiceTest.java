package test;

import factory.HouseFactory;
import model.*;
import service.PropertyService;
import storage.AppData;

public class PropertyServiceTest {
    public static void run() {
        System.out.println("\n--- ۴. تست‌های کارخانه و سرویس املاک (PropertyServiceTest) ---");

        // Factory Tests
        HouseFactory.HouseTypeSpecificParams params = new HouseFactory.HouseTypeSpecificParams();
        params.setUnitNumber(4);
        params.setTotalFloors(5);
        params.setTotalUnits(10);
        params.setYardArea(80);
        params.setFloorsCount(2);
        params.setTerraceArea(30);

        House apt = HouseFactory.create(HouseFactory.TYPE_APARTMENT, "H-1", 100, 2, 1, 1, 1, "ali", House.DealStatus.FOR_SALE, params);
        TestAssert.assertTrue("تولید آپارتمان توسط کارخانه", apt instanceof Apartment);

        House villa = HouseFactory.create(HouseFactory.TYPE_VILLA, "H-2", 200, 3, 2, 1, 1, "ali", House.DealStatus.FOR_SALE, params);
        TestAssert.assertTrue("تولید ویلا توسط کارخانه", villa instanceof Villa);

        House ph = HouseFactory.create(HouseFactory.TYPE_PENTHOUSE, "H-3", 300, 4, 3, 1, 1, "ali", House.DealStatus.FOR_SALE, params);
        TestAssert.assertTrue("تولید پنت‌هاوس توسط کارخانه", ph instanceof Penthouse);

        House invalid = HouseFactory.create("99", "H-4", 100, 2, 1, 1, 1, "ali", House.DealStatus.FOR_SALE, params);
        TestAssert.assertTrue("تولید با کد نامعتبر باید null برگرداند", invalid == null);

        // PropertyService Tests
        AppData data = new AppData();
        PropertyService propertyService = new PropertyService(data);

        String id1 = propertyService.generateNextHouseId();
        TestAssert.assertEquals("تولید شناسه اول خانه", "HSE-1", id1);

        Apartment houseObj = new Apartment(id1, 100, 2, 1, 3, 1, "ali", House.DealStatus.FOR_SALE, 12, 5, 20);
        propertyService.registerHouse(houseObj);

        TestAssert.assertEquals("تعداد خانه‌ها پس از ثبت", 1, propertyService.getHousesCount());
        House found = propertyService.findHouseById("hse-1");
        TestAssert.assertTrue("یافتن خانه با شناسه بدون حساسیت به حروف بزرگ/کوچک", found != null && found.getId().equals("HSE-1"));

        House notFound = propertyService.findHouseById("HSE-999");
        TestAssert.assertTrue("عدم یافتن شناسه ناموجود", notFound == null);

        String id2 = propertyService.generateNextHouseId();
        TestAssert.assertEquals("تولید شناسه دوم خانه", "HSE-2", id2);
    }
}
