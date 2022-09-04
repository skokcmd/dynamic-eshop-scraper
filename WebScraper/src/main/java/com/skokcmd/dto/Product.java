package com.skokcmd.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class Product {
    private String name;
    private String imageSrc;
    private BigDecimal price;
}
