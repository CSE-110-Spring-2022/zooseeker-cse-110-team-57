package edu.ucsd.cse110.project_ms1.location;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.project_ms1.OnLocationChangeListener;

public class LocationModelFactory extends ViewModelProvider.NewInstanceFactory {
    @NonNull
    private Application mApplication;
    private OnLocationChangeListener onLocationChangeListener;


    public LocationModelFactory(@NonNull Application application, OnLocationChangeListener onLocationChangeListener) {
        mApplication = application;
        this.onLocationChangeListener = onLocationChangeListener;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == LocationModel.class){
            return (T) new LocationModel(mApplication, onLocationChangeListener);
        }
        return null;
    }
}
