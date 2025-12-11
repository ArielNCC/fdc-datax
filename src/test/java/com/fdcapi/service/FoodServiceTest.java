package com.fdcapi.service;

import com.fdcapi.client.FdcClient;
import com.fdcapi.client.dto.FdcFoodItem;
import com.fdcapi.client.dto.FdcNutrient;
import com.fdcapi.client.dto.FdcSearchResponse;
import com.fdcapi.exception.FoodNotFoundException;
import com.fdcapi.model.Food;
import com.fdcapi.service.impl.FoodServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para FoodService
 * Prueba la lógica de negocio sin llamadas reales a la API
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del Servicio de Alimentos")
class FoodServiceTest {

    @Mock
    private FdcClient fdcClient;

    @InjectMocks
    private FoodServiceImpl foodService;

    private FdcFoodItem fdcApple;
    private FdcSearchResponse searchResponse;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba con estructura anidada
        FdcNutrient.NutrientInfo proteinInfo = new FdcNutrient.NutrientInfo();
        proteinInfo.setId(1003L);
        proteinInfo.setName("Protein");
        proteinInfo.setUnitName("g");
        
        FdcNutrient protein = new FdcNutrient();
        protein.setNutrient(proteinInfo);
        protein.setAmount(0.26);

        FdcNutrient.NutrientInfo fatInfo = new FdcNutrient.NutrientInfo();
        fatInfo.setId(1004L);
        fatInfo.setName("Total lipid (fat)");
        fatInfo.setUnitName("g");
        
        FdcNutrient fat = new FdcNutrient();
        fat.setNutrient(fatInfo);
        fat.setAmount(0.17);

        fdcApple = new FdcFoodItem();
        fdcApple.setFdcId(123456L);
        fdcApple.setDescription("Apple, raw");
        fdcApple.setFoodNutrients(Arrays.asList(protein, fat));

