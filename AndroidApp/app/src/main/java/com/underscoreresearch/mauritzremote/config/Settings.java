package com.underscoreresearch.mauritzremote.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.underscoreresearch.mauritzremote.R;

public class Settings {
    private static final String PREFERENCES_KEY = "remotepreferences";
    private static final String ROOM_PREFERENCE = "ROOM";
    private static final String PASSWORD_PREFERENCE = "PASSWORD";
    private static final String SEARCH_TYPE_PREFERENCE = "SEARCH_TYPE";

    public static String getPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        return preferences.getString(PASSWORD_PREFERENCE, Constants.DEFAULT_SERVER_PASSWORD);
    }

    public static void setPassword(Context context, String password) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        preferences.edit().putString(PASSWORD_PREFERENCE, password).apply();
    }

    public static void setSelectedSearchType(Context context, String type) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        preferences.edit().putString(SEARCH_TYPE_PREFERENCE, type).apply();
    }

    public static String getSelectedSearchType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        return preferences.getString(SEARCH_TYPE_PREFERENCE, "Entered");
    }

    public enum Room {
        Livingroom,
        Office,
        Zone2
    };

    public static Room getSelectedRoom(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        try {
            return Room.valueOf(preferences.getString(ROOM_PREFERENCE, Room.Livingroom.name()));
        } catch (Exception exc) {
            return Room.Livingroom;
        }
    }

    public static void setSelectedRoom(Context context, Room room) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        preferences.edit().putString(ROOM_PREFERENCE, room.name()).apply();
    }


    public static int getSelectedTab(Context context, String room) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        return preferences.getInt(ROOM_PREFERENCE + room, 0);
    }

    public static void setSelectedTab(Context context, String room, int page) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        preferences.edit().putInt(ROOM_PREFERENCE + room, page).apply();
    }
}
