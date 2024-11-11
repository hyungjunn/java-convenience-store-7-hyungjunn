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
    private final PromotionHandler promotionHandler;

    public ConvenienceSystem(Convenience convenience) {
        this.convenience = convenience;
        this.promotionHandler = new PromotionHandler(convenience);
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
                .multiply(BigDecimal.valueOf(0.3))
                .setScale(-3, RoundingMode.DOWN)
                .toBigInteger();
    }

    private ProductAmountDetail calculateProductAmount(PurchaseProduct purchaseProduct) {
        String purchaseProductName = purchaseProduct.getName();
        Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
        Product product = convenience.findProduct(purchaseProductName);
        BigDecimal totalByProduct = product.getPrice().multiply(BigDecimal.valueOf(purchaseProduct.getPurchaseQuantity()));
        BigDecimal totalAmount = (BigDecimal.ZERO).add(totalByProduct);
        BigDecimal eventDiscountAmount = BigDecimal.ZERO;

        if (isPromotionalApplicable(purchaseProduct)) {
            eventDiscountAmount = product.applyPromotionDiscount(purchaseQuantity);
        }
        if (promotionHandler.isPromotionalOutOfStock(purchaseProduct)) {
            boolean wantedPayFixedPriceForSomeQuantity = inputView.readWantedNoPromotionBenefit(product, purchaseQuantity);
            totalAmount = purchaseProduct.notifyRegularPaymentSomeQuantities(product, wantedPayFixedPriceForSomeQuantity, totalAmount);
        }
        if (canApplyPromotion(purchaseProduct)) {
            PromotionBenefitResult promotionBenefitResult = handlePromotionBenefit(purchaseProduct);
            totalAmount = totalAmount.add(promotionBenefitResult.totalAmount());
            eventDiscountAmount = promotionBenefitResult.eventDiscountAmount();
        }
        return new ProductAmountDetail(totalAmount, eventDiscountAmount);
    }

    private boolean canApplyPromotion(PurchaseProduct purchaseProduct) {
        Product product = convenience.findProduct(purchaseProduct.getName());
        return product.canApplyPromotion(purchaseProduct.getPurchaseQuantity(), DateTimes.now().toLocalDate());
    }



    private boolean isPromotionalApplicable(PurchaseProduct purchaseProduct) {
        Product product = convenience.findProduct(purchaseProduct.getName());
        return product.isApplyPromotion(purchaseProduct.getPurchaseQuantity(), DateTimes.now().toLocalDate());
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

    private PromotionBenefitResult handlePromotionBenefit(PurchaseProduct purchaseProduct) {
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
