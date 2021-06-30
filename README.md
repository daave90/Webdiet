#Swagger 
Json - http://localhost:8080/v2/api-docs  
UI - http://localhost:8080/swagger-ui/
#Build
1. Install npm and nodejs
2. Go to main directory and build all modules `mvn clean package`
3. To run webdiet-rest go to **webdiet-rest** and invoke following command `mvn spring-boot:run`
4. To run webdiet-ui go to **webdiet-ui/src/main/web** and invoke following command `ng serve`