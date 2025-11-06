package RoniShlomo_And_NikolYosef;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
import java.sql.*;
import java.util.Comparator;


public class Main {
    static Scanner s = new Scanner(System.in);

    public static void menu() {
        int choice;
        System.out.println("Welcome to 'Buy&Sell' virtual shop!");

        do {
            System.out.println("Choose one of the following options:");
            System.out.println("1 - add a seller");
            System.out.println("2 - add a buyer");
            System.out.println("3 - adding a product to the seller");
            System.out.println("4 - adding a product to a buyer");
            System.out.println("5 - order payment");
            System.out.println("6 - displaying buyers details");
            System.out.println("7 - displaying sellers details");
            System.out.println("8 - displaying all products in the same category");
            System.out.println("9 - create a new cart from order history");
            System.out.println("10 - remove a product from the cart");
            System.out.println("0 - exit");

            System.out.println("Enter your choice --> ");
            choice = getValidIntInput();

            switch (choice) {
                case 0:
                    System.out.println("Exit the program");
                    break;
                case 1:
                    addNewSeller();
                    break;
                case 2:
                    addNewBuyer();
                    break;
                case 3:
                    addProductToSeller();
                    break;
                case 4:
                    addProductToBuyer();
                    break;
                case 5:
                    orderPayment();
                    break;
                case 6:
                    printBuyers();
                    break;
                case 7:
                    printSellers();
                    break;
                case 8:
                    printProductsFromCategory();
                    break;
                case 9:
                    createNewCartFromHistory();
                    break;
                case 10:
                    removeProductFromCart();
                    break;
                default:
                    System.out.println("Invalid option, please try again");
                    break;
            }
            System.out.println();
        } while (choice != 0);

        System.out.println("bye!");

    }
    // case 1 //
    public static void addNewSeller() {
        try {
            System.out.println("Enter a seller's name (instead of a space write '_') ");
            String seller_name = getValidStringInput();
            while (Manager_db.isSellerExists(seller_name)) {
                System.out.println("Seller with this name already exists. Choose a different name. ");
                seller_name = getValidStringInput();
            }
            System.out.println("Enter your password ");
            String seller_password = getValidStringInput();
            Manager_db.addSeller(seller_name, seller_password);
            System.out.println("The seller has been successfully added.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // case 2 //
    public static void addNewBuyer() {
        try {
            System.out.println("Enter a buyer's name (instead of a space write '_')");
            String buyer_name = getValidStringInput();
            while (Manager_db.isBuyerExists(buyer_name)) {
                System.out.println("Buyer with this name already exists. Choose a different name. ");
                buyer_name = getValidStringInput();
            }
            System.out.println("Enter your password ");
            String buyer_password = getValidStringInput();

            System.out.println("Enter your street address ");
            String buyer_street = getValidStringInput();
            System.out.println("Enter your street number ");
            int buyer_street_number = getValidIntInput();
            System.out.println("Enter your city address ");
            String buyer_city = getValidStringInput();
            System.out.println("Enter your country address ");
            String buyer_country = getValidStringInput();

            Address address = new Address(buyer_street, buyer_street_number, buyer_city, buyer_country);
            Manager_db.addBuyer(buyer_name, buyer_password, address);

            System.out.println("The buyer has been successfully added");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // case 3 //
    public static void addProductToSeller() {
        try {
            if (Manager_db.isEmptySeller()) {
                throw new EmptySellerException("There are no sellers, you need to add a seller first.");
            }
            System.out.println("These are all the sellers' names: ");
            List<Seller> sellers = Manager_db.getAllSellers();
            for (Seller seller : sellers) {
                System.out.println(seller.getName());
            }

            System.out.println("Write the seller's name you want to add a product to:");
            String pick_seller_name = getValidStringInput();

            while (!Manager_db.isSellerExists(pick_seller_name)) {
                System.out.println("There is no a seller with this name, please try again. ");
                pick_seller_name = getValidStringInput();
            }

            System.out.println("Please choose a category number (not name)  for the product:");
            for (Product.eCategory category : Product.eCategory.values()) {
                System.out.println(category.ordinal() + 1 + ". " + category);
            }
            int categoryChoice = getValidIntInput();
            // check if the category number valid
            while (!Product.ifValidCategoryChoice(categoryChoice)){
                System.out.println("Invalid choice. Please enter a valid category number.");
                categoryChoice = getValidIntInput();
            }
            Product.eCategory selectedCategory = Product.eCategory.values()[categoryChoice - 1];

            System.out.println("Enter the product name you want to add for the seller:");
            String pick_product_name = getValidStringInput();

            // Check if the product already exists for this seller
            while(Manager_db.isProductExists(pick_product_name, pick_seller_name)){
                System.out.println("A product with this name already exists for the seller. Please choose a different name:");
                pick_product_name = getValidStringInput();
            }
            System.out.println("Enter the product's price");
            double pick_product_price = getValidDoubleInput();

            System.out.println("Do you want to add this product with packing? yes / no");
            String ans_pack = getValidStringInput();

            while (!ans_pack.equals("yes") && !ans_pack.equals("no")){
                System.out.println("You need to write only yes / no : ");
                ans_pack = getValidStringInput();
            }

            Product product;

            if (ans_pack.equals("yes")){
                System.out.println("Enter the packing price");
                double pack_price = getValidDoubleInput();
                product = new SpecialPackedProduct(0, pick_product_name, pick_product_price, selectedCategory, pack_price);
                System.out.println("The product has been successfully packaged");
            } else {
                product = new Product(0, pick_product_name, pick_product_price, selectedCategory);
            }
            Manager_db.addProduct(product, pick_seller_name);
            System.out.println("The product has been successfully added to the requested seller");
        } catch (EmptySellerException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // case 4 //
    public static void addProductToBuyer() {
        try {
            if (Manager_db.getAllBuyers().isEmpty()) {
                throw new EmptyBuyerException("There are no buyers, you need to add a buyer first.");
            }
            if (Manager_db.getAllSellers().isEmpty()) {
                throw new EmptySellerException("There are no sellers, you need to add a seller first.");
            }

            // Requests for buyer name until a valid one is provided
            System.out.println("Type a buyer name from the list:");
            for (Buyer b : Manager_db.getAllBuyers()) {
                System.out.println(b.getName());
            }

            String buyerName;
            Buyer buyer;
            do {
                buyerName = getValidStringInput();
                buyer = Manager_db.getBuyerByName(buyerName);
                if (buyer == null) {
                    System.out.println("There is no buyer with this name, please try again:");
                }
            } while (buyer == null);



            // Requests for seller name until a valid one is provided
            System.out.println("Type a seller name from the list:");
            for (Seller s : Manager_db.getAllSellers()) {
                System.out.println(s.getName());
            }

            String sellerName;
            Seller seller;
            do {
                sellerName = getValidStringInput();
                seller = Manager_db.getSellerByName(sellerName);
                if (seller == null) {
                    System.out.println("There is no seller with this name, please try again:");
                }
            } while (seller == null);

            List<Product> products = Manager_db.getProductsBySeller(sellerName);
            if (products.isEmpty()) {
                System.out.println("There are no products for the requested seller.");
                return;
            }

            // Display the seller's products
            System.out.println("These are the seller's products:");
            for (Product product : products) {
                System.out.println(product);
            }

            // Requests for product name until a valid one is provided
            Product selectedProduct = null;
            System.out.println("Enter the product name you want to buy:");
            while (selectedProduct == null) {
                String productName = getValidStringInput();
                for (Product product : products) {
                    if (product.getName().equals(productName)) {
                        selectedProduct = product;
                        selectedProduct.setSellerName(sellerName); // ✅ מוסיף את שם המוכר
                        break;
                    }

                }
                if (selectedProduct == null) {
                    System.out.println("No product with that name. Try again:");
                }
            }

            // Add the product to the buyer's cart
            buyer.addProductToCart(selectedProduct, 1);
            System.out.println("Product added to cart successfully.");

        } catch (EmptyBuyerException | EmptySellerException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // case 5 //
    public static void orderPayment() {
        try {
            if (Manager_db.isEmptyBuyer()) {
                throw new EmptyBuyerException("There are no buyers, you need to add a buyer first.");
            }
            System.out.println("Select a buyer to complete the purchase:");
            List<Buyer> allBuyers = Manager_db.getAllBuyers();
            for (Buyer b : allBuyers) {
                System.out.println(b.getName());
            }

            Buyer buyer = null;
            while (buyer == null) {
                String buyerName = getValidStringInput();
                for (Buyer b : allBuyers) {
                    if (b.getName().equals(buyerName)) {
                        buyer = b;
                        break;
                    }
                }
                if (buyer == null) {
                    System.out.println("No buyer with that name. Try again:");
                }
            }

            Cart openCart = Manager_db.getOpenCartByBuyer(buyer.getName());
            if (openCart == null || openCart.getAllProducts().isEmpty()) {
                throw new EmptyCartException("The cart is empty, nothing to pay for.");
            }

            System.out.println("Cart total price: " + openCart.getTotalPrice());

            Manager_db.markCartAsPaid(openCart.getCartID());
            System.out.println("Purchase completed.");

        } catch (EmptyBuyerException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // case 6 //
    public static void printBuyers(){
        System.out.println("|Details of all buyers|");
        try {
            List<Buyer> buyers = Manager_db.getAllBuyers();

            if (!buyers.isEmpty()) {
                // מיון לפי שם
                buyers.sort(Comparator.comparing(Buyer::getName));

                for (Buyer buyer : buyers) {
                    System.out.println(buyer.toString());
                }
            } else {
                System.out.println("There are no buyers. Please add a buyer first.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    // case 7 //
    public static void printSellers(){
        System.out.println("|Details of all sellers|");
        try {
            List<Seller> sellers = Manager_db.getSortedSellersByProductCount();
            if (!sellers.isEmpty()) {
                for (Seller seller : sellers) {
                    System.out.println(seller);
                }
            } else {
                System.out.println("There are no sellers. Please add a seller first.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    // case 8 //
    public static void printProductsFromCategory(){
        System.out.println("Please choose a category number to display all the products in it:");
        for (Product.eCategory category : Product.eCategory.values()) {
            System.out.println(category.ordinal() + 1 + ". " + category);
        }
        int categoryChoiceToPrint = getValidIntInput();
        // check if the category number valid
        while (!Product.ifValidCategoryChoice(categoryChoiceToPrint)){
            System.out.println("Invalid choice. Please enter a valid category number.");
            categoryChoiceToPrint = getValidIntInput();
        }

        Product.eCategory selectedCategory = Product.eCategory.values()[categoryChoiceToPrint - 1];

        try {
            List<Product> products = Manager_db.getProductsByCategory(selectedCategory);
            if (products.isEmpty()) {
                System.out.println("There are no products in this category.");
            } else {
                System.out.println("Products in category " + selectedCategory + ":");
                for (Product product : products) {
                    System.out.println(product);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    // case 9 //
    public static void createNewCartFromHistory() {
        try {
            if (Manager_db.isEmptyBuyer()) {
                throw new EmptyBuyerException("There are no buyers, you need to add a buyer first.");
            }

            System.out.println("Select a buyer to create a new cart from order history:");
            List<Buyer> buyers = Manager_db.getAllBuyers();
            for (Buyer b : buyers) {
                System.out.println(b.getName());
            }

            // קליטת שם קונה
            String buyerName;
            do {
                buyerName = getValidStringInput();
                if (!Manager_db.isBuyerExists(buyerName)) {
                    System.out.println("There is no buyer with this name, please try again:");
                    buyerName = null;
                }
            } while (buyerName == null);

            Cart openCart = Manager_db.getOpenCartByBuyer(buyerName);
            List<Cart> history = Manager_db.getOrdersHistoryByBuyer(buyerName);

            if (history.isEmpty()) {
                System.out.println("No previous orders found.");
                return;
            }

            if (openCart != null && !openCart.getAllProducts().isEmpty()) {
                System.out.println("Your current shopping cart is not empty. Do you want to replace it? yes / no");
                String ans = getValidStringInput();
                while (!ans.equals("yes") && !ans.equals("no")) {
                    System.out.println("You need to write only yes / no:");
                    ans = getValidStringInput();
                }
                if (ans.equals("no")) {
                    System.out.println("The current cart was not replaced.");
                    return;
                }
            }

            // בחירת הזמנה מההיסטוריה
            System.out.println("Select an order number from your order history:");
            for (int i = 0; i < history.size(); i++) {
                Cart order = history.get(i);
                System.out.println((i + 1) + ". Order from " + order.getBuyDate());
                for (Product p : order.getAllProducts()) {
                    System.out.println("   - " + p);
                }
            }

            int orderChoice = getValidIntInput();
            while (orderChoice < 1 || orderChoice > history.size()) {
                System.out.println("Invalid choice. Please select a valid order number:");
                orderChoice = getValidIntInput();
            }

            Cart selectedOrder = history.get(orderChoice - 1);

            // מחיקה/החלפה של העגלה הקיימת
            if (openCart != null) {
                // טריק: פשוט יוצרים עגלה חדשה ולא משתמשים בקודמים
                Manager_db.markCartAsPaid(openCart.getCartID()); // סוגר את הישנה אם פתוחה
            }

            int newCartID = Manager_db.createEmptyCartForBuyer(buyerName);

            for (Product product : selectedOrder.getAllProducts()) {
                int pnum = Manager_db.getPnum(product.getName(), product.getSellerName());
                Manager_db.insertProductIntoCart(newCartID, pnum, 1); // תמיד כמות 1
            }

            System.out.println("The cart has been successfully replaced with the selected order.");

        } catch (EmptyBuyerException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //case 10//
    public static void removeProductFromCart() {
        try {
            System.out.println("Enter buyer's name:");
            String buyerName = getValidStringInput();
            Buyer buyer = Manager_db.getBuyerByName(buyerName);

            if (buyer == null) {
                System.out.println("Buyer not found.");
                return;
            }

            Cart cart = buyer.getOpenCart();
            if (cart == null || cart.getAllProducts().isEmpty()) {
                System.out.println("Cart is empty or not found.");
                return;
            }

            System.out.println("Products in your cart:");
            for (Product p : cart.getAllProducts()) {
                System.out.println("- " + p.getName());
            }

            System.out.println("Enter the name of the product you want to remove:");
            String productName = getValidStringInput();

            Product productToRemove = null;
            for (Product p : cart.getAllProducts()) {
                if (p.getName().equals(productName)) {
                    productToRemove = p;
                    break;
                }
            }

            if (productToRemove == null) {
                System.out.println("Product not found in cart.");
                return;
            }

            Manager_db.removeProductFromCart(cart.getCartID(), productToRemove.getSerialNumber());
            System.out.println("Product removed from cart successfully.");

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static int getValidIntInput() {
        while (true) {
            try {
                return s.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer:");
                s.next(); // clear the invalid input
            }
        }
    }

    public static double getValidDoubleInput() {
        while (true) {
            try {
                return s.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid decimal number:");
                s.next(); // clear the invalid input
            }
        }
    }

    public static String getValidStringInput() {
        while (true) {
            String input = s.next();
            if (!input.trim().isEmpty()) {
                return input;
            } else {
                System.out.println("Invalid input. Please enter a non-empty string:");
            }
        }
    }

    public static void main(String[] args){
        try {
            menu();
        } finally {
            s.close();
        }

    }
}