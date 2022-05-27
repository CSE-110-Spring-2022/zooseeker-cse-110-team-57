package edu.ucsd.cse110.project_ms1;
import static org.junit.Assert.assertNull;

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
    public ActivityScenarioRule<SearchAnimalActivity> scenarioRule = new ActivityScenarioRule<>(SearchAnimalActivity.class);

    @Before
    public void add_animal(){
        ActivityScenario search = scenarioRule.getScenario();
        search.moveToState(Lifecycle.State.CREATED);

        search.onActivity(activity -> {
            Button add = activity.findViewById(R.id.add_to_button);
             add.performClick();
            Button plan = activity.findViewById(R.id.plan_button);
            plan.performClick();
        });
    }


    @Test
    public void Mocking_of_Location(){
        return;
    }

    @Test
    public void dir_clear(){
        ActivityScenario search = scenarioRule.getScenario();
        search.moveToState(Lifecycle.State.CREATED);

        search.onActivity(activity -> {
            Button add = activity.findViewById(R.id.add_to_button);
            add.performClick();
            Button plan = activity.findViewById(R.id.plan_button);
            plan.performClick();
        });



//        activityScenario.onActivity(activity -> {
//            SharedPreferences sharedPreferences = activity.getSharedPreferences("team57",0);
//
//            //find and performclick
//            Button clear = activity.findViewById(R.id.clear_button);
//            clear.performClick();
//
//            //check whether route has been clear or not
//            String route = sharedPreferences.getString("route",null);
//            assertNull(route);
//        });


    }

}
