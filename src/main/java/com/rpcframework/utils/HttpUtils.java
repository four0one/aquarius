package com.rpcframework.utils;

import com.dyuproject.protostuff.MapSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.Map;

/**
 * @author wei.chen1
 * @since 2018/1/24
 */
public class HttpUtils {
	private static HttpUtils ourInstance = new HttpUtils();

	public static HttpUtils getInstance() {
		return ourInstance;
	}

	private OkHttpClient client;

	private HttpUtils() {
		client = new OkHttpClient();
	}

	public void postJson(Object object, String url) {
		ObjectMapper mapper = new ObjectMapper();
		RequestBody body = null;
		try {
			body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
					mapper.writeValueAsString(object));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		Request request = new Request.Builder()
				.post(body)
				.url("http://" + url)
				.build();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {

			}

			@Override
			public void onResponse(Response response) throws IOException {
			}
		});
	}

	public <T> T getJson(String url, Class<T> clazz) {
		T result = null;
		try {
			Request request = new Request.Builder()
					.get()
					.url("http://" + url)
					.build();
			Response response = client.newCall(request).execute();
			String respStr = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			result = objectMapper.readValue(respStr, clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getJson(String url) {
		String result = null;
		try {
			Request request = new Request.Builder()
					.get()
					.url("http://" + url)
					.build();
			Response response = client.newCall(request).execute();
			result = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
