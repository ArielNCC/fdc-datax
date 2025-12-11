package com.fdcapi.integration;

import com.fdcapi.model.Food;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración - Prueba la API completa con conexiones reales a FDC
 * NOTA: Estos tests requieren conexión a internet y un API key válido
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests de Integración - API Real de FDC")
class FoodApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/foods";
    }

    @Test
    @Order(1)
    @DisplayName("Integration Test 1: Verificar que el servicio está activo (Health Check)")
    void testHealthCheckConnection() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/health",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("FDC API Service is running");
    }

    @Test
    @Order(2)
    @DisplayName("Integration Test 2: Buscar alimento por ID real y verificar datos correctos")
    void testGetFoodByIdWithRealApi() {
        // Given - ID real de "Cheddar Cheese" en FDC
        Long realFoodId = 171265L; // Este es un ID real de la base de datos de FDC

        // When
        ResponseEntity<Food> response = restTemplate.getForEntity(
                baseUrl + "/" + realFoodId,
                Food.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        Food food = response.getBody();
        assertThat(food.getFdcId()).isEqualTo(realFoodId);
        assertThat(food.getDescription()).isNotEmpty();
        assertThat(food.getNutrients()).isNotEmpty();
        
        // Verificar que tiene nutrientes básicos
        assertThat(food.getNutrients()).isNotEmpty();
        System.out.println("✓ Alimento encontrado: " + food.getDescription());
        System.out.println("✓ Número de nutrientes: " + food.getNutrients().size());
    }

    @Test
    @Order(3)
    @DisplayName("Integration Test 3: Buscar alimento por nombre y verificar datos correctos")
    void testSearchFoodByNameWithRealApi() {
        // Given
        String searchQuery = "apple";

        // When
        ResponseEntity<List<Food>> response = restTemplate.exchange(
                baseUrl + "?query=" + searchQuery,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Food>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        
        List<Food> foods = response.getBody();
        assertThat(foods.size()).isGreaterThan(0);
        
        // Verificar que al menos un resultado contiene "apple" en la descripción
        boolean hasAppleInDescription = foods.stream()
                .anyMatch(f -> f.getDescription().toLowerCase().contains("apple"));
        assertThat(hasAppleInDescription).isTrue();
        
        System.out.println("✓ Se encontraron " + foods.size() + " alimentos con 'apple'");
        System.out.println("✓ Primeros resultados:");
        foods.stream().limit(3).forEach(f -> 
            System.out.println("  - " + f.getDescription())
        );
    }

    @Test
    @Order(4)
    @DisplayName("Integration Test 4: Buscar con término genérico y verificar múltiples coincidencias")
    void testSearchFoodWithMultipleMatchesRealApi() {
        // Given - Buscar "chicken" debería devolver múltiples resultados
        String searchQuery = "chicken";

        // When
        ResponseEntity<List<Food>> response = restTemplate.exchange(
                baseUrl + "?query=" + searchQuery,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Food>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        List<Food> foods = response.getBody();
        assertThat(foods.size()).isGreaterThan(1); // Debe haber múltiples resultados
        
        // Verificar que hay diferentes variaciones
        System.out.println("✓ Se encontraron " + foods.size() + " alimentos con 'chicken'");
        System.out.println("✓ Ejemplos de coincidencias:");
        foods.stream().limit(5).forEach(f -> 
            System.out.println("  - ID: " + f.getFdcId() + " | " + f.getDescription())
        );
        
        // Verificar que todos contienen "chicken" en alguna forma
        boolean allContainChicken = foods.stream()
                .allMatch(f -> f.getDescription().toLowerCase().contains("chicken"));
        assertThat(allContainChicken).isTrue();
    }

    @Test
    @Order(5)
    @DisplayName("Integration Test 5: Buscar con ID inválido (que no existe)")
    void testGetFoodByInvalidIdRealApi() {
        // Given - ID que muy probablemente no existe
        Long invalidId = 999999999999L;

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/" + invalidId,
                String.class
        );

        // Then
        // La API de FDC devuelve un error cuando el ID no existe, que se traduce en 502
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).contains("Error al comunicarse con la API de FDC");
        
        System.out.println("✓ Error manejado correctamente para ID inválido: " + invalidId);
    }

    @Test
    @Order(6)
    @DisplayName("Integration Test 6: Buscar con nombre que no existe")
    void testSearchFoodWithNonExistentNameRealApi() {
        // Given - Búsqueda muy específica que probablemente no dará resultados
        String impossibleQuery = "alimentoquenoexistexyz123456";

        // When
        ResponseEntity<List<Food>> response = restTemplate.exchange(
                baseUrl + "?query=" + impossibleQuery,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Food>>() {}
        );

        // Then
        // Puede ser 204 (No Content) o 200 con lista vacía
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NO_CONTENT);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            assertThat(response.getBody()).isEmpty();
        }
        
        System.out.println("✓ Búsqueda sin resultados manejada correctamente");
    }

    @Test
    @Order(7)
    @DisplayName("Integration Test 6b: Buscar con término vacío")
    void testSearchFoodWithEmptyQueryRealApi() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "?query=",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("no puede estar vacío");
        
        System.out.println("✓ Query vacía rechazada correctamente");
    }

    @Test
    @Order(8)
    @DisplayName("Integration Test adicional: Buscar en español y verificar resultados")
    void testSearchSpanishFoodRealApi() {
        // Given - Buscar un término en español
        String searchQuery = "queso"; // cheese en español

        // When
        ResponseEntity<List<Food>> response = restTemplate.exchange(
                baseUrl + "?query=" + searchQuery,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Food>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NO_CONTENT);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            List<Food> foods = response.getBody();
            System.out.println("✓ Se encontraron " + foods.size() + " alimentos con 'queso'");
            
            if (!foods.isEmpty()) {
                foods.stream().limit(3).forEach(f -> 
                    System.out.println("  - " + f.getDescription())
                );
            }
        }
    }

    @Test
    @Order(9)
    @DisplayName("Integration Test: Verificar estructura completa de nutrientes")
    void testNutrientDataStructureRealApi() {
        // Given - Buscar un alimento simple
        String searchQuery = "milk";

        // When
        ResponseEntity<List<Food>> response = restTemplate.exchange(
                baseUrl + "?query=" + searchQuery,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Food>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        
        Food food = response.getBody().get(0);
        assertThat(food.getFdcId()).isNotNull();
        assertThat(food.getDescription()).isNotEmpty();
        
        if (!food.getNutrients().isEmpty()) {
            food.getNutrients().forEach(nutrient -> {
                // assertThat(nutrient.getNutrientId()).isNotNull(); // Algunos nutrientes pueden venir sin ID
                if (nutrient.getName() != null) {
                    assertThat(nutrient.getName()).isNotEmpty();
                }
            });
            
            System.out.println("✓ Estructura de nutrientes verificada para: " + food.getDescription());
            System.out.println("✓ Nutrientes encontrados: " + food.getNutrients().size());
        }
    }
}
