package edu.ucsd.cse110.project_ms1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import edu.ucsd.cse110.project_ms1.location.Coord;
import edu.ucsd.cse110.project_ms1.location.Coords;
import edu.ucsd.cse110.project_ms1.location.LocationModel;
import edu.ucsd.cse110.project_ms1.location.LocationModelFactory;

public class DirectionActivity extends AppCompatActivity implements Serializable,
        OnLocationChangeListener{
    private static Context mContext;
    int order;
    private boolean useLocationService;
    boolean displayStatus;
    // which direction of the user is going: forward/backward
    static boolean going_forward = true;
    Button detailBtn;
    Button skipBtn ;
    Button prevBtn ;
    Button nextBtn ;
    Intent intent;
    LocationModel viewModel;

    public static ArrayList<String> orderedAnimal;
    DirectionAdapter direction_adapter;
    RecyclerView direction_recyclerView;
    List<String> orderedAnimalList_Names;
    List<String> orderedAnimalList_IDs;
    List<String> orderedAnimalList_child;
    List<AnimalItem> animalItems;
    List<route_node> planned_route;

    private static final String TAG = "Location6666666";
    private static final int ACTIVITY_CONSTANT = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        //retain the DirectionActivity
        Utilities.changeCurrentActivity(this, "DirectionActivity");
        AnimalUtilities.loadZooInfo(this);


        //use physical real location
        useLocationService = true;
        //-------------------Comment this line when demo-------------------------------------------
        useLocationService = false;
        //-----------------------------------------------------------------------------------------


        // intialize  status
        going_forward = true;
        //bind the  buttons
        skipBtn= findViewById(R.id.skip_button);
        prevBtn = findViewById(R.id.previous_button);
        nextBtn = findViewById(R.id.next_button);

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
        int size = orderedAnimal.size();
        int k=0;
        for (int i=0; i<size; i++){

            if (orderedAnimal.get(k).equals("Entrance and Exit Gate")) {
                orderedAnimal.remove(k);
                k--;
            }
            k++;
        }
//        orderedAnimal.remove(orderedAnimal.size() - 1);
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


        //Get the order and going_forward
        List<String> retainedInfo = DirectionHelper.loadDirectionsInformation(this);
        order = Integer.valueOf(retainedInfo.get(0));
        going_forward = Boolean.valueOf(retainedInfo.get(1));

        // display status
        loadDisplayStatus();
        display(order, going_forward);
        setClosestLandmarkText();

    } //Initial End



    //populate lists that are used for display
    private void populate_lists() {
        orderedAnimalList_Names = new ArrayList<>();
        orderedAnimalList_IDs = new ArrayList<>();
        orderedAnimalList_child = new ArrayList<>();
        for(route_node node : planned_route){
            orderedAnimalList_Names.add(node.exhibit.name);
            orderedAnimalList_IDs.add(node.exhibit.id);
            orderedAnimalList_child.add(node.get_concat_names());

        }

        //we need add the front gate into orderedAnimalList, so that route begin at gate
        orderedAnimalList_Names.add(0, "Entrance and Exit Gate");
        orderedAnimalList_IDs.add(0, "entrance_exit_gate");
        orderedAnimalList_child.add(0, "Entrance and Exit Gate");
//        Log.d("orderedAnimalList_Names",orderedAnimalList_Names.toString());
    }

    public void display(int index, boolean isGoingForward) {
        TextView next = findViewById(R.id.next_text);
        TextView prev = findViewById(R.id.previous_text);
        Button mockBtn = findViewById(R.id.enter_button);
        detailBtn = findViewById(R.id.detail_button);

        //set the directions text
        setDirectionTextDisplay();

        String source_id;
        String goal_id;
        List<IdentifiedWeightedEdge> path;
        Log.v("indexDisplay", " "+index+" ");
        Log.v("sizeDisplay", " "+orderedAnimalList_IDs.size()+" ");

        //Set the sourceExibit and "To"
        String endText = null;
        if (isGoingForward){
            source_id = orderedAnimalList_IDs.get(index);
            goal_id = orderedAnimalList_IDs.get(index+1);
            endText = orderedAnimalList_child.get(index+1);

            path = DirectionHelper.findPathBetween(source_id,goal_id);
            DirectionHelper.saveDirectionsInformation(this, order, true);
        }
        else{
            source_id = orderedAnimalList_IDs.get(index);
            goal_id = orderedAnimalList_IDs.get(index-1);
            endText = orderedAnimalList_child.get(index-1);
            path = DirectionHelper.findPathBetween(source_id,goal_id);
            DirectionHelper.saveDirectionsInformation(this, order, false);
        }
        setDisplay(source_id, goal_id, path, displayStatus, false);
        setToText(endText);


    //setting next button and next direction distance
        //disable the prev btn at the first page and next btn at last page.
        if (going_forward) {
            //click button

            // next button
            if (index < orderedAnimalList_Names.size() - 2) {
                nextBtn.setEnabled(true);
                skipBtn.setEnabled(true);
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
                skipBtn.setEnabled(false);
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
                skipBtn.setEnabled(false);
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




        //----------------Comment these line when demo, this is ony used for mocking mode-----------
        if (!useLocationService){
            //plug in the current path order
            autoUpdate_currentLocation_mocking(order);
        }
        //----------------------------------------------------------------------------------------




        if (order < planned_route.size() + 1) {
            boolean needUpdate = isNeedUpdate(order);
            if (needUpdate){
                updateRoute(order, going_forward, Coords.currentLocationCoord, orderedAnimalList_IDs);
            }
            else{
                display(order, true);
            }
        }
        //in case we have bug
        else {
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



        //----------------Comment these line when demo, this is ony used for mocking mode-----------
        if (!useLocationService){
            //plug in the current path order
            autoUpdate_currentLocation_mocking(order);
        }
        //----------------------------------------------------------------------------------------



        if (order >= 0) {
            int order_change = order;
//            if (order >= orderedAnimalList_IDs.size()){
//                order_change = orderedAnimalList_IDs.size()-1;
//            }
            boolean needUpdate = isNeedUpdate(order_change);
            if (needUpdate){
                updateRoute(order, going_forward, Coords.currentLocationCoord, orderedAnimalList_IDs);
            }
            else{
                display(order, false);
            }
        }
        //in case we have bug
        else {
            order = 0;
            display(order, false);
            Utilities.showAlert(this, "Invalid Action");
        }

    }

    //update current location for mocking mode only
    public void autoUpdate_currentLocation_mocking(int order){
        //String goal_id = (going_forward) ? orderedAnimalList_IDs.get(order + 1): orderedAnimalList_IDs.get(order);
        String goal_id = orderedAnimalList_IDs.get(order);
        Coords.curr_loc_id = goal_id;
        Coord updateCoord = getParentCoord_byID(goal_id);
        Coords.currentLocationCoord = updateCoord;
        setClosestLandmarkText();
    }

    private void setClosestLandmarkText() {
        //set "Closest Landmark"
        String ClosestLandMark_name = AnimalItem.getClosestLandmark(Coords.currentLocationCoord).get(1);
        String startText = "Closest Landmark: " + ClosestLandMark_name;
        TextView start = findViewById(R.id.start_exhibit_name);
        start.setText(startText);
    }

    private void setToText(String ToExhibit) {
        String endText = "To: " + ToExhibit;
        TextView end = findViewById(R.id.goal_exhibit_name);
        end.setText(endText);
    }


    //act when currentlocation is changed
    @Override
    public void OnLocationChange(Coord current) {
        //update currentLocationCoord
        Coords.curr_loc_id = AnimalItem.getClosestLandmark(current).get(0);
        Coord nearestLandmark_coord = getParentCoord_byID(Coords.curr_loc_id);
        Coords.currentLocationCoord = nearestLandmark_coord;
        LatLngs.currentLocationLatLng = nearestLandmark_coord.toLatLng();
        setClosestLandmarkText();
        //check is off_route
        boolean isOffRoute = AnimalUtilities.check_off_route(order, planned_route, Coords.currentLocationCoord.toLatLng());
        if (isOffRoute){
            showReplanAlert(this, Coords.currentLocationCoord);
        }
        else{
            boolean needUpdate = isNeedUpdate(order);
            //if direction need update
            if (needUpdate){
                updateRoute(this.order, this.going_forward, Coords.currentLocationCoord, orderedAnimalList_IDs);
            }
            //follow the normal instruction
            else{
                display(order, going_forward);
            }
        }
    }

    @NonNull
    public static Coord getParentCoord_byID(String landmark_id) {
        String nearestLandmark_parent_id = AnimalItem.Latlng_ids_Map.get(landmark_id);
        ZooData.VertexInfo nearestLandmark_parent = AnimalItem.vInfo.get(nearestLandmark_parent_id);
        return new Coord(nearestLandmark_parent.lat, nearestLandmark_parent.lng);
    }

    //compare current location and path source, check if direction need update
    private boolean isNeedUpdate(int order) {
        Coord current = Coords.currentLocationCoord;
        String CurrentPathSource_id = orderedAnimalList_IDs.get(order);
        Coord CurrentPathSource_coord = getParentCoord_byID(CurrentPathSource_id);
        boolean needUpdate = !CurrentPathSource_coord.equals(current);
        return needUpdate;
    }


    //pop out a window that  ask user whether replan
    public void showReplanAlert(Activity activity, Coord currentCoord){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        String message = "Do you want to replan the route?";
        alertBuilder.setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, id)->{
                    replan_and_save_status(currentCoord);
                    String goalExhibit = (going_forward)?
                            orderedAnimalList_child.get(order + 1): orderedAnimalList_child.get(order - 1);
                    setToText(goalExhibit);
                    Utilities.showAlert(this, "The route is replanned.");
                    updateRoute(this.order, this.going_forward, currentCoord, orderedAnimalList_IDs);
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
            for (String myAnimal : myRoute_node.names)
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
        intent = new Intent();
        setResult(Activity.RESULT_CANCELED,intent);
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
    public void setDisplay(String source_id, String goal_id, List<IdentifiedWeightedEdge> path,
                           boolean displayStatus, boolean NeedUpdate) {
        //brief version
        if (displayStatus) {
            displayBrief(source_id, goal_id, path, NeedUpdate);
        }
        //detailed version
        else {
            displayDetail(source_id, goal_id, path, NeedUpdate);
        }
    }
    //set the directions in detail version
    public void displayDetail(String source_id, String goal_id, List<IdentifiedWeightedEdge> path, boolean NeedUpdate){
        List<String> pathDisplay = new ArrayList<>();
        Double updateDistance = 0.0;
        if (path.size() == 0){
            pathDisplay.add("You have already been there. No need to move.");
        }
        else{
            if (NeedUpdate){
                //show update Alert
                String nextExhibit_name = AnimalItem.vInfo.get(goal_id).name;
                DirectionHelper.showUpdateAlert(this, nextExhibit_name);
            }
            pathDisplay = DirectionHelper.detailPath(path, source_id);
            //set the Total Distance
            updateDistance = DirectionHelper.totalDistance(path);
            showUpdateTotalDistance(updateDistance);
        }
        direction_adapter.setDirectionsStringList(pathDisplay);
    }


    //set the directions in brief version
    public void displayBrief(String source_id, String goal_id, List<IdentifiedWeightedEdge> path, boolean NeedUpdate){
        List<String> pathDisplay = new ArrayList<>();
        Double updateDistance = 0.0;
        if (path.size() == 0){
            pathDisplay.add("You have already been there. No need to move.");
        }
        else {
            if (NeedUpdate){
                //show update Alert
                String nextExhibit_name = AnimalItem.vInfo.get(goal_id).name;
                DirectionHelper.showUpdateAlert(this, nextExhibit_name);
            }
            pathDisplay = DirectionHelper.briefPath(path, source_id);
            //set the Total Distance
            updateDistance = DirectionHelper.totalDistance(path);
            showUpdateTotalDistance(updateDistance);
        }
        direction_adapter.setDirectionsStringList(pathDisplay);
    }

    //show update total distance
    private void showUpdateTotalDistance(Double updateDistance) {
        TextView totalDistance = findViewById(R.id.path_total_distance);
        totalDistance.setText(Double.toString(updateDistance) + " ft");
    }

    //load displaying status brief/detain from shared preference
    public void loadDisplayStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        displayStatus = sharedPreferences.getBoolean("currentDisplayStatus", true);
    }

    //save displaying status brief/detain from shared preference
    public void saveDisplayStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("currentDisplayStatus", !displayStatus);
        displayStatus = !displayStatus;
        editor.commit();
        editor.apply();
    }

    //get the context
    public static Context getContext() {
        return mContext;
    }

    //act when use GPS
    public void onGPSButtonClick(View view){
        // If GPS is enabled, then update the model from the Location service.
//        useLocationService = true;
//        var permissionChecker = new LocationPermissionChecker(this);
//        permissionChecker.ensurePermissions();
//
//        var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        var provider = LocationManager.GPS_PROVIDER;
//        viewModel.addLocationProviderSource(locationManager, provider);
//        viewModel.getLastKnownCoords().observe(this, (coord) -> {
//            Log.i(TAG, String.format("Observing location model update to %s", coord));
//        });

        Log.i("order",Integer.toString(order));
        Log.i("route status", String.join(", ", orderedAnimalList_IDs));
        Log.i("curr position", Coords.curr_loc_id);
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


    public void OnMockChange(Coord entered_coord) {
        //use mocking location
        //this.useLocationService = getIntent().getBooleanExtra(EXTRA_USE_LOCATION_SERVICE, false);
        useLocationService = false;
        mockASinglePoint(entered_coord);

        //---------------comment when demo--------------------------------------------------
        /*
        //Step 1: Create a mocking point
        // create your own Coord manually
        Coord koi_fish_coord = new Coord(32.72109826903826, -117.15952052282296);
        Coord bali_mynah_coord  = new Coord(32.73697286273083, -117.17319785958958);

        //Another way to create a Coord automatically
        //get 10 evenly spaced points in the line between "start" and "goal" (include "start" and "goal")
        // "start" and "goal" must be the Name of landmark
        List<Coord> TenPoints = Coords.getTenPointsInLine("Siamangs", "Orangutans");
        //change the indexes as you wish
        Coord point_near_start = TenPoints.get(2);
        Coord point_near_goal = TenPoints.get(7);


        //Step 2.1: call mockASinglePoint function
        //mockASinglePoint(koi_fish_coord);
        mockASinglePoint(bali_mynah_coord);

        //mockASinglePoint(point_near_start);
        //mockASinglePoint(point_near_goal);

        //Step 2.2: call mockAListOfPoints function
        //mockAListOfPoints(TenPoints);


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
        String goalExhibit = (going_forward)?
                orderedAnimalList_child.get(order + 1): orderedAnimalList_child.get(order - 1);
        setToText(goalExhibit);

        //skip last exihibit and go to gate
        if ( going_forward && order >= orderedAnimalList_Names.size() - 2){
            skipBtn.setEnabled(false);
            Utilities.showAlert(this, "You are back to \"Entrance and Exit Gate\".");
            updateRoute(order, going_forward, Coords.currentLocationCoord, orderedAnimalList_IDs);
            order--;
            nextBtn.setEnabled(false);
            if (planned_route.size()<=1) prevBtn.setEnabled(false);
        }
        //skip first exhibit and go back to gate
        else if (!going_forward && order <= 1){
            skipBtn.setEnabled((false));
            Utilities.showAlert(this, "You are back to \"Entrance and Exit Gate\".");
            updateRoute(order, going_forward, Coords.currentLocationCoord, orderedAnimalList_IDs);
            order++;
            prevBtn.setEnabled(false);
            if (planned_route.size()<=1) prevBtn.setEnabled(false);
        }
        //normal
        else{
            Utilities.showAlert(this, "The route has been replanned.");
            display(order, going_forward);
        }

        Log.d("order",Integer.toString(order));
        Log.d("route status", String.join(", ", orderedAnimalList_IDs));

    }

    //skip the current animal
    public static void skip(boolean going_forward, int order, List<String> a1, List<String> a2, List<String> a3, List<route_node> route_nodeList) {
        int index_remove;
        if (going_forward) index_remove=order+1;
        else index_remove = order-1;
        a1.remove(index_remove);
        a2.remove(index_remove);
        a3.remove(index_remove);
        route_nodeList.remove(index_remove-1);
    }

    //@order:
    public void updateRoute(int order, boolean going_forward, Coord current, List<String> orderedAnimalList_IDs) {
        //get the closestLandmark
        List<IdentifiedWeightedEdge> updatePath = new ArrayList<>();
        String closestLandmark_id = AnimalItem.getClosestLandmark(current).get(0);
        String nextExhibit_id = null;
        //get the goal exhibit
        if (going_forward){
            nextExhibit_id = orderedAnimalList_IDs.get(order + 1);
        }
        else{
            nextExhibit_id = orderedAnimalList_IDs.get(order - 1);
        }
        //get the path between closest landmark to next exhibit
        if (!closestLandmark_id.equals(nextExhibit_id)){
            updatePath = DirectionHelper.findPathBetween(closestLandmark_id, nextExhibit_id);
        }
        List<IdentifiedWeightedEdge> updated_path = new ArrayList<>(updatePath);
        //display updated directions
        setDisplay(closestLandmark_id, nextExhibit_id, updated_path, displayStatus, true);

    }

    public void onEnterButtonClick(View view) {
        Intent intent = new Intent(this, EnterActivity.class);
        startActivityForResult(intent,ACTIVITY_CONSTANT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //call OnMockChange
        Coord gate_coord = AnimalItem.getExtranceGateCoord();
        SharedPreferences sharedPreferences = getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        String lat_string = sharedPreferences.getString("currentLat", Double.toString(gate_coord.lat));
        String lng_string = sharedPreferences.getString("currentLng", Double.toString(gate_coord.lng));
        Coord updateCoord = new Coord(Double.valueOf(lat_string), Double.valueOf(lng_string));
        OnMockChange(updateCoord);
    }



}
