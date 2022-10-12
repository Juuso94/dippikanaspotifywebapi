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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.*;
import org.springframework.web.bind.annotation.*;

import com.dippikana.spotifywebapi.models.TokenResponse;
import com.dippikana.spotifywebapi.services.Utilities;


@RestController
//@RequestMapping("/admin")
public class AuthenticationController {

  @GetMapping("/login")
	public String loginToSpotify() {

    String permissionScope = "streaming user-read-playback-state";
		String spotifyLoginUrl = "https://accounts.spotify.com/authorize/";
		String fullUrl = UriComponentsBuilder.fromUriString(spotifyLoginUrl).
		queryParam("scope", permissionScope).
		queryParam("client_id", Utilities.getClientId()).
		queryParam("redirect_uri", "http://localhost:5000/auth/callback").
		queryParam("response_type", "code").build().toUriString();
		
		return fullUrl;
	}

	@GetMapping("/auth/callback")
	public RedirectView callback(@RequestParam(value = "code", defaultValue = "nicenice")String code) {

		Timestamp now = Timestamp.from(Instant.now());

		if (!Utilities.isTokenValid()) {
			MultiValueMap<String, String> formValues = new LinkedMultiValueMap<String, String>();
			String auth = Utilities.getClientId() + ":" + Utilities.getClientSecret();
			String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

			formValues.add("code", code);
			formValues.add("redirect_uri", "http://localhost:5000/auth/callback");
			formValues.add("grant_type", "authorization_code");

			URI spotifyTokenUrl = UriComponentsBuilder.fromUriString("https://accounts.spotify.com/api/token").build().toUri();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setBasicAuth(encodedAuth);

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(formValues, headers);

			TokenResponse response = new RestTemplate().postForObject(spotifyTokenUrl, entity, TokenResponse.class);

			Utilities.setAccessToken(response.access_token);
			Utilities.setRefreshToken(response.refresh_token);
			Utilities.setExpireTime(now.getTime() + (response.expires_in * 1000));

		}

		return new RedirectView("http://localhost:3000/");		
	}

	@GetMapping("/checkToken")
  public ResponseEntity<Object> checkTokenValidity() {
	
    if(Utilities.isTokenValid()) {
      return new ResponseEntity<Object>(null, HttpStatus.OK);
    }
    else {
      return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
    }
  }

	
}
