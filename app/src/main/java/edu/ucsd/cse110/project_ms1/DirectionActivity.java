package edu.ucsd.cse110.project_ms1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import edu.ucsd.cse110.project_ms1.location.Coord;
import edu.ucsd.cse110.project_ms1.location.Coords;
import edu.ucsd.cse110.project_ms1.location.LocationModel;
import edu.ucsd.cse110.project_ms1.location.LocationPermissionChecker;

public class DirectionActivity extends AppCompatActivity implements OnLocationChangeListener {
    int order;
    private boolean useLocationService;
    // direction display status
    boolean displayStatus;
    boolean isNext;
    Button detailBtn;
    Intent intent;
    LocationModel viewModel;

    public static ArrayList<String> orderedAnimal;
    DirectionAdapter direction_adapter;
    RecyclerView direction_recyclerView;
    List<String> orderedAnimalList_Names;
    List<String> orderedAnimalList_IDs;
    List<List<String>> orderedAnimalList_child;
    List<AnimalItem> animalItems;
    List<route_node> planned_route;
    boolean going_forward;
    private static final String TAG = "Location6666666";
    public static final String MOCKING_FILE_NAME = "mocking.json";
    public static final String EXTRA_USE_LOCATION_SERVICE = "use_location_updated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        //use physical real location
        useLocationService = true;

        //retain the DirectionActivity
        Utilities.changeCurrentActivity(this, "DirectionActivity");
        going_forward = true;

        //grab ordered list of animal id, begin from first item in the route.
        Intent intent = getIntent();
        orderedAnimal = intent.getStringArrayListExtra("routedAnimalNameList");
        if (orderedAnimal == null){
            SharedPreferences sharedPreferences = getSharedPreferences("Team57", 0);
            String concated_animal_names = sharedPreferences.getString("route",
                    "No found such animal in sharedPreference");
            orderedAnimal = new ArrayList<>(Arrays.asList(concated_animal_names.split("\\s*,\\s*"))) ;
            DirectionHelper.restoreCurrentOrderAndIsNext(this);
        }

        //remove the Entrance and Exit Gate
        orderedAnimal.remove(orderedAnimal.size() - 1);
        animalItems = DirectionHelper.loadAnimalItem(this, orderedAnimal);

        //find the shortest Path by given ordered route.
        //(order -order of animal in the route , paths -list of edges in the path)
        planned_route = AnimalItem.plan_route(animalItems);
        //List<DirectionData> orderedAnimalList = DirectionHelper.routeNode_to_DirectionData(planned_route);

        orderedAnimalList_Names = new ArrayList<>();
        orderedAnimalList_IDs = new ArrayList<>();
        orderedAnimalList_child = new ArrayList<>();
        for(route_node node : planned_route){
            orderedAnimalList_Names.add(node.exhibit.name);
            orderedAnimalList_IDs.add(node.exhibit.id);
            orderedAnimalList_child.add(node.names);

        }

        //we need add the front gate into orderedAnimalList, so that route begin at gate
        orderedAnimalList_Names.add(0, "Entrance and Exit Gate");
        orderedAnimalList_IDs.add(0, "entrance_exit_gate");
        orderedAnimalList_child.add(0, Arrays.asList("Entrance and Exit Gate"));
        Log.d("orderedAnimalList_Names",orderedAnimalList_Names.toString());


//        orderedAnimalList.add("entrance_exit_gate");
        //<order -the index of exhibit in the route, edges -the edges between exhibits
        //HashMap<Integer, List<IdentifiedWeightedEdge>> route = DirectionHelper.findRoute(planned_route);


        //adapter
        direction_adapter = new DirectionAdapter();
        direction_adapter.setHasStableIds(true);
        direction_recyclerView = this.findViewById(R.id.brief_path);
        direction_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        direction_recyclerView.setAdapter(direction_adapter);

        //connect LocationModel class
        viewModel = new ViewModelProvider(this).get(LocationModel.class);

