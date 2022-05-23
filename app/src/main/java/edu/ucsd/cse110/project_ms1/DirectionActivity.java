package edu.ucsd.cse110.project_ms1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DirectionActivity extends AppCompatActivity implements OnLocationChangeListener {
    int order;
    String currentLocation; //current exhibit or closest exhibit
    HashMap<Integer, DirectionData> zooRoute;
    boolean isNext;
    Intent intent;
    ArrayList<String> orderedAnimal;
    DirectionAdapter direction_adapter;
    RecyclerView direction_recyclerView;
    List<String> orderedAnimalList;
    List<AnimalItem> animalItems;
    List<route_node> planned_route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        Utilities.changeCurrentActivity(this, "DirectionActivity");


        //grab ordered list of animal id, begin from first item in the route.
        Intent intent = getIntent();
        List<String> orderedAnimal = intent.getStringArrayListExtra("routedAnimalNameList");
        if (orderedAnimal == null){
            SharedPreferences sharedPreferences = getSharedPreferences("Team57", 0);
            String concated_animal_names = sharedPreferences.getString("route",
                    "No found such animal in sharedPreference");
            orderedAnimal = Arrays.asList(concated_animal_names.split("\\s*,\\s*"));
        }

        //remove the Entrance and Exit Gate
        orderedAnimal.remove(orderedAnimal.size() - 1);
        animalItems = DirectionHelper.loadAnimalItem(this, orderedAnimal);

        //find the shortest Path by given ordered route.
        //(order -order of animal in the route , paths -list of edges in the path)
        planned_route = AnimalItem.plan_route(animalItems);

        for(route_node node : planned_route){
            orderedAnimalList.add(node.animal.name);
        }

        //we need add the front gate into orderedAnimalList, so that route begin at gate
//        orderedAnimalList.add(0, "entrance_exit_gate");
        orderedAnimalList.add("entrance_exit_gate");
        //<order -the index of exhibit in the route, edges -the edges between exhibits
        //HashMap<Integer, List<IdentifiedWeightedEdge>> route = DirectionHelper.findRoute(planned_route);


        //adapter
        direction_adapter = new DirectionAdapter();
        direction_adapter.setHasStableIds(true);

        direction_recyclerView = this.findViewById(R.id.brief_path);
        direction_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        direction_recyclerView.setAdapter(direction_adapter);

        //Get the order and isNext
        List<String> retainedInfo = DirectionHelper.loadDirectionsInformation(this);
        order = Integer.valueOf(retainedInfo.get(0));
        isNext = Boolean.valueOf(retainedInfo.get(1));
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
        Button detailBtn = findViewById(R.id.detail_button);
        detailBtn.setText("Brief");


         String sourceExhibit;
         String goalExhibit;
         List<IdentifiedWeightedEdge> path;

         if (isNext) {
             sourceExhibit = orderedAnimalList.get(index);
             goalExhibit = orderedAnimalList.get(index+1);
             path = DirectionHelper.findPathBetween(sourceExhibit,goalExhibit);
             DirectionHelper.saveDirectionsInformation(this, DirectionHelper.getNodeName(sourceExhibit), DirectionHelper.getNodeName(goalExhibit), order);
         } else {
             sourceExhibit = orderedAnimalList.get(index);
             goalExhibit = orderedAnimalList.get(index-1);
             path = DirectionHelper.findPathBetween(sourceExhibit,goalExhibit);
             DirectionHelper.saveDirectionsInformation(this, DirectionHelper.getNodeName(sourceExhibit), DirectionHelper.getNodeName(goalExhibit), order);
                        //        Button prevbtn = findViewById(R.id.previous_button);
                        //        Button nextbtn = findViewById(R.id.next_button);
                        //        Button detailBtn = findViewById(R.id.detail_button);
                        //        detailBtn.setText("Brief");
                        //
                        //        DirectionData pathData = zooRoute.get(index);
                        //        List<String> path;
                        //        String startText;
                        //        String endText;
                        //        if (isNext){
                        //            path = new ArrayList<>(pathData.paths);
                        //            startText = "From: "+ pathData.startExhibit;
                        //            endText = "To: "+ pathData.goalExhibit;
                        //            DirectionHelper.saveDirectionsInformation(this, order, true);
                        //        }else{
                        //            path = new ArrayList<>(pathData.prevPaths);
                        //            startText = "From: "+ pathData.goalExhibit;
                        //            endText = "To: "+ pathData.startExhibit;
                        //            DirectionHelper.saveDirectionsInformation(this, order, false);
         }



        List<String> pathDisplay = new ArrayList<>(DirectionHelper.briefPath(path,goalExhibit));;

        String startText = "From: " + DirectionHelper.getNodeName(sourceExhibit);
        String endText = "To: " + DirectionHelper.getNodeName(goalExhibit);


        direction_adapter.setDirectionsStringList(pathDisplay);

        start.setText(startText);
        end.setText(endText);
        distance.setText(Double.toString(DirectionHelper.totalDistance(path)) + " ft");

        //setting next button and next direction distance
        //disable the prev btn at the first page and next btn at last page.

        if (index < orderedAnimalList.size() - 2) {
            nextBtn.setEnabled(true);
            String nextSource = orderedAnimalList.get(index+1);
            String nextGoal = orderedAnimalList.get(index+2);
            List<IdentifiedWeightedEdge> nextPath = DirectionHelper.findPathBetween(nextSource,nextGoal);
            double nextDistance = DirectionHelper.totalDistance(nextPath);
            String nextText = (nextGoal + "  " + nextDistance + " ft");
            next.setText(nextText);
        } else {
            nextBtn.setEnabled(false);
            next.setText("End of tour");
        }

        //setting previous button and previous direction distance
        if (index == 0) {
            prevBtn.setEnabled(false);
            prev.setText("Begin of tour");

        } else {
            prevBtn.setEnabled(true);
            String current = orderedAnimalList.get(index);
            String lastSource = orderedAnimalList.get(index-1);
            List<IdentifiedWeightedEdge> prevPath = DirectionHelper.findPathBetween(current,lastSource);
            double prevDistance = DirectionHelper.totalDistance(prevPath);
            String prevText = (lastSource + "  " + prevDistance + " ft");
                    // }else{
                    //     prevbtn.setEnabled(true);
                    //     DirectionData prevData = zooRoute.get(index-1);
                    //     String prevText = (prevData.goalExhibit + "  " + prevData.distance + " ft");
            prev.setText(prevText);
        }
    }

    public void displayDetail(String source,String goal){
        List<IdentifiedWeightedEdge> path = DirectionHelper.findPathBetween(source,goal);
        List<String> pathDisplay = DirectionHelper.detailPath(path,source);
        direction_adapter.setDirectionsStringList(pathDisplay);
    }

    public void displayBrief(String source,String goal){
        List<IdentifiedWeightedEdge> path = DirectionHelper.findPathBetween(source,goal);
        List<String> pathDisplay = DirectionHelper.briefPath(path,source);
        direction_adapter.setDirectionsStringList(pathDisplay);
    }

    public void onNextButtonClick(View view) {
        order++;
        if (order < zooRoute.size()) {
            display(order, true);
        } else {
            order = zooRoute.size() - 1;
            display(order, true);
            Utilities.showAlert(this, "invalid Action");
        }
    }

    public void onBackButtonClick(View view) {
        order--;
        if (order >= 0) {
            display(order, false);
        } else {
            order = 0;
            display(order, false);
            Utilities.showAlert(this, "invalid Action");
        }
    }


    public void onClearButtonClick(View view) {
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
    }

    @Override
    public void OnLocationChange(LatLng current) {
        ZooData.VertexInfo closestlandmark = AnimalUtilities.getClosestLandmark(current);
        if (AnimalUtilities.check_off_route(order,planned_route,current)){
            boolean user_want_update = true;
            if (user_want_update) {
                planned_route = AnimalUtilities.reroute(order, planned_route, current);

                //pop out a notification window

                //save to SharedPreferences
                List<String> animal_strings = new ArrayList<>();
                for (route_node myRoute_node : planned_route) {
                    String myAnimal = myRoute_node.animal.name;
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
        orderedAnimal.clear();
        Utilities.clearSavedAnimalItem(this);
        intent = new Intent(this, SearchAnimalActivity.class);
        startActivity(intent);
    }
}
