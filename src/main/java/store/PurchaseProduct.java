package store;

import java.math.BigDecimal;

public class PurchaseProduct {
    private final String name;
    private Long purchaseQuantity; // TODO: 불변 고려하기!

    public PurchaseProduct(String name, Long purchaseQuantity) {
        this.name = name;
        this.purchaseQuantity = purchaseQuantity;
    }

    public BigDecimal notifyRegularPaymentSomeQuantities(Product product, boolean wantedNoPromotionBenefit, BigDecimal totalAmount) {
        if (product.isPromotionalOutOfStock(purchaseQuantity, product.getPromotion().getBuy())) {
            // Y: 일부 수량에 대해 정가로 결제한다.
            if (wantedNoPromotionBenefit) {
                BigDecimal promotionDiscount = product.applyPromotionDiscount(purchaseQuantity);// 프로모션 할인 금액 <<< 영수증 정보
                return totalAmount.subtract(promotionDiscount);
            }
            // N: 정가로 결제해야하는 수량만큼 제외한 후 결제를 진행한다.
            BigDecimal amountWithoutFixedPrice = product.calculateAmountWithoutFixedPrice(purchaseQuantity);// 정가 결제 수량 제외한 결제 금액
            purchaseQuantity -= product.countNoBenefitQuantity(purchaseQuantity);
            totalAmount = amountWithoutFixedPrice;
        }
        return totalAmount;
    }

    public void notifyGiftBenefitMessage(long presentedQuantity, boolean wantedAddBenefitProduct) {
        if (wantedAddBenefitProduct) {
            purchaseQuantity += presentedQuantity;
        }
    }

    public String getName() {
        return name;
    }

    public Long getPurchaseQuantity() {
        return purchaseQuantity;
    }

    @Override
    public String toString() {
        return "PurchaseProduct{" +
               "name='" + name + '\'' +
               ", purchaseQuantity=" + purchaseQuantity +
               '}';
    }

}
