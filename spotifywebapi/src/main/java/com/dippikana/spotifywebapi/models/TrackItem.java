package com.dippikana.spotifywebapi.models;

import java.util.ArrayList;

public class TrackItem {
  public String name;
  public String id;
  public Long duration_ms;
  public ArrayList<ArtistItem> artists;
  public AlbumItem album;
  public String uri;

  public TrackItem(){ }

  public TrackItem(String name, String artist, String id, ArrayList<ArtistItem> artists) {
    this.name = name;
    this.id = id;
    this.artists = artists;
  }

}

