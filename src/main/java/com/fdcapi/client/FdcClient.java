package com.fdcapi.client;

import com.fdcapi.client.dto.FdcFoodItem;
import com.fdcapi.client.dto.FdcSearchResponse;
import com.fdcapi.config.FdcConfig;
import com.fdcapi.exception.FdcApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class FdcClient {

    private final RestTemplate restTemplate;
    private final FdcConfig fdcConfig;

    public FdcClient(RestTemplate restTemplate, FdcConfig fdcConfig) {
        this.restTemplate = restTemplate;
        this.fdcConfig = fdcConfig;
    }

    public FdcSearchResponse searchFoods(String query) {
        try {
            String url = buildSearchUrl(query);
            log.debug("Buscando alimentos con query: {}", query);
            FdcSearchResponse response = restTemplate.getForObject(url, FdcSearchResponse.class);
            log.debug("Respuesta recibida con {} resultados", 
                response != null && response.getFoods() != null ? response.getFoods().size() : 0);
            return response;
        } catch (RestClientException e) {
            log.error("Error al buscar alimentos: {}", e.getMessage());
            throw new FdcApiException("Error al buscar alimentos en FDC API", e);
        }
    }

    public FdcFoodItem getFoodDetails(Long fdcId) {
        try {
            String url = buildFoodDetailsUrl(fdcId);
            FdcFoodItem foodItem = restTemplate.getForObject(url, FdcFoodItem.class);
            log.debug("Respuesta FDC recibida para ID {}: {} nutrientes", 
                fdcId, 
                foodItem != null && foodItem.getFoodNutrients() != null ? foodItem.getFoodNutrients().size() : 0);
            return foodItem;
        } catch (RestClientException e) {
            log.error("Error al obtener detalles del alimento {}: {}", fdcId, e.getMessage());
            throw new FdcApiException("Error al obtener detalles del alimento desde FDC API", e);
        }
    }

    private String buildSearchUrl(String query) {
        return UriComponentsBuilder.fromUriString(fdcConfig.getBaseUrl() + "/foods/search")
                .queryParam("api_key", fdcConfig.getApiKey())
                .queryParam("query", query)
                .queryParam("pageSize", 25)
                .toUriString();
    }

    private String buildFoodDetailsUrl(Long fdcId) {
        return UriComponentsBuilder.fromUriString(fdcConfig.getBaseUrl() + "/food/" + fdcId)
                .queryParam("api_key", fdcConfig.getApiKey())
                .toUriString();
    }
}
