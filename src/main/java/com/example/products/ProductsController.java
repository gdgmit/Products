package com.example.products;

import com.example.entities.Entities.Products;
import com.example.entities.Entities.Provisions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/prod")
@RestController
public class ProductsController {
    private final ProductsService productsService;
    @Autowired
    public ProductsController(ProductsService productsService){
        this.productsService=productsService;
    }

    @PatchMapping("/products/{prId}/stock")
    public ResponseEntity<Object> updateProductStock(@PathVariable int prId,@RequestBody Products updateProduct,@RequestHeader(value="Auth_Token",required = false)String authToken ){

        Map<String,String> response=new HashMap<>();
        if(!productsService.validateAuthToken(authToken)){
            response.put("message","Unauthorized User");
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }
        int status=productsService.updateProductStock((long)prId,updateProduct);
        if(status==0){
            response.put("message","Stock updated successfully");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        else if(status==1){
            response.put("message","the quantity attribute is missing-Invalid request body");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        else if(status==2){
            response.put("message","Invalid value for Quantity-must be non zero");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        else{
            response.put("message","The Product doesnt exist");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("/provisions")
    public ResponseEntity<Object> addNewProvisions(@RequestBody Provisions provisions,@RequestHeader(value="Auth_Token",required = false) String authToken){
        Map<String,Object> response=new LinkedHashMap<>();
        if(!productsService.validateAuthToken(authToken)){
            response.put("message","Unauthorized User");
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }
        try{
            boolean success= productsService.addNewProvisions(provisions);
            if(success){
                response.put("provId",provisions.getProvId());
                response.put("message","Provisions added successfully");
                return new ResponseEntity<>(response,HttpStatus.OK);
            }
            else{
                response.put("provId",provisions.getProvId());
                response.put("message","Provisions already exist-failed to add");
                return new ResponseEntity<>(response,HttpStatus.CONFLICT);
            }
        }catch (IllegalArgumentException e){
            response.put("provId",provisions.getProvId());
            response.put("message",e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/provisions")
    public ResponseEntity<Object> getAllProvisions(@RequestHeader(value="Auth_Token",required = false) String authToken){

        if(!productsService.validateAuthToken(authToken)){
            Map<String,Object> response=new HashMap<>();
            response.put("message","Unauthorized User");
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }
        else{
            List<Provisions> provisionsList=productsService.getAllProvisions();
            if(provisionsList==null || provisionsList.isEmpty()){
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NOT_FOUND);
            }
            else{
                return new ResponseEntity<>(provisionsList,HttpStatus.OK);
            }
        }
    }

    @GetMapping("/provisions/{provId}")
    public ResponseEntity<Object> getSingleProvision(@PathVariable int provId,@RequestHeader(value="Auth_Token",required = true) String authToken){
        if(!productsService.validateAuthToken(authToken)){
            Map<String,Object> response=new HashMap<>();
            response.put("message","Unauthorized User");
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }
        Provisions provisions=productsService.getSingleProvisions((long)provId);
        if(provisions!=null){
            return new ResponseEntity<>(provisions,HttpStatus.OK);
        }
        else{
            Map<String,Object> response=new LinkedHashMap<>();
            response.put("message","The provision with provId: "+provId+" not found");
            return new ResponseEntity<>(new Provisions(),HttpStatus.NOT_FOUND);
        }
    }

}
