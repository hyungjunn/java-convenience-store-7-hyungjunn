package store;

import camp.nextstep.edu.missionutils.DateTimes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
        while (true) {
            outputView.printProductList(products);
            List<PurchaseProduct> purchaseProducts = inputView.readProductDetail(convenience); // TODO: 디테일 입력 검증
            PaymentInformation paymentInformation = determinePaymentAmount(purchaseProducts);
            BigDecimal finalAmount = paymentInformation.calculateFinalAmount();

            System.out.println("==============W 편의점================");
            outputView.printThreeTitle("상품명", "수량", "금액");
            long totalQuantity = countTotalQuantity(purchaseProducts);
            System.out.println("=============증     정===============");
            for (PurchaseProduct purchaseProduct : purchaseProducts) {
                String purchaseProductName = purchaseProduct.getName();
                Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
                long numberOfGiveaway = convenience.determineGiftItemCount(purchaseProductName, purchaseQuantity);
                if (numberOfGiveaway != 0) {
                    outputView.printFreeGift(purchaseProductName, numberOfGiveaway);
                }
                convenience.decreaseStock(purchaseProductName, purchaseQuantity);
            }
            outputView.printAboutAmount(totalQuantity, paymentInformation.getTotalAmount(), paymentInformation.getEventDiscountAmount(), paymentInformation.getMembershipDiscountAmount(), finalAmount);

            boolean wantedPurchaseOther = inputView.readWantedPurchaseOther();
            if (wantedPurchaseOther) {
                continue;
            }
            break;
        }
    }

    private PaymentInformation determinePaymentAmount(List<PurchaseProduct> purchaseProducts) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal eventDiscountAmount = BigDecimal.ZERO;
        BigInteger membershipDiscountAmount = BigInteger.ZERO;

        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            String purchaseProductName = purchaseProduct.getName();
            Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
            Product product = convenience.findProduct(purchaseProductName);
            BigDecimal totalByProduct = product.getPrice().multiply(BigDecimal.valueOf(purchaseProduct.getPurchaseQuantity()));
            totalAmount = totalAmount.add(totalByProduct);

            if (product.isApplyPromotion(purchaseQuantity, DateTimes.now().toLocalDate())) {
                eventDiscountAmount = product.applyPromotionDiscount(purchaseQuantity);
            }

            if (product.isAppliedPromotion() && product.isPromotionalOutOfStock(purchaseQuantity)) {
                boolean wantedPayFixedPriceForSomeQuantity = inputView.readWantedNoPromotionBenefit(product, purchaseQuantity);
                totalAmount = purchaseProduct.notifyRegularPaymentSomeQuantities(product, wantedPayFixedPriceForSomeQuantity, totalAmount);
            }

            if (product.canApplyPromotion(purchaseQuantity, DateTimes.now().toLocalDate())) {
                long promotionGetQuantity = product.getPromotion().getGet();
                boolean wantedAddBenefitProduct = inputView.readWantedAddBenefitProduct(purchaseProductName, promotionGetQuantity);
                long presentedQuantity = product.countNumberOfGiveAway(purchaseQuantity);
                purchaseProduct.notifyGiftBenefitMessage(presentedQuantity, wantedAddBenefitProduct);
                product.decreaseStock(promotionGetQuantity, DateTimes.now().toLocalDate());
                totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(presentedQuantity)));
                eventDiscountAmount = product.getPrice().multiply(BigDecimal.valueOf(presentedQuantity));
            }
        }

        boolean wantedMembershipDiscount = inputView.readWantedMembershipDiscount();
        if (wantedMembershipDiscount) {
            membershipDiscountAmount = totalAmount.subtract(eventDiscountAmount).multiply(BigDecimal.valueOf(0.3)).setScale(-3, RoundingMode.DOWN).toBigInteger();
        }

        return new PaymentInformation(totalAmount, eventDiscountAmount, membershipDiscountAmount);
    }

    private long countTotalQuantity(List<PurchaseProduct> purchaseProducts) {
        long totalQuantity = 0L;
        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            String purchaseProductName = purchaseProduct.getName();
            Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
            totalQuantity += purchaseQuantity;
            BigDecimal totalPrice = convenience.determineTotalPriceForPurchaseQuantity(purchaseProductName, purchaseQuantity);
            outputView.printThreeColumn(purchaseProductName, purchaseQuantity, totalPrice);
        }
        return totalQuantity;
    }
}
