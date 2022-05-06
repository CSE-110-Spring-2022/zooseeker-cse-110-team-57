package edu.ucsd.cse110.project_ms1;



import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.junit.Assert.*;

import android.content.Context;

import android.content.SharedPreferences;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class USTest {
    @Rule
    public ActivityScenarioRule<SearchAnimalActivity> scenarioRule = new ActivityScenarioRule<>(SearchAnimalActivity.class);

    @Test
    public void LoadTest() {
        //User Story 0
        ActivityScenario<SearchAnimalActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            List<AnimalItem> toBeShown = null;
            Context context = ApplicationProvider.getApplicationContext();
            try {
                AnimalItem.loadInfo(context, "sample_node_info.json", "sample_edge_info.json", "sample_zoo_graph.json");
                toBeShown = AnimalItem.search_by_tag(null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<String> allAnimals = new ArrayList<>();

            for(AnimalItem a : toBeShown){
                allAnimals.add(a.name);
            }

            assertTrue(allAnimals.contains("Lions"));

        });

    }

    @Test
    public void SearchByTagTest() {
        //User Story 1
        ActivityScenario<SearchAnimalActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            List<AnimalItem> animalItem = AnimalItem.search_by_tag("lions");

            for(AnimalItem item : animalItem){
                assertTrue(item.tags.contains("lions"));
            }
        });
    }


    @Test
    public void SearchByNameTest() {
        //User Story 1
        ActivityScenario<SearchAnimalActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {

            List<AnimalItem> animalItem = AnimalItem.search_by_tag("Unicorn");

            for(AnimalItem item : animalItem){
                assertEquals(false,item.tags.contains("Unicorn"));
            }
            assertEquals(true, animalItem.isEmpty());
        });
    }

    @Test
    public void SearchByNameTest() {
        //User Story 1

        ActivityScenario<SearchAnimalActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {

            List<AnimalItem> animalItem = AnimalItem.search_by_tag("Elephant Odyssey");

            for(AnimalItem item : animalItem){
                assertTrue(item.name.contains("Elephant Odyssey"));
            }
        });
    }
//================================================================================================================================================================

//========================================================== US 4 ======================================================================================================
    @Test
    public void AddListTest() {
        //User Story 4
//        ActivityScenario<SearchAnimalActivity> scenario = scenarioRule.getScenario();
//        scenario.moveToState(Lifecycle.State.CREATED);
//        scenario.onActivity(activity -> {
//
//            SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferences.edit();
//
//            TextView animal = activity.findViewById(R.id."");
//            editor.putString("",animal.getText().toString());
//
//
//        });
    }

//========================================================== US 5 ======================================================================================================
    @Test
    public void displayNumbOfExhibit() {
//        ActivityScenario<SearchAnimalActivity> scenario = scenarioRule.getScenario();
//        scenario.moveToState(Lifecycle.State.CREATED);
//        scenario.onActivity(activity -> {
//
//
//
//        });
    }
}
