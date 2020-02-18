package com.group3.apiserver.message;

public class ErrorMessage {
    public static final String AUTHENTICATION_FAIL = "Authentication failed.";
    public static final String PRODUCT_NOT_FOUND = "Product not found.";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String WRONG_PASSWORD = "Wrong password.";
    public static final String EMAIL_VALIDATION_FAIL = "E-Mail address has already been used or invalid e-mail.";
    public static final String PURCHASE_ORDER_NOT_FOUND = "Purchase order not found.";
    public static final String HAVE_NO_RIGHT = "Don't have right.";
    public static final String CANNOT_CHANGE_SHIPPED_AND_CANCELLED = "Cannot change status of cancelled or shipped purchase orders.";
    public static final String CANNOT_CHANGE_TO_TARGET_STATUS = "Cannot change to target status.";
}
