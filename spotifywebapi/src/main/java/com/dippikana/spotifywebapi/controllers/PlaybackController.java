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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.*;
import org.springframework.web.bind.annotation.*;

import com.dippikana.spotifywebapi.models.PlaybackData;
import com.dippikana.spotifywebapi.services.Utilities;

@Component
@RestController
//@RequestMapping("/api")
public class PlaybackController {

  private String apiUrl = "https://api.spotify.com/v1";

	@Autowired
	private Utilities utilities;

  @GetMapping("/currentlyPlaying")
		public ResponseEntity<Object> fetchCurrentlyPlaying() {

			if(!utilities.isTokenValid()) {

				if(!utilities.refreshAuthenticationToken()) {
					return new ResponseEntity<Object>("Something went wrong while refreshing the accesstoken", HttpStatus.BAD_REQUEST);
				}
			}

			String apiLocation = "/me/player";
			URI playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation).build().toUri();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(utilities.getAccessToken());

			HttpEntity entity = new HttpEntity<>(headers);

			ResponseEntity<PlaybackData> response = new RestTemplate().exchange(playerURI, HttpMethod.GET, entity, PlaybackData.class);
			if(response.getStatusCodeValue() == 204) {
				return new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);
			}
			else {
				return new ResponseEntity<Object>(response.getBody(), HttpStatus.OK);
			}
		}

		@GetMapping("/startPlayback")
		public ResponseEntity<Object> resumePlayback(@RequestParam(required = false) String songURI) {
			String apiLocation = "/me/player/play";

			if(!utilities.isTokenValid()) {
				if(!utilities.refreshAuthenticationToken()) {
					return new ResponseEntity<Object>("Something went wrong while refreshing the accesstoken", HttpStatus.BAD_REQUEST);
				}
			}

			URI playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation).build().toUri();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(utilities.getAccessToken());

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
			ResponseEntity<Void> response;

			try {
				response = new RestTemplate().exchange(playerURI, HttpMethod.PUT, entity, Void.class);
			}
			catch (Exception e) {
				response = null;
			}
			

			if(response != null && response.getStatusCodeValue() == 204) {
				return new ResponseEntity<Object>(null, HttpStatus.OK);
			}
			else {
				return utilities.createErrorResponse(HttpStatus.BAD_REQUEST, "Something went wrong while starting playback");
			}

			
		}

		@GetMapping("/stopPlayback")
		public ResponseEntity<Object> stopPlayback() {
			String apiLocation = "/me/player/pause";

			if(!utilities.isTokenValid()) {
				utilities.refreshAuthenticationToken();
			}

			URI playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation).build().toUri();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(utilities.getAccessToken());

			HttpEntity entity = new HttpEntity<>(null, headers);

			ResponseEntity<Void> response;

			try {
				response = new RestTemplate().exchange(playerURI, HttpMethod.PUT, entity, Void.class);
			}
			catch (Exception e) {
				response = null;
			}

			if(response != null && response.getStatusCodeValue() == 204) {
				return new ResponseEntity<Object>(null, HttpStatus.OK);
			}
			else {
				return utilities.createErrorResponse(HttpStatus.BAD_REQUEST, "Something went wrong while stopping playback");
			}
		}

		@GetMapping("/addToQueue")
		public ResponseEntity<Object> addToQueue(@RequestParam(name = "songURI") String queryString) {
			String apiLocation = "/me/player/queue";

			if(!utilities.isTokenValid()) {
				utilities.refreshAuthenticationToken();
			}

			URI playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation)
			.queryParam("uri", queryString)
			.build().toUri();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(utilities.getAccessToken());

			HttpEntity entity = new HttpEntity<>(null, headers);

			ResponseEntity<Void> response = new RestTemplate().exchange(playerURI, HttpMethod.POST, entity, Void.class);

			if(response.getStatusCodeValue() == 204) {
				return new ResponseEntity<Object>(null, HttpStatus.OK);
			}
			else {
				return utilities.createErrorResponse(HttpStatus.BAD_REQUEST, "Something went wrong while adding song to queue");
			}
		}
}
