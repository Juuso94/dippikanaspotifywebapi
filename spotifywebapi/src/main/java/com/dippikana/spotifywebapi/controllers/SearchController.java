package com.dippikana.spotifywebapi.controllers;

import java.util.Base64;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.*;
import org.springframework.web.bind.annotation.*;

import com.dippikana.spotifywebapi.models.PlaybackData;
import com.dippikana.spotifywebapi.models.SearchResult;
import com.dippikana.spotifywebapi.services.Utilities;

@RestController
//@RequestMapping("/api")
public class SearchController {

  private String apiUrl = "https://api.spotify.com/v1";

  @GetMapping("/search")
		public ResponseEntity<SearchResult> searchSongs(@RequestParam(name = "q") String queryString) {

			if(!Utilities.isTokenValid()) {
				Utilities.refreshAuthenticationToken();
			}

			String apiLocation = "/search";
			URI playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation)
      .queryParam("q", queryString)
      .queryParam("type", "track")
      .build().toUri();

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(Utilities.getAccessToken());

			HttpEntity entity = new HttpEntity<>(headers);

			ResponseEntity<SearchResult> response = new RestTemplate().exchange(playerURI, HttpMethod.GET, entity, SearchResult.class);

			return new ResponseEntity<SearchResult>(response.getBody(), HttpStatus.OK);
		}
}
