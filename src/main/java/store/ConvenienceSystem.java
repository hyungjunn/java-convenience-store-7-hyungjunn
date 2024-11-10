package store;

import java.math.BigDecimal;
import java.math.BigInteger;
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
        long presentedQuantity = 0L; // 증정 갯수
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal eventDiscountAmount = BigDecimal.ZERO;
        BigInteger membershipDiscountAmount = BigInteger.ZERO;
        BigDecimal finalAmount = BigDecimal.ZERO;

        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            String purchaseProductName = purchaseProduct.getName();
            Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
            Product product = convenience.findProduct(purchaseProductName);
            BigDecimal totalByProduct = product.getPrice().multiply(BigDecimal.valueOf(purchaseProduct.getPurchaseQuantity()));
            // 구매 상품을 순회하면서 합계를 더한다
            totalAmount = totalAmount.add(totalByProduct);
            if (product.getPromotion() != null && product.isPromotionalOutOfStock(purchaseQuantity, product.getGet())) {
                boolean wantedNoPromotionBenefit = inputView.readWantedNoPromotionBenefit(product, purchaseQuantity);
                finalAmount = finalAmount.add(purchaseProduct.notifyRegularPaymentSomeQuantities(product, wantedNoPromotionBenefit, totalAmount));
            }
            if (product.canApplyPromotion(purchaseQuantity)) {
                int promotionGetQuantity = product.getPromotion().getGet();
                boolean wantedAddBenefitProduct = inputView.readWantedAddBenefitProduct(purchaseProductName, promotionGetQuantity);
                presentedQuantity = product.countNumberOfGiveAway(purchaseQuantity);
                purchaseProduct.notifyGiftBenefitMessage(presentedQuantity, wantedAddBenefitProduct);
                // totalAmount 에 증정 상품의 금액만큼 더해준다.
                totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(presentedQuantity)));
                eventDiscountAmount = product.getPrice().multiply(BigDecimal.valueOf(presentedQuantity));
                // finalAmount = totalAmount.subtract(eventDiscountAmount);
            }
            if (product.isApplyPromotion(purchaseQuantity)) {
                eventDiscountAmount = product.applyPromotionDiscount(purchaseQuantity);
            }
        }

        // 멤버쉽 할인
        boolean wantedMembershipDiscount = inputView.readWantedMembershipDiscount();
        if (wantedMembershipDiscount) {
            membershipDiscountAmount = finalAmount.multiply(BigDecimal.valueOf(0.3)).setScale(-3, RoundingMode.DOWN).toBigInteger();
        }

        // 최종 금액
        finalAmount = totalAmount
                .subtract(eventDiscountAmount)
                .subtract(new BigDecimal(membershipDiscountAmount));

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
        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            String purchaseProductName = purchaseProduct.getName();
            Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
            Product product = convenience.findProduct(purchaseProductName);
            long numberOfGiveaway = product.countNumberOfGiveAway(purchaseQuantity);
            if (numberOfGiveaway != 0) {
                System.out.println(product.getName() + "             " + numberOfGiveaway);
            }
        }

        System.out.println("totalAmount = " + df.format(totalAmount));
        System.out.println("eventDiscountAmount = -" + df.format(eventDiscountAmount));
        System.out.println("membershipDiscountAmount = -" + df.format(membershipDiscountAmount));
        System.out.println("finalAmount = " + df.format(finalAmount));
    }
}
