package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--------------Comment this line if you don't want to select animals again-------------
        //Utilities.clearSharedPreference(this);
        //----------------------------------------------------------------------------------
        //--------Comment these lines if you want to direct to the page before the app is killed----------------
        goToSearchAnimalActivity();
        //goToPlanActivity();
        //goToDirectionActivity();
        //goToEnterActivity();
        //-----------------------------------------------------------------------------------------

        //restore the current activity
        SharedPreferences sharedPreferences = getSharedPreferences("Team57", Activity.MODE_PRIVATE);
        String ActivityTarget = sharedPreferences.getString("currentActivity", "SearchAnimalActivity");
        switch(ActivityTarget){
            case "PlanActivity":
                intent = new Intent(this, PlanActivity.class);
                break;
            case "DirectionActivity":
                intent = new Intent(this, DirectionActivity.class);
                break;
            case "EnterActivity":
                intent = new Intent(this, EnterActivity.class);
                break;
            default:
                intent = new Intent(this, SearchAnimalActivity.class);
                break;
        }
        startActivity(intent);
    }

    //go to SearchAnimalActivity
    public void goToSearchAnimalActivity(){
        Utilities.changeCurrentActivity(this, "SearchAnimalActivity");
    }
    //go to PlanActivity
    public void goToPlanActivity(){
        Utilities.changeCurrentActivity(this, "PlanActivity");
    }
    //go to DirectionActivity
    public void goToDirectionActivity(){
        Utilities.changeCurrentActivity(this, "DirectionActivity");
    }
    //go to EnterActivity
    public void goToEnterActivity(){
        Utilities.changeCurrentActivity(this, "EnterActivity");
    }
}