        searchResponse = new FdcSearchResponse();
        searchResponse.setFoods(Collections.singletonList(fdcApple));
    }

    @Test
    @DisplayName("Test: Buscar alimentos por nombre - éxito")
    void testSearchFoodsSuccess() {
        // Given
        when(fdcClient.searchFoods("apple")).thenReturn(searchResponse);

        // When
        List<Food> result = foodService.searchFoods("apple");

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Apple, raw");
        assertThat(result.get(0).getNutrients()).hasSize(2);
        
        verify(fdcClient, times(1)).searchFoods("apple");
    }

    @Test
    @DisplayName("Test: Buscar alimentos con query vacío - debe lanzar excepción")
    void testSearchFoodsWithEmptyQuery() {
        // When & Then
        assertThatThrownBy(() -> foodService.searchFoods(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El término de búsqueda no puede estar vacío");
        
        verify(fdcClient, never()).searchFoods(anyString());
    }

    @Test
    @DisplayName("Test: Buscar alimentos con query null - debe lanzar excepción")
    void testSearchFoodsWithNullQuery() {
        // When & Then
        assertThatThrownBy(() -> foodService.searchFoods(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El término de búsqueda no puede estar vacío");
        
        verify(fdcClient, never()).searchFoods(anyString());
    }

    @Test
    @DisplayName("Test: Buscar alimentos sin resultados")
    void testSearchFoodsWithNoResults() {
        // Given
        FdcSearchResponse emptyResponse = new FdcSearchResponse();
        emptyResponse.setFoods(Collections.emptyList());
        when(fdcClient.searchFoods("noexiste")).thenReturn(emptyResponse);

        // When
        List<Food> result = foodService.searchFoods("noexiste");

        // Then
        assertThat(result).isEmpty();
        verify(fdcClient, times(1)).searchFoods("noexiste");
    }

    @Test
    @DisplayName("Test: Obtener alimento por ID - éxito")
    void testGetFoodByIdSuccess() {
        // Given
        when(fdcClient.getFoodDetails(123456L)).thenReturn(fdcApple);

        // When
        Food result = foodService.getFoodById(123456L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFdcId()).isEqualTo(123456L);
        assertThat(result.getDescription()).isEqualTo("Apple, raw");
        assertThat(result.getNutrients()).hasSize(2);
        assertThat(result.getNutrients().get(0).getName()).isEqualTo("Protein");
        
        verify(fdcClient, times(1)).getFoodDetails(123456L);
    }

    @Test
    @DisplayName("Test: Obtener alimento por ID null - debe lanzar excepción")
    void testGetFoodByIdWithNullId() {
        // When & Then
        assertThatThrownBy(() -> foodService.getFoodById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El ID del alimento debe ser un número positivo");
        
        verify(fdcClient, never()).getFoodDetails(anyLong());
    }

    @Test
    @DisplayName("Test: Obtener alimento por ID negativo - debe lanzar excepción")
    void testGetFoodByIdWithNegativeId() {
        // When & Then
        assertThatThrownBy(() -> foodService.getFoodById(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El ID del alimento debe ser un número positivo");
        
        verify(fdcClient, never()).getFoodDetails(anyLong());
    }

    @Test
    @DisplayName("Test: Obtener alimento por ID cero - debe lanzar excepción")
    void testGetFoodByIdWithZeroId() {
        // When & Then
        assertThatThrownBy(() -> foodService.getFoodById(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El ID del alimento debe ser un número positivo");
        
        verify(fdcClient, never()).getFoodDetails(anyLong());
    }

    @Test
    @DisplayName("Test: Obtener alimento por ID que no existe")
    void testGetFoodByIdNotFound() {
        // Given
        when(fdcClient.getFoodDetails(999999L)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> foodService.getFoodById(999999L))
                .isInstanceOf(FoodNotFoundException.class)
                .hasMessage("No se encontró alimento con ID: 999999");
        
        verify(fdcClient, times(1)).getFoodDetails(999999L);
    }

    @Test
    @DisplayName("Test: Mapeo correcto de nutrientes")
    void testNutrientMapping() {
        // Given
        when(fdcClient.getFoodDetails(123456L)).thenReturn(fdcApple);

        // When
        Food result = foodService.getFoodById(123456L);

        // Then
        assertThat(result.getNutrients()).hasSize(2);
        
        // Verificar primer nutriente
        assertThat(result.getNutrients().get(0).getNutrientId()).isEqualTo(1003L);
        assertThat(result.getNutrients().get(0).getName()).isEqualTo("Protein");
        assertThat(result.getNutrients().get(0).getValue()).isEqualTo(0.26);
        assertThat(result.getNutrients().get(0).getUnitName()).isEqualTo("g");
        
        // Verificar segundo nutriente
        assertThat(result.getNutrients().get(1).getNutrientId()).isEqualTo(1004L);
        assertThat(result.getNutrients().get(1).getName()).isEqualTo("Total lipid (fat)");
        assertThat(result.getNutrients().get(1).getValue()).isEqualTo(0.17);
        assertThat(result.getNutrients().get(1).getUnitName()).isEqualTo("g");
    }

    @Test
    @DisplayName("Test: Alimento sin nutrientes")
    void testFoodWithoutNutrients() {
        // Given
        FdcFoodItem fdcFoodWithoutNutrients = new FdcFoodItem();
        fdcFoodWithoutNutrients.setFdcId(111111L);
        fdcFoodWithoutNutrients.setDescription("Food without nutrients");
        fdcFoodWithoutNutrients.setFoodNutrients(null);
        
        when(fdcClient.getFoodDetails(111111L)).thenReturn(fdcFoodWithoutNutrients);

        // When
        Food result = foodService.getFoodById(111111L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNutrients()).isEmpty();
    }

    @Test
    @DisplayName("Test: Búsqueda con espacios en blanco")
    void testSearchFoodsWithWhitespace() {
        // Given
        when(fdcClient.searchFoods("apple")).thenReturn(searchResponse);

        // When
        List<Food> result = foodService.searchFoods("  apple  ");

        // Then
        assertThat(result).isNotEmpty();
        verify(fdcClient, times(1)).searchFoods("apple"); // Verifica que se eliminaron espacios
    }
}
