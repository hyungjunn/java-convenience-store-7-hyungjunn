package store;

import camp.nextstep.edu.missionutils.DateTimes;

import java.math.BigDecimal;

public class PurchaseProduct {
    private final String name;
    private Long purchaseQuantity; // TODO: 불변 고려하기!

    public PurchaseProduct(String name, Long purchaseQuantity) {
        this.name = name;
        this.purchaseQuantity = purchaseQuantity;
    }

    // 일부 수량에 대해 정가로 구매할건지 안내 메시지
    public BigDecimal notifyRegularPaymentSomeQuantities(Product product, boolean wantedNoPromotionBenefit, BigDecimal totalAmount) {
        if (product.isPromotionalOutOfStock(purchaseQuantity, product.getPromotion().getBuy())) {
            long noBenefitQuantity = product.countNoBenefitQuantity(purchaseQuantity);
            // Y: 일부 수량에 대해 정가로 결제한다.
            if (wantedNoPromotionBenefit) {
                // totalAmount 에서 promotionDiscount 를 빼야함.
                BigDecimal promotionDiscount = product.applyPromotionDiscount(purchaseQuantity);// 프로모션 할인 금액 <<< 영수증 정보
                // TODO: 필요없는거 같으나, 일단 남겨둠. 진짜 필요없어지면 없애기!
                BigDecimal regularPaymentAmount = product.calculateWithFixedPrice(noBenefitQuantity);// 일부 수량만큼 정가 결제 금액
                return totalAmount.add(totalAmount.subtract(promotionDiscount));
            }
            // N: 정가로 결제해야하는 수량만큼 제외한 후 결제를 진행한다.
            // totalAmount 이 요걸로 바뀜
            BigDecimal amountWithoutFixedPrice = product.calculateAmountWithoutFixedPrice(purchaseQuantity);// 정가 결제 수량 제외한 결제 금액
            purchaseQuantity -= product.countNoBenefitQuantity(purchaseQuantity);
            totalAmount = amountWithoutFixedPrice; // totalAmount 는 정가 결제는 빼고 프로모션 결제만 한 값
        }
        return totalAmount;
    }

    // 증정 혜택 안내 메시지
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
