# my-retail-api-endpoint-java-sr

This server-side code is a back-end api service written in Java and using the Spring Boot framework. It serves to respond to api requests to retrieve data or update data for products in myRetail's products collection. 

Details of the code include:

* 'router' and 'controller' code written in the 'MyRetailApiServiceJavaController.java' file, located in the '/src/main/java/fakecom/myretail/myRetailAPIServiceJava' directory. The router uses Spring's @GetMapping and @PutMapping annotations to route https requests to handler methods.  

* The @GetMapping annotation, which receives 'GET' requests from the front-end, passes the necessary data to the getProductByID handler method. This method retrieves data from another api source, via a 'GET' request, and pricing data from a remote MongoDB NoSQL source, and assigns them to the corresponding fields of a Product class instance. That Product instance is then rendered to a JSON string automatically by the Spring framework.

* The @PutMapping annotation, which receives 'PUT' requests from the front-end, passes the necessary data to the setProductByID handler method. This method parses the string in the 'data' url parameter and produces a JsonNode object via methods from the 'Jackson' JSON processing library. The JsonNode data is then used to update pricing data in the remote MongoDB NoSQL source.

* CORS restrictions are accounted for by use of Spring's @CrossOrigin annotation which takes an array of allowed origins.
