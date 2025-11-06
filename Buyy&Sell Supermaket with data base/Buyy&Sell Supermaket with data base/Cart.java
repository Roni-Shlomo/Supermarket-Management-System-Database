package RoniShlomo_And_NikolYosef;

import java.time.LocalDate;
import java.util.List;

public class Cart {
    private int cartID;
    private String buyerName;
    private LocalDate buyDate;
    private double totalPrice;
    private List<Product> allProducts;

    public Cart(int cartID, String buyerName, LocalDate buyDate, double totalPrice, List<Product> allProducts) {
        this.cartID = cartID;
        this.buyerName = buyerName;
        this.buyDate = buyDate;
        this.totalPrice = totalPrice;
        this.allProducts = allProducts;
    }

    public int getCartID() {
        return cartID;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public LocalDate getBuyDate() {
        return buyDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public List<Product> getAllProducts() {
        return allProducts;
    }

    @Override
    public String toString() {
        return "Cart ID: " + cartID +
                ", Total Price: " + totalPrice +
                ", Date: " + buyDate +
                ", Products: " + allProducts;
    }
}
