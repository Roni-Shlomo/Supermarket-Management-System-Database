package RoniShlomo_And_NikolYosef;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class Buyer extends User {
    private Address addresses;

    public Buyer(String name, String password, Address addresses) {
        super(name, password);
        this.addresses = addresses;
    }

    public Address getAddresses() {
        return addresses;
    }

    public void setAddresses(Address addresses) {
        this.addresses = addresses;
    }

    // Retrieving the open cart from the database
    public Cart getOpenCart() {
        try {
            return Manager_db.getOpenCartByBuyer(this.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Add product to cart with quantity
    public void addProductToCart(Product product, int quantity) {
        try {
            Cart cart = getOpenCart();
            if (cart == null) {
                int newCartID = Manager_db.createEmptyCartForBuyer(this.getName());
                cart = new Cart(newCartID, this.getName(), null, 0.0, new ArrayList<>());
            }
            int pnum = Manager_db.getPnum(product.getName(), product.getSellerName());

            // נוסיף את המוצר לעגלה
            Manager_db.insertProductIntoCart(cart.getCartID(), pnum, quantity);
            System.out.println("Product added to cart.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Retrieve order history
    public List<Cart> getOrdersHistory() {
        try {
            return Manager_db.getOrdersHistoryByBuyer(this.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Check if there is an open and empty cart
    public boolean isCartEmpty() {
        Cart cart = getOpenCart();
        return cart == null || cart.getAllProducts().isEmpty();
    }

    public void completePurchase() throws EmptyCartException {
        Cart cart = getOpenCart();

        if (cart == null || cart.getAllProducts().isEmpty()) {
            throw new EmptyCartException("Cannot complete purchase: The shopping cart is empty.");
        }

        try {
            Manager_db.markCartAsPaid(cart.getCartID());
            System.out.println("Purchase completed. Cart ID " + cart.getCartID() + " is now marked as paid.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "\nBuyer's name: " + getName() +
                "\nAddress: " + addresses +
                "\nCurrent Cart: " + getOpenCart() +
                "\nOrders History: " + getOrdersHistory() +
                "\n-------------";
    }
}
