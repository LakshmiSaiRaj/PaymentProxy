package com.czar.service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClientHelper {
  public  static HttpClient build(){
    HttpClient client = HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_1_1)
      .followRedirects(HttpClient.Redirect.NORMAL)
      .connectTimeout(Duration.ofSeconds(20))
      .build();
    return client;
  } public static HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {

    return build().send(request, HttpResponse.BodyHandlers.ofString());
  }
}
