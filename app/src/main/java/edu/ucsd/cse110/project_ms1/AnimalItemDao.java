package edu.ucsd.cse110.project_ms1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AnimalItemDao {
    @Insert
    long insert(AnimalItem animalItem);

    @Insert
    List<Long> insertAll(List<AnimalItem> todoListItem);

    @Query("SELECT * FROM `animal_items` WHERE `id`=:id")
    AnimalItem get(long id);

    @Query("SELECT * FROM `animal_items` ORDER BY `order`")
    List<AnimalItem> getAll();

    @Update
    int update(AnimalItem animalItem);

    @Delete
    int delete(AnimalItem animalItem);

    @Query("SELECT * FROM `animal_items` ORDER BY `order`")
    LiveData<List<AnimalItem>> getAllLive();

    @Query("SELECT `order` + 1 FROM `animal_items` ORDER BY `order` DESC LIMIT 1")
    int getOrderForAppend();

    //@Query()
}
