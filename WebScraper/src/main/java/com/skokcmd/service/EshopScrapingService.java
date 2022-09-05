package com.skokcmd.service;

import com.skokcmd.dto.Product;
import com.skokcmd.dto.ScrapingRequest;
import com.skokcmd.exception.IllegalItemArgumentException;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EshopScrapingService implements IWebScrapingService<Product, ScrapingRequest> {

    Logger logger = LoggerFactory.getLogger(EshopScrapingService.class);

    private final ChromeDriver driver;

    @Override
    public List<Product> getItemsFromRequest(ScrapingRequest request) {
        this.driver.get(request.getUrl());
        WebElement htmlElement = this.driver.findElement(By.tagName("html"));
        safeLoadInitialProductsHtml(htmlElement, request);

        List<WebElement> foundItems = this.driver.findElements(By.className(request.getProductClassName()));

        return convertFoundItemsToProducts(foundItems, request);
    }

    /**
     * Accepts cookies, loads more products and handles exceptions if occurred
     *
     * @param htmlElement root element
     * @param request     from client
     */
    private void safeLoadInitialProductsHtml(WebElement htmlElement, ScrapingRequest request) {
        try {
            // accept cookies
            triggerBtnClickForClassName(request.getAcceptCookiesClassName());
            // load more button loading
            loadProductPages(htmlElement, request.getLoadMoreButtonClassName());
        } catch (InterruptedException e) {
            logger.error("Error during the initial load!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * While there's loadMore btn class, load more products
     *
     * @param htmlElement             root element
     * @param loadMoreButtonClassName that loads products
     * @throws InterruptedException
     */
    private void loadProductPages(WebElement htmlElement, String loadMoreButtonClassName) throws InterruptedException {
        if (loadMoreButtonClassName.isEmpty() || loadMoreButtonClassName == null) {
            return;
        }
        // check if element with the className exists
        while (!this.driver.findElements(By.className(loadMoreButtonClassName)).isEmpty()) {
            triggerBtnClickForClassName(loadMoreButtonClassName);
            htmlElement.sendKeys(Keys.END);
            Thread.sleep(5000);
        }
    }

    /**
     * Triggers click of a button found by class name
     *
     * @param btnClassName of the clickable button
     * @throws InterruptedException
     */
    private void triggerBtnClickForClassName(String btnClassName) throws InterruptedException {
        if (btnClassName.isEmpty() || btnClassName == null) {
            return;
        }
        if (!this.driver.findElements(By.className(btnClassName)).isEmpty()) {
            WebElement button = this.driver.findElement(By.className(btnClassName));

            button.click();
            Thread.sleep(1500);
        }
    }

    private void waitForElementToBeClickable(WebElement element, int seconds) {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.parse(String.format("PT%ds", seconds)));
        wait.until(ExpectedConditions.elementToBeClickable(element));

    }

    private void waitForDocumentToBeLoaded() {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.parse(String.format("PT%ds", 2)));
        wait.until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
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
                    getTextForElementByClassNameOrByTag(item, request.getTitleClassName(), request.getTitleHtmlTag());

            BigDecimal price = new BigDecimal(
                    getTextForElementByClassNameOrByTag(
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

    private String getTextForElementByClassNameOrByTag(WebElement item, String className, String htmlTag) {
        if (!className.isEmpty() && className != null) {
            return item.findElement(By.className(className)).getText().trim();
        } else if (!htmlTag.isEmpty() && htmlTag != null) {
            return item.findElement(By.tagName(htmlTag)).getText().trim();
        }

        logger.error("Invalid item arguments (className or htmlTag) passed!");
        throw new IllegalItemArgumentException("Invalid item arguments (className or htmlTag) passed!");
    }

    // finds "img" element and gets src attribute value
    private String getImgSrc(WebElement item, String imgSrcAtr) {
        return item.findElement(By.tagName("img")).getAttribute(imgSrcAtr).trim();
    }
}
