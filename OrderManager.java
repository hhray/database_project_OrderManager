
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.sql.*;
public class OrderManager {

    /**
     * Determins if sku is a valid sku
     * @param sku the sku
     * @return if the sku is a valid sku
     */
    public static boolean isSKU (String sku) {
        return sku.matches("[A-Z]{2}-\\d{6}-([A-Z]|\\d){2}");
    }

    public static void main(String[] args) {
        // the default framework is embedded
        String protocol = "jdbc:derby:";
        String dbName = "OrderManager";
        String connStr = protocol + dbName + ";create=true";

        // tables created by this program
        String dbTables[] = {
                "InventoryRecord", "OrderRecord", "SingleOrder", "Customer", "Product"        // entities
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

            // drop the database triggers and recreate them below
//            for (String tgr : dbTriggers) {
//                try {
//                    stmt.executeUpdate("drop trigger " + tgr);
//                    System.out.println("Dropped trigger " + tgr);
//                } catch (SQLException ex) {
//                    System.out.println("Did not drop trigger " + tgr);
//                }
//            }

//            // drop the database tables and recreate them below
            for (String tbl : dbTables) {
                try {
                    stmt.executeUpdate("drop table " + tbl);
                    System.out.println("Dropped table " + tbl);
                } catch (SQLException ex) {
                    System.out.println("Did not drop table " + tbl);
//                    ex.printStackTrace();
                }
            }

            for (String f : dbFunctions) {
                try {
                    stmt.executeUpdate("drop function " + f);
                    System.out.println("Dropped function " + f);
                } catch (SQLException ex) {
                    System.out.println("Did not drop function " + f);
                }
            }

            // drop the database tables and recreate them below
            for (String tbl : dbTables) {
                try {
                    stmt.executeUpdate("drop table " + tbl);
                    System.out.println("Dropped table " + tbl);
                } catch (SQLException ex) {
                    System.out.println("Did not drop table " + tbl);
                }
            }



            // Create functions
            String createFunction_isSKU =
                    "create function isSKU(sku char(12))"
                            + " RETURNS boolean"
                            + " parameter style java"
                            + " language java"
                            + " deterministic"
                            + " no sql"
                            + " external name"
                            + " 'OrderManager.isSKU'";
            stmt.executeUpdate(createFunction_isSKU);
            System.out.println("Created function isSKU");

            // Create tables
            String createTable_Product =
                    "create table Product ("
                            + " SKU char(12),"
                            + " Name varchar(32) not null,"
                            + " Description varchar(16) not null,"
                            + " primary key (SKU)"
                            + ")";
            stmt.executeUpdate(createTable_Product);
            System.out.println("Created entity table Product");

            String createTable_InventoryRecord =
                    "create table InventoryRecord("
                            + " Unit int not null check(Unit >= 0),"
                            + " Price decimal(31, 2) not null check(Price > 0),"
                            + " SKU char(12),"
                            + " primary key (SKU),"
                            + " foreign key (SKU) references Product(SKU) on delete cascade "
                            + ")";
            stmt.executeUpdate(createTable_InventoryRecord);
            System.out.println("Create entity table InventoryRecord");

            String createTable_Customer =
                    "create table Customer("
                            + " CustomerId int not null generated always as identity (start with 1, increment by 1),"
                            + " Name varchar(32) not null,"
                            + " Address varchar(100) not null,"
                            + " City varchar(32),"
                            + " State varchar(32),"
                            + " Country varchar(32),"
                            + " primary key (CustomerId)"
                            + ")";
            stmt.executeUpdate(createTable_Customer);
            System.out.println("Create entity table Customer");

            String createTable_SingleOrder =
                    "create table SingleOrder("
                            + " OrderId int not null generated always as identity (start with 1, increment by 1),"
                            + " CustomerId int not null,"
                            + " OrderDate date not null,"
                            + " ShipDate date,"
                            + " primary key (OrderId),"
                            + " foreign key (CustomerId) references Customer(CustomerId) on delete cascade"
                            + " )";
            stmt.executeUpdate(createTable_SingleOrder);
            System.out.println("Create entity table SingleOrder");

            String createTable_OrderRecord =
                    "create table OrderRecord("
                            + " OrderId int not null,"
                            + " SKU char(12),"
                            + " Unit int not null check(Unit > 0),"
                            + " Price decimal(31, 2) not null check(Price > 0),"
                            + " primary key (OrderId, SKU),"
                            + " foreign key (OrderId) references SingleOrder(OrderId) on delete cascade,"
                            + " foreign key (SKU) references Product(SKU)"
                            + ")";
            stmt.executeUpdate(createTable_OrderRecord);
            System.out.println("Create entity table OrderRecord");


//            conn.setAutoCommit(false);

            // Create Trigger
            String createTrigger_ReduceInventory =
                    "CREATE trigger ReduceInventory "
                            + " AFTER INSERT ON OrderRecord"
                            + " REFERENCING NEW AS NEW"
                            + " for each row"
                            + " UPDATE InventoryRecord"
                            + " SET Unit = InventoryRecord.unit - NEW.Unit"
                            + " WHERE InventoryRecord.SKU = NEW.SKU";

            stmt.executeUpdate(createTrigger_ReduceInventory);
            System.out.println("Created trigger ReduceInventory");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
