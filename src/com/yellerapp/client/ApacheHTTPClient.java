package com.yellerapp.client;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ApacheHTTPClient implements HTTPClient {
	private final HttpClient http;
	private final ObjectMapper mapper;

	public ApacheHTTPClient() {
		this.http = new DefaultHttpClient();
		http.getParams().setParameter("http.socket.timeout", new Integer(2000));
		this.mapper = new ObjectMapper();
		this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	public void post(String url, FormattedException exception)
			throws IOException, AuthorizationException {
		HttpPost post = new HttpPost(url);
		final String encoded = encode(exception);
		post.setEntity(new StringEntity(encoded));
		HttpResponse response = http.execute(post);
		if (response.getStatusLine().getStatusCode() == 401) {
			throw new AuthorizationException("API key was invalid. For more, see http://yellerapp.com/docs/invalid-version");
		}
	}

	private String encode(FormattedException exception) throws JsonProcessingException {
		return this.mapper.writeValueAsString(exception);
	}
}
