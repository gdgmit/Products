package com.example.products;
import com.example.entities.Entities.Products;
import com.example.entities.Entities.Provisions;
import com.example.entities.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductsService {
    private final ProductsRepository productsRepository;
    private final ProvisionsRepository provisionsRepository;

    @Autowired
    public ProductsService(ProductsRepository productsRepository,ProvisionsRepository provisionsRepository){
        this.productsRepository=productsRepository;
        this.provisionsRepository=provisionsRepository;
    }

    public List<Products> getAllProducts(){
        return productsRepository.findAll();
    }
    public Products addProduct(Products products){
        productsRepository.save(products);

        return products;
    }
    
    public List<Products> getProductsByCategory(String category){
        List<Products> allProducts = productsRepository.findAll();
        return allProducts.stream().filter(product->product.getPrCategory().equals(category)).collect(Collectors.toList());
    }
    public Provisions updateProvisionDetails(Long id, Provisions updateProvision) {
        try{
            Optional<Provisions> provisionOptional = provisionsRepository.findById(id);
            if (provisionOptional.isPresent()) {
                Provisions provision = provisionOptional.get();
                if (updateProvision.getProvName() != null) {
                    provision.setProvName(updateProvision.getProvName());
                }
                if (updateProvision.getProvPrice() != null) {
                    provision.setProvPrice(updateProvision.getProvPrice());
                }
                if (updateProvision.getProvQuantity() != null) {
                    provision.setProvQuantity(updateProvision.getProvQuantity());
                }
                if (updateProvision.getProvPurchaseDate() != null) {
                    provision.setProvPurchaseDate(updateProvision.getProvPurchaseDate());
                }
                return provisionsRepository.save(provision);

            } else {
                return null;
            }
        }
        catch(Exception e){
            throw new RuntimeException("Failed to update the provision details: " +e.getMessage());
        }

    }

    public boolean deleteProvisionById(Long provId) {
        try {
            if (provisionsRepository.existsById(provId)) {
                provisionsRepository.deleteById(provId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete provision: " + e.getMessage());
        }
    }

    public List<Products> getLowStockProducts(int threshold) {
        List<Products> allProducts = productsRepository.findAll();
        return allProducts.stream()
                .filter(product -> product.getPrCurrentQuantity() < threshold) // Filter products with low stock
                .collect(Collectors.toList());
    }
}
