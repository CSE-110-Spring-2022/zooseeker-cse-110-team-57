package edu.ucsd.cse110.project_ms1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ucsd.cse110.project_ms1.location.Coord;
import edu.ucsd.cse110.project_ms1.location.Coords;


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

//    @Test
//    public void read_mocking_file_test() throws IOException {
//        ActivityScenario<DirectionActivity> scenario = DirectionScenarioRule.getScenario();
//        scenario.moveToState(Lifecycle.State.CREATED);
//        scenario.onActivity(activity -> {
//            Context context = ApplicationProvider.getApplicationContext();
//            try {
//                InputStream input = context.getAssets().open(DirectionActivity.MOCKING_FILE_NAME);
//                List<Coord> points = ZooData.loadMockingJSON(input);
//                List<Coord> correctPoints = new ArrayList<Coord>();
//                correctPoints.add(new Coord(32.73459618734685, -117.14936));
//                correctPoints.add(new Coord(32.73453269952234, -117.1526194979576));
//                int i = 0;
//                for (Coord point : points) {
//                    assertEquals(point, correctPoints.get(i));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }

    @Test
    public void read_mocking_file_test() throws IOException {
        Context context = ApplicationProvider.getApplicationContext();
        InputStream input = context.getAssets().open("mocking_location.json");
        List<Coord> points = ZooData.loadMockingJSON(input);
        List<Coord> correctPoints = new ArrayList<Coord>();
        correctPoints.add(new Coord(32.73459618734685, -117.14936));
        correctPoints.add(new Coord(32.73453269952234, -117.1526194979576));
        int i = 0;
        for (Coord point : points) {
            assertEquals(point, correctPoints.get(i));
            i++;
        }
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
    public void test_skip_remove(){
        List<String> a1 = new ArrayList<>(Arrays.asList("a","b","c","d","e"));
        List<String> a2 = new ArrayList<>(Arrays.asList("1","2","3","4","5"));
        List<List<String>> a3 = new ArrayList<>();
        a3.add(List.of("1,2,3"));
        a3.add(List.of("1,2,3"));
        a3.add(List.of("1,2,3"));
        a3.add(List.of("1,2,3"));
        a3.add(List.of("1,2,3"));

        List<String> a1_copy = new ArrayList<>(a1);
        List<String> a2_copy = new ArrayList<>(a2);
        List<List<String>> a3_copy = new ArrayList<>(a3);
        DirectionActivity.skip(true,1,a1,a2,a3, null);
        a1_copy.remove(2);
        a2_copy.remove(2);
        a3_copy.remove(2);
        assertEquals(a1_copy,a1);
        assertEquals(a2_copy,a2);
        assertEquals(a3_copy,a3);

        DirectionActivity.skip(false,1,a1,a2,a3, null);
        a1_copy.remove(0);
        a2_copy.remove(0);
        a3_copy.remove(0);
        assertEquals(a1_copy,a1);
        assertEquals(a2_copy,a2);
        assertEquals(a3_copy,a3);
//        System.out.println(a1);
//        System.out.println(a2);
//        System.out.println(a3);
//        System.out.println(a1_copy);
//        System.out.println(a2_copy);
//        System.out.println(a3_copy);

    }
    @Test
    public void getEntranceGateCoord_test(){
        ActivityScenario<SearchAnimalActivity> scenario = searchScenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            Coord coord = AnimalItem.getExtranceGateCoord();
            assertEquals(new Coord(32.73459618734685, -117.14936), coord);
        });


    }
