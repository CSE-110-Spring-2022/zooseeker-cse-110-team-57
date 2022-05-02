package edu.ucsd.cse110.project_ms1;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagConverter {
    @TypeConverter
    public static ArrayList<String> storedStringToList(String value) {
        ArrayList<String> tags = (ArrayList<String>) Arrays.asList(value.split("\\s*,\\s*"));
        return tags;
    }

    @TypeConverter
    public static String listToStoredString(AnimalItem item) {
        String value = "";
        for (String tag : item.tags)
            value += tag + ",";

        return value;
    }
}
