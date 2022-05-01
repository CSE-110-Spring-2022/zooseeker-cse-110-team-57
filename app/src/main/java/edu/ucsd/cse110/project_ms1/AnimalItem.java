package edu.ucsd.cse110.project_ms1;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnimalItem {
    public long id  = 0;
    public String name; //essentially, the name is the tag in json file
    public ZooData.VertexInfo exhibit;
    public boolean selected;
    public boolean searched;
    public int order; // used for displaying on the screen
    public static Map<String, ZooData.VertexInfo> vInfo;

    //not sure if i will change this constructor
    public AnimalItem(int order, ZooData.VertexInfo exhibit){
        this.name = exhibit.name;
        this.order = order;
        this.exhibit = exhibit;
    }

    public static void loadInfo(Context context,String path) throws IOException {
        InputStream input = context.getAssets().open(path);
        vInfo = ZooData.loadVertexInfoJSON(input);
    }

    @Override
    public String toString() {
        return "animal_item{" +
                "name='" + name + '\'' +
                ", exhibit=" + exhibit +
                ", selected=" + selected +
                ", searched=" + searched +
                ", order=" + order +
                '}';
    }

    public static List<AnimalItem> search_by_tag(String tag){
        List<AnimalItem> retVal =  new ArrayList<AnimalItem>();
        int i =0;
        for (Map.Entry<String, ZooData.VertexInfo> set : vInfo.entrySet()) {
            ZooData.VertexInfo currentVertex = set.getValue();
            if(currentVertex.kind.name().equals("EXHIBIT")){
                if (tag==null || currentVertex.tags.contains(tag.toLowerCase()) ||
                        currentVertex.name.toLowerCase().equals(tag.toLowerCase())){
                    retVal.add(new AnimalItem(i,set.getValue()));
                }
            }

            i++;
        }
        return  retVal;
    }
}
