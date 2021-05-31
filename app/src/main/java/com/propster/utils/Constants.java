package com.propster.utils;

public class Constants {

    /* INTENT EXTRA */
//    public static final String INTENT_EXTRA_LANDLORD_ADD_PROPERTY = "INTENT_EXTRA_LANDLORD_ADD_PROPERTY";
//    public static final String INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_PROPERTY_ID = "INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_PROPERTY_ID";
//    public static final String INTENT_EXTRA_LANDLORD_PROPERTY_NAME = "INTENT_EXTRA_LANDLORD_PROPERTY_NAME";
//    public static final String INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST = "INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST";
//    public static final String INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_EXPENSES_ID = "INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_EXPENSES_ID";
//    public static final String INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_TENANT_ID = "INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_TENANT_ID";
//    public static final String INTENT_EXTRA_LANDLORD_PROPERTY_DETAIL_PROPERTY_ID = "INTENT_EXTRA_LANDLORD_PROPERTY_DETAIL_PROPERTY_ID";
//    public static final String INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_ALL_TENANTS = "INTENT_EXTRA_LANDLORD_PROPERTY_TENANT_LIST_ALL_TENANTS";
//    public static final String INTENT_EXTRA_PROPERTY_EXPENSES_LIST = "INTENT_EXTRA_PROPERTY_EXPENSES_LIST";
//    public static final String INTENT_EXTRA_PROPERTY_EXPENSES_LIST_PROPERTY_ID = "INTENT_EXTRA_PROPERTY_EXPENSES_LIST_PROPERTY_ID";
//    public static final String INTENT_EXTRA_PROPERTY_EXPENSES_LIST_PROPERTY_EXPENSES_ID = "INTENT_EXTRA_PROPERTY_EXPENSES_LIST_PROPERTY_EXPENSES_ID";
//    public static final String INTENT_EXTRA_PROPERTY_EXPENSES_LIST_ALL = "INTENT_EXTRA_PROPERTY_EXPENSES_LIST_ALL";
//    public static final String INTENT_EXTRA_PROPERTY_EXPENSES_ID = "INTENT_EXTRA_PROPERTY_EXPENSES_ID";
//    public static final String INTENT_EXTRA_PROPERTY_EXPENSES_NAME = "INTENT_EXTRA_PROPERTY_EXPENSES_NAME";
//    public static final String INTENT_EXTRA_PROPERTY_EXPENSES_PROPERTY_ID = "INTENT_EXTRA_PROPERTY_EXPENSES_PROPERTY_ID";
//    public static final String INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_LIST = "INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_LIST";
//    public static final String INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_LIST_ALL = "INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_LIST_ALL";
//    public static final String INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_PROPERTY_ID = "INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_PROPERTY_ID";
//    public static final String INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_ID = "INTENT_EXTRA_PROPERTY_TENURE_CONTRACTS_ID";



    public static final String INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN = "INTENT_EXTRA_CONTENT_FIRST_TIME_LOGIN";
    public static final String INTENT_EXTRA_LIST_ALL_TENANTS = "INTENT_EXTRA_LIST_ALL_TENANTS";
    public static final String INTENT_EXTRA_LIST_ALL_PROPERTY_EXPENSES = "INTENT_EXTRA_LIST_ALL_PROPERTY_EXPENSES";
    public static final String INTENT_EXTRA_LIST_ALL_TENURE_CONTRACTS = "INTENT_EXTRA_LIST_ALL_TENURE_CONTRACTS";

    public static final String INTENT_EXTRA_PROPERTY_ID = "INTENT_EXTRA_PROPERTY_ID";
    public static final String INTENT_EXTRA_PROPERTY_NAME = "INTENT_EXTRA_PROPERTY_NAME";
    public static final String INTENT_EXTRA_TENANT_ID = "INTENT_EXTRA_TENANT_ID";
    public static final String INTENT_EXTRA_TENANT_NAME = "INTENT_EXTRA_TENANT_NAME";
    public static final String INTENT_EXTRA_PROPERTY_EXPENSES_ID = "INTENT_EXTRA_PROPERTY_EXPENSES_ID";
    public static final String INTENT_EXTRA_PROPERTY_EXPENSES_NAME = "INTENT_EXTRA_PROPERTY_EXPENSES_NAME";
    public static final String INTENT_EXTRA_TENURE_CONTRACTS_ID = "INTENT_EXTRA_TENURE_CONTRACTS_ID";
    public static final String INTENT_EXTRA_TENURE_CONTRACTS_NAME = "INTENT_EXTRA_TENURE_CONTRACTS_NAME";

    public static final String INTENT_EXTRA_IMAGE_URL = "INTENT_EXTRA_IMAGE_URL";
    public static final String INTENT_EXTRA_IMAGE_NAME = "INTENT_EXTRA_IMAGE_NAME";

