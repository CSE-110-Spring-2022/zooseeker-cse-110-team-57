package edu.ucsd.cse110.project_ms1;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {AnimalItem.class}, version = 1, exportSchema = false)
@TypeConverters(TagConverter.class)
public abstract class AnimalItemDatabase extends RoomDatabase {
    public abstract AnimalItemDao AnimalItemDao();



    static  AnimalItemDatabase makeDatabase(Context context){
        return Room.databaseBuilder(context, AnimalItemDatabase.class, "zoo.db").allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public  void onCreate(@NonNull SupportSQLiteDatabase db){
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(()->{
                            List<AnimalItem> todos = AnimalItem
                                    .search_by_tag(null);
                            //getSingleton(context).todoListItemDao().insertAll(todos);
                        });
                    }
                }).build();
    }
}


