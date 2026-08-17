package model;

import java.io.Serializable;

public class Contract implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ContractType {
        RENT,
        SPECIAL_PURCHASE
    }

    private String id;
    private String houseId;
    private String landlordName;
    private String tenantOrBuyerName;
    private long price;
    private long cancellationPenalty;
    private ContractType contractType;

    public Contract(String id, String houseId, String landlordName, String tenantOrBuyerName,
                    long price, ContractType contractType) {
        this.id = id;
        this.houseId = houseId;
        this.landlordName = landlordName;
        this.tenantOrBuyerName = tenantOrBuyerName;
        this.price = price;
        this.contractType = contractType;

        if (contractType == ContractType.RENT) {
            this.cancellationPenalty = (long) (price * 1.5);
        } else {
            this.cancellationPenalty = 0L;
        }
    }

    public boolean canCancel(long userBudget) {
        if (this.contractType != ContractType.RENT) {
            return false;
        }
        return userBudget >= this.cancellationPenalty;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getHouseId() { return houseId; }
    public void setHouseId(String houseId) { this.houseId = houseId; }

    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }

    public String getTenantOrBuyerName() { return tenantOrBuyerName; }
    public void setTenantOrBuyerName(String tenantOrBuyerName) { this.tenantOrBuyerName = tenantOrBuyerName; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public long getCancellationPenalty() { return cancellationPenalty; }
    public void setCancellationPenalty(long cancellationPenalty) { this.cancellationPenalty = cancellationPenalty; }

    public ContractType getContractType() { return contractType; }
    public void setContractType(ContractType contractType) { this.contractType = contractType; }
}