    /* REQUEST CODE */
    public static final int REQUEST_CODE_FIRST_LOGIN_USER_PROFILE_IMAGE = 1000;
    public static final int REQUEST_CODE_USER_PROFILE_DETAIL = 1200;
    public static final int REQUEST_CODE_LANDLORD_ADD_PROPERTY = 2000;
    public static final int REQUEST_CODE_LANDLORD_PROPERTY_DETAIL = 2200;
    public static final int REQUEST_CODE_LANDLORD_PROPERTY_TENANT_LIST = 2500;
    public static final int REQUEST_CODE_LANDLORD_PROPERTY_ADD_TENANT = 3000;
    public static final int REQUEST_CODE_LANDLORD_PROPERTY_TENANT_DETAIL = 3500;
    public static final int REQUEST_CODE_SWITCH_ROLE = 4000;
    public static final int REQUEST_CODE_TENANT_ADD_PROPERTY = 5000;
    public static final int REQUEST_CODE_ADD_PROPERTY_EXPENSES = 6200;
    public static final int REQUEST_CODE_PROPERTY_EXPENSES_DETAIL = 6500;
    public static final int REQUEST_CODE_PROPERTY_EXPENSES_IMAGE_DOCUMENT = 6700;
    public static final int REQUEST_CODE_ADD_TENURE_CONTRACTS = 7000;
    public static final int REQUEST_CODE_TENURE_CONTRACTS_DETAIL = 7200;
    public static final int REQUEST_CODE_TENURE_CONTRACTS_IMAGE_DOCUMENT = 7500;

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
    public static final String SESSION_ID_PREFIX = "Bearer ";

    /* URL */
    public static final String PAGE = "page";
    public static final String PROPERTY = "assets";
    public static final String TENANTS = "tenants";
    public static final String PROPERTY_EXPENSES = "asset-expenses";
    public static final String TENURE_CONTRACTS = "tenure-contracts";
    public static final String FIELD_VALUE = "field-value";
    public static final String FIELDS = "fields";
    public static final String URL_BASE = "https://propster.io/api/dashboard/";

    public static final String URL_LOGIN = URL_BASE + "login";
    public static final String URL_APP_VERSION = URL_BASE + "app-version?os_type=android";
    public static final String URL_CHECK_MIDDLEWARE_VERIFICATION = URL_BASE + "middleware-verification";
    public static final String URL_RESEND_EMAIL_VERIFICATION = URL_BASE + "email-not-verified";
    public static final String URL_UPDATE_FCM_TOKEN = URL_BASE + "";
    public static final String URL_REGISTER = URL_BASE + "register";
    public static final String URL_FORGOT_PASSWORD = URL_BASE + "password/email";
    public static final String URL_SAVE_USER_PROFILE = URL_BASE + "create-profile";
    public static final String URL_SELECT_ROLE = URL_BASE + "select-role";
    public static final String URL_LOGOUT = URL_BASE + "logout";
    public static final String URL_USER = URL_BASE + "users";
    public static final String URL_LANDLORD_PROPERTY = URL_BASE + "assets";
    public static final String URL_LANDLORD_TENANT = URL_BASE + "tenants";
    public static final String URL_LANDLORD_PROPERTY_TENANT = URL_LANDLORD_PROPERTY + "/tenants";
//    public static final String URL_LANDLORD_PROPERTY_PROPERTY_EXPENSES = URL_LANDLORD_PROPERTY + "/asset-expenses";
//    public static final String URL_LANDLORD_PROPERTY_TENURE_CONTRACTS = URL_LANDLORD_PROPERTY + "/tenure-contract";
    public static final String URL_LANDLORD_PROPERTY_EXPENSES = URL_BASE + "asset-expenses";
    public static final String URL_LANDLORD_TENURE_CONTRACTS = URL_BASE + "tenure-contracts";

//    public static final String URL_LANDLORD_PROPERTY_LIST = URL_BASE + "assets";
//    public static final String URL_LANDLORD_ADD_PROPERTY = URL_BASE + "assets";
//    public static final String URL_LANDLORD_REMOVE_PROPERTY = URL_BASE + "assets";
//    public static final String URL_LANDLORD_PROPERTY_DETAIL = URL_BASE + "assets";
//    public static final String URL_LANDLORD_PROPERTY_TENANT_LIST = URL_BASE + "tenants";
//    public static final String URL_LANDLORD_PROPERTY_ADD_TENANT = URL_BASE + "tenants";
//    public static final String URL_LANDLORD_PROPERTY_TENANT_DETAIL = URL_BASE + "tenants";
//    public static final String URL_LANDLORD_PROPERTY_REMOVE_TENANT = URL_BASE + "tenants";


    /* ERROR */
    public static final String ERROR_COMMON = "Error retrieving data from server. Please contact us.";
    public static final String ERROR_LOGIN_FAILED_CREDENTIALS = "Email or password is incorrect. Please try again.";
    public static final String ERROR_LOGIN_FAILED_NOT_VERIFIED = "Email is not verified. Please verify the email before logging in.\nIf you did not receive any email notification, please click \"Resend Email Verification\" button.";
    public static final String RESEND_EMAIL_VERIFICATION_SUCCESSFUL = "Email verification is resent successfully. Please check your email inbox.";
    public static final String ERROR_RESEND_EMAIL_VERIFICATION_FAILED = "Failed resending email verification. Please try again.";
    public static final String ERROR_USER_PROFILE_FAILED = "Failed saving using profile. Please try again.";
    public static final String ERROR_USER_ADD_PROPERTY_LIMIT_FAILED = "Your limit is up. Please upgrade to higher subscription plan.";
    public static final String ERROR_USER_PROPERTY_DETAIL_ID_NOT_MATCHED = "Something wrong with the server. Please restart this app.";
    public static final String ERROR_USER_TENANT_DETAIL_ID_NOT_MATCHED = "Something wrong with the server. Please restart this app.";
    public static final String ERROR_IMAGE_DOCUMENT_FILE_NOT_LOADED = "Failed loading the file. Please contact us.";
    public static final String ERROR_IMAGE_DOCUMENT_FILE_NOT_SAVED = "Failed saving the file. Please contact us.";

}
