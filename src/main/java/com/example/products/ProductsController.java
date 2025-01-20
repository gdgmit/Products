/*
package com.example.products;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductsController {

}
*/

package com.example.products;

import com.example.entities.Entities.Products;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/products")
@Tag(name = "Products API", description = "Endpoints for managing products")
public class ProductsController {

    private final ProductsService productsService;

    @Autowired
    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping("/")
    public ResponseEntity<Object> getProductPage(@RequestHeader(value = "Auth_Token", required = false) String authToken) {
        if (!productsService.validateAuthToken(authToken)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Unauthorized User");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "Products Page");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Add New Product
    @PostMapping
    @Operation(summary = "Add a new product", description = "Create a new product with details like name, price, category, etc.")
    public ResponseEntity<Object> addProduct(
            @RequestBody Products product,
            @RequestHeader(value = "Auth_Token", required = false) String authToken) {
        try {
            Map<String, Object> response = new HashMap<>();
            if (!productsService.validateAuthToken(authToken)) {
                response.put("message", "Unauthorized User");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            Products savedProduct = productsService.addProduct(product); // Save and retrieve the saved entity
            response.put("message", "Product added successfully");
            response.put("productId", savedProduct.getPrId()); // Include the product ID in the response
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidCategoryException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Get All Products
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a list of all products in the database.")
    public ResponseEntity<Object> getAllProducts(
            @RequestHeader(value = "Auth_Token", required = false) String authToken) {
        Map<String, String> response = new HashMap<>();
        if (!productsService.validateAuthToken(authToken)) {
            response.put("message", "Unauthorized User");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        List<Products> products = productsService.getAllProducts();
        if (products == null || products.isEmpty()) {
            response.put("message", "No Products found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Get Single Product
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID.")
    public ResponseEntity<Object> getProductById(
            @PathVariable Long id,
            @RequestHeader(value = "Auth_Token", required = false) String authToken) {
        Map<String, String> response = new HashMap<>();
        if (!productsService.validateAuthToken(authToken)) {

            response.put("message", "Unauthorized User");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Optional<Products> product = productsService.getProductById(id);
        List<Products> products = productsService.getAllProducts();
        if (products == null || products.isEmpty()) {
            response.put("message", "No Products found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        else if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            response.put("message", "Product with ID: " + id + " not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
