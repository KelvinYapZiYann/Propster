package com.propster.utils;

public class Constants {

    /* INTENT EXTRA */
    public static final String INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN = "INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN";
    public static final String INTENT_EXTRA_LANDLORD_ADD_PROPERTY = "INTENT_EXTRA_LANDLORD_ADD_PROPERTY";
    public static final String INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST = "INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST";
    public static final String INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_ALL_TENANTS = "INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_ALL_TENANTS";

    /* REQUEST CODE */
    public static final int REQUEST_CODE_FIRST_LOGIN_USER_PROFILE_IMAGE = 1000;
    public static final int REQUEST_CODE_LANDLORD_ADD_PROPERTY = 2000;
    public static final int REQUEST_CODE_LANDLORD_PROPERTY_ADD_TENANT = 3000;
    public static final int REQUEST_CODE_SWITCH_ROLE = 4000;
    public static final int REQUEST_CODE_TENANT_ADD_PROPERTY = 5000;

    /* SHARED PREFERENCES */
    public static final String SHARED_PREFERENCES = "Propster_SP";
    public static final String SHARED_PREFERENCES_EMAIL = "SP_EMAIL";
    public static final String SHARED_PREFERENCES_PASSWORD = "SP_PASSWORD";
    public static final String SHARED_PREFERENCES_SESSION_ID = "SP_SESSION_ID";
    public static final String SHARED_PREFERENCES_ROLE = "SP_ROLE";

    /* ROLE */
    public static final int ROLE_SUPER_ADMIN = 100;
    public static final int ROLE_ADMIN = 0;
    public static final int ROLE_LANDLORD = 1;
    public static final int ROLE_TENANT = 2;

    /* PLAN */
    public static final int BASIC = 0;
    public static final int PRO = 1;
    public static final int PREMIUM = 2;

    /* URL */
    public static final String URL_BASE = "https://propster.io/api/dashboard/";
    public static final String URL_LOGIN = URL_BASE + "login";
    public static final String URL_CHECK_MIDDLEWARE_VERIFICATION = URL_BASE + "middleware-verification";
    public static final String URL_UPDATE_FCM_TOKEN = URL_BASE + "";
    public static final String URL_REGISTER = URL_BASE + "";
    public static final String URL_FORGOT_PASSWORD = URL_BASE + "";
    public static final String URL_SAVE_USER_PROFILE = URL_BASE + "";
    public static final String URL_LANDLORD_ADD_PROPERTY = URL_BASE + "";
    public static final String URL_LANDLORD_PROPERTY_LIST = URL_BASE + "";
    public static final String URL_LANDLORD_PROPERTY_TENANT_LIST = URL_BASE + "";
    public static final String URL_LANDLORD_PROPERTY_ADD_TENANT = URL_BASE + "";

    /* ERROR */
    public static final String ERROR_COMMON = "Error retrieving data from server. Please contact us.";
    public static final String ERROR_LOGIN_FAILED_CREDENTIALS = "Email or password is incorrect. Please try again.";
    public static final String ERROR_LOGIN_FAILED_NOT_VERIFIED = "Email is not verified. Please verify the email before logging in.";

}
