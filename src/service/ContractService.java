package service;

import java.util.ArrayList;
import java.util.List;
import model.Agency;
import model.Contract;
import model.House;
import model.User;
import storage.AppData;
import storage.StorageManager;

public class ContractService {
    private final AppData data;
    private final UserService userService;
    private final PropertyService propertyService;

    public ContractService(AppData data, UserService userService, PropertyService propertyService) {
        this.data = data;
        this.userService = userService;
        this.propertyService = propertyService;
    }

    public List<Contract> getContractsForUser(String username) {
        List<Contract> result = new ArrayList<>();
        for (Contract c : data.getContracts()) {
            if (c.getLandlordName().equalsIgnoreCase(username) || c.getTenantOrBuyerName().equalsIgnoreCase(username)) {
                result.add(c);
            }
        }
        return result;
    }

    public Contract findContractById(String id) {
        for (Contract c : data.getContracts()) {
            if (c.getId().equalsIgnoreCase(id)) {
                return c;
            }
        }
        return null;
    }

    public TransactionResult cancelContract(String contractId, User currentUser) {
        Contract contract = findContractById(contractId);
        if (contract == null) {
            return TransactionResult.CONTRACT_NOT_FOUND;
        }
        if (contract.getContractType() != Contract.ContractType.RENT) {
            return TransactionResult.NOT_CANCELLABLE;
        }
        if (!contract.getTenantOrBuyerName().equalsIgnoreCase(currentUser.getUsername())) {
            return TransactionResult.NOT_THE_TENANT;
        }
        if (!contract.canCancel(currentUser.getBudget())) {
            return TransactionResult.INSUFFICIENT_FUNDS;
        }

        long penalty = contract.getCancellationPenalty();
        currentUser.withdraw(penalty);

        if (!contract.getLandlordName().equals(Agency.AGENCY_OWNER_NAME)) {
            User landlord = userService.findUserByUsername(contract.getLandlordName());
            if (landlord != null) {
                landlord.deposit(penalty);
            }
        }

        House house = propertyService.findHouseById(contract.getHouseId());
        if (house != null) {
            house.setTenantName("");
            house.setDealStatus(House.DealStatus.FOR_RENT);
        }
        currentUser.removeRentedHouse(contract.getHouseId());

        data.getContracts().remove(contract);
        StorageManager.saveData(data);
        return TransactionResult.SUCCESS;
    }
}
