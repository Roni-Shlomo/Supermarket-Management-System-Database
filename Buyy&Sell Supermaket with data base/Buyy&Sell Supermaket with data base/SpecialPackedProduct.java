package RoniShlomo_And_NikolYosef;

public class SpecialPackedProduct extends Product {
    private double packingPrice;

    public SpecialPackedProduct(int pnum, String pname, double price, eCategory category, double packingPrice) {
        super(pnum, pname, price, category);
        this.packingPrice = packingPrice;
    }

    /*
    public SpecialPackedProduct(SpecialPackedProduct special_product) {
        super(special_product.getName(), special_product.getPrice(), special_product.getCategory());
        this.packingPrice = special_product.packingPrice;
    }

     */

    public double getPackingPrice() {
        return packingPrice;
    }

    public void setPackingPrice(double packingPrice) {
        this.packingPrice = packingPrice;
    }

    @Override
    public String toString() {
        return getName() + ": " + price + " NIS, serial number: " + serialNumber + ", category: " + category +
                "\n, Price includes packaging fee of: " + (price) + " NIS";
    }

}