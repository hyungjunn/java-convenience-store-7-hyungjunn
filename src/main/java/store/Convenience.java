package store;

import camp.nextstep.edu.missionutils.DateTimes;

import java.math.BigDecimal;
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

    public BigDecimal determineTotalPriceForPurchaseQuantity(String purchaseProductName, long purchaseQuantity) {
        Product product = findProduct(purchaseProductName);
        return product.calculateWithFixedPrice(purchaseQuantity);
    }

    public void decreaseStock(String name, long purchaseQuantity) {
        Product product = findProduct(name);
        product.decreaseStock(purchaseQuantity, DateTimes.now().toLocalDate());
    }

}
