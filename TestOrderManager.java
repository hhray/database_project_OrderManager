import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class TestOrderManager {
    public static void main(String[] args) {
        // the default framework is embedded
        String protocol = "jdbc:derby:";
        String dbName = "OrderManager";
        String connStr = protocol + dbName + ";create=true";

        // tables created by this program
        String dbTables[] = {
                "InventoryRecord",  "OrderRecord", "SingleOrder", "Customer", "Product"        // entities
        };

        // triggers created by this program
        String dbTriggers[] = {
                "ReduceInventory"
        };

        String dbFunctions[] = {
                "isSKU"
        };

        Properties props = new Properties(); // connection properties

        props.put("user","user1");
        props.put("password","user1");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try(
                // connect to the database using URL
                Connection conn = DriverManager.getConnection(connStr, props);

                // statement is channel for sending commands thru connection
                Statement stmt = conn.createStatement();
        ){

            System.out.println("Connected to and created database " + dbName);
            // clear data from tables
            for (String tbl : dbTables) {
                try {
                    stmt.executeUpdate("delete from " + tbl);
                    System.out.println("Truncated table " + tbl);
                } catch (SQLException ex) {
                    System.out.println("Did not truncate table " + tbl);
                }
            }


            //Test function
            // create test that invoke the stored function isSKU using a values query

            // test valid isSKU
            System.out.println("______________________________");
            System.out.println("Test Function for isSKU START ");
            rs = stmt.executeQuery("values isSKU('AA-000000-A0')");
            rs.next();
            boolean isSKU = rs.getBoolean(1);
            if (isSKU) {
                System.out.printf("isSKU 'AA-000000-A0' is valid\n");
            } else {
                System.out.printf("isSKU'AA-000000-A0'  is invalid\n");
            }
            rs.close();

            // test invalid isSKU
            rs = stmt.executeQuery("values isSKU('AA-000-A0')");
            rs.next();
            isSKU = rs.getBoolean(1);
            if (isSKU) {
                System.out.printf("isSKU 'AA-000-A0' is valid\n");
            } else {
                System.out.printf("isSKU'AA-000-A0'  is invalid\n");
            }
            rs.close();
            System.out.println("Test Function for isSKU END  ");
            System.out.println("______________________________");


            // populate Customer table
            String[] insertRow_Customer = {
                    "insert into Customer (Name, Address, City, State, Country) " +
                    "values('Harry', 'Street1', 'San Jose','CA','US')",
                    "insert into Customer (Name, Address, City, State, Country) " +
                    "values('Sally', 'Street2', 'San Jose','CA','US')",
                    "insert into Customer (Name, Address, City, State, Country) " +
                    "values('George', 'Street3', 'Fremont','CA','US')",
                    "insert into Customer (Name, Address, City, State, Country) " +
                    "values('Ada', 'Street4', 'Fremont','CA','US')",
            };
            for (String row: insertRow_Customer) {
                stmt.executeUpdate(row);
            }
            System.out.println();
            System.out.println("Inserted rows into table Customer");
            System.out.println("Test for customer table including insert(create), delete, update START");
            System.out.println("1: Test inserted (created) customer information and auto-genarated ID");
            // query for contents of Customer table
            String queryAll_Customer = "select * from Customer";
            rs = stmt.executeQuery(queryAll_Customer);
            System.out.println("\nCustomer:");
            System.out.printf("%-8s  %-8s   %-8s  %8s  %-8s  %-8s\n",
                    "CustomerId", "Name","Address","City","State","Country");
            while (rs.next()) {
                String cid = rs.getString("CustomerId");
                String name = rs.getString("Name");
                String address = rs.getString("Address");
                String city = rs.getString("City");
                String state = rs.getString("State");
                String country = rs.getString("Country");
                System.out.printf("%-10s  %-8s    %-8s  %-8s  %-8s  %-8s\n",
                        cid, name,address,city,state,country);
            }
            rs.close();
            System.out.println("");
            System.out.println("2: Test Deleted customer(name: ada Id 104) information");
            stmt.executeUpdate("delete from Customer where CustomerId = 4");
            // query for contents of Customer table
            queryAll_Customer = "select * from Customer";
            rs = stmt.executeQuery(queryAll_Customer);
            System.out.println("\nCustomer:");
            System.out.printf("%-8s  %-8s   %-8s  %8s  %-8s  %-8s\n",
                    "CustomerId", "Name","Address","City","State","Country");
            while (rs.next()) {
                String cid = rs.getString("CustomerId");
                String name = rs.getString("Name");
                String address = rs.getString("Address");
                String city = rs.getString("City");
                String state = rs.getString("State");
                String country = rs.getString("Country");
                System.out.printf("%-10s  %-8s    %-8s  %-8s  %-8s  %-8s\n",
                        cid, name,address,city,state,country);
            }
            rs.close();
            System.out.println("");
            System.out.println("3: Test Updated customer(id = 3 old Address: street3; new Address : new street 3) information");
            stmt.executeUpdate("Update Customer set Address = 'new street 3' where CustomerId = 3");
            queryAll_Customer = "select * from Customer";
            rs = stmt.executeQuery(queryAll_Customer);
            System.out.println("\nCustomer:");
            System.out.printf("%-8s  %-8s   %-18s  %8s  %-8s  %-8s\n",
                    "CustomerId", "Name","Address","City","State","Country");
            while (rs.next()) {
                String cid = rs.getString("CustomerId");
                String name = rs.getString("Name");
                String address = rs.getString("Address");
                String city = rs.getString("City");
                String state = rs.getString("State");
                String country = rs.getString("Country");
                System.out.printf("%-10s  %-8s    %-18s  %-8s  %-8s  %-8s\n",
                        cid, name,address,city,state,country);
            }

            System.out.println();
            System.out.println("Test for customer table including insert(create), delete, update END");
            System.out.println("______________________________");


            //Product table
            String pInsertRow_Product = "insert into Product values(?, ?, ?)";
            pstmt = conn.prepareStatement(pInsertRow_Product);
            String preparedRow_Product[][] = {
                    {"AA-000000-0A", "MacBook Air","Laptop"},
                    {"AA-000000-0B", "MacBook","Laptop"},
                    {"AA-000000-0C", "MacBook Pro","Laptop"},
                    {"AA-000000-0D", "Ipad Pro","Ipad"},
                    {"AA-000000-0E", "Ipad min","Ipad"},
                    {"AA-000000-0F", "Apple Watch","Watch"},
                    {"AA-000000-0G", "Apple TV","TV"},
                    {"AA-000000-0H", "Multi_Apple TV","TV"}
            };
            for (String[] data : preparedRow_Product) {
                pstmt.setString(1, data[0]);
                pstmt.setString(2, data[1]);
                pstmt.setString(3, data[2]);
                pstmt.execute();  // result is false
            }
            System.out.println("Inserted rows into table Product");
            pstmt.close();

            System.out.println("Test for Product table including insert(create), delete, update Start");
            // query for contents of Product table
            String testProduct = "SELECT * FROM Product ";
            rs = stmt.executeQuery(testProduct);
            System.out.println("\nProduct:");

            System.out.printf("%-18s  %-18s   %-18s\n",
                    "SKU", "Name","Description");
            while (rs.next()) {
                String sku= rs.getString("SKU");
                String name = rs.getString("Name");
                String description = rs.getString("Description");
                System.out.printf("%-18s  %-18s   %-18s\n",
                        sku, name,description);
            }
            rs.close();
            // query for contents of Product table after delete
            System.out.println("\nDelete Product with SKU: AA-000000-0H");
            stmt.executeUpdate("delete from Product where SKU = 'AA-000000-0H'");
            testProduct = "select * from Product";
            rs = stmt.executeQuery(testProduct);
            System.out.println("\nProduct:");
            System.out.printf("%-18s  %-18s   %-18s\n",
                    "SKU", "Name","Description");
            while (rs.next()) {
                String sku= rs.getString("SKU");
                String name = rs.getString("Name");
                String description = rs.getString("Description");
                System.out.printf("%-18s  %-18s   %-18s\n",
                        sku, name,description);
            }
            rs.close();
            // query for contents of Product table after update
            System.out.println("\nUpdate Product with SKU: AA-000000-0F");
            stmt.executeUpdate("Update  Product Set Name = 'Multi TV Apple'where SKU = 'AA-000000-0F'");
            testProduct = "select * from Product";
            rs = stmt.executeQuery(testProduct);
            System.out.println("\nProduct:");
            System.out.println("\nProduct with SKU: AA-000000-0A");
            System.out.printf("%-18s  %-18s   %-18s\n",
                    "SKU", "Name","Description");
            while (rs.next()) {
                String sku= rs.getString("SKU");
                String name = rs.getString("Name");
                String description = rs.getString("Description");
                System.out.printf("%-18s  %-18s   %-18s\n",
                        sku, name,description);
            }
            rs.close();
            System.out.println("Test for Product table including insert(create), delete, update END");
            System.out.println("_________________________________");



            // populate InventoryRecord table
            String pInsertRow_InventoryRecord  = "insert into InventoryRecord  values(?, ?, ?)";
            pstmt = conn.prepareStatement(pInsertRow_InventoryRecord);
            Object preparedRow_InventoryRecord [][] = {
                    {100, 1100.00, "AA-000000-0A" },
                    {100, 1500.00, "AA-000000-0B"},
                    {200, 1200.00, "AA-000000-0C"},
                    {120, 500.00, "AA-000000-0D"},
                    {110, 400.00, "AA-000000-0E"},
                    {230, 300.00, "AA-000000-0F"},
                    {400, 900.00, "AA-000000-0G"},

            };
            for (Object[] data : preparedRow_InventoryRecord ) {
                pstmt.setInt(1, (int)data[0]);
                pstmt.setDouble(2, (double)data[1]);
                pstmt.setString(3, (String)data[2]);
                pstmt.execute();  // result is false
            }
            System.out.println("");
            System.out.println("Inserted rows into InventoryRecord  Product");
            pstmt.close();

            System.out.println("Test for Product table including insert(create), delete, update Start");

            String testInventory = "SELECT * FROM InventoryRecord ";
            rs = stmt.executeQuery(testInventory);
            System.out.println("\nInventoryRecord ");
            System.out.printf("%-18s  %-18s   %-18s\n",
                    "Unit","Price","SKU");
            while (rs.next()) {
                int unit = rs.getInt("Unit");
                Double price = rs.getDouble("Price");
                String sku= rs.getString("SKU");
                System.out.printf("%-18s  %-18s    %-18s\n",
                        unit, price, sku);
            }
            rs.close();

            stmt.executeUpdate("delete from InventoryRecord where SKU = 'AA-000000-0G'");
            testInventory = "SELECT * FROM InventoryRecord";
            rs = stmt.executeQuery(testInventory);
            System.out.println("\nInventoryRecord Delete SKU: AA-000000-0G");
            System.out.printf("%-18s  %-18s   %-18s\n",
                    "Unit","Price","SKU");
            while (rs.next()) {
                int unit = rs.getInt("Unit");
                Double price = rs.getDouble("Price");
                String sku= rs.getString("SKU");
                System.out.printf("%-18s  %-18s    %-18s\n",
                        unit, price, sku);
            }
            rs.close();;

            System.out.println("\nUpdate Inventory with SKU: AA-000000-0F");
            stmt.executeUpdate("Update  InventoryRecord Set Unit = 130 where SKU = 'AA-000000-0F'");
            testInventory = "SELECT * FROM InventoryRecord";
            rs = stmt.executeQuery(testInventory);
            System.out.println("\nInventoryRecord Update SKU: AA-000000-0F from 230 to unit 130");
            System.out.printf("%-18s  %-18s   %-18s\n",
                    "Unit","Price","SKU");
            while (rs.next()) {
                int unit = rs.getInt("Unit");
                Double price = rs.getDouble("Price");
                String sku= rs.getString("SKU");
                System.out.printf("%-18s  %-18s    %-18s\n",
                        unit, price, sku);
            }
            rs.close();;

            System.out.println("Test for Product table including insert(create), delete, update End");
            System.out.println("_________________________________");



            String[] insertRow_SingleOrder = {
                    "insert into SingleOrder (CustomerId, OrderDate, ShipDate) " +
                    "values(1, '2018-11-25', '2018-11-26')",
                    "insert into SingleOrder (CustomerId,OrderDate, ShipDate) " +
                    "values(2, '2018-11-25', null)",
                    "insert into SingleOrder (CustomerId,OrderDate, ShipDate) " +
                    "values(3, '2018-11-25', '2018-11-26')",
                    "insert into SingleOrder (CustomerId,OrderDate, ShipDate) " +
                    "values(3, '2018-11-26', '2018-11-27')"
            };
            for (String row: insertRow_SingleOrder) {
                stmt.executeUpdate(row);
            }
            System.out.println("Inserted rows into SingleOrder");
            pstmt.close();

            System.out.println("Test for SingleOrder table including insert(create), delete, update Start");

            String testSingleOrder = "SELECT * FROM SingleOrder";
            rs = stmt.executeQuery(testSingleOrder);
            System.out.println("\ntestSingleOrder");
            System.out.printf("%-18s  %-18s   %-18s\n",
                    "CustomerId","OrderDate","ShipDate");
            while (rs.next()) {
                int customerId = rs.getInt("CustomerId");
                Date orderDate= rs.getDate("OrderDate");
                Date shipDate= rs.getDate("ShipDate");
                System.out.printf("%-18s  %-18s    %-18s\n",
                        customerId, orderDate, shipDate);
            }
            rs.close();


            stmt.executeUpdate("delete from SingleOrder where CustomerId = 3");
            testSingleOrder = "SELECT * FROM SingleOrder";
            rs = stmt.executeQuery(testSingleOrder);
            System.out.println("\nDelete_SingleOrder with CustomerId: 3");
            System.out.printf("%-18s  %-18s   %-18s\n",
                    "CustomerId","OrderDate","ShipDate");
            while (rs.next()) {
                int customerId = rs.getInt("CustomerId");
                Date orderDate= rs.getDate("OrderDate");
                Date shipDate= rs.getDate("ShipDate");
                System.out.printf("%-18s  %-18s    %-18s\n",
                        customerId, orderDate, shipDate);
            }
            rs.close();

            System.out.println("\nUpdate SingleOrder with CustomerId: 1");
            stmt.executeUpdate("Update  SingleOrder Set shipdate = '2018-11-25' where CustomerId = 1");
            testSingleOrder = "SELECT * FROM SingleOrder";
            rs = stmt.executeQuery(testSingleOrder);
            System.out.println("\nupdate_SingleOrder shipdate to 2018-11-25 CustomerId = 1");
            System.out.printf("%-18s  %-18s   %-18s\n",
                    "CustomerId","OrderDate","ShipDate");
            while (rs.next()) {
                int customerId = rs.getInt("CustomerId");
                Date orderDate= rs.getDate("OrderDate");
                Date shipDate= rs.getDate("ShipDate");
                System.out.printf("%-18s  %-18s    %-18s\n",
                        customerId, orderDate, shipDate);
            }
            rs.close();;

            System.out.println("Test for SingleOrder table including insert(create), delete, update End");
            System.out.println("_________________________________");



            String[] insertRow_OrderRecord = {
                    "insert into OrderRecord (OrderId, SKU, Unit, Price) " +
                    "values(1, 'AA-000000-0A', 2, 1100.00)",
                    "insert into OrderRecord (OrderId, SKU, Unit, Price) " +
                    "values(1, 'AA-000000-0B', 1, 1500.00)",
                    "insert into OrderRecord (OrderId, SKU, Unit, Price) " +
                    "values(2, 'AA-000000-0A', 2, 1100.00)",
                    "insert into OrderRecord (OrderId, SKU, Unit, Price) " +
                    "values(2, 'AA-000000-0C', 2, 1200.00)",
            };
            for (String row: insertRow_OrderRecord) {
                stmt.executeUpdate(row);
            }
            System.out.println("Inserted rows into OrderRecord");
            pstmt.close();


            System.out.println("Test for OrderRecord table including insert(create), delete, update Start");

            String testOrderRecord = "SELECT * FROM OrderRecord";
            rs = stmt.executeQuery(testOrderRecord);
            System.out.println("\nShow testOrderRecord with OrderId: 1");
            System.out.printf("%-18s  %-18s   %-18s  %-18s\n",
                    "OrderId","SKU","Unit", "Price");
            while (rs.next()) {
                int orderId = rs.getInt("OrderId");
                String SKU= rs.getString("SKU");
                int  unit= rs.getInt("unit");
                double  Price= rs.getDouble("Price");
                System.out.printf("%-18s  %-18s  %-18s  %-18s\n",
                        orderId, SKU, unit, Price);
            }
            rs.close();


            stmt.executeUpdate("delete from OrderRecord WHERE OrderId = 2");
            testSingleOrder = "SELECT * FROM OrderRecord";
            rs = stmt.executeQuery(testOrderRecord);
            System.out.println("\nDelete_OrderRecord with OrderId: 2");
            System.out.printf("%-18s  %-18s   %-18s  %-18s\n",
                    "OrderId","SKU","Unit", "Price");
            while (rs.next()) {
                int orderId = rs.getInt("OrderId");
                String SKU= rs.getString("SKU");
                int  unit= rs.getInt("unit");
                double  price= rs.getDouble("Price");
                System.out.printf("%-18s  %-18s  %-18s  %-18s\n",
                        orderId, SKU, unit, price);
            }
            rs.close();

            System.out.println("\nUpdate OrderRecord with OrderId: 1");
            stmt.executeUpdate("Update  OrderRecord Set unit = 20 where OrderId = 1 and SKu = 'AA-000000-0A' ");
            testSingleOrder = "SELECT * FROM OrderRecord";
            rs = stmt.executeQuery(testOrderRecord);
            System.out.println("\nUpdate_OrderRecord with OrderId: 1 and SKu = 'AA-000000-0A' : unit change from 2 to 20");
            System.out.printf("%-18s  %-18s   %-18s ` %-18s\n",
                    "OrderId","SKU","Unit", "Price");
            while (rs.next()) {
                int orderId = rs.getInt("OrderId");
                String SKU= rs.getString("SKU");
                int  unit= rs.getInt("unit");
                double  Price= rs.getDouble("Price");
                System.out.printf("%-18s  %-18s  %-18s  %-18s\n",
                        orderId, SKU, unit, Price);
            }
            rs.close();
            System.out.println("Test for Orderrecord table including insert(create), delete, update End");
            System.out.println("_________________________________");

            // test Trigger_ReduceInventory
            // after insert 4 unit of item SKu AA-000000-0A , the unit of SKU should be 100 - 4 = 96
            testInventory = "SELECT * FROM InventoryRecord";
            rs = stmt.executeQuery(testInventory);
            System.out.println("\n new InventoryRecord ");
            System.out.printf("%-18s  %-18s   %-18s\n",
                    "Unit","Price","SKU");
            while (rs.next()) {
                int unit = rs.getInt("Unit");
                Double price = rs.getDouble("Price");
                String sku= rs.getString("SKU");
                System.out.printf("%-18s  %-18s    %-18s\n",
                        unit, price, sku);
            }
            rs.close();




            //edge case for trigger : orderrecord. unit > prouct.unit and product unit auto substract
            //test a real-world order case:
            // create costumer + product -> create order  -> create orderrecord  check the product unit
            // if a single orderRecord failed (throw exception) the whole order will be rolled back

            System.out.println("Before insert SingleOrder with back-ordered OrderRecord");
            testSingleOrder = "SELECT * FROM SingleOrder";
            rs = stmt.executeQuery(testSingleOrder);
            System.out.printf("%-18s  %-18s   %-18s   %-18s\n",
                    "OrderId", "CustomerId","OrderDate","ShipDate");
            while (rs.next()) {
                int orderId = rs.getInt("OrderId");
                int customerId = rs.getInt("CustomerId");
                Date orderDate= rs.getDate("OrderDate");
                Date shipDate= rs.getDate("ShipDate");
                System.out.printf("%-18s   %-18s  %-18s    %-18s\n",
                        orderId, customerId, orderDate, shipDate);
            }
            rs.close();

            conn.setAutoCommit(false);

            try {
                stmt.executeUpdate("insert into SingleOrder (CustomerId, OrderDate, ShipDate) " +
                                   "values(1, '2018-11-25', '2018-11-26')");
                stmt.executeUpdate("insert into OrderRecord (OrderId, SKU, Unit, Price) " +
                                   "values(5, 'AA-000000-0B', 100, 1100.00)");
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
            }

            conn.setAutoCommit(true);

            System.out.println("\nAfter insert SingleOrder with back-ordered OrderRecord");
            testSingleOrder = "SELECT * FROM SingleOrder";
            rs = stmt.executeQuery(testSingleOrder);
            System.out.printf("%-18s  %-18s   %-18s   %-18s\n",
                    "OrderId", "CustomerId","OrderDate","ShipDate");
            while (rs.next()) {
                int orderId = rs.getInt("OrderId");
                int customerId = rs.getInt("CustomerId");
                Date orderDate= rs.getDate("OrderDate");
                Date shipDate= rs.getDate("ShipDate");
                System.out.printf("%-18s   %-18s  %-18s    %-18s\n",
                        orderId, customerId, orderDate, shipDate);
            }
            rs.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}