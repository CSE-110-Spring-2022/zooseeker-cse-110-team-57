package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

public class DirectionActivity extends AppCompatActivity {
    public DirectionAdapter direction_adapter;
    public RecyclerView direction_recyclerView;
    public TextView goalExhibitTitle, startExhibitTitle, nextText, previousText;
    public StringAndAnimalItem stringAndAnimalItem;
    private ArrayList<String> orderedAnimalNameString, edgeStringList, edgeWeightList;
    private List<AnimalItem> orderedAnimalItemList;
    private List<String> directionStringList, start_endpoints, end_endpoints;;
    private List<Double> pathDistances;
    public GraphPath<String, IdentifiedWeightedEdge> path;
    public String start_id, start_name, goal_id, goal_name, next_name, next_distance,
            previous_name, previous_distance;
    public String entrance_id, entrance_name;
    public int directionItemOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        //initiate field
        edgeStringList = new ArrayList<String>();
        edgeWeightList = new ArrayList<String>();
        directionStringList = new ArrayList<String>();
        start_endpoints = new ArrayList<String>();
        end_endpoints = new ArrayList<String>();
        orderedAnimalItemList = new ArrayList<AnimalItem>();
        stringAndAnimalItem = new StringAndAnimalItem();
        directionItemOrder = 0;
        entrance_id = "entrance_exit_gate";
        entrance_name = "Entrance and Exit Gate";

        //adapter
        direction_adapter = new DirectionAdapter();
        direction_adapter.setHasStableIds(true);
        direction_recyclerView = this.findViewById(R.id.all_direction_items);
        direction_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        direction_recyclerView.setAdapter(direction_adapter);
        startExhibitTitle = findViewById(R.id.start_exhibit_name);
        goalExhibitTitle = findViewById(R.id.goal_exhibit_name);
        nextText = findViewById(R.id.next_text);
        previousText = findViewById(R.id.previous_text);

        //get the ordered AnimalItem string
        Intent intent = getIntent();
        orderedAnimalNameString = intent.getStringArrayListExtra("routedAnimalNameList");
        pathDistances = new ArrayList<Double>(orderedAnimalNameString.size() - 1);
        orderedAnimalItemList = loadOrderedAnimals(orderedAnimalNameString);

        //set the first Exhibit
        displayDirection(directionItemOrder);
    }

    public List<AnimalItem> loadOrderedAnimals(List<String> orderedAnimalNameString){
        List<AnimalItem> orderedAnimalItemList = new ArrayList<AnimalItem>();
        SharedPreferences sharedPreferences = getSharedPreferences("Team57", 0);
        for (String animalName: orderedAnimalNameString){
            //find the string containing all related information of a selected animal
            String animalItemInfoString = sharedPreferences.getString(animalName,
                    "No found such animal in sharedPreference");
            //get the animalItem
            AnimalItem animalItem = stringAndAnimalItem.StringToAnimalItem(animalItemInfoString);
            //add animalItem to the AnimalItem list
            orderedAnimalItemList.add(animalItem);
        }
        return orderedAnimalItemList;
    }

    public void setDirectionList(String startId, String goalId){
        Double pathSum;
        path = DijkstraShortestPath.findPathBetween(AnimalItem.gInfo, startId, goalId);
        //save path total distance
        pathDistances.add(Double.valueOf(path.getLength()));
        for (IdentifiedWeightedEdge edge: path.getEdgeList()){
            //save each edge
            ZooData.EdgeInfo edgeInfo = AnimalItem.eInfo.get(edge.getId());
            edgeStringList.add(edgeInfo.street);
            //save 2 endpoints of each edge
            Graph<String, IdentifiedWeightedEdge> myGraph = AnimalItem.gInfo;
            String start_endpoint_id = myGraph.getEdgeSource(edge);
            String end_endpoint_id = myGraph.getEdgeTarget(edge);
            String startName = AnimalItem.vInfo.get(start_endpoint_id).name;
            String endName = AnimalItem.vInfo.get(end_endpoint_id).name;
            start_endpoints.add(startName);
            end_endpoints.add(endName);
            //save edge weight
            Double myWeight = myGraph.getEdgeWeight(edge);
            edgeWeightList.add(Double.toString(myWeight));
        }

        for (int i = 0; i < edgeStringList.size(); i++){
            String displayInfo = "Walk "+edgeWeightList.get(i)+"ft along \""+edgeStringList.get(i)
                    +"\" from \""+start_endpoints.get(i)+"\" to \""+end_endpoints.get(i)+"\".";
            directionStringList.add(displayInfo);
        }
    }

    public void displayDirection(int order){
        //set the start and goal
        if ((order != 0) & (order != orderedAnimalItemList.size() - 1)){
            start_id = goal_id;
            start_name = goal_name;
            goal_id = orderedAnimalItemList.get(order).id;
            goal_name = orderedAnimalItemList.get(order).name;
            next_name = orderedAnimalItemList.get(order + 1).name;
            next_distance = pathDistances.get(order + 1).toString();
            previous_name = orderedAnimalItemList.get(order - 1).name;
            previous_distance = pathDistances.get(order - 1).toString();
        }
        else if (order == 0){
            start_id = entrance_id;
            start_name = entrance_name;
            goal_id = orderedAnimalItemList.get(order).id;
            goal_name = orderedAnimalItemList.get(order).name;
            next_name = orderedAnimalItemList.get(order + 1).name;
            next_distance = pathDistances.get(order + 1).toString();
            previous_name = "";
            previous_distance = "0";
        }
        else{
            start_id = goal_id;
            start_name = goal_name;
            goal_id = orderedAnimalItemList.get(order).id;
            goal_name = orderedAnimalItemList.get(order).name;
            next_name = "";
            next_distance = "0";
            previous_name = orderedAnimalItemList.get(order - 1).name;
            previous_distance = pathDistances.get(order - 1).toString();
        }
        //display directions
        String startExhibit = "From: "+start_name;
        String goalExhibit = "To: "+goal_name;
        startExhibitTitle.setText(startExhibit);
        goalExhibitTitle.setText(goalExhibit);
        String nextT = "\""+next_name+"\" ("+next_distance+"ft)";
        String prevT = "\""+previous_name+"\" ("+next_distance+"ft)";
        nextText.setText(nextT);
        previousText.setText(prevT);
        setDirectionList(start_id, goal_id);
        direction_adapter.setDirectionsStringList(directionStringList);
    }

    public void onNextButtonClick(View view) {
        if (directionItemOrder == orderedAnimalItemList.size() - 1){
            Utilities.showAlert(this, "This is the end of your tour");
            return;
        }
        else{
            directionItemOrder++;
            displayDirection(directionItemOrder);
        }
    }

    public void onBackButtonClick(View view) {
        if (directionItemOrder == 0){
            Utilities.showAlert(this, "This is the beginning of your tour");
            return;
        }
        else{
            directionItemOrder--;
            displayDirection(directionItemOrder);
        }
    }
}