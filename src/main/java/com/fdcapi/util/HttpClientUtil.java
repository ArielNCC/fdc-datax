package com.fdcapi.util;

import com.fdcapi.exception.ApiException;
import com.fdcapi.exception.ErrorCodes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClientUtil {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private HttpClientUtil() {
        // Utility class
    }

    public static String get(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            } else if (response.statusCode() == 404) {
                throw new ApiException(ErrorCodes.FOOD_NOT_FOUND, "Resource not found: " + url);
            } else if (response.statusCode() == 401) {
                throw new ApiException(ErrorCodes.INVALID_API_KEY, "Invalid API key");
            } else {
                throw new ApiException(ErrorCodes.EXTERNAL_API_ERROR, 
                    "External API returned status: " + response.statusCode());
            }
        } catch (IOException e) {
            throw new ApiException(ErrorCodes.EXTERNAL_API_ERROR, 
                "Network error communicating with FDC API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(ErrorCodes.EXTERNAL_API_ERROR, 
                "Request interrupted", e);
        }
    }

    public static HttpClient getHttpClient() {
        return httpClient;
    }
}
