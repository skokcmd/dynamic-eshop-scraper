package com.skokcmd.service;

import com.skokcmd.dto.Product;
import com.skokcmd.dto.ScrapingRequest;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EshopScrapingService implements IWebScrapingService<Product, ScrapingRequest> {

    private final ChromeDriver driver;

    @Override
    public List<Product> getItemsFromRequest(ScrapingRequest request) {
        this.driver.get(request.getUrl());
        List<WebElement> foundItems = this.driver.findElements(By.className(request.getProductClassName()));

        return convertFoundItemsToProducts(foundItems, request);
    }

    // Takes WebElement list and parses it to Product list based on values from request
    private List<Product> convertFoundItemsToProducts(List<WebElement> foundItems, ScrapingRequest request) {
        List<Product> products = new ArrayList<>();
        if (foundItems == null || foundItems.isEmpty()) {
            return products;
        }

        foundItems.forEach(item -> {
            String imgSrc = getImgSrc(item, request.getImageSrcAttribute());
            String productName =
                    getItemTextFromClassAndTag(item, request.getTitleClassName(), request.getTitleHtmlTag());

            BigDecimal price = new BigDecimal(
                    getItemTextFromClassAndTag(
                            item, request.getPriceClassName(), request.getPriceHtmlTag())
                            .replaceAll("\\D+", ""));

            Product product = Product.builder()
                    .name(productName)
                    .price(price)
                    .imageSrc(imgSrc)
                    .build();

            products.add(product);
        });

        return products;
    }

    // finds element by tag in the product outer div and then finds element by class in case of duplicated tags
    private String getItemTextFromClassAndTag(WebElement item, String className, String htmlTag) {
        return item.findElement(By.tagName(htmlTag)).findElement(By.className(className)).getText().trim();
    }

    // finds "img" element and gets src attribute value
    private String getImgSrc(WebElement item, String imgSrcAtr) {
        return item.findElement(By.tagName("img")).getAttribute(imgSrcAtr).trim();
    }
}
