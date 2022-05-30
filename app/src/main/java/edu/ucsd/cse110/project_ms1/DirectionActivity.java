package edu.ucsd.cse110.project_ms1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import edu.ucsd.cse110.project_ms1.location.Coord;
import edu.ucsd.cse110.project_ms1.location.Coords;
import edu.ucsd.cse110.project_ms1.location.LocationModel;
import edu.ucsd.cse110.project_ms1.location.LocationModelFactory;
import edu.ucsd.cse110.project_ms1.location.LocationPermissionChecker;

public class DirectionActivity extends AppCompatActivity implements OnLocationChangeListener {
    private static Context mContext;
    int order;
    private boolean useLocationService;
    boolean displayStatus;

    static boolean going_forward = true; // which direction of the user is going: forward/backward
    Button detailBtn;
    Button skipBtn ;
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

    private static final String TAG = "Location6666666";
    public static final String MOCKING_FILE_NAME = "mocking_location.json";
    public static final String EXTRA_USE_LOCATION_SERVICE = "use_location_updated";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        //retain the DirectionActivity
        Utilities.changeCurrentActivity(this, "DirectionActivity");
        AnimalUtilities.loadZooInfo(this);

        // intialize button status
        going_forward = true;
        skipBtn= findViewById(R.id.skip_button);


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
        planned_route = AnimalItem.plan_route(animalItems, "entrance_exit_gate", false);
        //List<DirectionData> orderedAnimalList = DirectionHelper.routeNode_to_DirectionData(planned_route);


        populate_lists();




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
        viewModel = ViewModelProviders.of(this,
                new LocationModelFactory(this.getApplication(), this,
                        this)).get(LocationModel.class);
        //intialize current location to be at the entrance gate
        mockASinglePoint(AnimalItem.getExtranceGateCoord());
        viewModel.getLastKnownCoords().observe(this, (coord) -> {
            Log.i(TAG, String.format("Observing location model update to %s", coord));
        });
        Coord current_lastKnownPoint = viewModel.getCurrentCoord();
        //use physical real location
        useLocationService = true;


        //Get the order and going_forward
        List<String> retainedInfo = DirectionHelper.loadDirectionsInformation(this);
        order = Integer.valueOf(retainedInfo.get(0));
        going_forward = Boolean.valueOf(retainedInfo.get(1));

