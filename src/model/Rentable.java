package model;

public interface Rentable {
    long calculateRent();
    String getTenantName();
    void setTenantName(String tenantName);
}
