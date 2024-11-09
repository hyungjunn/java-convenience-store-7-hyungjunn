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
            Product product = convenience.findProduct(purchaseProduct.name());
            Long purchaseQuantity = purchaseProduct.quantity();
            long noBenefitQuantity = product.countNoBenefitQuantity(purchaseQuantity);
            boolean wantedNoPromotionBenefit = inputView.readWantedNoPromotionBenefit(product, purchaseQuantity);
            if (wantedNoPromotionBenefit) {
                // Y: 일부 수량에 대해 정가로 결제한다.
                // 1. 프로모션 적용된 수량 만큼 적용해서 결제
                // 2. 그 이후 일부 수량에 대해 정가로 결제
                BigDecimal promotionDiscount = product.applyPromotionDiscount(purchaseQuantity);// 프로모션 할인 금액
                BigDecimal regularPaymentAmount = product.calculateWithFixedPrice(noBenefitQuantity);// 일부 수량만큼 정가 결제 금액
            }
            // N: 정가로 결제해야하는 수량만큼 제외한 후 결제를 진행한다.
            if (!wantedNoPromotionBenefit) {
                BigDecimal amountWithoutFixedPrice = product.calculateAmountWithoutFixedPrice(purchaseQuantity);// 정가 결제 수량 제외한 결제 금액
            }

        }
    }

}
