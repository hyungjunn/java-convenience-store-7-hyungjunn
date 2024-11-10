package store;

import java.util.List;

public class Convenience {
    private final StoreRoom storeRoom;

    public Convenience(StoreRoom storeRoom) {
        this.storeRoom = storeRoom;
    }

    public List<Product> findAll() {
        storeRoom.save();
        return storeRoom.readAll();
    }

    public Product findProduct(String name) {
        return storeRoom.findByName(name);
    }

    public long determineGiftItemCount(String purchaseProductName, long purchaseQuantity) {
        Product product = findProduct(purchaseProductName);
        return product.countNumberOfGiveAway(purchaseQuantity);
    }

}
