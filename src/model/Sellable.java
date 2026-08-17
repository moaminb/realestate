package model;

public interface Sellable {
    long calculatePrice();
    String getOwnerName();
    void setOwnerName(String ownerName);
}
