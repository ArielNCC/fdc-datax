package com.fdcapi.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FdcSearchResponse {
    private List<FdcFoodItem> foods;
    private Integer totalHits;
    private Integer currentPage;
    private Integer totalPages;
}
