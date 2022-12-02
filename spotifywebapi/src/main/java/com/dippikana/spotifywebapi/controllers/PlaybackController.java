package com.dippikana.spotifywebapi.controllers;

import java.util.HashMap;
import java.util.Map;
import java.net.URI;


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
import com.dippikana.spotifywebapi.models.QueueResult;
import com.dippikana.spotifywebapi.models.Devices;
import com.dippikana.spotifywebapi.services.Utilities;

@Component
@RestController
//@RequestMapping("/api")
public class PlaybackController {

  private String apiUrl = "https://api.spotify.com/v1";
	private String latestSongId = "";

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

	@GetMapping("/queue")
	public ResponseEntity<Object> fetchQueue() {

		if(!utilities.isTokenValid()) {

			if(!utilities.refreshAuthenticationToken()) {
				return new ResponseEntity<Object>("Something went wrong while refreshing the accesstoken", HttpStatus.BAD_REQUEST);
			}
		}

		String apiLocation = "/me/player/queue";
		URI queueURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation).build().toUri();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(utilities.getAccessToken());

		HttpEntity entity = new HttpEntity<>(headers);

		ResponseEntity<QueueResult> response = new RestTemplate().exchange(queueURI, HttpMethod.GET, entity, QueueResult.class);
		if(response.getStatusCodeValue() == 200) {
			Map<String, Object> responseData = new HashMap<String, Object>();
			responseData.put("queue", response.getBody().queue);
			responseData.put("songID", latestSongId);
			return new ResponseEntity<Object>(responseData, HttpStatus.OK);
		}
		else {
			return utilities.createErrorResponse(HttpStatus.BAD_REQUEST, "Something went wrong with fetching queue");
		}
	}

	@GetMapping("/devices")
	public ResponseEntity<Object> fetchDevices() {

		if(!utilities.isTokenValid()) {

			if(!utilities.refreshAuthenticationToken()) {
				return new ResponseEntity<Object>("Something went wrong while refreshing the accesstoken", HttpStatus.BAD_REQUEST);
			}
		}

		String apiLocation = "/me/player/devices";
		URI queueURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation).build().toUri();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(utilities.getAccessToken());

		HttpEntity entity = new HttpEntity<>(headers);

		ResponseEntity<Devices> response = new RestTemplate().exchange(queueURI, HttpMethod.GET, entity, Devices.class);
		if(response.getStatusCodeValue() == 200) {
			return new ResponseEntity<Object>(response.getBody(), HttpStatus.OK);
		}
		else {
			System.out.println(response);
			return utilities.createErrorResponse(HttpStatus.BAD_REQUEST, "Something went wrong with fetching devices");
		}
	}

	@GetMapping("/startPlayback")
	public ResponseEntity<Object> resumePlayback(@RequestParam(required = false) String deviceID) {
		String apiLocation = "/me/player/play";

		if(!utilities.isTokenValid()) {
			if(!utilities.refreshAuthenticationToken()) {
				return new ResponseEntity<Object>("Something went wrong while refreshing the accesstoken", HttpStatus.BAD_REQUEST);
			}
		}

		URI playerURI;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(utilities.getAccessToken());

		HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);;

		if( deviceID == null) {
			playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation).build().toUri();
		}
		else {
			playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation)
			.queryParam("device_id", deviceID)
			.build().toUri();
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
	public ResponseEntity<Object> addToQueue(@RequestParam(name = "songURI") String songUriString, @RequestParam(name = "songID") String idString) {
		String apiLocation = "/me/player/queue";

		if(!utilities.isTokenValid()) {
			utilities.refreshAuthenticationToken();
		}

		URI playerURI = UriComponentsBuilder.fromUriString(apiUrl + apiLocation)
		.queryParam("uri", songUriString)
		.build().toUri();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(utilities.getAccessToken());

		HttpEntity entity = new HttpEntity<>(null, headers);

		ResponseEntity<Void> response = new RestTemplate().exchange(playerURI, HttpMethod.POST, entity, Void.class);

		if(response.getStatusCodeValue() == 204) {
			latestSongId = idString;
			return new ResponseEntity<Object>(null, HttpStatus.OK);
		}
		else {
			return utilities.createErrorResponse(HttpStatus.BAD_REQUEST, "Something went wrong while adding song to queue");
		}
	}
}
