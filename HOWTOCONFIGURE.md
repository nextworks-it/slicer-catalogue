
# 5GEVE Portal Catalogue

## Instruction for configuring the 5GEVE Portal Catalogue application

For properly configuring the 5GEVE Portal Catalogue, the [application.properties](https://github.com/nextworks-it/slicer-catalogue/blob/5geve-release/EVE_CATALOGUE_APP/src/main/resources/application.properties)
has to be modified.

### POSTGRES
This is the URL to the postgres db to be used by the 5GEVE Portal Catalogue
```
spring.datasource.url= jdbc:postgresql://localhost:5432/5geveportalcatalogue
```
Username configured for 5geveportalcatalogue
```	
spring.datasource.username=postgres
```
Password configured for 5geveportalcatalogue
```
spring.datasource.password=postgres
```
If you want to not drop db data when the application is restarted, this has to be put in "update" mode
```
spring.jpa.hibernate.ddl-auto=create-drop
```

### BINDING TOMCAT ON MANAGEMENT IP ADDRESS
Server is running by default on 0.0.0.0, port can be changed
```
server.port=8082
```
### NFVO CATALOGUE
Type of NFVO catalogue type to use. For the moment only DUMMY is fully supported.
```
nfvo.catalogue.type=DUMMY
```

