package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlanActivity extends AppCompatActivity{
    /*
    public RecyclerView plan_recyclerView;
    PlanAdapter plan_adapter;
    List<AnimalItem> selectedAnimalItemList;
    List<String> selectedAnimalNameStringList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        plan_adapter = new PlanAdapter();
        plan_adapter.setHasStableIds(true);

        plan_recyclerView = findViewById(R.id.planed_route);
        plan_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plan_recyclerView.setAdapter(plan_adapter);

        Intent intent = getIntent();
        selectedAnimalNameStringList = intent.getStringArrayListExtra("selectedAnimalNameStringList");
    }

    public List<AnimalItem> loadAddToList(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        List<AnimalItem> selectedAnimalItemList = new ArrayList<AnimalItem>();

        //check if the user hasn't selected any animal
        if (selectedAnimalNameStringList.isEmpty()) {
            //return empty list of AnimalItem
            return selectedAnimalItemList;
        }
        //set the recycler view
        for (String animalName: selectedAnimalNameStringList){
            //find the string containing all related information of a selected animal
            String animalItemInfoString = sharedPreferences.getString(animalName,
                    "No found such animal in sharedPreference");
            //get the animalItem
            AnimalItem animalItem = StringToAnimalItem(animalItemInfoString);
            //add animalItem to the AnimalItem list
            selectedAnimalItemList.add(animalItem);
        }
        addToList_adapter.setSelectedAnimalItems(selectedAnimalItemList);

        return selectedAnimalItemList;
    }
*/


}