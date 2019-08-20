/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nanodegrees.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;

import com.nanodegrees.models.Movie;
import com.nanodegrees.models.MovieDetails;

import java.util.Date;
import java.util.List;

/**
 * {@link Dao} which provides an api for all data operations with the {@link MoviesDatabase}
 */
@Dao
public interface MoviesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertFavourite(MovieDetails movie);

    @Query("SELECT id, poster_path FROM movie_details")
    LiveData<List<Movie>> getAllFavourites();

    @Query("SELECT count(id) > 0 FROM movie_details WHERE id = :id")
    LiveData<Boolean> isFavourite(Integer id);

    @Query("DELETE FROM movie_details WHERE id = :id")
    void deleteFavourite(Integer id);

}
