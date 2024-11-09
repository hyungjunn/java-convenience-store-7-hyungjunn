package store;

import java.math.BigDecimal;
import java.util.List;

public class ConvenienceSystem {
    private final Convenience convenience;
    private final OutputView outputView;
    private final InputView inputView;

    public ConvenienceSystem(Convenience convenience) {
        this.convenience = convenience;
        this.outputView = new OutputView();
        this.inputView = new InputView();
    }

    public void productGuide() {
        List<Product> products = convenience.findAll();
        outputView.printProductList(products);
        // ex. [{사이다, 3}, {감자칩, 1}]
        List<PurchaseProduct> purchaseProducts = inputView.readProductDetail(convenience); // TODO: 디테일 입력 검증
        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            String purchaseProductName = purchaseProduct.name();
            Long purchaseQuantity = purchaseProduct.quantity();
            Product product = convenience.findProduct(purchaseProductName);
            notifyRegularPaymentSomeQuantities(product, purchaseQuantity);
            notifyGiftBenefitMessage(product, purchaseQuantity, purchaseProductName);
        }
    }

    private void notifyRegularPaymentSomeQuantities(Product product, Long purchaseQuantity) {
        // 일부 수량에 대해 정가로 구매할건지 안내 메시지
        if (product.isPromotionalOutOfStock(purchaseQuantity, product.getPromotion().getBuy())) {
            long noBenefitQuantity = product.countNoBenefitQuantity(purchaseQuantity);
            boolean wantedNoPromotionBenefit = inputView.readWantedNoPromotionBenefit(product, purchaseQuantity);
            // Y: 일부 수량에 대해 정가로 결제한다.
            if (wantedNoPromotionBenefit) {
                BigDecimal promotionDiscount = product.applyPromotionDiscount(purchaseQuantity);// 프로모션 할인 금액
                BigDecimal regularPaymentAmount = product.calculateWithFixedPrice(noBenefitQuantity);// 일부 수량만큼 정가 결제 금액
            }
            // N: 정가로 결제해야하는 수량만큼 제외한 후 결제를 진행한다.
            if (!wantedNoPromotionBenefit) {
                BigDecimal amountWithoutFixedPrice = product.calculateAmountWithoutFixedPrice(purchaseQuantity);// 정가 결제 수량 제외한 결제 금액
            }
        }
    }

    private void notifyGiftBenefitMessage(Product product, Long purchaseQuantity, String purchaseProductName) {
        // 증정 혜택 안내 메시지
        if (product.canApplyPromotion(purchaseQuantity)) {
            int presentedQuantity = product.getPromotion().getGet();
            boolean wantedAddBenefitProduct = inputView.readWantedAddBenefitProduct(purchaseProductName, presentedQuantity);
            if (wantedAddBenefitProduct) {
                purchaseQuantity += presentedQuantity;
            }
        }
    }

}
