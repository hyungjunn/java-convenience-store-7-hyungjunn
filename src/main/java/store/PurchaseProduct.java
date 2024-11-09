package store;

public record PurchaseProduct(String name, Long quantity) {

    @Override
    public String toString() {
        return "PurchaseProduct{" +
               "name='" + name + '\'' +
               ", quantity=" + quantity +
               '}';
    }
}
