package edu.ucsd.cse110.project_ms1;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public void SearchByNameTest1() {
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
    public void SearchByNameTest2() {
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
    public void saveListTest() {
        //User Story 4
        //testing saveAddToList() and loadAddToList()
        ActivityScenario<SearchAnimalActivity> scenario = scenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            String animalId = "lions";
            String animalName = "Lion";
            ArrayList<String> tags = new ArrayList<>(Arrays.asList("lions"));
            AnimalItem animalItem = new AnimalItem(animalId, tags , animalName);

            activity.saveAddToList(animalItem);
            List<AnimalItem> animalItems = activity.loadAddToList();
            List<AnimalItem> actualValue = animalItems.stream().filter(animalItem1 -> animalItem1.name.equals(animalName)).collect(Collectors.toList());

            assertEquals(animalName,actualValue.get(0).name);
        });
    }

//========================================================== US 5 ======================================================================================================
    @Test
    public void displayNumbOfExhibit() {
        //testing the number of animal in the list
        ActivityScenario<SearchAnimalActivity> scenario = scenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            //clear the list -set number of animals in list to 0
            activity.clearSavedAnimalItem();
            int actualSize = activity.addToList_adapter.getItemCount();
            //before putting animal in the list
            assertEquals(0,actualSize);


            //add 1 animal in list
            String animalId = "lions";
            String animalName = "Lion";
            ArrayList<String> tags = new ArrayList<>(Arrays.asList("lions"));
            AnimalItem animalItem = new AnimalItem(animalId, tags , animalName);

            activity.saveAddToList(animalItem);
            activity.loadAddToList();

            //after load
            actualSize = activity.addToList_adapter.getItemCount();
            assertEquals(1,actualSize);

            //add 1 more animal in list
            animalId = "cat";
            animalName = "yomi";
            tags = new ArrayList<>(Arrays.asList("lions"));
            animalItem = new AnimalItem(animalId, tags , animalName);

            activity.saveAddToList(animalItem);
            activity.loadAddToList();

            actualSize = activity.addToList_adapter.getItemCount();
            assertEquals(2,actualSize);

            activity.clearSavedAnimalItem();

        });
    }

//========================================================== US 7 ======================================================================================================




}
