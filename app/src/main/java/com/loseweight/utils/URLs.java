package com.loseweight.utils;

public class URLs {

        private static String BASE_URL_LIVE = "http://soteriamobile.azurewebsites.net/mobile/api/visitor/";


    private static String getBaseUrl() {
        return BASE_URL_LIVE;
    }

    public static String GET_EMPLOYEE_BY_ID() {
        return getBaseUrl() + "/employee/get/byIdCard";
    }

    public static String SEARCH_EMPLOYEE() {
        return getBaseUrl() + "/employee/search";
    }

    public static String EMPLOYEE_VERIFYPIN() {
        return getBaseUrl() + "/employee/verifypin";
    }

    public static String EMPLOYEE_CLOCKOUT() {
        return getBaseUrl() + "/employee/clockout";
    }

    public static String EMPLOYEE_CLOCKIN() {
        return getBaseUrl() + "/employee/clockin";
    }

    public static String GET_APP_CONFIG() {
        return getBaseUrl() + "appconfig";
    }

    public static String REGISTER() {
        return getBaseUrl() + "register";
    }

    public static String TRAINFACE() {
        return getBaseUrl() + "trainface";
    }

    public static String VISITOR_LIST() {
        return getBaseUrl() + "list";
    }

    public static String IDENTIFY_PERSON() {
        return getBaseUrl() + "identifyperson";
    }


    public static String DRAWWINNER() {
        return getBaseUrl() + "drawwinner";
    }

}
