package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, SearchAnimalActivity.class);
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
