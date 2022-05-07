package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends AppCompatActivity {
    public DirectionAdapter direction_adapter;
    public RecyclerView direction_recyclerView;
    public StringAndAnimalItem stringAndAnimalItem;
    private ArrayList<String> orderedAnimalNameString;
    private List<AnimalItem> orderedAnimalItemList;
    private ArrayList<String> edgeStringList;
    private ArrayList<Double> edgeWeightList;
    private List<String> directionStringList;
    public GraphPath<String, IdentifiedWeightedEdge> path;
    public String start;
    public String goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        direction_adapter = new DirectionAdapter();
        direction_adapter.setHasStableIds(true);

        direction_recyclerView = this.findViewById(R.id.all_direction_items);
        direction_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        direction_recyclerView.setAdapter(direction_adapter);

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
        for (IdentifiedWeightedEdge edge: path.getEdgeList()){
            String currentEdge = AnimalItem.eInfo.get(edge.getId()).street;
            edgeStringList.add(currentEdge);
            edgeWeightList.add(AnimalItem.gInfo.getEdgeWeight(edge));
        }

        for (int i = 0; i < edgeStringList.size(); i++){
            String edgeW = Double.toString(edgeWeightList.get(i));
            String displayInfo = "Walk "+edgeW+" along "+edgeStringList.get(i)+" from "+start+" to "+goal+".";
            directionStringList.add(displayInfo);
        }

        direction_adapter.setDirectionsStringList(directionStringList);
        
    }
}