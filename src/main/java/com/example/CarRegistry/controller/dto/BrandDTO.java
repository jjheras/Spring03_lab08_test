package com.example.CarRegistry.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BrandDTO {
    private Integer id;
    private String name;
    private Integer warranty;
    private String country;
}
