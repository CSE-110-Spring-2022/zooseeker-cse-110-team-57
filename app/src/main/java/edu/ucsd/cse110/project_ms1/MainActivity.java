package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("Team57", Activity.MODE_PRIVATE);

        String ActivityTarget = sharedPreferences.getString("currentActivity", "SearchAnimalActivity");
        switch(ActivityTarget){
            case "PlanActivity":
                intent = new Intent(this, PlanActivity.class);
                break;
            case "DirectionActivity":
                intent = new Intent(this, DirectionActivity.class);
                break;
            default:
                intent = new Intent(this, SearchAnimalActivity.class);
                break;
        }
        startActivity(intent);
    }
}

/**
 * Reference:
 *
 * For techniques related to TypeConvert, this website provided much help
 * https://medium.com/@toddcookevt/android-room-storing-lists-of-objects-766cca57e3f9
 *
 * Also thanks for help from https://www.youtube.com/watch?v=pM1fAmUQn8g about using search
 * bar in android studio
 *
 *
 */
