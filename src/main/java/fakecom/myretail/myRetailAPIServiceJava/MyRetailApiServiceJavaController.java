package fakecom.myretail.myRetailAPIServiceJava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin(origins = {"https://my-retail-api-consumer-site-sr.herokuapp.com"})
public class MyRetailApiServiceJavaController {

    private final Product product;
    private final CurrentPrice currentPrice;
    private final MongoDbInteractions mongoDbInteractions;

    @Autowired
    public MyRetailApiServiceJavaController(Product product, MongoDbInteractions mongoDbInteractions, CurrentPrice currentPrice) {
        this.product = product;
        this.currentPrice = currentPrice;
        this.mongoDbInteractions = mongoDbInteractions;
    } // end constructor MyRetailApiServiceJavaController()

    @GetMapping("/api/v1/products/{id}")
    public String getProductByID(@PathVariable String id) {
        ObjectMapper mapper = new ObjectMapper();

        String output = "fail_", errorMsg = "";

        try {
            errorMsg = product.getFromAPI(id);
        } catch (NullPointerException e) {
            errorMsg = "null";
        } // end try catch

        if (errorMsg.equals("")) {
            try {
                output = "pass_" + mapper.writeValueAsString(product);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                output += e.getMessage();
            } catch (NullPointerException e) {
                output = "null";
            }// end try catch
        } else {
            output += errorMsg;
        } // end if else
        return output;
    } // end getProductByID()

    @PutMapping("/api/v1/products/{id}")
    public String setProductByID(@PathVariable String id, @RequestParam String data) {
        ObjectMapper mapper = new ObjectMapper();
        String output = "fail";
        try {
            JsonNode jn = mapper.readValue(data, JsonNode.class);
            String value = jn.get("current_price").get("value").asText();
            if (currentPrice.isNumeric(value)) {
                String currency_code = jn.get("current_price").get("currency_code").asText();
                output = mongoDbInteractions.savePriceDataToMongoDb(id, value, currency_code);
            } // end if
        } catch (JsonProcessingException | NullPointerException e) {
            e.printStackTrace();
            output = "empty";
        } // end try catch
        return output;
    } // end setProductByID()

} // end class MyRetailApiServiceJavaController