package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import java.io.IOException;
import java.util.List;

public class SearchAnimalActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    public RecyclerView recyclerView;
    SearchedAnimalsAdapter search_adapter;
    AddToListAdapter addToList_adapter;
    String selected_animal;
    String animalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_animal);

        AnimalItemDao animalItemDao = AnimalItemDatabase.makeDatabase(this).AnimalItemDao();
        List<AnimalItem> animalItemDaos = animalItemDao.getAll();

        search_adapter = new SearchedAnimalsAdapter();
        search_adapter.setHasStableIds(true);
        search_adapter.setSearched_animal_items(animalItemDaos);
        //dropdown bar
        recyclerView = findViewById(R.id.all_searched_animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(search_adapter);
        selected_animal = search_adapter.getSelectedAnimal();

        List<AnimalItem> toBeShown= null;
        try {
            AnimalItem.loadInfo(this, "sample_node_info.json","sample_edge_info.json","sample_zoo_graph.json");
            toBeShown= AnimalItem.search_by_tag(null);
            Log.d("TodoListActivity", toBeShown.toString());
            search_adapter.setSearched_animal_items(toBeShown);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //display list
        addToList_adapter = new AddToListAdapter();
        addToList_adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.all_selected_animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(addToList_adapter);

        //link the name with the combo string
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        AnimalItem currentAnimal = AnimalItem.search_by_tag(selected_animal).get(0);
        animalId = currentAnimal.id;
        editor.putString(selected_animal, animalId);

        editor.commit();
        editor.apply();


    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        List<AnimalItem> searchedAnimalsList = AnimalItem.search_by_tag(query);
        search_adapter.setSearched_animal_items(searchedAnimalsList);
        //List<AnimalItem> selectedAnimalsList = ???;
        //addToList_adapter.setSelectedAnimalItems();
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public void onAddToListClicked(View view) {
        Intent intent = new Intent(this, AddToListActivity.class);
        startActivity(intent);
    }
/*
    public AnimalItem getAnimalItemFromId(String Id){
        String
    }
*/
/*
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.animal_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar_2);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Please enter an animal's name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
*/

}