/*
    @Test
    public void getNearestNode(){
        LatLng expectLatLng = new LatLng(32.72442520989079, -117.16066409380507);
        String expectNode = "intxn_front_lagoon_2";
//        String actualNode1 = AnimalItem.getNearestExhibit(expectLatLng);

//        assertEquals(expectNode,actualNode1);

        LatLng expectLatLng2 = new LatLng(32.746320519009030, -117.16364410510080);
        String expectNode2 = "hippo";
//        String actualNode12 = AnimalItem.getNearestExhibit(expectLatLng2);

        assertEquals(expectNode2,actualNode12);
//        assertEquals(expectNode2,actualNode12);

    }
*/


    @Test
    public void onRangeTrue(){
        //NodeA = owens_aviary
        //NodeB = fern_canyon
        //expect street = owens_to_fern

        LatLng nodeALatLng = new LatLng(32.73798565400121, -117.16949876733686);
        LatLng nodeBLatLng = new LatLng(32.73480907296893, -117.16066409380508);
        double midpointLat = (nodeALatLng.latitude + nodeBLatLng.latitude) /2;
        double midpointLng = (nodeALatLng.longitude + nodeBLatLng.longitude) /2;
        LatLng currLatLng = new LatLng(midpointLat, midpointLng);

        boolean actual = DirectionHelper.onRange(currLatLng,nodeALatLng,nodeBLatLng);
        assertTrue(actual);
    }

    @Test
    public void onTrackTrue(){
        //NodeA = owens_aviary
        //NodeB = fern_canyon
        //expect street = owens_to_fern

        LatLng nodeALatLng = new LatLng(32.73798565400121, -117.16949876733686);
        LatLng nodeBLatLng = new LatLng(32.73480907296893, -117.16066409380508);
        double midpointLat = (nodeALatLng.latitude + nodeBLatLng.latitude) /2;
        double midpointLng = (nodeALatLng.longitude + nodeBLatLng.longitude) /2;
        LatLng currLatLng = new LatLng(midpointLat, midpointLng);

        boolean actual = DirectionHelper.onTrack(currLatLng,nodeALatLng,nodeBLatLng);
        assertTrue(actual);

        LatLng currLatLng2 = new LatLng(32.736864688333235,-117.16079397323202);
        boolean actual2 = DirectionHelper.onTrack(currLatLng2,nodeALatLng,nodeBLatLng);
        assertFalse(actual2);
    }


    @Test
    public void findCurrStreet(){
        //NodeA = owens_aviary
        //NodeB = fern_canyon
        //expect street = owens_to_fern
        String nearestExhibit = "owens_aviary";
        LatLng nodeALatLng = new LatLng(32.73798565400121, -117.16949876733686);
        LatLng nodeBLatLng = new LatLng(32.73480907296893, -117.17269958736802);
        double midpointLat = (nodeALatLng.latitude + nodeBLatLng.latitude) /2;
        double midpointLng = (nodeALatLng.longitude + nodeBLatLng.longitude) /2;
        LatLng currLatLng = new LatLng(midpointLat, midpointLng);
        String expectStreet = "owens_to_fern";
        String actualStreet = DirectionHelper.findCurrStreet(nearestExhibit,currLatLng).toString();
        assertEquals(expectStreet,actualStreet);
    }

    @Test
    public void briefDistances(){
        //NodeA = owens_aviary
        //NodeB = parker_aviary

        String start = "owens_aviary";
        String end = "parker_aviary";

        List<IdentifiedWeightedEdge> path = DirectionHelper.findPathBetween(start,end);
        List<Double> distances = new ArrayList<>();
        List<String> streets = new ArrayList<>();
        List<String> targets = new ArrayList<>();

        DirectionHelper.briefBuild(path,start,distances,streets,targets);
        assertEquals(distances.size(),streets.size());
        assertEquals(1, distances.size());
        assertEquals(targets.get(targets.size()-1),AnimalItem.vInfo.get(end).name);

        //NodeA = owens_aviary
        //NodeB = parker_aviary

        String start2 = "crocodile";
        String end2 = "scripps_aviary";
        List<IdentifiedWeightedEdge> path2 = DirectionHelper.findPathBetween(start2,end2);
        List<Double> distances2 = new ArrayList<>();
        List<String> streets2 = new ArrayList<>();
        List<String> targets2 = new ArrayList<>();


        DirectionHelper.briefBuild(path2,start2,distances2,streets2,targets2);
        assertEquals(distances2.size(),streets2.size());
        assertEquals(streets2.size(),targets2.size());
        assertEquals(targets2.get(targets2.size()-1),AnimalItem.vInfo.get(end2).name);

    }


    @Test
    public void reroute() throws IOException {
        //load zooData
        String vPath = "exhibit_info.json";
        String ePath = "trail_info.json";
        String gPath = "zoo_graph.json";
        Context context = ApplicationProvider.getApplicationContext();

        LatLng latlng = new LatLng(32.73561,-117.14936);
        boolean going_forward =  true;
        ArrayList<AnimalItem> animalItemArrayList = new ArrayList<>();
        AnimalItem a1 = new AnimalItem("orangutan",
                new ArrayList<>(),
                "Orangutans",
                new LatLng(32.736864688333235,-117.16364410510093));
        AnimalItem a2 = new AnimalItem("siamang",
                new ArrayList<>(),
                "Siamangs",
                new LatLng(32.736864688333235,-117.16079397323202));
        AnimalItem a3 = new AnimalItem("toucan",
                new ArrayList<>(),
                "Toucan",
                new LatLng(32.736864688333235,-117.16079397323202));
        AnimalItem a4 = new AnimalItem("entrance_exit_gate",
                new ArrayList<>(),
                "Entrance and Exit Gate",
                new LatLng(32.73561,-117.14936));

        AnimalItem.loadInfo(context, vPath, ePath, gPath);

        //selected animal list
        animalItemArrayList.add(a1);
        animalItemArrayList.add(a2);
        animalItemArrayList.add(a3);


        // optimized route
        List<route_node> route = AnimalItem.plan_route(animalItemArrayList, a4.id, false);

        for (route_node node : route){
            System.out.println(node.names);
        }

        route.remove(2);

        // reroute on order i
        List<route_node> r = AnimalUtilities.reroute(1,  route, latlng, going_forward);
        for (route_node node : r){
            System.out.println(node.names);
        }
        //wait for reroute
        //??
    }


    @Test
    public void findStartPoint() throws IOException {//load zooData
        String vPath = "exhibit_info.json";
        String ePath = "trail_info.json";
        String gPath = "zoo_graph.json";
        Context context = ApplicationProvider.getApplicationContext();

        LatLng latlng = new LatLng(32,-117);
        boolean going_forward =  true;
        ArrayList<AnimalItem> animalItemArrayList = new ArrayList<>();
        AnimalItem a1 = new AnimalItem("orangutan",
                new ArrayList<>(),
                "Orangutans",
                new LatLng(32.736864688333235,-117.16364410510093));
        AnimalItem a2 = new AnimalItem("siamang",
                new ArrayList<>(),
                "Siamangs",
                new LatLng(32.736864688333235,-117.16079397323202));
        AnimalItem a3 = new AnimalItem("toucan",
                new ArrayList<>(),
                "Toucan",
                new LatLng(32.736864688333235,-117.16079397323202));
        AnimalItem a4 = new AnimalItem("entrance_exit_gate",
                new ArrayList<>(),
                "Entrance and Exit Gate",
                new LatLng(32.73561,-117.14936));

        AnimalItem.loadInfo(context, vPath, ePath, gPath);

        animalItemArrayList.add(a1);
        animalItemArrayList.add(a2);
        animalItemArrayList.add(a3);
        animalItemArrayList.add(a4);


        String result = AnimalUtilities.find_starting_point(animalItemArrayList,new LatLng(32.73561,-117.14936));
        System.out.println(result);
    }

    @Test
    public void checkOffRoute() throws IOException{
        //load zooData
        String vPath = "exhibit_info.json";
        String ePath = "trail_info.json";
        String gPath = "zoo_graph.json";
        Context context = ApplicationProvider.getApplicationContext();

        LatLng latlng = new LatLng(32,-117);
        boolean going_forward =  true;
        ArrayList<AnimalItem> animalItemArrayList = new ArrayList<>();
        AnimalItem a1 = new AnimalItem("orangutan",
                new ArrayList<>(),
                "Orangutans",
                new LatLng(32.736864688333235,-117.16364410510093));
        AnimalItem a2 = new AnimalItem("siamang",
                new ArrayList<>(),
                "Siamangs",
                new LatLng(32.736864688333235,-117.16079397323202));
        AnimalItem a3 = new AnimalItem("toucan",
                new ArrayList<>(),
                "Toucan",
                new LatLng(32.736864688333235,-117.16079397323202));
        AnimalItem a4 = new AnimalItem("entrance_exit_gate",
                new ArrayList<>(),
                "Entrance and Exit Gate",
                new LatLng(32.73561,-117.14936));

        AnimalItem.loadInfo(context, vPath, ePath, gPath);

        animalItemArrayList.add(a1);
        animalItemArrayList.add(a2);
        animalItemArrayList.add(a3);

        List<route_node> route = AnimalItem.plan_route(animalItemArrayList,a4.id, false);
    }


}


