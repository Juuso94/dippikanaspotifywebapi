package com.dippikana.spotifywebapi.models;

public class TokenForm {
  public String code;
  public String redirect_uri;
  public String grant_type;


  public TokenForm(String code, String redirect_uri, String grant_type) {
    this.code = code;
    this.redirect_uri = redirect_uri;
    this.grant_type = grant_type;
  }

}

