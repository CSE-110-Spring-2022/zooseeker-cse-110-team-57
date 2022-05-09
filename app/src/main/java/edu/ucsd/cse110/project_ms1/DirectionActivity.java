package edu.ucsd.cse110.project_ms1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionActivity extends AppCompatActivity {
    int order;

    HashMap<Integer,DirectionData> zooRoute;
    DirectionAdapter direction_adapter;
    RecyclerView direction_recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        //grab ordered list of animal id, begin from first item in the route.
        Intent intent = getIntent();
        ArrayList<String> orderedAnimal = intent.getStringArrayListExtra("routedAnimalNameList");
        //remove the Entrance and Exit Gate
        orderedAnimal.remove(orderedAnimal.size() - 1);
        List<String> orderedAnimalList = DirectionHelper.loadAnimalItem(this, orderedAnimal);

        //find the shortest Path by given ordered route.
        //(order -order of animal in the route , paths -list of edges in the path)
        HashMap<Integer,List<IdentifiedWeightedEdge>> route = DirectionHelper.findRoute(orderedAnimalList);
        zooRoute = new HashMap<>();
        order = 0;


        //find the path and info, then save it for recycle use.
        for(int i = 0; i < route.size(); i++){
            List<IdentifiedWeightedEdge> path = route.get(i);
            String startExhibit = DirectionHelper.getNodeName(orderedAnimalList.get(i));
            String goalExhibit = DirectionHelper.getNodeName(orderedAnimalList.get(i+1));
            List<String> paths = DirectionHelper.displayPath(path,startExhibit);
            Double distance = DirectionHelper.totalDistance(path);
            DirectionData walk = new DirectionData(startExhibit,goalExhibit,distance,paths);
            zooRoute.put(i,walk);
        }

        //adapter
        direction_adapter = new DirectionAdapter();
        direction_adapter.setHasStableIds(true);

        direction_recyclerView = this.findViewById(R.id.all_direction_items);
        direction_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        direction_recyclerView.setAdapter(direction_adapter);

        display(order);

    } //Initial End

    public void display(int index){
        DirectionData pathData = zooRoute.get(index);

        List<String> path = new ArrayList<>(pathData.paths);
        direction_adapter.setDirectionsStringList(path);

        TextView start = findViewById(R.id.start_exhibit_name);
        TextView end = findViewById(R.id.goal_exhibit_name);
        TextView next = findViewById(R.id.next_text);
        TextView prev = findViewById(R.id.previous_text);
        TextView distance = findViewById(R.id.path_total_distance);
        Button prevbtn = findViewById(R.id.previous_button);
        Button nextbtn = findViewById(R.id.next_button);


        start.setText("From: "+ pathData.startExhibit);
        end.setText("To: "+ pathData.goalExhibit);
        distance.setText(Double.toString(pathData.distance));

        //setting next button and next direction distance
        //disable the prev btn at the first page and next btn at last page.

        if(index < zooRoute.size()-1){
            nextbtn.setEnabled(true);
            DirectionData nextData = zooRoute.get(index+1);
            String nextText = (nextData.goalExhibit + "  " + nextData.distance + " ft");
            next.setText(nextText);
        }else{
            nextbtn.setEnabled(false);
            next.setText("End of tour");
        }

        //setting previous button and previous direction distance
        if(index == 0){
            prevbtn.setEnabled(false);
            prev.setText("Begin of tour");

        }else{
            prevbtn.setEnabled(true);
            DirectionData prevData = zooRoute.get(index-1);
            String prevText = (prevData.goalExhibit + "  " + prevData.distance + " ft");
            prev.setText(prevText);
        }
    }

    public void onNextButtonClick(View view) {
        order++;
        if(order < zooRoute.size()){
            display(order);
        }else{
            order = zooRoute.size()-1;
            display(order);
            Utilities.showAlert(this, "invalid Action");
        }
    }

    public void onBackButtonClick(View view) {
        order--;
        if(order >= 0){
            display(order);
        }else{
            order = 0;
            display(order);
            Utilities.showAlert(this, "invalid Action");
        }
    }


}
