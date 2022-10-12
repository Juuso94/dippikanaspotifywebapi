package com.dippikana.spotifywebapi.services;

import java.util.Base64;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.*;

import com.dippikana.spotifywebapi.models.TokenResponse;
import com.dippikana.spotifywebapi.services.Utilities;

public class Utilities {
  private static String accessToken = "";
	private static String refreshToken = "";
	private static Timestamp expireTime = new Timestamp(0);

	// Move to conf
	private static final String spotifyClientId = "";
	private static final String spotifyClientSecret = "";

	public static String getClientId(){
		return spotifyClientId;
	}

	public static String getClientSecret(){
		return spotifyClientSecret;
	}

	public static String getAccessToken() {
		return accessToken;
	}

	public static String getRefreshToken() {
		return refreshToken;
	}

	public static void setAccessToken(String newAccessToken) {
		accessToken = newAccessToken;
	}

	public static void setRefreshToken(String newRefreshToken) {
		refreshToken = newRefreshToken;
	}

	public static Timestamp getExpireTime() {
		return expireTime;
	}

	public static void setExpireTime(Long newExpireTime) {
		expireTime.setTime(newExpireTime);;
	}

  public static boolean isTokenValid() {
    Timestamp now = Timestamp.from(Instant.now());	
    return (!accessToken.isBlank() && expireTime.after(now));
  }

	public static boolean refreshAuthenticationToken() {

		if(refreshToken.isEmpty()) { 
			return false;
		}

		Timestamp now = Timestamp.from(Instant.now());

		MultiValueMap<String, String> formValues = new LinkedMultiValueMap<String, String>();
			String auth = spotifyClientId + ":" + spotifyClientSecret;
			String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

			formValues.add("refresh_token", refreshToken);
			formValues.add("redirect_uri", "http://localhost:5000/auth/callback");
			formValues.add("grant_type", "refresh_token");

		URI spotifyTokenUrl = UriComponentsBuilder.fromUriString("https://accounts.spotify.com/api/token").build().toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBasicAuth(encodedAuth);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(formValues, headers);

		TokenResponse response = new RestTemplate().postForObject(spotifyTokenUrl, entity, TokenResponse.class);

		setAccessToken(response.access_token);
		Utilities.setExpireTime(now.getTime() + (response.expires_in * 1000));
		return true;
	}
}
