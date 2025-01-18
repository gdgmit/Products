package com.example.products;

import com.example.entities.Entities.Provisions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsService productsService;

    @PutMapping("/provisions/{id}")
    public ResponseEntity<?> updateProvision(@PathVariable Long provId, @RequestBody Provisions provision, @RequestHeader(value = "Auth_token", required = true)String authToken){
        try{
            if(authToken ==null||authToken.isEmpty()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Unauthorised: missing or invalid"));
            }
            Provisions updateProvision = productsService.updateProvisionDetails(provId, provision);
            if(updateProvision != null){
                return ResponseEntity.ok(Map.of("message","provisions updated successfully"));
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "provisions not found with id: "+ provId));
            }
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "an error had occured :"+ e.getMessage()));
        }

    }


}
