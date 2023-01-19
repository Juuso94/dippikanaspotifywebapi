package com.dippikana.spotifywebapi.controllers;


import java.net.URI;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.*;
import org.springframework.web.bind.annotation.*;

import com.dippikana.spotifywebapi.models.SearchResult;
import com.dippikana.spotifywebapi.services.Utilities;

@Component
@RestController
//@RequestMapping("/api")
public class SearchController {

  @Autowired
  private Utilities utilities;
  private String apiUrl = "https://api.spotify.com/v1";

  @GetMapping("/search")
    public ResponseEntity<Object> searchSongs(@RequestParam(name = "q") String queryString) {

      if(!utilities.isTokenValid()) {
        if(!utilities.refreshAuthenticationToken()) {
          return new ResponseEntity<Object>("Something went wrong while refreshing the accesstoken", HttpStatus.BAD_REQUEST);
        }
      }
      String apiLocation = "/search";
      URI playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation)
      .queryParam("q", queryString)
      .queryParam("type", "track").encode()
      .build().toUri();

      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(utilities.getAccessToken());

      HttpEntity entity = new HttpEntity<>(headers);
      ResponseEntity<SearchResult> response;

      try {
        response = new RestTemplate().exchange(playerURI, HttpMethod.GET, entity, SearchResult.class);
      }
      catch (Exception e) {
        System.out.println(e.toString());
        response = null;
      }


      if( response!= null && response.getStatusCodeValue() == 200) {
        return new ResponseEntity<Object>(response.getBody(), HttpStatus.OK);
      }
      else {
        return utilities.createErrorResponse(HttpStatus.BAD_REQUEST, "Something went wrong with search");
      }
    }
}
