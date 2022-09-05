package com.skokcmd.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ScrapingRequest {

    private String url;
    private String acceptCookiesClassName;
    private String productClassName;
    private String imageSrcAttribute;
    private String titleClassName;
    private String titleHtmlTag;
    private String priceClassName;
    private String priceHtmlTag;
    private String loadMoreButtonClassName;

}
