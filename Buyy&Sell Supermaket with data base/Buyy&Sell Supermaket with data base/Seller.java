package RoniShlomo_And_NikolYosef;


import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;


public class Seller extends User {

    private List<Product> products = new ArrayList<>();

    public Seller(String name, String password) {
        super(name, password);
    }

    public List<Product> getAllProducts() {
        try {
            return Manager_db.getProductsBySeller(this.getName());
        } catch (SQLException e) {
            System.out.println("(Failed to load products from DB for " + getName() + ")");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addProduct(Product product) {
        products.add(product);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nSeller's name: ").append(getName()).append("\n");

        if (products.isEmpty()) {
            sb.append("No products.\n");
        } else {
            sb.append("Products:\n");
            for (Product p : products) {
                sb.append("  ").append(p);
            }
        }

        sb.append("-------------");
        return sb.toString();
    }


}
