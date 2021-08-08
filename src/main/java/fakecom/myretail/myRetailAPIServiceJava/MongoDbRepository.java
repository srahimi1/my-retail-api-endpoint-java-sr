package fakecom.myretail.myRetailAPIServiceJava;

import org.springframework.stereotype.Repository;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class MongoDbRepository {

    private MongoDatabase database;

    public MongoDbRepository() {
        database = mongoDbConnect("myRetailAPIServiceJava");
    }

    public Document addRandomPriceDataToMongoDb(String id, String price) {
        if (database == null) database = mongoDbConnect("myRetailAPIServiceJava");
        MongoCollection<Document> priceDataCollection = database.getCollection("priceData");
        Document priceData = new Document("id", id);
        Document cp = new Document();
        cp.put("value", price);
        cp.put("currency_code", "USD");
        priceData.put("current_price", cp);
        priceDataCollection.insertOne(priceData);
        return priceData;
    } // end addRandomPriceDataToMongoDb()

    public Document findPriceDataByIdMongoDb(String id) {
        if (database == null) database = mongoDbConnect("myRetailAPIServiceJava");
        MongoCollection<Document> priceDataCollection = database.getCollection("priceData");
        return priceDataCollection.find(eq("id", id)).first();
    } // end findPriceDataByIdMongoDb()

    public String savePriceDataToMongoDb(String id, String value, String cc) {
        String output = "fail";
        if (database == null) database = mongoDbConnect("myRetailAPIServiceJava");
        MongoCollection<Document> priceDataCollection = database.getCollection("priceData");
        Document findCriteria = new Document();
        findCriteria.put("id", id);
        Document setData = new Document();
        setData.put("current_price.value", value);
        setData.put("current_price.currency_code", cc);
        Document setDoc = new Document();
        setDoc.put("$set", setData);
        UpdateResult result = priceDataCollection.updateOne(findCriteria, setDoc);
        if (result.getMatchedCount() == 1L) output = "pass";
        return output;
    } // end savePriceDataToMongoDb()

    private MongoDatabase mongoDbConnect(String dbName) {
        MongoDatabase temp;
        ConnectionString connectionString = new ConnectionString(System.getenv("MONGODB_URI"));
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
        MongoClient mongoClient = MongoClients.create(settings);
        try {
            temp = mongoClient.getDatabase(dbName);
        } catch (IllegalArgumentException e) {
            temp = null;
        } // end try catch
        return temp;
    } // end mongoDbConnect()
} // end class MongoDbRepository