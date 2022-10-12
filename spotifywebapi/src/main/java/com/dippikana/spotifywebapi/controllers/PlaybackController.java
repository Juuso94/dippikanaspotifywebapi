package com.dippikana.spotifywebapi.controllers;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
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
import com.dippikana.spotifywebapi.services.Utilities;

@RestController
//@RequestMapping("/api")
public class PlaybackController {

  private String apiUrl = "https://api.spotify.com/v1";

  @GetMapping("/currentlyPlaying")
		public ResponseEntity<Object> fetchCurrentlyPlaying() {

			if(!Utilities.isTokenValid()) {

				if(!Utilities.refreshAuthenticationToken()) {
					return new ResponseEntity<Object>("Application is not logged in", HttpStatus.BAD_REQUEST);
				}
			}

			String apiLocation = "/me/player";
			URI playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation).build().toUri();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(Utilities.getAccessToken());

			HttpEntity entity = new HttpEntity<>(headers);

			ResponseEntity<PlaybackData> response = new RestTemplate().exchange(playerURI, HttpMethod.GET, entity, PlaybackData.class);

			return new ResponseEntity<Object>(response.getBody(), HttpStatus.OK);
		}

		@GetMapping("/startPlayback")
		public ResponseEntity<Object> resumePlayback(@RequestParam(required = false) String songURI) {
			String apiLocation = "/me/player/play";

			if(!Utilities.isTokenValid()) {
				Utilities.refreshAuthenticationToken();
			}

			URI playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation).build().toUri();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(Utilities.getAccessToken());

			HttpEntity<Object> entity;

			if( songURI == null) {
				entity = new HttpEntity<Object>(null, headers);
			}
			else {
				Map<String, String[]> requestBody = new HashMap<String, String[]>();
				String[] uris  = {songURI};
				requestBody.put("uris", uris);
				entity = new HttpEntity<Object>(requestBody, headers);
			}

			ResponseEntity<Void> response = new RestTemplate().exchange(playerURI, HttpMethod.PUT, entity, Void.class);

			return new ResponseEntity<Object>(null, HttpStatus.OK);
		}

		@GetMapping("/stopPlayback")
		public ResponseEntity<Object> stopPlayback() {
			String apiLocation = "/me/player/pause";

			if(!Utilities.isTokenValid()) {
				Utilities.refreshAuthenticationToken();
			}

			URI playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation).build().toUri();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(Utilities.getAccessToken());

			HttpEntity entity = new HttpEntity<>(null, headers);

			System.out.println(entity.toString());

			ResponseEntity<Void> response = new RestTemplate().exchange(playerURI, HttpMethod.PUT, entity, Void.class);

			System.out.println(response);

			return new ResponseEntity<Object>(null, HttpStatus.OK);
		}

		@GetMapping("/addToQueue")
		public ResponseEntity<Object> addToQueue(@RequestParam(name = "songURI") String queryString) {
			String apiLocation = "/me/player/queue";

			if(!Utilities.isTokenValid()) {
				Utilities.refreshAuthenticationToken();
			}

			URI playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation)
			.queryParam("uri", queryString)
			.build().toUri();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(Utilities.getAccessToken());

			HttpEntity entity = new HttpEntity<>(null, headers);

			System.out.println(entity.toString());

			ResponseEntity<Void> response = new RestTemplate().exchange(playerURI, HttpMethod.POST, entity, Void.class);

			System.out.println(response);

			return new ResponseEntity<Object>(null, HttpStatus.OK);
		}
}
