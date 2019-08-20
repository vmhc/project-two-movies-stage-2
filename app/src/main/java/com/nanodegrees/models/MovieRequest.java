package com.nanodegrees.models;

import java.util.List;

public class MovieRequest {

    private Integer page;
    private Integer total_results;
    private Integer total_pages;
    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }

}
