package com.dippikana.spotifywebapi.models;

public class PlaybackData {

  public TrackItem item;
  public boolean is_playing;
  public Long progress_ms;

  public PlaybackData(TrackItem item, boolean is_playing) {
    this.item = item;
    this.is_playing = is_playing;
    
  }
  
  public PlaybackData() {}
}
