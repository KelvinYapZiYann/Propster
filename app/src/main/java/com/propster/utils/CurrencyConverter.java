package com.propster.utils;

public class CurrencyConverter {

    public static String convertCurrency(String currency) {
        switch (currency.toLowerCase()) {
            case "rm":
            case "myr":
                return "RM";
            default:
                return currency;
        }
    }

}
