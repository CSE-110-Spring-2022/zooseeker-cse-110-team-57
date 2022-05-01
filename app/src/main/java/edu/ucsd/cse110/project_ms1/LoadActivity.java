package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        List<animal_item> animals = animal_item.loadJSON(this,"sample_node_info");
        Log.d("LoadActivity", animals.toString());
    }
}