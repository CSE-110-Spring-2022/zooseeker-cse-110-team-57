package edu.ucsd.cse110.project_ms1;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class AnimalViewModel extends AndroidViewModel {
    private LiveData<List<AnimalItem>> searchedAnimals;
    private final AnimalItemDao animalItemDao;


    public AnimalViewModel(@NonNull Application application){
        super(application);
        Context context = getApplication().getApplicationContext();
        AnimalItemDatabase db = AnimalItemDatabase.getSingleton(context);
        animalItemDao = db.AnimalItemDao();
    }

    public LiveData<List<AnimalItem>> getSearchedAnimalItems(){
        if (searchedAnimals == null){
            loadUsers();
        }
        return searchedAnimals;
    }

    private void loadUsers() {
        searchedAnimals = animalItemDao.getAllLive();
    }



}
