package edu.ucsd.cse110.project_ms1;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



import android.content.Context;

import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@RunWith(AndroidJUnit4.class)
public class MS2_USTest {

    @Rule
    public ActivityScenarioRule<SearchAnimalActivity> searchScenarioRule = new ActivityScenarioRule<>(SearchAnimalActivity.class);
    public ActivityScenarioRule<SearchAnimalActivity> DirectionScenarioRule = new ActivityScenarioRule<>(SearchAnimalActivity.class);
//    @Before
//    public void add_animal(){
//        ActivityScenario search = scenarioRule.getScenario();
//        search.moveToState(Lifecycle.State.CREATED);
//
//        search.onActivity(activity -> {
//            imalItem newSelectedAnimalItem = search_adapter.searched_animal_items.get(position);
//            saveAddToList(newSelectedAnimalItem);
//            Button plan = activity.findViewById(R.id.plan_button);
//            plan.performClick();
//        });
//    }

    @Before
    public void loadGraph(){
        ActivityScenario<SearchAnimalActivity> scenario = searchScenarioRule.getScenario();
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
        });
    }

    @Test
    public void Mocking_of_Location(){
        assert(true);
    }

    public void testFindPathBetween(){

//    @Test
//    public void dir_clear(){
//        ActivityScenario directionScenarioRuleScenario = DirectionScenarioRule.getScenario();
//        directionScenarioRuleScenario.onActivity(activity -> {
//            activity.clearRoute
//        });
        String expectSource = "crocodile";
        String expectGoal = "hippo";

        List<IdentifiedWeightedEdge> paths =  DirectionHelper.findPathBetween(expectSource,expectGoal);
        //first edge has two endpoint, source could be one of them.
        String actualSource1 = AnimalItem.gInfo.getEdgeSource(paths.get(0));
        String actualSource2 = AnimalItem.gInfo.getEdgeTarget(paths.get(0));
        //last edge is similar, goal could be either one of them.
        String actualGoal1 = AnimalItem.gInfo.getEdgeSource(paths.get(paths.size()-1));
        String actualGoal2 = AnimalItem.gInfo.getEdgeTarget(paths.get(paths.size()-1));

        boolean sourceTrue = expectSource.equals(actualSource1) || expectSource.equals(actualSource2);
        boolean goalTrue = expectGoal.equals(actualGoal1) || expectGoal.equals(actualGoal2);

        assertTrue(sourceTrue);
        assertTrue(goalTrue);

    }
}


