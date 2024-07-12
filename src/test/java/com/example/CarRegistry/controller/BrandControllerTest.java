package com.example.CarRegistry.controller;

import com.example.CarRegistry.config.SecurityConfig;
import com.example.CarRegistry.controller.dto.BrandDTO;
import com.example.CarRegistry.repository.BrandRepository;
import com.example.CarRegistry.repository.CarRepository;
import com.example.CarRegistry.repository.UserRepository;
import com.example.CarRegistry.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(BrandController.class)
public class BrandControllerTest {
    private static final Logger log = LoggerFactory.getLogger(BrandControllerTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BrandController brandController;

    @MockBean
    private BrandService brandService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CarRepository carRepository;
    @MockBean
    private BrandRepository brandRepository;
    @Autowired
    private SecurityConfig securityConfig;

    private BrandDTO brandDTO1;
    private BrandDTO brandDTO2;

    @BeforeEach
    void setUp() {
        brandDTO1 = new BrandDTO(1, "testBrand1", 1, "testCountry1");
        brandDTO2 = new BrandDTO(2, "testBrand2", 2, "testCountry2");
    }

    @Test
    @WithMockUser(roles = "CLIENT")
        //simular role
    void test_getBrandById() throws Exception {

        //El comportamiento que esperamos del metodo
        when(brandService.getBrandById(1)).thenReturn(brandDTO1);
        //Ejecución de la solicitud GET simulada y verificando la respuesta.
        this.mockMvc.perform(MockMvcRequestBuilders.get("/getBrand/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(brandDTO1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(brandDTO1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.warranty").value(brandDTO1.getWarranty()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.country").value(brandDTO1.getCountry()));

    }
    //Prueba para obtener todas las marcas
    @Test
    @WithMockUser(roles = "CLIENT") //simular role
    void test_showBrand() throws Exception{
        //crear lista de brand
        List<BrandDTO> BrandDTOList = Arrays.asList(brandDTO1,brandDTO2);
        CompletableFuture<List<BrandDTO>> completableFuture =CompletableFuture.completedFuture(BrandDTOList);
        // Simular el servicio para devolver la lista de BrandDTOs
        when(brandService.getAllBrands()).thenReturn(completableFuture);
        log.info("Lista BranDTO "+BrandDTOList);

        // Realizar solicitud GET y verificar
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/brand")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();


        // Esperar a que la tarea asíncrona se complete
        this.mockMvc.perform(asyncDispatch(result))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(brandDTO1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(brandDTO1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].warranty").value(brandDTO1.getWarranty()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].country").value(brandDTO1.getCountry()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(brandDTO2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(brandDTO2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].warranty").value(brandDTO2.getWarranty()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].country").value(brandDTO2.getCountry()))
                .andExpect(status().isOk());//verificar que la respuesta es 200;
    }

    @Test
    @WithMockUser(roles = "VENDOR")
        //simular role
    void test_addBrand() throws Exception {
        //comprobar comportamiento
        doNothing().when(brandService).addBrand(brandDTO1);

        //Ejecución de la solicitud POST y verificar respuesta
        this.mockMvc.perform(MockMvcRequestBuilders.post("/addBrand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"id\": 1, \"name\": \"testBrand1\", \"warranty\": 1, \"country\": \"testCountry1\" }"))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "VENDOR")
    void test_addBrands() throws Exception {
        //Comportamientos que esperamos del metodo
        CompletableFuture<Void> completableFuture = CompletableFuture.completedFuture(null);
        when(brandService.addBrands(any(List.class))).thenReturn(completableFuture);

        //Ejecución del POST simulada y verificando la respuesta
        this.mockMvc.perform(MockMvcRequestBuilders.post("/addBrands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{ \"id\": 1, \"name\": \"testBrand1\", \"warranty\": 1, \"country\": \"testCountry1\" }, " +
                                "{ \"id\": 2, \"name\": \"testBrand2\", \"warranty\": 2, \"country\": \"testCountry2\" }]"))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "VENDOR")
    void test_deleteBrand() throws Exception {
        //El comportamiento que esperamos del metodo
        doNothing().when(brandService).deleteBrandById(1);

        //Ejecución de DELETE y verificar respuesta
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/delBrand/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "VENDOR")
    void test_updateBrand() throws Exception {
        //comprobar comportamiento del metodo
        doNothing().when(brandService).updateBrand(1, brandDTO1);

        //Ejecución de Put y verificar respuesta
        this.mockMvc.perform(MockMvcRequestBuilders.put("/putBrand/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"id\": 1, \"name\": \"updatedBrand\", \"warranty\": 1, \"country\": \"updatedCountry\" }"))
                .andExpect(status().isOk());
    }
}
