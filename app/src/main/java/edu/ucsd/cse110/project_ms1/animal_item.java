package edu.ucsd.cse110.project_ms1;

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
}
