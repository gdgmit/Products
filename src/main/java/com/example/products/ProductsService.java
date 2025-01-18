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

    /**
     * Constructs a new ProductsService with the specified repositories.
     *
     * <p>This constructor initializes the service with the required
     * {@link ProductsRepository} and {@link ProvisionsRepository}
     * to perform operations related to products and provisions.</p>
     *
     * <p>The {@code @Autowired} annotation ensures that Spring's Dependency
     * Injection framework automatically injects the required repository dependencies.</p>
     *
     * @param productsRepository   the repository responsible for managing product data
     * @param provisionsRepository the repository responsible for managing provision data
     */
    @Autowired
    public ProductsService(ProductsRepository productsRepository,ProvisionsRepository provisionsRepository){
        this.productsRepository=productsRepository;
        this.provisionsRepository=provisionsRepository;
    }

    /**
     * Validates the provided authentication token.
     *
     * <p>This method checks if the given authentication token is non-null and not empty.</p>
     *
     * @param authToken the authentication token to validate
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
    public boolean validateAuthToken(String authToken){
        if(authToken==null || authToken.isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * Updates the stock quantity of a specific product identified by its ID.
     *
     * <p>This method validates the provided product details and updates the stock quantity
     * in the repository if the product exists and the quantity is valid.</p>
     *
     * <p>Return codes:</p>
     * <ul>
     *   <li>Returns {@code 0} if the stock is updated successfully.</li>
     *   <li>Returns {@code 1} if the product's current quantity is zero.</li>
     *   <li>Returns {@code 2} if the product's current quantity is negative.</li>
     *   <li>Returns {@code 3} if the product does not exist in the repository.</li>
     * </ul>
     *
     * @param prId    the ID of the product to update
     * @param product the {@link Products} object containing the updated stock information
     * @return an integer indicating the status of the operation
     */
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

    /**
     * Adds a new provision to the repository.
     *
     * <p>This method validates the provision details before saving them to the repository.
     * If the provision ID already exists, the method returns {@code false}, indicating
     * that the addition failed due to a conflict.</p>
     *
     * <p>Exceptions:</p>
     * <ul>
     *   <li>Throws {@link IllegalArgumentException} if the provision ID, name, price,
     *       quantity, or purchase date is invalid.</li>
     * </ul>
     *
     * @param provisions the {@link Provisions} object containing the provision details
     * @return {@code true} if the provision is added successfully, {@code false} if it already exists
     */
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

    /**
     * Retrieves all provisions from the repository.
     *
     * <p>This method fetches a list of all provisions available in the repository.</p>
     *
     * @return a {@link List} of {@link Provisions} objects containing all provisions
     */
    public List<Provisions> getAllProvisions(){
        return provisionsRepository.findAll();
    }

    /**
     * Retrieves a specific provision based on the provided ID.
     *
     * <p>This method fetches the provision details if the provision ID exists in the repository.
     * Returns {@code null} if the provision does not exist.</p>
     *
     * @param provId the ID of the provision to retrieve
     * @return a {@link Provisions} object if found, or {@code null} if not found
     */
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