        //Get the order and isNext
        List<String> retainedInfo = DirectionHelper.loadDirectionsInformation(this);
        order = Integer.valueOf(retainedInfo.get(0));
        isNext = Boolean.valueOf(retainedInfo.get(1));

        // display status
        loadDisplayStatus();
        display(order, isNext);

    } //Initial End

    public void display(int index, boolean isNext) {
        TextView start = findViewById(R.id.start_exhibit_name);
        TextView end = findViewById(R.id.goal_exhibit_name);
        TextView next = findViewById(R.id.next_text);
        TextView prev = findViewById(R.id.previous_text);
        TextView distance = findViewById(R.id.path_total_distance);
        Button prevBtn = findViewById(R.id.previous_button);
        Button nextBtn = findViewById(R.id.next_button);
        Button mockBtn = findViewById(R.id.mock_button);
        detailBtn = findViewById(R.id.detail_button);

        //---------------comment this line if you enable the mock button---------------------------
        //mockBtn.setEnabled(false);
        //-----------------------------------------------------------------------------------

        setDirectionTextDisplay();

        String sourceExhibit;
        String goalExhibit;
        List<IdentifiedWeightedEdge> path;
        Log.d("indexDisplay", " "+index+" ");
        Log.d("sizeDisplay", " "+orderedAnimalList_IDs.size()+" ");

        //Set the "From" and "To"
        String endText = "To: ";
        if (isNext){
            sourceExhibit = orderedAnimalList_IDs.get(index);
            goalExhibit = orderedAnimalList_IDs.get(index+1);
            for(String child : orderedAnimalList_child.get(index+1)){
                endText += child;
            }

            path = DirectionHelper.findPathBetween(sourceExhibit,goalExhibit);
            DirectionHelper.saveDirectionsInformation(this, order, true);
        }
        else{
            sourceExhibit = orderedAnimalList_IDs.get(index);
            goalExhibit = orderedAnimalList_IDs.get(index-1);
            for(String child : orderedAnimalList_child.get(index-1)){
                endText += child;
            }
            path = DirectionHelper.findPathBetween(sourceExhibit,goalExhibit);
            DirectionHelper.saveDirectionsInformation(this, order, false);
        }
        setDisplay(sourceExhibit, goalExhibit, path);

        String startText = "From: " + DirectionHelper.getNodeName(sourceExhibit);
        Log.d("endText3",endText);
        start.setText(startText);
        end.setText(endText);
        distance.setText(Double.toString(DirectionHelper.totalDistance(path)) + " ft");

//        List<String> pathDisplay = new ArrayList<>(DirectionHelper.briefPath(path,goalExhibit));;
//        direction_adapter.setDirectionsStringList(pathDisplay);


        //setting next button and next direction distance
        //disable the prev btn at the first page and next btn at last page.
        if (going_forward) {
            // next button
            if (index < orderedAnimalList_Names.size() - 2) {
                nextBtn.setEnabled(true);
                String nextSource = orderedAnimalList_IDs.get(index + 1);
                String nextGoal = orderedAnimalList_IDs.get(index + 2);
                List<IdentifiedWeightedEdge> nextPath = DirectionHelper.findPathBetween(nextSource, nextGoal);
                double nextDistance = DirectionHelper.totalDistance(nextPath);
                String nextGoalName = orderedAnimalList_Names.get(index + 2);
                String nextText = (nextGoalName + "  " + nextDistance + " ft");
                next.setText(nextText);
            }
            else {
                nextBtn.setEnabled(false);
                next.setText("End of tour");
            }
            //setting previous button and previous direction distance
            if (index == 0) {
                prevBtn.setEnabled(false);
                prev.setText("Beginning of tour");
            }
            else {
                prevBtn.setEnabled(true);
                String current = orderedAnimalList_IDs.get(index);
                String lastSource = orderedAnimalList_IDs.get(index - 1);
                List<IdentifiedWeightedEdge> prevPath = DirectionHelper.findPathBetween(current, lastSource);
                double prevDistance = DirectionHelper.totalDistance(prevPath);
                String lastSourceName = orderedAnimalList_Names.get(index - 1);
                String prevText = (lastSourceName + "  " + prevDistance + " ft");
                prev.setText(prevText);
            }
        }
        else{
            //next button
            nextBtn.setEnabled(true);
            String nextSource = orderedAnimalList_IDs.get(index - 1);
            String nextGoal = orderedAnimalList_IDs.get(index);
            List<IdentifiedWeightedEdge> nextPath = DirectionHelper.findPathBetween(nextSource, nextGoal);
            double nextDistance = DirectionHelper.totalDistance(nextPath);
            String nextGoalName = orderedAnimalList_Names.get(index);
            String nextText = (nextGoalName + "  " + nextDistance + " ft");
            next.setText(nextText);

            // previous button
            if (index == 1) {
                prevBtn.setEnabled(false);
                prev.setText("Beginning of tour");
            }
            else {
                prevBtn.setEnabled(true);
                String current = orderedAnimalList_IDs.get(index-1);
                String lastSource = orderedAnimalList_IDs.get(index - 2);
                List<IdentifiedWeightedEdge> prevPath = DirectionHelper.findPathBetween(current, lastSource);
                double prevDistance = DirectionHelper.totalDistance(prevPath);
                String lastSourceName = orderedAnimalList_Names.get(index - 2);
                String prevText = (lastSourceName + "  " + prevDistance + " ft");
                prev.setText(prevText);
            }
        }
    }

    public void onNextButtonClick(View view) {
        order++;
        if (!going_forward){
            order-=2;
        }
        going_forward = true;
        if (order < planned_route.size()+1) {
            display(order, true);
        } else {
            order = planned_route.size() ;
            display(order, true);
            Utilities.showAlert(this, "invalid Action");
        }
        int a =1;
    }

    public void onBackButtonClick(View view) {
        order--;
        if (going_forward){
            order+=2;
        }
        going_forward = false;
        if (order >= 0) {
            if (order >= orderedAnimalList_IDs.size()){
                order = orderedAnimalList_IDs.size()-1;
            }
            display(order, false);
        } else {
            order = 0;
            display(order, false);
            Utilities.showAlert(this, "invalid Action");
        }
        int a =1;
    }




    @Override
    public void OnLocationChange(LatLng current) {
        //ZooData.VertexInfo closestlandmark = AnimalUtilities.getClosestLandmark(current);
        if (AnimalUtilities.check_off_route(order,planned_route,current)){
            boolean user_want_update = true;
            if (user_want_update) {
                planned_route = AnimalUtilities.reroute(order, planned_route, current);

                //pop out a notification window

                //save to SharedPreferences
                List<String> animal_strings = new ArrayList<>();
                for (route_node myRoute_node : planned_route) {
                    String myAnimal = myRoute_node.exhibit.name;
                    animal_strings.add(myAnimal);
                }
                SharedPreferences sharedPreferences = getSharedPreferences("Team57", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String joined = String.join(",", animal_strings);
                editor.putString("route", joined);
                editor.commit();
                editor.apply();

                //apply changes to display?
            }
        }


    }

    public void onClearButtonClick(View view) {
        /*
        zooRoute.clear();
        String startExhibit = DirectionHelper.getNodeName(currentLocation);
        String goalExhibit = DirectionHelper.getNodeName("entrance_exit_gate");
        List<IdentifiedWeightedEdge> path = DirectionHelper.findPathBetween(startExhibit,goalExhibit);
        List<String> paths = DirectionHelper.detailPath(path, startExhibit);
        List<String> briefPaths = DirectionHelper.briefPath(path, startExhibit);
        Double distance = DirectionHelper.totalDistance(path);
        //temp
        DirectionData walk = new DirectionData(startExhibit, goalExhibit, distance, paths, paths, briefPaths, briefPaths);
        zooRoute.put(0, walk);
        display(0,true);
         */
        clearRoute();
        Utilities.clearSavedAnimalItem(this);
        intent = new Intent(this, SearchAnimalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }

    public static void clearRoute(){
        orderedAnimal.clear();
    }

    // Direction Display
    public void OnSettingDisplayClick(View view) {
        saveDisplayStatus();
        setDirectionTextDisplay();
        display(order, isNext);
    }

    public void setDirectionTextDisplay() {
        if (displayStatus) {
            detailBtn.setText("DETAIL");
        }
        else {
            detailBtn.setText("BRIEF");
        }
    }

    public void setDisplay(String source, String goal, List<IdentifiedWeightedEdge> path) {
        if (displayStatus) {
            displayBrief(source, goal, path);
        } else {
            displayDetail(source, goal, path);
        }
    }

    public void displayDetail(String source, String goal, List<IdentifiedWeightedEdge> path){
        List<String> pathDisplay = DirectionHelper.detailPath(path,source);
        direction_adapter.setDirectionsStringList(pathDisplay);
    }

    public void displayBrief(String source, String goal, List<IdentifiedWeightedEdge> path){
        List<String> pathDisplay = DirectionHelper.briefPath(path,source);
        direction_adapter.setDirectionsStringList(pathDisplay);
    }

    public void loadDisplayStatus() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        displayStatus = preferences.getBoolean("DISPLAYSTATUS", true);
    }
    public void saveDisplayStatus() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("DISPLAYSTATUS", !displayStatus);
        displayStatus = !displayStatus;
        editor.apply();
    }


    public void GPSButtonClick(View view){
        // If GPS is enabled, then update the model from the Location service.
        useLocationService = true;
        var permissionChecker = new LocationPermissionChecker(this);
        permissionChecker.ensurePermissions();

        var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        var provider = LocationManager.GPS_PROVIDER;
        viewModel.addLocationProviderSource(locationManager, provider);
        viewModel.getLastKnownCoords().observe(this, (coord) -> {
            Log.i(TAG, String.format("Observing location model update to %s", coord));
        });
    }


    public void mockASinglePoint(Coord singlePoint){
        viewModel.mockLocation(singlePoint);
        viewModel.getLastKnownCoords().observe(this, (coord) -> {
            Log.i(TAG, String.format("Observing location model update to %s", coord));
        });
/*
        viewModel.getLastKnownCoords().observe(this, new Observer<Coord>() {
            @Override
            public void onChanged(Coord coord) {
                Log.i(TAG, String.format("Observing location model update to %s", coord));
            }
        });

 */
    }

    public void mockAListOfPoints(List<Coord> route){
        viewModel.mockRoute(route, 500, TimeUnit.MILLISECONDS);
        viewModel.getLastKnownCoords().observe(this, (coord) -> {
            Log.i(TAG, String.format("Observing location model update to %s", coord));
        });
    }

    public void onMockButtonClick(View view) throws IOException {
        //use mocking location
        //this.useLocationService = getIntent().getBooleanExtra(EXTRA_USE_LOCATION_SERVICE, false);

        //---------------mocking for test--------------------------------------------------
        List<Coord> TenPoints = Coords.getTenPointsInLine("Siamangs", "Orangutans");
        Coord point_near_start = TenPoints.get(2);
        Coord point_near_goal = TenPoints.get(7);

        mockASinglePoint(point_near_start);

        mockASinglePoint(point_near_goal);

        //-------------------uncomment these lines when demo----------------------------------
        /*
        useLocationService = false;
        InputStream input = this.getAssets().open(MOCKING_FILE_NAME);
        List<Coord> Coords = ZooData.loadMockingJSON(input);
        if (route.size() == 1){
            mockASinglePoint(route.get(0));
        }
        else{
            mockAListOfPoints(route);
        }
         */
        //------------------------------------------------------------------------------
    }
}
