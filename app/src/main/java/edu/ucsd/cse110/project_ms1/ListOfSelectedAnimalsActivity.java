package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ListOfSelectedAnimalsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_selected_animals);
    }

    public void onGoBackToSearchClicked(View view) {
        Intent intent = new Intent(this, SearchAnimalActivity.class);
        startActivity(intent);
    }
}