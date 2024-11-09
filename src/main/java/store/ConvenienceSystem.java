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
        List<PurchaseProduct> purchaseProducts = inputView.readProductDetail(convenience); // TODO: 디테일 입력 검증
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            String purchaseProductName = purchaseProduct.getName();
            Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
            Product product = convenience.findProduct(purchaseProductName);
            BigDecimal totalByProduct = product.getPrice().multiply(BigDecimal.valueOf(purchaseProduct.getPurchaseQuantity()));
            // 구매 상품을 순회하면서 합계를 더한다
            totalAmount = totalAmount.multiply(totalByProduct);
            if (product.isPromotionalOutOfStock(purchaseQuantity, product.getPromotion().getBuy())) {
                boolean wantedNoPromotionBenefit = inputView.readWantedNoPromotionBenefit(product, purchaseQuantity);
                purchaseProduct.notifyRegularPaymentSomeQuantities(product, wantedNoPromotionBenefit);
            }
            if (product.canApplyPromotion(purchaseQuantity)) {
                int presentedQuantity = product.getPromotion().getGet();
                boolean wantedAddBenefitProduct = inputView.readWantedAddBenefitProduct(purchaseProductName, presentedQuantity);
                purchaseProduct.notifyGiftBenefitMessage(presentedQuantity, wantedAddBenefitProduct);
            }
        }
    }

}
