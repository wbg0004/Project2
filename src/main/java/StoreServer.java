import java.io.PrintWriter;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;

public class StoreServer {
    static String dbfile = "/Users/willgarrison/Documents/Software Engineering/Fall 2019/COMP 3700/StoreManagement.db";

    public static void main(String[] args) {

        int port = 1000;

        if (args.length > 0) {
            System.out.println("Running arguments: ");
            for (String arg : args)
                System.out.println(arg);
            port = Integer.parseInt(args[0]);
            dbfile = args[1];
        }

        try {
            ServerSocket server = new ServerSocket(port);

            System.out.println("Server is listening at port = " + port);

            while (true) {
                Socket pipe = server.accept();
                PrintWriter out = new PrintWriter(pipe.getOutputStream(), true);
                Scanner in = new Scanner(pipe.getInputStream());

                int command = Integer.parseInt(in.nextLine());
                if (command == MessageModel.GET_PRODUCT) {
                    String str = in.nextLine();
                    System.out.println("GET product with id = " + str);
                    int productID = Integer.parseInt(str);

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Product WHERE ProductID = " + productID;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        if (rs.next()) {
                            out.println(rs.getString("Name")); // send back product name!
                            out.println(rs.getDouble("Price")); // send back product price!
                            out.println(rs.getDouble("TaxRate")); // send back product tax rate!
                            out.println(rs.getDouble("Quantity")); // send back product quantity!
                            out.println(rs.getString("Vendor")); // send back product vendor!
                            out.println(rs.getString("Description")); // send back product description!
                        }
                        else
                            out.println("null");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();
                }

                else if (command == MessageModel.PUT_PRODUCT || command == MessageModel.ADD_PRODUCT) {
                    String id = in.nextLine();  // read all information from client
                    String name = in.nextLine();
                    String price = in.nextLine();
                    String taxRate = in.nextLine();
                    String quantity = in.nextLine();
                    String vendor = in.nextLine();
                    String description = in.nextLine();

                    if (command == MessageModel.PUT_PRODUCT) {
                        System.out.println("PUT command with ProductID = " + id);
                    }
                    else {
                        System.out.println("ADD command with ProductID = " + id);
                    }

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Product WHERE ProductID = " + id;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        boolean duplicate = rs.next();
                        if (duplicate && command == MessageModel.PUT_PRODUCT) {
                            rs.close();
                            stmt.execute("DELETE FROM Product WHERE ProductID = " + id);
                        }
                        else if (duplicate && command == MessageModel.ADD_PRODUCT) {
                            out.println(MessageModel.DUPLICATE_PRODUCT);
                            conn.close();
                            continue;
                        }

                        sql = "INSERT INTO Product VALUES (" + id + ",\"" + name + "\","
                                + price + "," + taxRate + "," + quantity + ",\"" + vendor
                                + "\",\"" + description + "\")";
                        if (command == MessageModel.PUT_PRODUCT) {
                            System.out.println("SQL for PUT: " + sql);
                        }
                        else {
                            System.out.println("SQL for ADD: " + sql);
                        }

                        stmt.execute(sql);
                        out.println(MessageModel.OPERATION_OK);

                    } catch (Exception e) {
                        out.println(MessageModel.OPERATION_FAILED);
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.GET_CUSTOMER) {
                    String str = in.nextLine();
                    System.out.println("GET customer with id = " + str);
                    int customerID = Integer.parseInt(str);

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Customer WHERE CustomerID = " + customerID;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        if (rs.next()) {
                            out.println(rs.getString("Name")); // send back customer name!
                            out.println(rs.getString("Address")); // send back customer address!
                            out.println(rs.getString("Phone")); // send back customer phone number!
                            out.println(rs.getString("PaymentInfo")); // send back customer payment info!
                        }
                        else
                            out.println("null");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.PUT_CUSTOMER || command == MessageModel.ADD_CUSTOMER) {
                    String id = in.nextLine();  // read all information from client
                    String name = in.nextLine();
                    String address = in.nextLine();
                    String phone = in.nextLine();
                    String paymentInfo = in.nextLine();

                    if (command == MessageModel.PUT_CUSTOMER) {
                        System.out.println("PUT command with CustomerID = " + id);
                    }
                    else {
                        System.out.println("ADD command with CustomerID = " + id);
                    }

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Customer WHERE CustomerID = " + id;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        boolean duplicate = rs.next();
                        if (duplicate && command == MessageModel.PUT_CUSTOMER) {
                            rs.close();
                            stmt.execute("DELETE FROM Customer WHERE CustomerID = " + id);
                        }
                        else if (duplicate && command == MessageModel.ADD_CUSTOMER) {
                            out.println(MessageModel.DUPLICATE_CUSTOMER);
                            conn.close();
                            continue;
                        }

                        sql = "INSERT INTO Customer VALUES (" + id + ",\"" + name + "\",\""
                                + address + "\",\"" + phone
                                + "\",\"" + paymentInfo + "\")";
                        if (command == MessageModel.PUT_CUSTOMER) {
                            System.out.println("SQL for PUT: " + sql);
                        }
                        else {
                            System.out.println("SQL for ADD: " + sql);
                        }
                        stmt.execute(sql);
                        out.println(MessageModel.OPERATION_OK);

                    } catch (Exception e) {
                        out.println(MessageModel.OPERATION_FAILED);
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.GET_PURCHASE) {
                    String str = in.nextLine();
                    System.out.println("GET purchase with id = " + str);
                    int purchaseID = Integer.parseInt(str);

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Purchase WHERE PurchaseID = " + purchaseID;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        String customerID;
                        String productID;

                        if (rs.next()) {
                            customerID = rs.getString("CustomerID");
                            productID = rs.getString("ProductID");
                            out.println(productID); // send back ID of product being purchased!
                            out.println(customerID); // send back ID of customer making purchase!
                            out.println(rs.getString("Quantity")); // send back quantity of item purchased!
                            out.println(rs.getString("Cost")); // send back cost of purchase without tax!
                            out.println(rs.getString("TaxCost")); // send back tax cost of purchase!
                            out.println(rs.getString("TotalCost")); // send back total cost of purchase!
                            out.println(rs.getString("DateOf")); // send back purchase date!

                            rs = stmt.executeQuery("SELECT * FROM Customer WHERE CustomerID = " + customerID);
                            if (rs.next()) {
                                out.println(rs.getString("Name")); // send back name of customer making purchase!
                                out.println(rs.getString("Address")); // send back address of customer making purchase!
                                out.println(rs.getString("Phone")); // send back phone number of customer making purchase!
                                out.println(rs.getString("PaymentInfo")); // send back payment info of customer making purchase!
                            }
                            else {
                                out.println("null");
                            }

                            rs = stmt.executeQuery("SELECT * FROM Product WHERE ProductID = " + productID);
                            if (rs.next()) {
                                out.println(rs.getString("Name")); // send back name of product being purchased!
                                out.println(rs.getString("Price")); // send back price of product being purchased!
                                out.println(rs.getString("Quantity")); // send back total quantity of product in store!
                                out.println(rs.getString("TaxRate")); // send back the tax rate of the product!
                                out.println(rs.getString("Vendor")); // send back the vendor of the product!
                                out.println(rs.getString("Description")); // send back the description of the product!
                            }
                            else {
                                out.println("null");
                            }
                        }

                        else
                            out.println("null");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.PUT_PURCHASE || command == MessageModel.ADD_PURCHASE) {
                    String purchaseID = in.nextLine();  // read all information from client
                    String productID = in.nextLine();
                    String customerID = in.nextLine();
                    String quantity = in.nextLine();
                    String cost = in.nextLine();
                    String taxCost = in.nextLine();
                    String totalCost = in.nextLine();
                    String dateOf = in.nextLine();

                    if (command == MessageModel.PUT_PURCHASE) {
                        System.out.println("PUT command with PurchaseID = " + purchaseID);
                    }
                    else {
                        System.out.println("ADD command with PurchaseID = " + purchaseID);
                    }

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Purchase WHERE PurchaseID = " + purchaseID;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        boolean duplicate = rs.next();
                        if (duplicate && command == MessageModel.PUT_PURCHASE) {
                            rs.close();
                            stmt.execute("DELETE FROM Purchase WHERE PurchaseID = " + purchaseID);
                        }
                        else if (duplicate && command == MessageModel.ADD_PURCHASE) {
                            out.println(MessageModel.DUPLICATE_PURCHASE);
                            conn.close();
                            continue;
                        }

                        sql = "INSERT INTO Purchase VALUES (" + purchaseID + "," + productID + "," + customerID
                                + "," + quantity + "," + cost + "," + taxCost + ","
                                + totalCost + ",\"" + dateOf + "\")";
                        if (command == MessageModel.PUT_PURCHASE) {
                            System.out.println("SQL for PUT: " + sql);
                        }
                        else {
                            System.out.println("SQL for ADD: " + sql);
                        }
                        stmt.execute(sql);
                        out.println(MessageModel.OPERATION_OK);

                    } catch (Exception e) {
                        out.println(MessageModel.OPERATION_FAILED);
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else {
                    out.println(0); // logout unsuccessful!
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}