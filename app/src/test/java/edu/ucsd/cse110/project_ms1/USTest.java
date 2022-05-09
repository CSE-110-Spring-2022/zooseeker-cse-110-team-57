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
import java.util.Arrays;
import java.util.List;

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
import java.util.Map;
import java.util.Set;

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
        ActivityScenario<SearchAnimalActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        ArrayList<String> list = new ArrayList<>(Arrays.asList());

        AnimalItem animalItem = new AnimalItem("1",list,"panda");
        scenario.onActivity(activity -> {
            activity.saveAddToList(animalItem);
            List<AnimalItem> res =  activity.loadAddToList();
            for(AnimalItem item : res){
                assertTrue(item.name.contains("panda"));
            }
        });

    }

    @Test
    public void AddListTest_empty() {
        //User Story 4
        ActivityScenario<SearchAnimalActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        ArrayList<String> list = new ArrayList<>(Arrays.asList());

        scenario.onActivity(activity -> {
            List<AnimalItem> res =  activity.loadAddToList();
            assertTrue(res.isEmpty());
        });

    }

//========================================================== US 5 ======================================================================================================
    @Test
    public void displayNumbOfExhibit() {
        ActivityScenario<SearchAnimalActivity> scenario = scenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        ArrayList<String> list = new ArrayList<>(Arrays.asList());
        AnimalItem animalItem = new AnimalItem("1",list,"panda");
        scenario.onActivity(activity -> {
            TextView text = activity.findViewById(R.id.selected_animals_number);
            assertTrue(Integer.valueOf(text.getText().toString()) == 0);

        });
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

    @Test
    public void RouteTest2(){
        //US11  only mammals
        List<AnimalItem> items = AnimalItem.search_by_tag("mammal");
        assertEquals(4,items.size());

        List<route_node> nodes = AnimalItem.plan_route(items);
        double total_dis;

        //checking the total distance is right
        total_dis = total_length(nodes);
        assertEquals(1720.0, total_dis ,0.01);
    }

    @Test
    public void RouteTest3(){
        //US11  only lions
        List<AnimalItem> items = AnimalItem.search_by_tag("lions");

        List<route_node> nodes = AnimalItem.plan_route(items);
        assertEquals(2,nodes.size());
        double total_dis;

        //checking the total distance is right
        total_dis = total_length(nodes);
        assertEquals(620, total_dis ,0.01);
    }





    //helper function to find length of a route
    private double total_length(List<route_node> nodes) {
        System.out.println(nodes.size());
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
           System.out.println("\ni am at "+start);
           System.out.println("step is "+curr);
           System.out.println("total is "+dis);
        }
        return  dis;
    }

    @Test
    public void planActivity(){
        List<route_node> routeNodeList;

        List<String> selectedAnimalNameStringList = new ArrayList<>();
        selectedAnimalNameStringList.add("Alligators");
        selectedAnimalNameStringList.add("Lions");
        selectedAnimalNameStringList.add("Gorillas");
        StringAndAnimalItem stringAndAnimalItem = new StringAndAnimalItem();

        List<String> selectedAnimalAddress = new ArrayList<>();
        selectedAnimalAddress.add("Reptile Road");
        selectedAnimalAddress.add("Sharp Teeth Shortcut");
        selectedAnimalAddress.add("Africa Rocks Street");

        List<Double> selectedAnimalDistance = new ArrayList<>();
        selectedAnimalDistance.add(110.0);
        selectedAnimalDistance.add(310.0);
        selectedAnimalDistance.add(210.0);

        List<AnimalItem> selectedAnimalItemList = AnimalItem.search_by_tag(null);
        selectedAnimalItemList.remove(0);
        selectedAnimalItemList.remove(2);
        routeNodeList = AnimalItem.plan_route(selectedAnimalItemList);


        for (int i = 0; i < routeNodeList.size() - 1; i++){
            assertEquals(routeNodeList.get(i).animal.name, selectedAnimalNameStringList.get(i));
            assertEquals(routeNodeList.get(i).address, selectedAnimalAddress.get(i));
            assertEquals(Double.toString(routeNodeList.get(i).distance), Double.toString(selectedAnimalDistance.get(i)));
        }

    }


}

