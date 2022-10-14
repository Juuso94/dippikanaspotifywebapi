package com.dippikana.spotifywebapi.services;

import java.util.Base64;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.*;

import com.dippikana.spotifywebapi.models.TokenResponse;
import com.dippikana.spotifywebapi.services.Utilities;

@Component("utilities")
public class Utilities {

  private String accessToken = "";
	private String refreshToken = "";
	private Timestamp expireTime = new Timestamp(0);

	@Value("${spotify.client_id}")
	private String client_id;
	@Value("${spotify.client_secret]")
	private String client_secret;

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setAccessToken(String newAccessToken) {
		accessToken = newAccessToken;
	}

	public void setRefreshToken(String newRefreshToken) {
		refreshToken = newRefreshToken;
	}

	public Timestamp getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long newExpireTime) {
		expireTime.setTime(newExpireTime);;
	}

  public boolean isTokenValid() {
    Timestamp now = Timestamp.from(Instant.now());	
    return (!accessToken.isBlank() && expireTime.after(now));
  }

	public boolean refreshAuthenticationToken() {

		if(refreshToken.isEmpty()) { 
			return false;
		}

		Timestamp now = Timestamp.from(Instant.now());

		MultiValueMap<String, String> formValues = new LinkedMultiValueMap<String, String>();
			String auth = client_id + ":" + client_secret;
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
		setExpireTime(now.getTime() + (response.expires_in * 1000));
		return true;
	}
}
