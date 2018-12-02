package com.sem.lamoot.elati.danstonplacard.danstonplacard.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sem.lamoot.elati.danstonplacard.danstonplacard.database.model.Ingredient;
import com.sem.lamoot.elati.danstonplacard.danstonplacard.database.model.ListeCourses;

import java.util.List;

@Dao
interface ListeCoursesDao {

    @Insert
    void insert(ListeCourses listeCourses);

    @Query("SELECT * FROM ingredient")
    List<ListeCourses> getAllListeCourses();
}
