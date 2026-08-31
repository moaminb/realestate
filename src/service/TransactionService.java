package service;

import model.*;
import storage.AppData;
import storage.StorageManager;

public class TransactionService {
    private final AppData data;
    private final AuthService authService;
    private final UserService userService;
    private final PropertyService propertyService;
    private final ContractService contractService;

    public TransactionService(AppData data, AuthService authService, UserService userService, PropertyService propertyService, ContractService contractService) {
        this.data = data;
        this.authService = authService;
        this.userService = userService;
        this.propertyService = propertyService;
        this.contractService = contractService;
    }

    public TransactionService(AppData data, AuthService authService, UserService userService, PropertyService propertyService) {
        this(data, authService, userService, propertyService, new ContractService(data, userService, propertyService));
    }

    public TransactionResult purchaseHouse(String houseId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return TransactionResult.NOT_LOGGED_IN;
        }

        House house = propertyService.findHouseById(houseId);
        if (house == null || (house.getDealStatus() != House.DealStatus.FOR_SALE && house.getDealStatus() != House.DealStatus.BOTH)) {
            return TransactionResult.INVALID_DEAL_STATUS;
        }

        if (house.getOwnerName().equalsIgnoreCase(currentUser.getUsername())) {
            return TransactionResult.SELF_PURCHASE_FORBIDDEN;
        }

        long price = house.calculatePrice();
        boolean isAgencyOwned = house.getOwnerName().equals(Agency.AGENCY_OWNER_NAME);
        User seller = isAgencyOwned ? null : userService.findUserByUsername(house.getOwnerName());

        if (currentUser.withdraw(price)) {
            if (!isAgencyOwned && seller != null) {
                seller.deposit(price);
                seller.removePurchasedHouse(houseId);
            }

            if (!house.getTenantName().isEmpty()) {
                contractService.terminateRentalContractsForHouse(houseId);
                house.setTenantName("");
            }

            house.setOwnerName(currentUser.getUsername());
            house.setDealStatus(House.DealStatus.NOT_LISTED);
            currentUser.addPurchasedHouse(houseId);

            if (isAgencyOwned) {
                data.getAgency().removeHouseFromAgency(houseId);
            }

            StorageManager.saveData(data);
            return TransactionResult.SUCCESS;
        } else {
            return TransactionResult.INSUFFICIENT_FUNDS;
        }
    }

    public TransactionResult rentHouse(String houseId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return TransactionResult.NOT_LOGGED_IN;
        }

        House house = propertyService.findHouseById(houseId);
        if (house == null
                || (house.getDealStatus() != House.DealStatus.FOR_RENT && house.getDealStatus() != House.DealStatus.BOTH)
                || !house.getTenantName().isEmpty()) {
            return TransactionResult.INVALID_DEAL_STATUS;
        }

        if (house.getOwnerName().equalsIgnoreCase(currentUser.getUsername())) {
            return TransactionResult.SELF_RENT_FORBIDDEN;
        }

        long rentPrice = house.calculateRent();
        if (currentUser.withdraw(rentPrice)) {
            User landlord = userService.findUserByUsername(house.getOwnerName());
            if (landlord != null) {
                landlord.deposit(rentPrice);
            }

            String contractId = "CTR-" + data.getNextContractSequence();
            Contract contract = new Contract(contractId, houseId, house.getOwnerName(), currentUser.getUsername(), rentPrice, Contract.ContractType.RENT);
            data.getContracts().add(contract);

            house.setTenantName(currentUser.getUsername());
            currentUser.addRentedHouse(houseId);

            StorageManager.saveData(data);
            return TransactionResult.SUCCESS;
        } else {
            return TransactionResult.INSUFFICIENT_FUNDS;
        }
    }

    public TransactionResult specialPurchaseHouse(String houseId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return TransactionResult.NOT_LOGGED_IN;
        }

        House house = propertyService.findHouseById(houseId);
        if (house == null) {
            return TransactionResult.HOUSE_NOT_FOUND;
        }
        if (house.getOwnerName().equalsIgnoreCase(currentUser.getUsername())) {
            return TransactionResult.SELF_PURCHASE_FORBIDDEN;
        }

        long specialPrice = house.calculatePrice() * 2;
        User seller = userService.findUserByUsername(house.getOwnerName());

        if (currentUser.withdraw(specialPrice)) {
            if (seller != null) {
                seller.deposit(specialPrice);
                seller.removePurchasedHouse(houseId);
            }

            if (!house.getTenantName().isEmpty()) {
                contractService.terminateRentalContractsForHouse(houseId);
                house.setTenantName("");
            }

            String contractId = "CTR-" + data.getNextContractSequence();
            Contract contract = new Contract(contractId, houseId, house.getOwnerName(), currentUser.getUsername(), specialPrice, Contract.ContractType.SPECIAL_PURCHASE);
            data.getContracts().add(contract);

            house.setOwnerName(currentUser.getUsername());
            house.setDealStatus(House.DealStatus.NOT_LISTED);
            currentUser.addPurchasedHouse(houseId);

            StorageManager.saveData(data);
            return TransactionResult.SUCCESS;
        } else {
            return TransactionResult.INSUFFICIENT_FUNDS;
        }
    }

    public TransactionResult quickSellToAgency(String houseId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return TransactionResult.NOT_LOGGED_IN;
        }

        House house = propertyService.findHouseById(houseId);
        if (house == null || !house.getOwnerName().equalsIgnoreCase(currentUser.getUsername())) {
            return TransactionResult.NOT_THE_OWNER;
        }

        long quickSellPrice = (long) (house.calculatePrice() * 0.9);
        currentUser.deposit(quickSellPrice);
        currentUser.removePurchasedHouse(houseId);

        if (!house.getTenantName().isEmpty()) {
            contractService.terminateRentalContractsForHouse(houseId);
            house.setTenantName("");
        }

        house.setOwnerName(Agency.AGENCY_OWNER_NAME);
        house.setDealStatus(House.DealStatus.BOTH);
        data.getAgency().addHouseToAgency(houseId);

        StorageManager.saveData(data);
        return TransactionResult.SUCCESS;
    }

    public TransactionResult relistHouse(String houseId, House.DealStatus newStatus) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return TransactionResult.NOT_LOGGED_IN;
        }

        House house = propertyService.findHouseById(houseId);
        if (house == null || !house.getOwnerName().equalsIgnoreCase(currentUser.getUsername())) {
            return TransactionResult.NOT_THE_OWNER;
        }

        house.setDealStatus(newStatus);
        StorageManager.saveData(data);
        return TransactionResult.SUCCESS;
    }
}
