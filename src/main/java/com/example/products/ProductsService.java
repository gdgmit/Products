/*
package com.example.products;

public class ProductsService {
    
}
*/

package com.example.products;

import com.example.entities.Entities.Products;
import com.example.entities.Enums.ProductCategory;
import com.example.entities.Repositories.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    private final ProductsRepository productsRepository;

    @Autowired
    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    // Validate Auth Token
    public boolean validateAuthToken(String authToken) {
        return authToken != null && !authToken.isEmpty();
    }

    // Add New Product
    public Products addProduct(Products product) {
        if (product == null) {
            throw new IllegalArgumentException("Product details cannot be null.");
        }

        if (product.getPrName() == null || product.getPrName().isEmpty()) {
            throw new IllegalArgumentException("Product name is invalid.");
        }

        if (product.getPrSellingPrice() == null || product.getPrSellingPrice().doubleValue() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }

        if (product.getPrCurrentQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative.");
        }

        // Validate ProductCategory
        if (product.getPrCategory() == null || !isValidCategory(product.getPrCategory())) {
            throw new IllegalArgumentException("Invalid product category. Allowed categories are: VEG, NON_VEG, CHINESE.");
        }
        List<Products> existingProducts = productsRepository.findAll(); // Assumes productsRepository has a method findAll()
        for (Products existingProduct : existingProducts) {
            if (existingProduct.getPrName().equalsIgnoreCase(product.getPrName())) {
                throw new IllegalArgumentException("A product with the same name already exists.");
            }
        }
        return productsRepository.save(product);
    }

    // Helper method to validate category
    private boolean isValidCategory(ProductCategory category) {
        // Assuming ProductCategory is an enum with values: VEG, NON_VEG, CHINESE
        return category == ProductCategory.VEG || category == ProductCategory.NON_VEG || category == ProductCategory.CHINESE;
    }
    // Get All Products
    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    // Get Single Product by ID
    public Optional<Products> getProductById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }
        return productsRepository.findById(id);
    }

    // Update Product Stock
    public int updateProductStock(Long prId, Products product) {
        if (prId == null || prId <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }
        if (product == null || product.getPrCurrentQuantity() == 0) {
            return 1; // Invalid quantity in the request
        } else if (product.getPrCurrentQuantity() < 0) {
            return 2; // Negative quantity value
        }

        Optional<Products> productFromRepo = productsRepository.findById(prId);
        if (productFromRepo.isPresent()) {
            Products existingProduct = productFromRepo.get();
            existingProduct.setPrCurrentQuantity(product.getPrCurrentQuantity());
            productsRepository.save(existingProduct);
            return 0; // Success
        } else {
            return 3; // Product not found
        }
    }
}

