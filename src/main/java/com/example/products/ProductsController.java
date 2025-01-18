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
    /**
     * Constructs a new ProductsController with the specified ProductsService.
     *
     * <p>This constructor initializes the controller with the given
     * {@link ProductsService} to handle business logic related to products.
     * The {@code @Autowired} annotation is used to automatically inject
     * the required dependency by Spring's Dependency Injection framework.</p>
     *
     * @param productsService the service layer responsible for handling
     *                        operations related to products.
     */

    @Autowired
    public ProductsController(ProductsService productsService){
        this.productsService=productsService;
    }
    /**
     * Updates the stock of a product based on the provided product ID and request body.
     *
     * <p>This endpoint is used to update the stock of a specific product identified by its ID.
     * The request must include an authorization token in the header and a valid request body
     * with the updated product details.</p>
     *
     * <p>Response scenarios:</p>
     * <ul>
     *   <li>Returns <b>401 Unauthorized</b> if the authorization token is invalid or missing.</li>
     *   <li>Returns <b>200 OK</b> if the stock update is successful.</li>
     *   <li>Returns <b>400 Bad Request</b> if:
     *     <ul>
     *       <li>The quantity attribute is missing in the request body.</li>
     *       <li>The quantity value is invalid (e.g., zero or negative).</li>
     *       <li>The product does not exist.</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @param prId       the ID of the product whose stock is to be updated
     * @param updateProduct the updated product details, provided in the request body
     * @param authToken  the authorization token provided in the request header
     * @return a {@link ResponseEntity} containing a message and the corresponding HTTP status
     */
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

    /**
     * Adds new provisions based on the provided {@link Provisions} object.
     *
     * <p>This endpoint allows authorized users to add new provisions to the system.
     * The request must include an authorization token in the header and a valid
     * {@code Provisions} object in the request body.</p>
     *
     * <p>Response scenarios:</p>
     * <ul>
     *   <li>Returns <b>401 Unauthorized</b> if the authorization token is invalid or missing.</li>
     *   <li>Returns <b>200 OK</b> if the provision is added successfully.</li>
     *   <li>Returns <b>409 Conflict</b> if the provision already exists.</li>
     *   <li>Returns <b>400 Bad Request</b> if the provided data is invalid.</li>
     * </ul>
     *
     * @param provisions the {@link Provisions} object containing the details of the provision to add
     * @param authToken  the authorization token provided in the request header
     * @return a {@link ResponseEntity} containing a message, provision ID (if applicable), and the corresponding HTTP status
     */
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
    /**
     * Retrieves all provisions available in the system.
     *
     * <p>This endpoint allows authorized users to fetch a list of all provisions.
     * The request must include an authorization token in the header.</p>
     *
     * <p>Response scenarios:</p>
     * <ul>
     *   <li>Returns <b>401 Unauthorized</b> if the authorization token is invalid or missing.</li>
     *   <li>Returns <b>200 OK</b> with the list of provisions if provisions are available.</li>
     *   <li>Returns <b>404 Not Found</b> if no provisions are found in the system.</li>
     * </ul>
     *
     * @param authToken the authorization token provided in the request header
     * @return a {@link ResponseEntity} containing the list of provisions or an error message with the corresponding HTTP status
     */
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

    /**
     * Retrieves the details of a single provision based on the provided provision ID.
     *
     * <p>This endpoint allows authorized users to fetch details of a specific provision
     * identified by its ID. The request must include an authorization token in the header.</p>
     *
     * <p>Response scenarios:</p>
     * <ul>
     *   <li>Returns <b>401 Unauthorized</b> if the authorization token is invalid or missing.</li>
     *   <li>Returns <b>200 OK</b> with the provision details if the provision is found.</li>
     *   <li>Returns <b>404 Not Found</b> if the provision with the specified ID does not exist.</li>
     * </ul>
     *
     * @param provId    the ID of the provision to retrieve
     * @param authToken the authorization token provided in the request header (required)
     * @return a {@link ResponseEntity} containing the provision details or an error message with the corresponding HTTP status
     */
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
