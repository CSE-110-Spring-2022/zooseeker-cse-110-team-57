package edu.ucsd.cse110.project_ms1;
import static org.junit.Assert.assertEquals;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.project_ms1.location.Coord;
import edu.ucsd.cse110.project_ms1.location.Coords;
import edu.ucsd.cse110.project_ms1.location.LocationModel;


@RunWith(AndroidJUnit4.class)
public class MS2_USTest {

    @Rule
    public ActivityScenarioRule<SearchAnimalActivity> searchScenarioRule = new ActivityScenarioRule<>(SearchAnimalActivity.class);
    public ActivityScenarioRule<DirectionActivity> DirectionScenarioRule = new ActivityScenarioRule<>(DirectionActivity.class);

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
            AnimalUtilities.loadZooInfo(activity);
            toBeShown = AnimalItem.search_by_tag(null);
        });
    }

    @Test
    public void Mocking_of_Location_Test(){
        assert(true);
        //get ten points in line of Siamangs and Orangutans
        List<Coord> route = Coords.getTenPointsInLine("Siamangs", "Orangutans");
        Coord point_near_start = route.get(2);
        Coord point_near_goal = route.get(7);
        //LocationModel viewModel = new ViewModelProvider().get(LocationModel.class);
    }

    @Test
    public void read_mocking_file_test() throws IOException {
        ActivityScenario<DirectionActivity> scenario = DirectionScenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            Context context = ApplicationProvider.getApplicationContext();
            try {
                InputStream input = context.getAssets().open(DirectionActivity.MOCKING_FILE_NAME);
                List<Coord> points = ZooData.loadMockingJSON(input);
                List<Coord> correctPoints = new ArrayList<Coord>();
                correctPoints.add(new Coord(32.73459618734685, -117.14936));
                correctPoints.add(new Coord(32.73453269952234, -117.1526194979576));
                int i = 0;
                for (Coord point : points) {
                    assertEquals(point, correctPoints.get(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
    @Test
    public void getEntranceGateCoord_test(){
        ActivityScenario<SearchAnimalActivity> scenario = searchScenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            Coord coord = AnimalItem.getExtranceGateCoord();
            assertEquals(new Coord(32.73460, -117.14936), coord);
        });


    }
}


