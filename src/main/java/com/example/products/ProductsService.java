package com.example.products;
import com.example.entities.Entities.Products;
import com.example.entities.Entities.Provisions;
import com.example.entities.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public boolean validateAuthToken(String authToken){
        if(authToken==null || authToken.isEmpty()){
            return false;
        }
        return true;
    }
    public int updateProductStock(long prId,Products product){
        Optional<Products> productFromRepo=productsRepository.findById(prId);
        System.out.println(productFromRepo);
        if(product.getPrCurrentQuantity()==0){
            return 1;
        }
        else if(product.getPrCurrentQuantity()<0){
            return 2;
        }
        else if(productFromRepo.isPresent()){
            Products existingProduct=productFromRepo.get();

            existingProduct.setPrCurrentQuantity(product.getPrCurrentQuantity());
            productsRepository.save(existingProduct);
            return 0;
        }

        else{
            return 3;
        }
    }

    public boolean addNewProvisions(Provisions provisions){
        if(provisions.getProvId()!=null && provisions.getProvId()<=0){
            throw new IllegalArgumentException("The provision id is invalid");
        }
        else if(provisions.getProvName()==null || provisions.getProvName().isEmpty()){
            throw new IllegalArgumentException("Invalid provision name");
        }
        else if(provisions.getProvPrice()==null || provisions.getProvPrice().compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("Provision price is invalid!");
        }
        else if(provisions.getProvQuantity()<=0){
            throw new IllegalArgumentException("Provision quantity is invalid:expected value>0");
        }
        else if(provisions.getProvPurchaseDate()==null){
            throw new IllegalArgumentException("Invalid provisions purchase date");
        }
        else{
            if(provisions.getProvId()!=null) {
                Optional<Provisions> provisionsOptional = provisionsRepository.findById(provisions.getProvId());
                if(provisionsOptional.isPresent()){
                    return false;
                }
                else{
                    provisionsRepository.save(provisions);
                    return true;
                }
            }
            else{
                provisionsRepository.save(provisions);
                return true;
            }

        }
    }

    public List<Provisions> getAllProvisions(){
        return provisionsRepository.findAll();
    }

    public Provisions getSingleProvisions(Long provId){
        Optional<Provisions> provisionsOptional=provisionsRepository.findById(provId);
        if(provisionsOptional.isPresent()){
            return provisionsOptional.get();
        }
        else{
            return null;
        }
    }

}