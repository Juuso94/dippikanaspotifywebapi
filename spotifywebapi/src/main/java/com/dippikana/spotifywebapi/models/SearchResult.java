package com.dippikana.spotifywebapi.models;

import java.util.ArrayList;

public class SearchResult {
  public Tracks tracks;


  public SearchResult() {
  }

  class Tracks {
    public ArrayList<TrackItem> items;

    public Tracks() {

    }
  }
}
