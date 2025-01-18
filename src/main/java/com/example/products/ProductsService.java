package com.example.products;
import com.example.entities.Entities.Products;
import com.example.entities.Entities.Provisions;
import com.example.entities.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Provisions updateProvisionDetails(Long provId, Provisions updateProvision) {
        try{
            Optional<Provisions> provisionOptional = provisionsRepository.findById(provId);
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
}