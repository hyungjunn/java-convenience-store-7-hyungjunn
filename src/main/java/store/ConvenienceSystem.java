package store;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
        BigDecimal eventDiscountAmount = BigDecimal.ZERO;
        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            String purchaseProductName = purchaseProduct.getName();
            Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
            Product product = convenience.findProduct(purchaseProductName);
            BigDecimal totalByProduct = product.getPrice().multiply(BigDecimal.valueOf(purchaseProduct.getPurchaseQuantity()));
            // 구매 상품을 순회하면서 합계를 더한다
            totalAmount = totalAmount.add(totalByProduct);
            if (product.getPromotion() != null && product.isPromotionalOutOfStock(purchaseQuantity, product.getGet())) {
                boolean wantedNoPromotionBenefit = inputView.readWantedNoPromotionBenefit(product, purchaseQuantity);
                totalAmount = purchaseProduct.notifyRegularPaymentSomeQuantities(product, wantedNoPromotionBenefit, totalAmount);
            }
            if (product.canApplyPromotion(purchaseQuantity)) {
                int presentedQuantity = product.getPromotion().getGet();
                boolean wantedAddBenefitProduct = inputView.readWantedAddBenefitProduct(purchaseProductName, presentedQuantity);
                purchaseProduct.notifyGiftBenefitMessage(presentedQuantity, wantedAddBenefitProduct);
                eventDiscountAmount = product.getPrice().multiply(BigDecimal.valueOf(presentedQuantity));
                totalAmount = totalAmount.subtract(eventDiscountAmount);
            }
        }

        // 멤버쉽 할인
        boolean wantedMembershipDiscount = inputView.readWantedMembershipDiscount();
        BigDecimal membershipDiscountAmount = BigDecimal.ZERO;
        if (wantedMembershipDiscount) {
            membershipDiscountAmount = totalAmount.multiply(BigDecimal.valueOf(0.3)).setScale(-3, RoundingMode.DOWN);
        }
        BigDecimal finalAmount = totalAmount.subtract(membershipDiscountAmount);

        System.out.println("==============W 편의점================");
        System.out.println("상품명                수량       금액");
        DecimalFormat df = new DecimalFormat("#,###");
        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            String purchaseProductName = purchaseProduct.getName();
            Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
            Product product = convenience.findProduct(purchaseProductName);
            System.out.println(purchaseProductName + "     " + purchaseQuantity + "    " + df.format(product.calculateWithFixedPrice(purchaseQuantity)));
        }
        System.out.println("=============증     정===============");
        System.out.println("finalAmount = " + finalAmount);
    }
}
