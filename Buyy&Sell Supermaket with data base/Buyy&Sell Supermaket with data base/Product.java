package RoniShlomo_And_NikolYosef;

public class Product implements Cloneable {
    private static int serialCounter;
    protected final int serialNumber;
    protected String name;
    protected double price;
    protected final eCategory category;
    private String sellerName;

    public enum eCategory {
        KIDS,
        ELECTRONICS,
        OFFICE,
        CLOTHING
    }

    public Product(int pnum, String name, double price, eCategory category, String sellerName) {
        this.serialNumber = pnum;
        this.name = name;
        this.price = price;
        this.category = category;
        this.sellerName = sellerName;
    }
    public Product(Product other) {
        this.serialNumber = other.serialNumber;
        this.name = other.name;
        this.price = other.price;
        this.category = other.category;
    }

    public Product(int serialNumber, String name, double price, String category, String sellerName) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.price = price;
        this.category = eCategory.valueOf(category.toUpperCase()); // המרה מ־String ל־enum
        this.sellerName = sellerName;
    }

    public Product(int serialNumber, String name, double price, eCategory category) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.price = price;
        this.category = category;
    }


    public eCategory getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public static boolean ifValidCategoryChoice(int categoryChoice) {
        return categoryChoice >= 1 && categoryChoice <= eCategory.values().length;
    }



    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ").append(price).append(" NIS, serial number: ").append(serialNumber).append(", category: ").append(category).append('\n');
        return sb.toString();
    }
}