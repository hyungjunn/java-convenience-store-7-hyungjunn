package store;

import camp.nextstep.edu.missionutils.DateTimes;

import java.math.BigDecimal;

// TODO: 리팩토링을 해서 전체 테스트는 통과. 단위 테스트 작성해야함!
// TODO: 그러나, inputView 에 의존하고 있어서 이 로직을 밖으로 빼주어야 함!
public class PromotionHandler {
    private final Convenience convenience;
    private final InputView inputView;

    public PromotionHandler(Convenience convenience, InputView inputView) {
        this.convenience = convenience;
        this.inputView = inputView;
    }

    public boolean isPromotionalOutOfStock(PurchaseProduct purchaseProduct) {
        Product product = convenience.findProduct(purchaseProduct.getName());
        return product.isAppliedPromotion() && product.isPromotionalOutOfStock(purchaseProduct.getPurchaseQuantity());
    }

    public boolean isPromotionalApplicable(PurchaseProduct purchaseProduct) {
        Product product = convenience.findProduct(purchaseProduct.getName());
        return product.isApplyPromotion(purchaseProduct.getPurchaseQuantity(), DateTimes.now().toLocalDate());
    }

    public PromotionBenefitResult handlePromotionBenefit(PurchaseProduct purchaseProduct) {
        Product product = convenience.findProduct(purchaseProduct.getName());
        long promotionGetQuantity = product.getPromotion().getGet();
        boolean wantedAddBenefitProduct = inputView.readWantedAddBenefitProduct(purchaseProduct.getName(), promotionGetQuantity);
        if (!wantedAddBenefitProduct) {
            return new PromotionBenefitResult(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        long presentedQuantity = calculatePresentedQuantity(purchaseProduct);
        processPromotionBenefit(purchaseProduct, presentedQuantity, promotionGetQuantity);
        return new PromotionBenefitResult(
                product.getPrice().multiply(BigDecimal.valueOf(presentedQuantity)),
                product.getPrice().multiply(BigDecimal.valueOf(presentedQuantity))
        );
    }

    private void processPromotionBenefit(PurchaseProduct purchaseProduct, long presentedQuantity, long promotionGetQuantity) {
        Product product = convenience.findProduct(purchaseProduct.getName());
        purchaseProduct.notifyGiftBenefit(presentedQuantity);
        product.decreaseStock(promotionGetQuantity, DateTimes.now().toLocalDate());
    }

    private long calculatePresentedQuantity(PurchaseProduct purchaseProduct) {
        Product product = convenience.findProduct(purchaseProduct.getName());
        return product.countNumberOfGiveAway(purchaseProduct.getPurchaseQuantity());
    }

}
