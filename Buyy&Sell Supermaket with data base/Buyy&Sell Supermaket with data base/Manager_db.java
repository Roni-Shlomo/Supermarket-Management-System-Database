package RoniShlomo_And_NikolYosef;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.sql.SQLException;

public class Manager_db {

    // Connect to the database (put your password in place of your_password_here)
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/superMarket_db",
                "postgres",
                "Ny12345678"
        );
    }

    // Check if seller exists in the database
    public static boolean isSellerExists(String sellerName) throws SQLException {
        String query = "SELECT 1 FROM Seller WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sellerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    // Check if buyer exists in the database
    public static boolean isBuyerExists(String buyerName) throws SQLException {
        String query = "SELECT 1 FROM Buyer WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, buyerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    // Adding a new seller (to both the Users and Seller tables)
    public static void addSeller(String sellerName, String password) throws SQLException {
        String insertUser = "INSERT INTO Users (name, password) VALUES (?, ?)";
        String insertSeller = "INSERT INTO Seller (name) VALUES (?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUser = conn.prepareStatement(insertUser);
                 PreparedStatement stmtSeller = conn.prepareStatement(insertSeller)) {

                stmtUser.setString(1, sellerName);
                stmtUser.setString(2, password);
                stmtUser.executeUpdate();

                stmtSeller.setString(1, sellerName);
                stmtSeller.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Add a new buyer including address (to both Users and Buyer tables)
    public static void addBuyer(String buyerName, String password, Address address) throws SQLException {
        String insertUser = "INSERT INTO Users (name, password) VALUES (?, ?)";
        String insertBuyer = "INSERT INTO Buyer (name, street, buildingNumber, city, country) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUser = conn.prepareStatement(insertUser);
                 PreparedStatement stmtBuyer = conn.prepareStatement(insertBuyer)) {

                stmtUser.setString(1, buyerName);
                stmtUser.setString(2, password);
                stmtUser.executeUpdate();

                stmtBuyer.setString(1, buyerName);
                stmtBuyer.setString(2, address.getStreet());
                stmtBuyer.setInt(3, address.getBuildingNumber());
                stmtBuyer.setString(4, address.getCity());
                stmtBuyer.setString(5, address.getCountry());
                stmtBuyer.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Checks if there are sellers in the system
    public static boolean isEmptySeller() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Seller";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total") == 0;
            }
        }
        return true; // אם משהו השתבש, נחזיר שהטבלה ריקה
    }


    // Checks if there are buyers in the system
    public static boolean isEmptyBuyer() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Buyer";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total") == 0;
            }
        }
        return true;
    }

    // Retrieving all sellers from the database
    public static ArrayList<Seller> getAllSellers() throws SQLException {
        ArrayList<Seller> sellers = new ArrayList<>();
        String query = "SELECT u.name, u.password FROM Seller s JOIN Users u ON s.name = u.name";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString("password");
                sellers.add(new Seller(name, password));
            }
        }
        return sellers;
    }

    // Retrieving all buyers from the database
    public static ArrayList<Buyer> getAllBuyers() throws SQLException {
        ArrayList<Buyer> buyers = new ArrayList<>();
        String query = "SELECT u.name, u.password, b.street, b.buildingNumber, b.city, b.country " +
                "FROM Buyer b JOIN Users u ON b.name = u.name";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString("password");
                Address address = new Address(
                        rs.getString("street"),
                        rs.getInt("buildingNumber"),
                        rs.getString("city"),
                        rs.getString("country")
                );
                buyers.add(new Buyer(name, password, address));
            }
        }
        return buyers;
    }

    public static List<Product> getProductsBySeller(String sellerName) throws SQLException {
        List<Product> products = new ArrayList<>();

        String query = "SELECT p.Pnum, p.Pname, p.price, p.category, spp.packing_price " +
                "FROM Product p " +
                "LEFT JOIN SpecialPackedProduct spp ON p.Pnum = spp.Pnum " +
                "WHERE p.sellerName = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sellerName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int pnum = rs.getInt("Pnum");
                String pname = rs.getString("Pname");
                double price = ((Number) rs.getObject("price")).doubleValue();
                String category = rs.getString("category");
                Double packingPrice = rs.getObject("packing_price") != null
                        ? ((Number) rs.getObject("packing_price")).doubleValue()
                        : null;

                Product product;
                if (packingPrice != null) {
                    product = new SpecialPackedProduct(pnum, pname, price, Product.eCategory.valueOf(category), packingPrice);
                } else {
                    product = new Product(pnum, pname, price, Product.eCategory.valueOf(category));
                }

                product.setSellerName(sellerName); // ✅ שורה קריטית לפתרון השגיאה שלך
                products.add(product);
            }
        }

        return products;
    }


    public static void addProduct(Product product, String sellerName) throws SQLException {
        String insertProduct = "INSERT INTO Product (Pname, price, category, sellerName) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertProduct, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, product.getCategory().name());
            stmt.setString(4, sellerName);
            stmt.executeUpdate();

            // אם מדובר במוצר עם אריזה – מוסיפים גם ל־SpecialPackedProduct
            if (product instanceof SpecialPackedProduct packedProduct) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int pnum = generatedKeys.getInt(1);
                    String insertPacking = "INSERT INTO SpecialPackedProduct (Pnum, packing_price) VALUES (?, ?)";
                    try (PreparedStatement packStmt = conn.prepareStatement(insertPacking)) {
                        packStmt.setInt(1, pnum);
                        packStmt.setDouble(2, packedProduct.getPackingPrice());
                        packStmt.executeUpdate();
                    }
                }
            }
        }
    }

    public static boolean isProductExists(String productName, String sellerName) throws SQLException {
        String query = "SELECT 1 FROM Product WHERE Pname = ? AND sellerName = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, productName);
            stmt.setString(2, sellerName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public static boolean isEmptyProduct(String sellerName) throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Product WHERE sellerName = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sellerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") == 0;
            }
        }
        return true;
    }

    public static void markCartAsPaid(int cartID) throws SQLException {
        String query = "UPDATE ShoppingCart SET ifPaid = true, buyDate = CURRENT_DATE WHERE cartID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cartID);
            stmt.executeUpdate();
        }
    }

    // Looking for an open cart in the database.
    //But if one doesn't exist – it returns null and doesn't create anything new.
    public static Cart getOpenCartByBuyer(String buyerName) throws SQLException {
        String query = "SELECT * FROM ShoppingCart WHERE buyerName = ? AND ifPaid = false";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, buyerName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int cartID = rs.getInt("cartID");
                LocalDate buyDate = rs.getDate("buyDate") != null ? rs.getDate("buyDate").toLocalDate() : null;
                List<Product> products = getProductsInCart(cartID);
                double totalPrice = 0.0;
                for (Product p : products) {
                    totalPrice += p.getPrice();
                    if (p instanceof SpecialPackedProduct packed) {
                        totalPrice += packed.getPackingPrice();
                    }
                }
                return new Cart(cartID, buyerName, buyDate, totalPrice, products);

            }
        }
        return null;
    }

    public static List<Cart> getOrdersHistoryByBuyer(String buyerName) throws SQLException {
        List<Cart> orders = new ArrayList<>();

        String query = "SELECT * FROM ShoppingCart WHERE buyerName = ? AND ifPaid = true ORDER BY buyDate DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, buyerName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int cartID = rs.getInt("cartID");
                LocalDate buyDate = rs.getDate("buyDate").toLocalDate();
                List<Product> products = getProductsInCart(cartID);
                double totalPrice = 0.0;
                for (Product p : products) {
                    totalPrice += p.getPrice();
                    if (p instanceof SpecialPackedProduct packed) {
                        totalPrice += packed.getPackingPrice();
                    }
                }


                for (Product p : products) {
                    p.setSellerName(getSellerNameByProduct(p.getSerialNumber()));  // נוסיף את זה גם אם צריך
                }
                orders.add(new Cart(cartID, buyerName, buyDate, totalPrice, products));
            }
        }

        return orders;
    }

    public static List<Product> getProductsInCart(int cartID) throws SQLException {
        List<Product> products = new ArrayList<>();

        String query = "SELECT p.Pnum, p.Pname, p.price, p.category, spp.packing_price " +
                "FROM CartProduct cp " +
                "JOIN Product p ON cp.Pnum = p.Pnum " +
                "LEFT JOIN SpecialPackedProduct spp ON p.Pnum = spp.Pnum " +
                "WHERE cp.cartID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, cartID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int pnum = rs.getInt("Pnum");
                String pname = rs.getString("Pname");
                double price = ((Number) rs.getObject("price")).doubleValue();
                String category = rs.getString("category");
                BigDecimal packingPriceBD = rs.getBigDecimal("packing_price");
                Double packingPrice = (packingPriceBD != null) ? packingPriceBD.doubleValue() : null;


                Product.eCategory eCat = Product.eCategory.valueOf(category);

                if (packingPrice != null) {
                    products.add(new SpecialPackedProduct(pnum, pname, price , eCat, packingPrice));
                } else {
                    products.add(new Product(pnum, pname, price, eCat));
                }
            }
        }

        return products;
    }

    public static void addProductToCart(int cartID, int pnum, int quantity) throws SQLException {
        String query = "INSERT INTO CartProduct (cartID, Pnum, quantity) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, cartID);
            stmt.setInt(2, pnum);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();
        }
    }


    public static int createEmptyCartForBuyer(String buyerName) throws SQLException {
        String insertCart = "INSERT INTO ShoppingCart (buyerName, ifPaid, total_price) VALUES (?, false, 0.0) RETURNING cartID";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertCart)) {

            stmt.setString(1, buyerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cartID");
            }
        }
        throw new SQLException("Failed to create new cart for buyer: " + buyerName);
    }

    public static int getPnum(String productName, String sellerName) throws SQLException {
        String query = "SELECT Pnum FROM Product WHERE Pname = ? AND sellerName = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, productName);
            stmt.setString(2, sellerName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Pnum");
            } else {
                throw new SQLException("Product not found for given seller.");
            }
        }
    }

    public static void insertProductIntoCart(int cartID, int pnum, int quantity) throws SQLException {
        String checkQuery = "SELECT quantity FROM CartProduct WHERE cartID = ? AND Pnum = ?";
        String updateQuery = "UPDATE CartProduct SET quantity = quantity + ? WHERE cartID = ? AND Pnum = ?";
        String insertQuery = "INSERT INTO CartProduct (cartID, Pnum, quantity) VALUES (?, ?, ?)";

        try (Connection conn = getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, cartID);
                checkStmt.setInt(2, pnum);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, cartID);
                        updateStmt.setInt(3, pnum);
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, cartID);
                        insertStmt.setInt(2, pnum);
                        insertStmt.setInt(3, quantity);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }


    public static Buyer getBuyerByName(String name) throws SQLException {
        String query = "SELECT u.name, u.password, b.street, b.buildingNumber, b.city, b.country " +
                "FROM Buyer b JOIN Users u ON b.name = u.name WHERE b.name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String password = rs.getString("password");
                Address address = new Address(
                        rs.getString("street"),
                        rs.getInt("buildingNumber"),
                        rs.getString("city"),
                        rs.getString("country")
                );
                return new Buyer(name, password, address);
            }
        }
        return null;
    }

    public static Seller getSellerByName(String name) throws SQLException {
        String query = "SELECT u.name, u.password " +
                "FROM Seller s JOIN Users u ON s.name = u.name WHERE s.name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String password = rs.getString("password");
                return new Seller(name, password);
            }
        }
        return null;
    }

    public static List<Seller> getSortedSellersByProductCount() throws SQLException {
        List<Seller> sellers = new ArrayList<>();

        String query = "SELECT s.name, u.password, COUNT(p.Pnum) AS productCount " +
                "FROM Seller s " +
                "JOIN Users u ON s.name = u.name " +
                "LEFT JOIN Product p ON s.name = p.sellerName " +
                "GROUP BY s.name, u.password " +
                "ORDER BY productCount DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString("password");

                Seller seller = new Seller(name, password);

                String productQuery = "SELECT * FROM Product WHERE sellerName = ?";
                try (PreparedStatement productStmt = conn.prepareStatement(productQuery)) {
                    productStmt.setString(1, name);
                    ResultSet productRs = productStmt.executeQuery();
                    while (productRs.next()) {
                        int pnum = productRs.getInt("Pnum");
                        String pname = productRs.getString("Pname");
                        double price = productRs.getDouble("price");
                        String category = productRs.getString("category");
                        Product product = new Product(pnum, pname, price, category, name);
                        seller.addProduct(product);
                    }
                }

                sellers.add(seller);

            }
        }

        return sellers;
    }

    public static List<Product> getProductsByCategory(Product.eCategory category) throws SQLException {
        List<Product> products = new ArrayList<>();

        String query = "SELECT p.Pnum, p.Pname, p.price, p.category, p.sellerName, spp.packing_price " +
                "FROM Product p " +
                "LEFT JOIN SpecialPackedProduct spp ON p.Pnum = spp.Pnum " +
                "WHERE p.category = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, category.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int pnum = rs.getInt("Pnum");
                String pname = rs.getString("Pname");
                double price = ((Number) rs.getObject("price")).doubleValue();

                String sellerName = rs.getString("sellerName");
                Double packingPrice = rs.getObject("packing_price") != null
                        ? ((Number) rs.getObject("packing_price")).doubleValue()
                        : null;

                Product product;
                if (packingPrice != null) {
                    product = new SpecialPackedProduct(pnum, pname, price, category, packingPrice);
                } else {
                    product = new Product(pnum, pname, price, category);
                }
                product.setSellerName(sellerName);
                products.add(product);
            }
        }

        return products;
    }

    public static String getSellerNameByProduct(int pnum) throws SQLException {
        String query = "SELECT sellerName FROM Product WHERE Pnum = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pnum);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("sellerName");
        }
        return null;
    }

    public static void removeProductFromCart(int cartID, int pnum) throws SQLException {
        String query = "DELETE FROM CartProduct WHERE cartID = ? AND Pnum = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cartID);
            stmt.setInt(2, pnum);
            stmt.executeUpdate();
        }
    }



}
