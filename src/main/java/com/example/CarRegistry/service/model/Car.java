package com.example.CarRegistry.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Car {

    private Integer id;
    private Integer brandid;
    private String model;
    private Integer year;
    private Integer numberofdoors;
    private Boolean isconvertible;
    private Integer mileage;
    private Double price;
    private String description;
    private String colour;
    private String fueltype;
}
