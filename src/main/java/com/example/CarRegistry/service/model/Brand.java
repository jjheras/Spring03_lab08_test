package com.example.CarRegistry.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Brand {
    private Integer id;
    private String name;
    private Integer warranty;
    private String country;
}
