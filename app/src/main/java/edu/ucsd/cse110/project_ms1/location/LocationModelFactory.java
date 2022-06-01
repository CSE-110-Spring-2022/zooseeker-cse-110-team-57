package edu.ucsd.cse110.project_ms1.location;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.project_ms1.OnLocationChangeListener;

public class LocationModelFactory extends ViewModelProvider.NewInstanceFactory {
    @NonNull
    private Application mApplication;
    private OnLocationChangeListener onLocationChangeListener;
    private Context context;

    //constructor with OnLocationChangeListener
    public LocationModelFactory(@NonNull Application application, Context context,
                                OnLocationChangeListener onLocationChangeListener) {
        mApplication = application;
        this.onLocationChangeListener = onLocationChangeListener;
        this.context = context;
    }
    //override create method
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == LocationModel.class){
            return (T) new LocationModel(mApplication, context, onLocationChangeListener);
        }
        return null;
    }
}
