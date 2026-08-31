package test;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("🚀 شروع اجرای مجموعه تست‌های ماژولار سیستم معاملات ملکی");
        System.out.println("==================================================");

        TestAssert.reset();

        ModelTest.run();
        SecurityAndAuthTest.run();
        UserServiceTest.run();
        PropertyServiceTest.run();
        ContractServiceTest.run();
        TransactionServiceTest.run();
        StorageManagerTest.run();

        TestAssert.printSummary();

        if (TestAssert.getFailedTests() > 0) {
            System.exit(1);
        }
    }
}
