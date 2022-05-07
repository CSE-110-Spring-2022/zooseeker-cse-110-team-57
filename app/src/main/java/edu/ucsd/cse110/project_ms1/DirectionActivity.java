package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends AppCompatActivity {
    public StringAndAnimalItem stringAndAnimalItem;
    private ArrayList<String> orderedAnimalNameString;
    private List<AnimalItem> orderedAnimalItemList;
    public GraphPath<String, IdentifiedWeightedEdge> path;
    public String start;
    public String goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        //get the ordered Animal name string
        Intent intent = getIntent();
        orderedAnimalNameString = intent.getStringArrayListExtra("routedAnimalNameList");

        SharedPreferences sharedPreferences = getSharedPreferences("Team57", 0);
        orderedAnimalItemList = new ArrayList<AnimalItem>();
        stringAndAnimalItem = new StringAndAnimalItem();

        for (String animalName: orderedAnimalNameString){
            //find the string containing all related information of a selected animal
            String animalItemInfoString = sharedPreferences.getString(animalName,
                    "No found such animal in sharedPreference");
            //get the animalItem
            AnimalItem animalItem = stringAndAnimalItem.StringToAnimalItem(animalItemInfoString);
            //add animalItem to the AnimalItem list
            orderedAnimalItemList.add(animalItem);
        }
        //Get the first Animal Exhibit Name
        start = "entrance_exit_gate";
        goal = orderedAnimalNameString.get(0);

        path = DijkstraShortestPath.findPathBetween(AnimalItem.gInfo, start, goal);
        
    }
}