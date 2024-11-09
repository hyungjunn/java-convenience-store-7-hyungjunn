package store;

public class PurchaseProduct {
    private final String name;
    private final Long quantity;

    public PurchaseProduct(String name, Long quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "PurchaseProduct{" +
               "name='" + name + '\'' +
               ", quantity=" + quantity +
               '}';
    }
}
