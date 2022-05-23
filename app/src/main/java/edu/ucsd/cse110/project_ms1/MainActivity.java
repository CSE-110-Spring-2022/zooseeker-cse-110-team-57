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
        //----------------Comment this line if you want to direct to the page before the app is killed----------------
        goToSearchAnimalActivity();
        //-----------------------------------------------------------------------------------------

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
    public void goToSearchAnimalActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //set the currentActivity to be SearchAnimalActivity
        editor.putString("currentActivity", "SearchAnimalActivity");
        editor.commit();
        editor.apply();
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
