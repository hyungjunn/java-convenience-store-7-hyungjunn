package store;

import java.math.BigDecimal;

public class PurchaseProduct {
    private final String name;
    private Long purchaseQuantity;

    public PurchaseProduct(String name, Long purchaseQuantity) {
        this.name = name;
        this.purchaseQuantity = purchaseQuantity;
    }

    public BigDecimal notifyRegularPaymentSomeQuantities(Product product, boolean wantedPayFixedPriceForSomeQuantity, BigDecimal totalAmount) {
        if (product.isPromotionalOutOfStock(purchaseQuantity)) {
            if (!wantedPayFixedPriceForSomeQuantity) {
                BigDecimal amountWithoutFixedPrice = product.calculateAmountWithoutFixedPrice(purchaseQuantity);
                purchaseQuantity -= product.countNoBenefitQuantity(purchaseQuantity);
                totalAmount = amountWithoutFixedPrice;
            }
        }
        return totalAmount;
    }

    public void notifyGiftBenefit(long presentedQuantity) {
        purchaseQuantity += presentedQuantity;
    }

    public String getName() {
        return name;
    }

    public Long getPurchaseQuantity() {
        return purchaseQuantity;
    }

}
