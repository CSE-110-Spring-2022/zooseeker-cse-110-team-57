package edu.ucsd.cse110.project_ms1;



import static android.util.Log.println;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
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

    @Test
    public void RouteTest(){
        //US11  without gorilla
        List<AnimalItem> items = AnimalItem.search_by_tag(null);
        List<route_node> nodes ;
        double total_dis;


        items.remove(4);
        nodes = AnimalItem.plan_route(items);

        //checking the order is right
        assertEquals("gators", nodes.get(0).animal.id );
        assertEquals("lions", nodes.get(1).animal.id );
        assertEquals("elephant_odyssey", nodes.get(2).animal.id );
        assertEquals("arctic_foxes", nodes.get(3).animal.id );
        assertEquals("entrance_exit_gate", nodes.get(4).animal.id );
//        assertEquals(110.0, nodes.get(1).distance, 0.01);

        //checking the total distance is right
        total_dis = total_length(nodes);
        assertEquals(1620.0, total_dis ,0.01);
    }





    //helper function to find length of a route
    private double total_length(List<route_node> nodes) {
        double dis=0;
        String goal = nodes.get(0).animal.id;
        String start = "entrance_exit_gate";
        for (int i =0; i<nodes.size(); i++){

           double curr = AnimalItem.route_length
                   (DijkstraShortestPath.findPathBetween(AnimalItem.gInfo, start, goal));

           if (i<nodes.size()-1){
               start = goal;
               goal = nodes.get(i+1).animal.id;
           }

           dis+=curr;
//           System.out.println("\ni am at "+goal);
//           System.out.println("step is "+curr);
//           System.out.println("total is "+dis);
        }
        return  dis;
    }
}

