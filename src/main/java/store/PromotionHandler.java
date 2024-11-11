package store;

public class PromotionHandler {
    private final Convenience convenience;

    public PromotionHandler(Convenience convenience) {
        this.convenience = convenience;
    }

    public boolean isPromotionalOutOfStock(PurchaseProduct purchaseProduct) {
        Product product = convenience.findProduct(purchaseProduct.getName());
        return product.isAppliedPromotion() && product.isPromotionalOutOfStock(purchaseProduct.getPurchaseQuantity());
    }
}
