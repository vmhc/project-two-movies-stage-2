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

package com.nanodegrees.utils;

import android.content.Context;

import com.nanodegrees.data.AppExecutors;
import com.nanodegrees.data.MoviesRepository;
import com.nanodegrees.data.database.MoviesDatabase;
import com.nanodegrees.data.network.MoviesNetworkDataSource;

/**
 * Provides static methods to inject the various classes needed for Movies App
 */
public class InjectorUtils {

    public static MoviesRepository provideRepository(Context context) {
        MoviesDatabase database = MoviesDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        MoviesNetworkDataSource networkDataSource =
                MoviesNetworkDataSource.getInstance(context.getApplicationContext(), executors);
        return MoviesRepository.getInstance(database.moviesDAO(), networkDataSource, executors);
    }

    public static MoviesNetworkDataSource provideNetworkDataSource(Context context) {
        // This call to provide repository is necessary if the app starts from a service - in this
        // case the repository will not exist unless it is specifically created.
        provideRepository(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        return MoviesNetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }

}