package com.dippikana.spotifywebapi.controllers;

import java.util.Base64;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.*;
import org.springframework.web.bind.annotation.*;

import com.dippikana.spotifywebapi.models.TokenResponse;
import com.dippikana.spotifywebapi.services.Utilities;

@Component
@RestController
//@RequestMapping("/admin")
public class AuthenticationController {
	
	@Autowired
	private Utilities utilities;

	@Value("${spotify.client_id}")
	private String client_id;

	@Value("${spotify.client_secret}")
	private String client_secret;

	@Value("${spotify.callback}")
	private String callback;

	@Value("${frontend}")
	private String frontendURI;

	@Value("${tymaPassu}")
	private String tymapassu;

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

  @GetMapping("/login")
	public String loginToSpotify(@RequestParam(value = "kekW", defaultValue = "nicenice")String passu) {

		System.out.println(passu);
		System.out.println(passu.equalsIgnoreCase(tymapassu));

		if(!passu.equals(tymapassu)) {
			return "vitun pelle kuole";
		}

    String permissionScope = "streaming user-read-playback-state";
		String spotifyLoginUrl = "https://accounts.spotify.com/authorize/";
		String fullUrl = UriComponentsBuilder.fromUriString(spotifyLoginUrl).
		queryParam("scope", permissionScope).
		queryParam("client_id", client_id).
		queryParam("redirect_uri", callback).
		queryParam("response_type", "code").build().toUriString();
		
		return fullUrl;
	}

	@GetMapping("/auth/callback")
	public RedirectView callback(@RequestParam(value = "code", defaultValue = "nicenice")String code) {

		Timestamp now = Timestamp.from(Instant.now());

		if (!utilities.isTokenValid()) {
			MultiValueMap<String, String> formValues = new LinkedMultiValueMap<String, String>();
			String auth = client_id + ":" + client_secret;
			String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

			formValues.add("code", code);
			formValues.add("redirect_uri", callback);
			formValues.add("grant_type", "authorization_code");

			URI spotifyTokenUrl = UriComponentsBuilder.fromUriString("https://accounts.spotify.com/api/token").build().toUri();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setBasicAuth(encodedAuth);

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(formValues, headers);

			TokenResponse response = new RestTemplate().postForObject(spotifyTokenUrl, entity, TokenResponse.class);

			utilities.setAccessToken(response.access_token);
			utilities.setRefreshToken(response.refresh_token);
			utilities.setExpireTime(now.getTime() + (response.expires_in * 1000));

		}

		return new RedirectView(frontendURI);		
	}

	@GetMapping("/checkToken")
  public ResponseEntity<Object> checkTokenValidity() {
	
    if(utilities.isTokenValid()) {
      return new ResponseEntity<Object>(null, HttpStatus.OK);
    }
    else {
      return new ResponseEntity<Object>(null, HttpStatus.NOT_FOUND);
    }
  }

	@GetMapping("logout")
	public ResponseEntity<Object> clearTokens() {
		utilities.setAccessToken("");
		utilities.setRefreshToken("");
		utilities.setExpireTime(Long.valueOf(0));

    return new ResponseEntity<Object>(null, HttpStatus.OK);
    
  }

	
}
