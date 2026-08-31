package test;

public class TestAssert {
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void reset() {
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;
    }

    public static void assertTrue(String testName, boolean condition) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.println("  [PASS] " + testName);
        } else {
            failedTests++;
            System.out.println("  [FAIL] " + testName);
        }
    }

    public static void assertEquals(String testName, long expected, long actual) {
        totalTests++;
        if (expected == actual) {
            passedTests++;
            System.out.println("  [PASS] " + testName + " (مقدار: " + actual + ")");
        } else {
            failedTests++;
            System.out.println("  [FAIL] " + testName + " (مورد انتظار: " + expected + "، دریافت شده: " + actual + ")");
        }
    }

    public static void assertEquals(String testName, Object expected, Object actual) {
        totalTests++;
        if (expected == null && actual == null) {
            passedTests++;
            System.out.println("  [PASS] " + testName);
        } else if (expected != null && expected.equals(actual)) {
            passedTests++;
            System.out.println("  [PASS] " + testName + " (" + actual + ")");
        } else {
            failedTests++;
            System.out.println("  [FAIL] " + testName + " (مورد انتظار: " + expected + "، دریافت شده: " + actual + ")");
        }
    }

    public static int getTotalTests() {
        return totalTests;
    }

    public static int getPassedTests() {
        return passedTests;
    }

    public static int getFailedTests() {
        return failedTests;
    }

    public static void printSummary() {
        System.out.println("\n==================================================");
        System.out.println("📊 خلاصه نتایج کلی آزمون‌ها:");
        System.out.println("تعداد کل تست‌ها: " + totalTests);
        System.out.println("✅ تست‌های موفق: " + passedTests);
        System.out.println("❌ تست‌های ناموفق: " + failedTests);
        int passRate = totalTests > 0 ? (passedTests * 100 / totalTests) : 0;
        System.out.println("درصد موفقیت: " + passRate + "%");
        System.out.println("==================================================");
    }
}
