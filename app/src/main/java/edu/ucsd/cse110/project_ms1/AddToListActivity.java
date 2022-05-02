package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class AddToListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_list);




    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //List<AnimalItem> selectedAnimalsList = ?????;
        //addToList_adapter.setSelectedAnimalItems();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}