package fakecom.myretail.myRetailAPIServiceJava;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;

@Component
public class Product {
    private final HttpClient client = HttpClient.newHttpClient();
    private String id, product_name, product_description, product_image_url, errorMsg;
    private final CurrentPrice current_price;
    private final MongoDbInteractions mongoDbInteractions;

    @Autowired
    public Product(MongoDbInteractions mongoDbInteractions, CurrentPrice currentPrice) {
        this.mongoDbInteractions = mongoDbInteractions;
        this.current_price = currentPrice;
        productReset();
    } // end constructor Product()

    private void productReset() {
        setId("-1");
        setProduct_name("");
        setProduct_description("");
        setProduct_image_url("");
        setErrorMsg("");
        current_price.setValue("");
        current_price.setCurrency_code("");
    } // end productReset()

    public String getFromAPI(String id) {
        String output = "";
        String uriString = String.format(System.getenv("REMOTE_API_URI_1"), id);
        URI uri = URI.create(uriString);
        HttpRequest request = HttpRequest.newBuilder(uri).build();
        productReset();
        try {
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            output = setProductFields(id, response.body().toString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            output = e.getMessage();
        } catch (NullPointerException e) {
            output = "null";
        } // end try catch
        return output;
    } // end getFromAPI()

    private String setProductFields(String id, String response) {
        String output = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jn = mapper.readTree(response);
            setId(id);
            setProduct_name(jn.get("product").get("item").get("product_description").get("title").asText());
            setProduct_description(jn.get("product").get("item").get("product_description").get("downstream_description").asText());
            setProduct_image_url(jn.get("product").get("item").get("enrichment").get("images").get(0).get("base_url").asText() + jn.get("product").get("item").get("enrichment").get("images").get(0).get("primary").asText());
            Document productPriceData = mongoDbInteractions.findPriceDataByIdMongoDb(id);
            if (productPriceData == null) {
                Random rand = new Random();
                String price = Integer.toString(rand.nextInt(200) + 100);
                productPriceData = mongoDbInteractions.addRandomPriceDataToMongoDb(id,price);
            } // end if
            String value = ((Document)productPriceData.get("current_price")).get("value").toString();
            String currency_code = ((Document)productPriceData.get("current_price")).get("currency_code").toString();
            current_price.setValue(value);
            current_price.setCurrency_code(currency_code);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            output = e.getMessage();
        } catch (NullPointerException e) {
            e.printStackTrace();
            output = String.format("Product with id %s not found", id);
        } // end try catch
        return output;
    } // end setProductFields()

    public void setId(String id) {
        this.id = id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public void setProduct_image_url(String product_image_url) {
        this.product_image_url = product_image_url;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getId() {
        return id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public String getProduct_image_url() {
        return product_image_url;
    }

    public CurrentPrice getCurrent_price() {
        return current_price;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
} // end class Product