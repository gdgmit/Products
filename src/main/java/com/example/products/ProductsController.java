package com.example.products;

import com.example.entities.Entities.Products;
import com.example.entities.Entities.Provisions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prod")
public class ProductsController {

    @Autowired
    private ProductsService productsService;


    @PutMapping("/provisions/{id}")
    public ResponseEntity<?> updateProvision(@PathVariable Long id, @RequestBody Provisions provision, @RequestHeader(value = "Auth_token", required = true)String authToken){
        try{
            if(authToken ==null||authToken.isEmpty()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Unauthorised: missing or invalid"));
            }
            Provisions updateProvision = productsService.updateProvisionDetails(id, provision);
            if(updateProvision != null){
                return ResponseEntity.ok(Map.of("message","provisions updated successfully"));
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "provisions not found with id: "+ id));
            }
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "an error had occured :"+ e.getMessage()));
        }

    }

    @DeleteMapping("/provisions/{provId}")
    public ResponseEntity<?> deleteProvision(@PathVariable Long provId,@RequestHeader(value = "Auth_token", required = true) String authToken){
        try {
            if (authToken == null || authToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized: missing or invalid"));
            }

            boolean isDeleted = productsService.deleteProvisionById(provId);

            if (isDeleted) {
                return ResponseEntity.ok(Map.of("message", "Provision deleted successfully"));

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Provision not found with id: " + provId));
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An error occurred: "+e.getMessage()));
        }
    }

    @GetMapping("/products/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String category) {
        try {
            List<Products> products = productsService.getProductsByCategory(category);
            if (products.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No products found for category: " + category));
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/products/low-stock")
    public ResponseEntity<?> getLowStockProducts(@RequestParam int threshold) {
        try {
            List<Products> lowStockProducts = productsService.getLowStockProducts(threshold);
            if (lowStockProducts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No products found with low stock"));
            }
            return ResponseEntity.ok(lowStockProducts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An error occurred: " + e.getMessage()));
        }
    }

}
