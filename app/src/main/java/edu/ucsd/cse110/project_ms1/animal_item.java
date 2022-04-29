package edu.ucsd.cse110.project_ms1;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class animal_item {
    public String name; //essentially, the name is the tag in json file
    public ZooData.VertexInfo exhibit;
    public boolean selected;
    public boolean searched;
    public int order; // used for displaying on the screen

    //not sure if i will change this constructor
    public animal_item(String name, int order, ZooData.VertexInfo exhibit){
        this.name = name;
        this.order = order;
        this.exhibit = exhibit;
    }

    public static List<animal_item> loadJSON(Context context, String path){
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON("sample_node_info.json");

        List<animal_item> retVal =  new ArrayList<animal_item>();
        int i =0;
        for (Map.Entry<String, ZooData.VertexInfo> set : vInfo.entrySet()) {

            retVal.add(new animal_item(set.getValue().name,i,set.getValue()));
            i++;
        }
        return  retVal;
    }

}
