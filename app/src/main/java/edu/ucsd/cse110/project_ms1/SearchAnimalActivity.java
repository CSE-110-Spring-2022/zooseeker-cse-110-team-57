package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchAnimalActivity extends AppCompatActivity{

    public RecyclerView recyclerView;
    public ArrayAdapter<AnimalItem> arrayAdapter;
    //public ArrayAdapter<AnimalItem> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_animal);

//        List<animal_item> animal_items = null;
//        try {
//            animal_items = animal_item.loadInfo(this,"sample_node_info.json");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        SearchedAnimalsAdapter search_adapter = new SearchedAnimalsAdapter();
        search_adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.all_searched_animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(search_adapter);

        List<AnimalItem> toBeShown= null;
        try {
            AnimalItem.loadInfo(this, "sample_node_info.json","sample_edge_info.json","sample_zoo_graph.json");
            toBeShown= AnimalItem.search_by_tag(null);

            Log.d("TodoListActivity", toBeShown.toString());
            search_adapter.setSearched_animal_items(toBeShown);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ////ListView listView = findViewById(R.id.my_list);
        //List<AnimalItem> mylist = new ArrayList<>();

        ListView listView = findViewById(R.id.my_list);
//        List<AnimalItem> mylist = new ArrayList<>();
//        mylist.add(new AnimalItem(1,
//                new ZooData.VertexInfo("1", ZooData.VertexInfo.Kind.EXHIBIT, "Gorillas", Collections.singletonList("new tag"))));

       /*
        mylist.add("Gorillas");
        mylist.add("Alligators");
        mylist.add("Lions");
        mylist.add("Elephant Odyssey");
        mylist.add("Arctic Foxes");
         */

        arrayAdapter = new ArrayAdapter<AnimalItem>(this, android.R.layout.simple_list_item_multiple_choice, toBeShown);
        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    public void onAddToListClicked(View view) {
        Intent intent = new Intent(this, ListOfSelectedAnimalsActivity.class);
        startActivity(intent);
    }


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


}