package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
        SharedPreferences preference = getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("currentActivity", "PlanActivity");
        editor.commit();
        editor.apply();

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

        if (selectedAnimalNameStringList == null){
            Set<String> selectedAnimalNameStringSet = sharedPreferences.getAll().keySet();
            selectedAnimalNameStringSet.remove("currentActivity");
            selectedAnimalNameStringList = new ArrayList<String>(selectedAnimalNameStringSet);
        }



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

        List<route_node> routeNodeListWithoutExitGate = new ArrayList<>(routeNodeList);
        int exit_gate_index = routeNodeList.size() - 1;
        route_node exit_gate_route_node = routeNodeListWithoutExitGate.remove(exit_gate_index);
        plan_adapter.setRouted_animal_items(routeNodeListWithoutExitGate);

        for (route_node myRoute_node: routeNodeList){
            String myAnimal = myRoute_node.animal.name;
            routedAnimalNameString.add(myAnimal);
        }

    }

    public void onDirectionsClick(View view) {
        saveToSharedPreference(routedAnimalNameString);

        //start the direction page
        Intent intent = new Intent(this, DirectionActivity.class);
        ArrayList<String> routedAnimalList = new ArrayList<String>(routedAnimalNameString);
        intent.putStringArrayListExtra("routedAnimalNameList", routedAnimalList);
        startActivity(intent);
    }

    public void saveToSharedPreference(List<String> animal_names) {
        //save routedAnimalNameString to sharedPreference
        //link the animalItem name with the string form of animalItem
        SharedPreferences sharedPreferences = getSharedPreferences("Team57", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //convert AnimalItem into a string containing all related information of a selected animal
        String joined = String.join(",", animal_names );

        //Map(animal name, animal information)
        editor.putString("route", joined);
        editor.commit();
        editor.apply();
    }
}