package edu.ucsd.cse110.project_ms1;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagConverter {
    //string to string list
    @TypeConverter
    public static ArrayList<String> storedStringToList(String value) {
        ArrayList<String> tags = new ArrayList<>(Arrays.asList(value.split("\\s*,\\s*")));
        return tags;
    }
    //String list to string
    @TypeConverter
    public static String listToStoredString(ArrayList<String> tags) {
        String value = "";
        for (String tag : tags)
            value += tag + ",";

        return value;
    }
}