        // display status
        loadDisplayStatus();
        display(order, going_forward);

    } //Initial End

    //populate lists that are used for display
    private void populate_lists() {
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
    }

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

        //set the directions text
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
        setDisplay(sourceExhibit, goalExhibit, path, false);

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
            //click button
            if (order >= orderedAnimalList_Names.size() - 1){
                skipBtn.setEnabled(false);
            }else{
                skipBtn.setEnabled(true);
            }

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

            prevBtn.setEnabled(true);
            String current = orderedAnimalList_IDs.get(index);

            if (index<1) { // if coming from original
                prev.setText("Beginning of tour");
            }
            else{ // if coming from original
                String lastSource = orderedAnimalList_IDs.get(index - 1);
                List<IdentifiedWeightedEdge> prevPath = DirectionHelper.findPathBetween(current, lastSource);
                double prevDistance = DirectionHelper.totalDistance(prevPath);
                String lastSourceName = orderedAnimalList_Names.get(index - 1);
                String prevText = (lastSourceName + "  " + prevDistance + " ft");
                prev.setText(prevText);
            }

        }
        else{
            //click button
            if (order <=  1){
                skipBtn.setEnabled(false);
            }else{
                skipBtn.setEnabled(true);
            }

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
                skipBtn.setEnabled(true);
                String current = orderedAnimalList_IDs.get(index-1);
                String lastSource = orderedAnimalList_IDs.get(index - 2);
                List<IdentifiedWeightedEdge> prevPath = DirectionHelper.findPathBetween(current, lastSource);
                double prevDistance = DirectionHelper.totalDistance(prevPath);
                String lastSourceName = orderedAnimalList_Names.get(index - 2);
                String prevText = (lastSourceName + "  " + prevDistance + " ft");
                prev.setText(prevText);
            }
        }
        int a =2;
    }
    //act when next button is clicked
    public void onNextButtonClick(View view) {
        order++;

        if (!going_forward){
            order-=2;
        }

        //grey out skip if going to the exit
//        if (order+1==animalItems.size())
//            skipBtn.setEnabled(false);
//        else skipBtn.setEnabled(true);

        going_forward = true;
        if (order < planned_route.size()+1) {
            display(order, true);
        } else {
            order = planned_route.size() ;
            display(order, true);
            Utilities.showAlert(this, "Invalid Action");
        }
    }

    ////act when previous button is clicked
    public void onBackButtonClick(View view) {
        order--;
        if (going_forward){
            order+=2;
        }

//        //grey out skip if going to the exit
//        if (order-1==0)
//            skipBtn.setEnabled(false);
//        else skipBtn.setEnabled(true);

        going_forward = false;
        if (order >= 0) {
            if (order >= orderedAnimalList_IDs.size()){
                order = orderedAnimalList_IDs.size()-1;
            }
            display(order, false);
        } else {
            order = 0;
            display(order, false);
            Utilities.showAlert(this, "Invalid Action");
        }
        int a =1;
    }



    //act when currentlocation is changed
    @Override
    public void OnLocationChange(Coord current) {
        boolean isOffRoute = AnimalUtilities.check_off_route(order, planned_route, current.toLatLng());
        if (isOffRoute){
            showReplanAlert(this, current);
        }
        else{
            updateRoute(this.order, this.going_forward, current, orderedAnimalList_IDs);
        }
    }

    //pop out a window that  ask user whether replan
    public void showReplanAlert(Activity activity, Coord currentCoord){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        String message = "Do you want to replan the route?";
        alertBuilder.setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, id)->{
                    replan_and_save_status(currentCoord);
                    Utilities.showAlert(this, "The route is replanned.");
                    display(order, going_forward);
                })
                .setNegativeButton("No",(dialog,id)->{
                    updateRoute(this.order, this.going_forward, currentCoord, orderedAnimalList_IDs);
                })
                .setCancelable(true);
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public void replan_and_save_status(Coord current) {
        planned_route = AnimalUtilities.reroute(order, planned_route, current.toLatLng(), going_forward);

        populate_lists();
        //apply changes to display


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
    }

    //act when clear button is clicked
    public void onClearButtonClick_DirectionActivity(View view) {
        clearRoute();
        Utilities.clearSharedPreference(this);
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
        display(order, going_forward);
    }

    //switch between detail and brief
    public void setDirectionTextDisplay() {
        if (displayStatus) {
            detailBtn.setText("DETAIL");
        }
        else {
            detailBtn.setText("BRIEF");
        }
    }
    //set the directions in which version
    public void setDisplay(String source, String goal, List<IdentifiedWeightedEdge> path, boolean NeedUpdate) {
        //brief version
        if (displayStatus) {
            displayBrief(source, goal, path, NeedUpdate);
        }
        //detailed version
        else {
            displayDetail(source, goal, path, NeedUpdate);
        }
    }
    //set the directions in detail version
    public void displayDetail(String source, String goal, List<IdentifiedWeightedEdge> path, boolean NeedUpdate){
        List<String> pathDisplay = new ArrayList<>();
        if (path.size() == 1){
            String edgeInfo = getSingleEdgeInfo(path);
            pathDisplay.add(0, edgeInfo);
        }
        else{
            if (NeedUpdate){
                //construct inserted edge info
                IdentifiedWeightedEdge insertion = path.remove(0);
                pathDisplay = DirectionHelper.detailPath(path,source);
                String edgeInfo = getInsertionEdgeInfo(path, insertion);
                pathDisplay.add(0, edgeInfo);
                //show alert
                String street =AnimalItem.vInfo.get(goal).name;
                showUpdateAlert(street);
            }
            else{
                pathDisplay = DirectionHelper.detailPath(path,source);
            }
        }
        direction_adapter.setDirectionsStringList(pathDisplay);
    }


    //set the directions in brief version
    public void displayBrief(String source, String goal, List<IdentifiedWeightedEdge> path, boolean NeedUpdate){
        List<String> pathDisplay = new ArrayList<>();
        if (path.size() == 1){
            String edgeInfo = getSingleEdgeInfo(path);
            pathDisplay.add(0, edgeInfo);
        }
        else{
            if (NeedUpdate){
                //construct inserted edge info
                IdentifiedWeightedEdge insertion = path.remove(0);
                pathDisplay = DirectionHelper.detailPath(path,source);
                String edgeInfo = getInsertionEdgeInfo(path, insertion);
                pathDisplay.add(0, edgeInfo);
                //show alert
                String street =AnimalItem.vInfo.get(goal).name;
                showUpdateAlert(street);
            }
            else{
                pathDisplay = DirectionHelper.briefPath(path,source);
            }
        }
        direction_adapter.setDirectionsStringList(pathDisplay);
    }

    private void showUpdateAlert(String street) {
        String reminder = "The direction instruction to \"" + street + "\" has been updated.";
        Utilities.showAlert(this, reminder);
    }

    @NonNull
    private String getInsertionEdgeInfo(List<IdentifiedWeightedEdge> path, IdentifiedWeightedEdge insertion) {
        double distance = AnimalItem.gInfo.getEdgeWeight(insertion);
        String street = AnimalItem.eInfo.get(insertion.getId()).street;
        String insertion_goal = AnimalItem.gInfo.getEdgeSource(path.get(0));
        String target = AnimalItem.vInfo.get(insertion_goal).name;
        String edgeInfo = "Proceed on \"" + street + "\" " + distance + " ft towards \"" + target + "\"";
        return edgeInfo;
    }

    @NonNull
    private String getSingleEdgeInfo(List<IdentifiedWeightedEdge> path){
        String street = AnimalItem.eInfo.get(path.get(0).getId()).street;
        String target = null;
        double distance = 999999999.0;
        ZooData.VertexInfo singleEdge_source = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeSource(path.get(0)));
        ZooData.VertexInfo singleEdge_goal = AnimalItem.vInfo.get(AnimalItem.gInfo.getEdgeTarget(path.get(0)));
        Coord singleEdge_source_coord = new Coord(singleEdge_source.lat, singleEdge_source.lng);
        Coord singleEdge_goal_coord = new Coord(singleEdge_goal.lat, singleEdge_goal.lng);
        double Source_distance = AnimalItem.distance_between_coords(Coords.currentLocationCoord, singleEdge_source_coord);
        double Goal_distance = AnimalItem.distance_between_coords(Coords.currentLocationCoord, singleEdge_goal_coord);
        if (Source_distance <= Goal_distance){
            distance = Source_distance;
            target = singleEdge_source.name;
        }
        else{
            distance = Goal_distance;
            target = singleEdge_goal.name;
        }
        String edgeInfo = "Proceed on \"" + street + "\" " + distance + " ft towards \"" + target + "\"";
        return edgeInfo;
    }



    //load displaying status brief/detain from shared preference
    public void loadDisplayStatus() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        displayStatus = preferences.getBoolean("DISPLAYSTATUS", true);
    }

    //save displaying status brief/detain from shared preference
    public void saveDisplayStatus() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("DISPLAYSTATUS", !displayStatus);
        displayStatus = !displayStatus;
        editor.apply();
    }

    //get the context
    public static Context getContext() {
        return mContext;
    }

    //act when use GPS
    public void onGPSButtonClick(View view){
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

    //mock a single point
    public void mockASinglePoint(Coord singlePoint){
        viewModel.mockLocation(singlePoint);
//        Coords.currentLocationCoord = singlePoint;
//        LatLngs.currentLocationLatLng = singlePoint.toLatLng();
    }

    //mock a list of points
    public Future<?> mockAListOfPoints(List<Coord> route){
        Future<?> myfuture = viewModel.mockRoute(this, route, 500, TimeUnit.MILLISECONDS);
        return myfuture;
    }

    //act when mock button is clicked
    public void onMockButtonClick(View view) throws IOException {
        //use mocking location
        //this.useLocationService = getIntent().getBooleanExtra(EXTRA_USE_LOCATION_SERVICE, false);
        useLocationService = false;
        //---------------mocking for test--------------------------------------------------
        //Step 1: Create a mocking point
        // create your own Coord manually
        Coord koi_fish_coord = new Coord(32.72109826903826, -117.15952052282296);
        
        //Another way to create a Coord automatically
        //get 10 evenly spaced points in the line between "start" and "goal" (include "start" and "goal")
        // "start" and "goal" must be the Name of landmark
        List<Coord> TenPoints = Coords.getTenPointsInLine("Siamangs", "Orangutans");
        //change the indexes as you wish
        Coord point_near_start = TenPoints.get(2);
        Coord point_near_goal = TenPoints.get(7);


        //Step 2.1: call mockASinglePoint function
        mockASinglePoint(koi_fish_coord);
        //mockASinglePoint(point_near_start);
        //mockASinglePoint(point_near_goal);

        //Step 2.2: call mockAListOfPoints function
        //mockAListOfPoints(TenPoints);

        //Step 3: check if Coords.currentCoord updates
        if (Coords.currentLocationCoord.equals(koi_fish_coord)){
            Log.d("koi_fish_coord", "Yes");
        }

        //-------------------uncomment these lines when demo----------------------------------
        /*
        useLocationService = false;
        InputStream input = this.getAssets().open(MOCKING_FILE_NAME);
        List<Coord> route = ZooData.loadMockingJSON(input);
        if (route.size() == 1){
            mockASinglePoint(route.get(0));
        }
        else{
            mockAListOfPoints(route);
        }
         */
        //------------------------------------------------------------------------------
    }

    //directly replan the route
    public void onReplanButtonClick(View view) {
        Coord current = Coords.currentLocationCoord;
        replan_and_save_status(current);
        display(order, going_forward);
    }

    //act when Skip button is click
    public void OnSkipClick(View view) {

        //determine which one to remove
        skip(this.going_forward,this.order,
                orderedAnimalList_Names,orderedAnimalList_IDs,orderedAnimalList_child, planned_route);

        if (!going_forward) order--;//because one less exhibit before

        Coord current = Coords.currentLocationCoord;
        replan_and_save_status(current);


        //skip last exihibit and go to gate
        if ( going_forward && order >= orderedAnimalList_Names.size() - 2){
            skipBtn.setEnabled(false);
            Utilities.showAlert(this, "You are back to \"Entrance and Exit Gate\".");
            updateRoute(order, going_forward, Coords.currentLocationCoord, orderedAnimalList_IDs);
            order--;
        }
        //skip first exhibit and go back to gate
        else if (!going_forward && order <= 1){
            skipBtn.setEnabled((false));
            Utilities.showAlert(this, "You are back to \"Entrance and Exit Gate\".");
            updateRoute(1, going_forward, Coords.currentLocationCoord, orderedAnimalList_IDs);
            order++;
        }
        //normal
        else{
            Utilities.showAlert(this, "The route has been replanned.");
            display(order, going_forward);
        }
    }

    //skip the current animal
    public static void skip(boolean going_forward, int order, List<String> a1, List<String> a2, List<List<String>> a3, List<route_node> route_nodeList) {
        int index_remove;
        if (going_forward) index_remove=order+1;
        else index_remove = order-1;
        a1.remove(index_remove);
        a2.remove(index_remove);
        a3.remove(index_remove);
        route_nodeList.remove(index_remove-1);
    }

    public void updateRoute(int order, boolean going_forward, Coord current, List<String> orderedAnimalList_IDs) {
        List<IdentifiedWeightedEdge> updatePath = new LinkedList<>();
        String goalExhibit_id = null;
        //get the goal exhibit
        if (going_forward){
            goalExhibit_id = orderedAnimalList_IDs.get(order + 1);
        }
        else{
            order--;
            goalExhibit_id = orderedAnimalList_IDs.get(order);
        }
        //get the closestLandmark
        AnimalItem closestLandmark = AnimalItem.getClosestLandmark(current);
        //get the path between closest landmark to goal exhibit
        if (!closestLandmark.name.equals(goalExhibit_id)){
            updatePath = DirectionHelper.findPathBetween(closestLandmark.id, goalExhibit_id);
        }
        //get the current street
        IdentifiedWeightedEdge currentStreet = DirectionHelper.findCurrStreet(closestLandmark.id,
                current.toLatLng());
        //add the insertion street in front
        List<IdentifiedWeightedEdge> updated_path = new ArrayList<>(updatePath);
        updated_path.add(0, currentStreet);
        //display updated directions
        setDisplay(closestLandmark.name, goalExhibit_id, updated_path, true);
    }


}
