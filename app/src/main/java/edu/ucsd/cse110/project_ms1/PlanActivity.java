package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

public class PlanActivity extends AppCompatActivity{
    public RecyclerView plan_recyclerView;
    PlanAdapter plan_adapter;
    List<AnimalItem> preSelectedAnimalItemList;
    List<String> selectedAnimalNameStringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        plan_adapter = new PlanAdapter();
        plan_adapter.setHasStableIds(true);

        plan_recyclerView = findViewById(R.id.planed_route);
        plan_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plan_recyclerView.setAdapter(plan_adapter);

    }

}