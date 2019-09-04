
## Project OrderManager: A Database for Managing Products and Orders
    

### Project Description 
The technologies used for this project is JavaDB and SQL. The project developed and documented a data model for representing  5 entities and 4 relationships in an order management system, provided DDL for creating the tables, DML for adding entries in the tables, and DQL for making commonly used queries to retrieve product, inventory, and order information from the database.   
The project is to enable business for manage information about products that can be sold to customers, to track current inventories of products, and to process orders for products from customers. The data model for the project is based on the concept of products that can be purchased, inventories of products available for purchase, customers who purchase products, and orders for products by costumers.    
### Entities:
#### Product   
- ProductName: string  
- Description: string 
- SKU: a 12-character value of the form AA-NNNNNN-CC where A is an upper-case letter, N is a digit from 0-9, and C is either a digit or an uppper case letter. For example, "AB-123456-0N"
#### InventoryRecord 
- Units: non-negative int
- UnitPrice: positive, with 2 digits after the decimal place    
- SKU: a 12-character value of the form AA-NNNNNN-CC where A is an upper-case letter, N is a digit from 0-9, and C is either a digit or an uppper case letter. For example, "AB-123456-0N"
#### Customer 
- CustomerId: gensym
- Customername: strings
- Address: String
- City: string
- State: string
- Country: string
#### TheOrder 
-	CustomerId: gensym
-	OrderID: gensym
-	OrderDate: date
-	ShipmentDate: date(default null)
#### OrderRecord 
- OrderID:g ensym
- Unitprice: positive, with 2 digits after the decimal place    
- Numberunits: The item must be available, and the inventory is automatically reduced when an order record is created for an order.
### Relationships:
- #### OrderIn: Product-OrderRecord  
    A one-to-many relationship between Product and OrderRecord. A product can be belong to many OrderRecord. One orderRecord has only one product.    
- #### OrderFrom: Product-InventotyRecord  
    A one-to-one relationship between Product and InventotyRecord. one product has one InventotyRecord. One InventotyRecord has one products.  
- #### OrderBy: TheOrder-Customer  
    A one-to-many relationship between TheOrder and Customer. A Customer can order many TheOrder. One TheOrder has only one Customer.
- #### RecordIn: TheOrder-OrderRecord  
    A one-to-many relationship between TheOrder and OrderRecord. A TheOrder can record in OrderRecord. One OrderRecord has only one TheOrder.    
    
### ER Diagram
![image](https://media.github.ccs.neu.edu/user/2837/files/aba5f900-f304-11e8-8515-61139542a445)

### Getting Started
To get started you can simply run the OrderManager.java first and then TestOrderManager.java.   
The OrderManager.java created:
1. Five Entity tables with constrains such as check SKU, not null, meet required format, etc.
2. One stored function (isSKU) which make sure the SKU matches its required format  
3. One trigger:  make sure product unit will auto substract from InventoryRecord when valid orders are made.(order quality will be checked by the check (unit >= 0) constrain, if not valid, order will roll back) .    
4. Transaction: If the order is back-ordered, a single orderRecord failed (throw exception) the whole order will be rolled back.  
 
ps: The TestOrderManager.java is explained in 'Run Test' section.
### Run Test
To run the test, you can run the TestOrderManager.java. The TestOrderManager.java covers the following:
1.  Stored Function isSKU: created test that invoke the stored function isSKU using a values query  
2.  Tested the create, delete and update operations for all Five entity tables.
3.  Tested the edge cases for trigger (transaction): orderrecord.unit > prouct.unit and product unit auto substract   
4.  Tested ordermanage system by simulating a real world order process and transaction.    
Please see the detailed test coverage and explaination in the test_result.pdf 



 


