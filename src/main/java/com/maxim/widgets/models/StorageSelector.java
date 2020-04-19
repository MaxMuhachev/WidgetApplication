package com.maxim.widgets.models;

public class StorageSelector {
    private static boolean inDatabase = false;

    public static String getInDatabase() {
        String message;
        if (inDatabase) {
            message = "Выбрано хранилище: база данных.";
        } else {
            message = "Выбрано хранилище: локальное хранилище.";
        }
        return message;
    }

    public static void setInDatabase(boolean inDatabase) {
        StorageSelector.inDatabase = inDatabase;
    }
}
