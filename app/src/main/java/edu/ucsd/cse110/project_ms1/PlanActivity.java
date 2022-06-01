package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
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

    private static final int ACTIVITY_CONSTANT = 0;
    public RecyclerView plan_recyclerView;
    StringAndAnimalItem stringAndAnimalItem;
    PlanAdapter plan_adapter;

    List<String> selectedAnimalNameStringList;
    List<route_node> routeNodeList;
    List<String> routedAnimalNameStrings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        //update the current Activity
        Utilities.changeCurrentActivity(this, "PlanActivity");
        AnimalUtilities.loadZooInfo(this);
        //initialization
        stringAndAnimalItem = new StringAndAnimalItem();
        routedAnimalNameStrings = new ArrayList<String>();
        plan_adapter = new PlanAdapter();
        plan_adapter.setHasStableIds(true);
        //bind the recycler view
        plan_recyclerView = this.findViewById(R.id.planed_route);
        plan_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plan_recyclerView.setAdapter(plan_adapter);


        //load the selected animals
        Intent intent = getIntent();
        selectedAnimalNameStringList =
                intent.getStringArrayListExtra("nameStringList");
        SharedPreferences sharedPreferences = getSharedPreferences("Team57", 0);
        List<AnimalItem> selectedAnimalItemList = new ArrayList<AnimalItem>();

        if (selectedAnimalNameStringList == null){
            Set<String> selectedAnimalNameStringSet = sharedPreferences.getAll().keySet();
            selectedAnimalNameStringSet.remove("currentActivity");
            selectedAnimalNameStringSet.remove("currentOrder");
            selectedAnimalNameStringSet.remove("currentIsNext");
            selectedAnimalNameStringSet.remove("currentLat");
            selectedAnimalNameStringSet.remove("currentLng");
            selectedAnimalNameStringSet.remove("route");
            selectedAnimalNameStringList = new ArrayList<String>(selectedAnimalNameStringSet);
        }

        //get the AnimalItem of selected animal names
        for (String animalName: selectedAnimalNameStringList){
            //find the string containing all related information of a selected animal
            String animalItemInfoString = sharedPreferences.getString(animalName,
                    "No found such animal in sharedPreference");
            //get the animalItem
            AnimalItem animalItem = stringAndAnimalItem.StringToAnimalItem(animalItemInfoString);
            //add animalItem to the AnimalItem list
            selectedAnimalItemList.add(animalItem);
        }
        //get the route
        routeNodeList = AnimalItem.plan_route(selectedAnimalItemList, "entrance_exit_gate", false);
        //remove the exit gate
        List<route_node> routeNodeListWithoutExitGate = new ArrayList<>(routeNodeList);
        int exit_gate_index = routeNodeList.size() - 1;
        route_node exit_gate_route_node = routeNodeListWithoutExitGate.remove(exit_gate_index);
        plan_adapter.setRouted_animal_items(routeNodeListWithoutExitGate);
        //get the routed animals name string
        for (route_node myRoute_node: routeNodeList){
            String myAnimal = myRoute_node.exhibit.name;
            routedAnimalNameStrings.add(myAnimal);
        }

    }
    //act when direction is clicked
    public void onDirectionsClick(View view) {
        saveToSharedPreference(routedAnimalNameStrings);

        //start the direction page
        Intent intent = new Intent(this, DirectionActivity.class);
        ArrayList<String> routedAnimalList = new ArrayList<String>(routedAnimalNameStrings);
        intent.putStringArrayListExtra("routedAnimalNameList", routedAnimalList);
        startActivityForResult(intent, ACTIVITY_CONSTANT);
    }
    //save the routed animals in sharedpreference
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

    //https://stackoverflow.com/questions/9664108/how-to-finish-parent-activity-from-child-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_CONSTANT)
        {
            finish();
        }
    }
}