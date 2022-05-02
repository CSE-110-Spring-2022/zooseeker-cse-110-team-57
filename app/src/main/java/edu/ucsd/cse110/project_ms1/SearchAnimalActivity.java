package edu.ucsd.cse110.project_ms1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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

import com.google.gson.Gson;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

public class SearchAnimalActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    public RecyclerView recyclerView;
    SearchedAnimalsAdapter search_adapter;
    AddToListAdapter addToList_adapter;
    private AnimalViewModel viewModel;
    List<String> selectedAnimalNameStringList;
    List<String> selectedAnimalItemStringList;
    List<AnimalItem> selectedAnimalItemList;
    String selected_animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_animal);

        viewModel = new ViewModelProvider(this)
                .get(AnimalViewModel.class);

        AnimalItemDao animalItemDao = AnimalItemDatabase.getSingleton(this).AnimalItemDao();
        List<AnimalItem> animalItemDaos = animalItemDao.getAll();
        //list of searched animals
        search_adapter = new SearchedAnimalsAdapter();
        search_adapter.setHasStableIds(true);
        search_adapter.setSearched_animal_items(animalItemDaos);
        search_adapter.setOnAnimalButtonClickedHandler(viewModel::select_animal);
        //viewModel.get

        //dropdown bar
        recyclerView = findViewById(R.id.all_searched_animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(search_adapter);



        //get the list of name string of selected animals
        selectedAnimalNameStringList = search_adapter.getSelectedAnimal();
        SaveAnimalItemString(selectedAnimalNameStringList);

        addToList_adapter = new AddToListAdapter();
        addToList_adapter.setHasStableIds(true);
        //list bar
        recyclerView = findViewById(R.id.all_selected_animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(addToList_adapter);

        //get the animalItem list of selected animals
        selectedAnimalItemList = LoadAnimalItemString(selectedAnimalNameStringList);

        addToList_adapter.setSelectedAnimalItems(selectedAnimalItemList);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        List<AnimalItem> searchedAnimalsList = AnimalItem.search_by_tag(query);
        TextView NoSuchAnimal = findViewById(R.id.no_such_animal);
        //link searched animals
        if (searchedAnimalsList.size() == 0){
            NoSuchAnimal.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            NoSuchAnimal.setVisibility(View.INVISIBLE);
        }
        search_adapter.setSearched_animal_items(searchedAnimalsList);
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

    public AnimalItem getAnimalNameFromId(String Id){
        AnimalItem animalItem = AnimalItem.search_by_tag(Id).get(0);
        return animalItem;
    }

    public String AnimalItemToString(AnimalItem animalItem){
        return new Gson().toJson(animalItem);
    }

    public AnimalItem StringToAnimalItem(String animalItem){
        Gson g = new Gson();
        AnimalItem currentAnimalItem = g.fromJson(animalItem, AnimalItem.class);
        return currentAnimalItem;
    }
    public void SaveAnimalItemString(List<String> selectedAnimalNameStringList){
        //link the animalItem name with the string form of animalItem
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < selectedAnimalNameStringList.size(); i++){
            AnimalItem animalItem = AnimalItem.search_by_tag(selectedAnimalNameStringList.get(i)).get(0);
            String currentString = AnimalItemToString(animalItem);
            editor.putString(currentString, selectedAnimalNameStringList.get(i));
        }
        editor.commit();
        editor.apply();
    }
    public List<AnimalItem> LoadAnimalItemString(List<String> currentStringList){
        List<AnimalItem> currentAnimalItemList = new ArrayList<AnimalItem>();
        if (currentStringList.size() == 0){
            return currentAnimalItemList;
        }
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        for (int i = 0; i < currentStringList.size(); i++){
            String currentAnimalString = sharedPreferences.getString(currentStringList.get(i), "default_if_not_found");
            currentAnimalItemList.add(StringToAnimalItem(currentAnimalString));
        }
        return currentAnimalItemList;
    }


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