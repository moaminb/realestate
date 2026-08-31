package test;

import model.*;
import service.ContractService;
import service.PropertyService;
import service.TransactionResult;
import service.UserService;
import storage.AppData;

import java.util.List;

public class ContractServiceTest {
    public static void run() {
        System.out.println("\n--- ۵. تست‌های سرویس قراردادها (ContractServiceTest) ---");
        AppData data = new AppData();
        UserService userService = new UserService(data);
        PropertyService propertyService = new PropertyService(data);
        ContractService contractService = new ContractService(data, userService, propertyService);

        userService.registerUser("landlord", "p1", 1000000L);
        userService.registerUser("tenant", "p2", 50000000L);

        Apartment apt = new Apartment("HSE-10", 100, 2, 1, 1, 1, "landlord", House.DealStatus.FOR_RENT, 1, 2, 4);
        apt.setTenantName("tenant");
        propertyService.registerHouse(apt);

        Contract contract = new Contract("CTR-100", "HSE-10", "landlord", "tenant", 10000000L, Contract.ContractType.RENT);
        data.getContracts().add(contract);
        User tenant = userService.findUserByUsername("tenant");
        tenant.addRentedHouse("HSE-10");

        List<Contract> userContracts = contractService.getContractsForUser("tenant");
        TestAssert.assertEquals("تعداد قراردادهای مستأجر", 1, userContracts.size());

        List<Contract> landlordContracts = contractService.getContractsForUser("landlord");
        TestAssert.assertEquals("تعداد قراردادهای موجر", 1, landlordContracts.size());

        // Cancel with non-tenant
        User stranger = new User("USR-9", "stranger", "p", 100000000L);
        TransactionResult resNotTenant = contractService.cancelContract("CTR-100", stranger);
        TestAssert.assertEquals("عدم امکان لغو قرارداد توسط فرد غیر مستأجر", TransactionResult.NOT_THE_TENANT, resNotTenant);

        // Cancel with tenant
        long penalty = contract.getCancellationPenalty();
        long tenantBudgetBefore = tenant.getBudget();
        User landlord = userService.findUserByUsername("landlord");
        long landlordBudgetBefore = landlord.getBudget();

        TransactionResult resCancel = contractService.cancelContract("CTR-100", tenant);
        TestAssert.assertEquals("لغو موفقیت‌آمیز قرارداد اجاره", TransactionResult.SUCCESS, resCancel);
        TestAssert.assertEquals("کسر جریمه از مستأجر", tenantBudgetBefore - penalty, tenant.getBudget());
        TestAssert.assertEquals("واریز جریمه به موجر", landlordBudgetBefore + penalty, landlord.getBudget());
        TestAssert.assertEquals("خالی شدن نام مستأجر در خانه", "", apt.getTenantName());
        TestAssert.assertEquals("تغییر وضعیت خانه به FOR_RENT پس از لغو", House.DealStatus.FOR_RENT, apt.getDealStatus());
        TestAssert.assertTrue("حذف از لیست اجاره‌های کاربر", !tenant.getRentedHouseIds().contains("HSE-10"));
        TestAssert.assertEquals("حذف قرارداد از حافظه", 0, data.getContracts().size());
    }
}
