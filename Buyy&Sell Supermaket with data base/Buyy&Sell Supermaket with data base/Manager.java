package RoniShlomo_And_NikolYosef;


import java.util.Comparator;
import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;



public class Manager {

    public boolean isEmptySeller() throws SQLException {
        return Manager_db.isEmptySeller();
    }

    public boolean isEmptyBuyer() throws SQLException {
        return Manager_db.isEmptyBuyer();
    }

    public void addSeller(String sellerName, String password) throws SQLException {
        Manager_db.addSeller(sellerName, password);
    }

    public void addBuyer(String buyerName, String password, Address address) throws SQLException {
        Manager_db.addBuyer(buyerName, password, address);
    }

    public boolean isSellerExists(String sellerName) throws SQLException {
        return Manager_db.isSellerExists(sellerName);
    }

    public boolean isBuyerExists(String buyerName) throws SQLException {
        return Manager_db.isBuyerExists(buyerName);
    }

    // Return the list of sellers
    public List<Seller> getSeller() throws SQLException {
        return Manager_db.getAllSellers();
    }


    // return the list of buyers
    public List<Buyer> getBuyer() throws SQLException {
        return Manager_db.getAllBuyers();
    }


    public void addProduct(String sellerName, Product product) throws SQLException {
        if (Manager_db.isSellerExists(sellerName)) {
            Manager_db.addProduct(product, sellerName);
        } else {
            System.out.println("Seller with name '" + sellerName + "' does not exist.");
        }
    }


    public void displayProductsByCategory(Product.eCategory category) throws SQLException {
        boolean foundProducts = false;
        List<Seller> sellers = Manager_db.getAllSellers();

        for (Seller seller : sellers) {
            List<Product> sellerProducts = seller.getAllProducts();

            for (Product product : sellerProducts) {
                if (product.getCategory() == category) {
                    if (!foundProducts) {
                        System.out.println("Products in category: " + category);
                        foundProducts = true;
                    }
                    if (product instanceof SpecialPackedProduct packedProduct) {
                        System.out.println(product + " price includes packaging fee of: " + packedProduct.getPackingPrice() + " NIS");
                    } else {
                        System.out.println(product);
                    }
                }
            }
        }

        if (!foundProducts) {
            System.out.println("No products found in category: " + category);
        }
    }



    // get seller by name
    public Seller getSellerByName(String sellerName) throws SQLException {
        List<Seller> sellers = Manager_db.getAllSellers();
        for (Seller seller : sellers) {
            if (seller.getName().equals(sellerName)) {
                return seller;
            }
        }
        return null;
    }





    // function to get sorted buyers by name
    public List<Buyer> getSortedBuyersByName() throws SQLException {
        List<Buyer> buyers = Manager_db.getAllBuyers();
        buyers.sort((b1, b2) -> b1.getName().compareTo(b2.getName()));
        return buyers;
    }



}
