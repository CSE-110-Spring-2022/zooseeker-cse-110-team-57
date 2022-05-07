package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends AppCompatActivity {
    StringAndAnimalItem stringAndAnimalItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        Intent intent = getIntent();
        //Only Animal String
        ArrayList<String> orderedAnimal = intent.getStringArrayListExtra("routedAnimalNameList");

        SharedPreferences sharedPreferences = getSharedPreferences("Team57", 0);
        List<AnimalItem> orderedAnimalList = new ArrayList<AnimalItem>();
        stringAndAnimalItem = new StringAndAnimalItem();

        for (String animalName: orderedAnimal){
            //find the string containing all related information of a selected animal
            String animalItemInfoString = sharedPreferences.getString(animalName,
                    "No found such animal in sharedPreference");
            //get the animalItem
            AnimalItem animalItem = stringAndAnimalItem.StringToAnimalItem(animalItemInfoString);
            //add animalItem to the AnimalItem list
            orderedAnimalList.add(animalItem);
        }

    }
}