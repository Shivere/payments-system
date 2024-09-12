package com.example.mamlaka.constants;

public final class PaymentsConstants {

    private PaymentsConstants() {
        // restrict instantiation
    }

    public static final String  PENDING = "PENDING";
    public static final String  FAILED = "FAILED";
    public static final String  SUCCESS = "SUCCESS";
    public static final String  STATUS_201 = "201";
    public static final String  MESSAGE_201 = "User created successfully";
    public static final String  STATUS_200 = "200";
    public static final String  MESSAGE_200 = "Request processed successfully";
    public static final String  STATUS_417 = "417";
    public static final String  MESSAGE_417_UPDATE= "Payment operation failed. Please try again or contact Dev team";
     public static final String  STATUS_500 = "500";
     public static final String  MESSAGE_500 = "An error occurred on processing transaction. Please try again or contact Dev team";

}
