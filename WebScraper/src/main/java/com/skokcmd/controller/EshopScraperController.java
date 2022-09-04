package com.skokcmd.controller;

import com.skokcmd.dto.Product;
import com.skokcmd.dto.ScrapingRequest;
import com.skokcmd.dto.ScrapingResponse;
import com.skokcmd.service.IWebScrapingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api/v1/scrape-website")
@RequiredArgsConstructor
public class EshopScraperController {

    private final IWebScrapingService<Product, ScrapingRequest> eshopScrapingService;

    @PostMapping
    public ResponseEntity<ScrapingResponse> getProductsForUrl(@RequestBody ScrapingRequest request) throws IOException {
        ScrapingResponse response = ScrapingResponse.builder()
                .products(eshopScrapingService.getItemsFromRequest(request))
                .build();

        return new ResponseEntity<ScrapingResponse>(response, HttpStatus.OK);
    }
}
