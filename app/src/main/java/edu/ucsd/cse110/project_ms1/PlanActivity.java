package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlanActivity extends AppCompatActivity{

    public RecyclerView plan_recyclerView;
    StringAndAnimalItem stringAndAnimalItem;
    PlanAdapter plan_adapter;

    List<String> selectedAnimalNameStringList;
    List<route_node> routeNodeList;
    List<String> routedAnimalNameString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        stringAndAnimalItem = new StringAndAnimalItem();
        routedAnimalNameString = new ArrayList<String>();

        plan_adapter = new PlanAdapter();
        plan_adapter.setHasStableIds(true);


        plan_recyclerView = this.findViewById(R.id.planed_route);
        plan_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plan_recyclerView.setAdapter(plan_adapter);


        //-----------------------------load selected animalItem----------------------------------
        Intent intent = getIntent();
        selectedAnimalNameStringList =
                intent.getStringArrayListExtra("nameStringList");

        SharedPreferences sharedPreferences = getSharedPreferences("Team57", 0);
        List<AnimalItem> selectedAnimalItemList = new ArrayList<AnimalItem>();

        for (String animalName: selectedAnimalNameStringList){
            //find the string containing all related information of a selected animal
            String animalItemInfoString = sharedPreferences.getString(animalName,
                    "No found such animal in sharedPreference");
            //get the animalItem
            AnimalItem animalItem = stringAndAnimalItem.StringToAnimalItem(animalItemInfoString);
            //add animalItem to the AnimalItem list
            selectedAnimalItemList.add(animalItem);
        }
        routeNodeList = AnimalItem.plan_route(selectedAnimalItemList);
        plan_adapter.setRouted_animal_items(routeNodeList);

        for (route_node myRoute_node: routeNodeList){
            String myAnimal = myRoute_node.animal.name;
            routedAnimalNameString.add(myAnimal);
        }

    }

    public void onDirectionsClick(View view) {
        Intent intent = new Intent(this, DirectionActivity.class);
        ArrayList<String> routedAnimalList = new ArrayList<String>(routedAnimalNameString);
        intent.putStringArrayListExtra("routedAnimalNameList", routedAnimalList);
        startActivity(intent);

    }



}