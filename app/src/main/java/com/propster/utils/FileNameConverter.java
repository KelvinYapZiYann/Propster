package com.propster.utils;

public class FileNameConverter {

    public static String convertFileName(String name) {
        return name.toLowerCase().replaceAll(" ", "_");
    }

}
