package store;

import camp.nextstep.edu.missionutils.DateTimes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

public class ConvenienceSystem {
    private static final double EVENT_DISCOUNT_RATE = 0.3;
    private static final int HUNDREDS_PLACE = -3; // 백의 자리

    private final Convenience convenience;
    private final OutputView outputView;
    private final InputView inputView = new InputView();
    private final PromotionHandler promotionHandler;

    public ConvenienceSystem(Convenience convenience) {
        this.convenience = convenience;
        this.promotionHandler = new PromotionHandler(convenience, inputView);
        this.outputView = new OutputView();
    }

    public void productGuide() {
        List<Product> products = convenience.findAll();
        do {
            purchase(products);
        } while (isContinueShopping());
    }

    private void purchase(List<Product> products) {
        outputView.printProductList(products);
        List<PurchaseProduct> purchaseProducts = inputView.readProductDetail(convenience); // TODO: 디테일 입력 검증
        PaymentInformation paymentInformation = determinePaymentAmount(purchaseProducts);
        BigDecimal finalAmount = paymentInformation.calculateFinalAmount();
        long totalQuantity = countTotalQuantity(purchaseProducts);
        renderPurchaseResult(purchaseProducts, totalQuantity, paymentInformation, finalAmount);
    }

    private void renderPurchaseResult(List<PurchaseProduct> purchaseProducts, long totalQuantity, PaymentInformation paymentInformation, BigDecimal finalAmount) {
        outputView.printConvenienceHeader();
        outputView.printThreeTitle("상품명", "수량", "금액");
        outputView.printGiftItemHeader();
        renderGiftItem(purchaseProducts);
        outputView.printAboutAmount(
                totalQuantity,
                paymentInformation.getTotalAmount(),
                paymentInformation.getEventDiscountAmount(),
                paymentInformation.getMembershipDiscountAmount(),
                finalAmount
        );
    }

    private void renderGiftItem(List<PurchaseProduct> purchaseProducts) {
        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            String purchaseProductName = purchaseProduct.getName();
            Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
            long numberOfGiveaway = convenience.determineGiftItemCount(purchaseProductName, purchaseQuantity);
            if (numberOfGiveaway != 0) {
                outputView.printFreeGift(purchaseProductName, numberOfGiveaway);
            }
            convenience.decreaseStock(purchaseProductName, purchaseQuantity);
        }
    }

    private boolean isContinueShopping() {
        return inputView.readWantedPurchaseOther();
    }

    private PaymentInformation determinePaymentAmount(List<PurchaseProduct> purchaseProducts) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal eventDiscountAmount = BigDecimal.ZERO;
        for (PurchaseProduct purchaseProduct : purchaseProducts) {
            ProductAmountDetail productAmountDetail = calculateProductAmount(purchaseProduct);
            totalAmount = totalAmount.add(productAmountDetail.getTotalAmount());
            eventDiscountAmount = eventDiscountAmount.add(productAmountDetail.getEventDiscountAmount());
        }
        BigInteger membershipDiscountAmount = determinePaymentAmount(totalAmount, eventDiscountAmount);
        return new PaymentInformation(totalAmount, eventDiscountAmount, membershipDiscountAmount);
    }

    private BigInteger determinePaymentAmount(BigDecimal totalAmount, BigDecimal eventDiscountAmount) {
        boolean wantedMembershipDiscount = inputView.readWantedMembershipDiscount();
        if (!wantedMembershipDiscount) {
            return BigInteger.ZERO;
        }
        return totalAmount
                .subtract(eventDiscountAmount)
                .multiply(BigDecimal.valueOf(EVENT_DISCOUNT_RATE))
                .setScale(HUNDREDS_PLACE, RoundingMode.DOWN)
                .toBigInteger();
    }

    private ProductAmountDetail calculateProductAmount(PurchaseProduct purchaseProduct) {
        Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
        Product product = convenience.findProduct(purchaseProduct.getName());
        BigDecimal totalByProduct = product.getPrice().multiply(BigDecimal.valueOf(purchaseProduct.getPurchaseQuantity()));
        BigDecimal totalAmount = (BigDecimal.ZERO).add(totalByProduct);
        BigDecimal eventDiscountAmount = BigDecimal.ZERO;
        if (promotionHandler.isPromotionalApplicable(purchaseProduct)) {
            eventDiscountAmount = product.applyPromotionDiscount(purchaseQuantity);
        }
        if (promotionHandler.isPromotionalOutOfStock(purchaseProduct)) {
            boolean wantedPayFixedPriceForSomeQuantity = inputView.readWantedNoPromotionBenefit(product, purchaseQuantity);
            totalAmount = purchaseProduct.notifyRegularPaymentSomeQuantities(product, wantedPayFixedPriceForSomeQuantity, totalAmount);
        }
        if (canApplyPromotion(purchaseProduct)) {
            PromotionBenefitResult promotionBenefitResult = promotionHandler.handlePromotionBenefit(purchaseProduct);
            totalAmount = totalAmount.add(promotionBenefitResult.totalAmount());
            eventDiscountAmount = promotionBenefitResult.eventDiscountAmount();
        }
        return new ProductAmountDetail(totalAmount, eventDiscountAmount);
    }

    private boolean canApplyPromotion(PurchaseProduct purchaseProduct) {
        Product product = convenience.findProduct(purchaseProduct.getName());
        return product.canApplyPromotion(purchaseProduct.getPurchaseQuantity(), DateTimes.now().toLocalDate());
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
