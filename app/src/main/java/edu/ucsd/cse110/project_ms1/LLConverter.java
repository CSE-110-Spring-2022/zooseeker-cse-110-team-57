package edu.ucsd.cse110.project_ms1;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

public class LLConverter {
    //String to LatLng
    @TypeConverter
    public static LatLng storedStringToLL(String value) {
        ArrayList<String> nums = new ArrayList<>(Arrays.asList(value.split("\\s*,\\s*")));
        double lat = Double.parseDouble(nums.get(0));
        double lng = Double.parseDouble(nums.get(1));
        return new LatLng(lat,lng);

    }
    //LatLng to String
    @TypeConverter
    public static String LLToStoredString(LatLng ll) {
        double var1 = ll.latitude;
        double var3 = ll.longitude;
        StringBuilder var5 = new StringBuilder(60);
        var5.append(var1);
        var5.append(",");
        var5.append(var3);
        return var5.toString();


    }
}
