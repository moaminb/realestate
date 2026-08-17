package service;

public enum TransactionResult {
    SUCCESS,
    NOT_LOGGED_IN,
    HOUSE_NOT_FOUND,
    INVALID_DEAL_STATUS,
    SELF_PURCHASE_FORBIDDEN,
    INSUFFICIENT_FUNDS,
    NOT_THE_OWNER
}
