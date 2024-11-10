package store;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Product {
    private final String name;
    private final BigDecimal price;
    private Long promotionQuantity;
    private Long generalQuantity;
    private Promotion promotion;

    public Product(ProductBuilder builder) {
        this.name = builder.name;
        this.price = builder.price;
        this.promotionQuantity = 0L;
        this.generalQuantity = 0L;
        this.promotion = builder.promotion;
    }

    public static class ProductBuilder {
        private String name;
        private BigDecimal price;
        private Long promotionQuantity;
        private Long generalQuantity;
        private Promotion promotion;

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public ProductBuilder promotionQuantity(Long promotionQuantity) {
            this.promotionQuantity = promotionQuantity;
            return this;
        }

        public ProductBuilder generalQuantity(Long generalQuantity) {
            this.generalQuantity = generalQuantity;
            return this;
        }

        public ProductBuilder promotion(Promotion promotion) {
            this.promotion = promotion;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }

    public Product(
            String name,
            BigDecimal price,
            Long promotionQuantity,
            Long generalQuantity,
            Promotion promotion
    ) {
        this.name = name;
        this.price = price;
        this.promotionQuantity = promotionQuantity;
        this.generalQuantity = generalQuantity;
        this.promotion = promotion;
    }

    public void decreaseStock(Long purchaseQuantity, LocalDate date) {
        if (isNotAppliedPromotion()) {
            decreaseGeneralQuantity(purchaseQuantity);
            return;
        }
        decreaseStockDuringPromotion(purchaseQuantity, date);
    }

    private void decreaseGeneralQuantity(Long purchaseQuantity) {
        validateQuantityExceeded(purchaseQuantity);
        generalQuantity -= purchaseQuantity;
    }

    private void decreaseStockDuringPromotion(Long purchaseQuantity, LocalDate date) {
        if (promotion.isPromotionPeriod(date)) {
            if (promotionQuantity < purchaseQuantity) {
                long exceedQuantity = purchaseQuantity - promotionQuantity;
                decreaseGeneralQuantity(exceedQuantity);
                clearOutPromotionStock();
                return;
            }
            promotionQuantity -= purchaseQuantity;
        }
    }

    private void clearOutPromotionStock() {
        promotionQuantity -= promotionQuantity;
    }

    private void validateQuantityExceeded(Long purchaseQuantity) {
        if (purchaseQuantity > generalQuantity) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    private boolean isNotAppliedPromotion() {
        return promotion == null;
    }

    public boolean isAppliedPromotion() {
        return promotion != null;
    }

    // 증정 혜택을 받을 수 있는지
    public boolean canApplyPromotion(Long purchaseQuantity, LocalDate date) {
        if (isNotAppliedPromotion()) {
            return false;
        }
        if (!promotion.isPromotionPeriod(date)) {
            return false;
        }
        return promotion.canApplyPromotion(purchaseQuantity);
    }

    public boolean isApplyPromotion(Long purchaseQuantity, LocalDate date) {
        if (isNotAppliedPromotion()) {
            return false;
        }
        if (!promotion.isPromotionPeriod(date)) {
            return false;
        }
        return promotion.isApplyPromotion(purchaseQuantity);
    }

    public BigDecimal applyPromotionDiscount(Long purchaseQuantity) {
        long numberOfGiveaway = 0L;
        if (purchaseQuantity <= promotionQuantity) {
            return promotionDiscount(purchaseQuantity, numberOfGiveaway);
        }
        if (promotionQuantity == 0) {
            return BigDecimal.ZERO;
        }
        numberOfGiveaway = promotion.countNumberOfFreeGift(promotionQuantity);
        return calculateDiscount(numberOfGiveaway);
    }

    private BigDecimal calculateDiscount(long numberOfGiveaway) {
        return price.multiply(BigDecimal.valueOf(numberOfGiveaway));
    }

    private BigDecimal promotionDiscount(Long purchaseQuantity, long numberOfGiveaway) {
        if (promotion.isApplyPromotion(purchaseQuantity)) {
            numberOfGiveaway = countNumberOfGiveAway(purchaseQuantity);
        }
        return calculateDiscount(numberOfGiveaway);
    }

    public long countNumberOfGiveAway(Long purchaseQuantity) {
        long numberOfGiveaway = 0L;
        if (promotion == null || promotionQuantity == 0) {
            return 0L;
        }
        if (purchaseQuantity <= promotionQuantity) {
            numberOfGiveaway = promotion.countNumberOfFreeGift(purchaseQuantity);
        }
        if (purchaseQuantity > promotionQuantity) {
            numberOfGiveaway = promotion.countNumberOfFreeGift(promotionQuantity);
        }
        return numberOfGiveaway;
    }

    public long countNoBenefitQuantity(Long purchaseQuantity) {
        int buyAndGet = promotion.extractBuyAndGet();
        if (isPromotionalOutOfStock(purchaseQuantity)) {
            return purchaseQuantity - countPromotionProduct(buyAndGet);
        }
        return purchaseQuantity;
    }

    private long countPromotionProduct(int buyAndGet) {
        return (promotionQuantity / buyAndGet) * buyAndGet;
    }

    public  boolean isPromotionalOutOfStock(Long purchaseQuantity) {
        return purchaseQuantity > countPromotionProduct(promotion.extractBuyAndGet());
    }

    // 주어진 수량을 정가로 계산한다.
    public BigDecimal calculateWithFixedPrice(Long purchaseQuantity) {
        return price.multiply(BigDecimal.valueOf(purchaseQuantity));
    }

    public long countQuantityWithoutFixedPrice(Long purchaseQuantity) {
        return purchaseQuantity - countNoBenefitQuantity(purchaseQuantity);
    }

    public BigDecimal calculateAmountWithoutFixedPrice(Long purchaseQuantity) {
        return price.multiply(BigDecimal.valueOf(countQuantityWithoutFixedPrice(purchaseQuantity)));
    }

    // 프로모션 미적용 금액의 30% 멤버십 할인
    public BigInteger nonPromotionalMembershipDiscount(Long purchaseQuantity) {
        BigInteger discount = calculateWithFixedPrice(purchaseQuantity)
                .multiply(BigDecimal.valueOf(0.3))
                .setScale(-3, RoundingMode.DOWN)
                .toBigInteger();
        return compareBetweenMaxMemberShipAnd(discount);
    }

    private BigInteger compareBetweenMaxMemberShipAnd(BigInteger discount) {
        if (discount.compareTo(BigInteger.valueOf(8000)) > 0) {
            return BigInteger.valueOf(8000);
        }
        return discount;
    }

    // 프로모션 적용 후 남은 금액에 멤버십 할인
    public BigInteger promotionalMembershipDiscount(Long purchaseQuantity) {
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(purchaseQuantity));
        BigDecimal promotionDiscount = applyPromotionDiscount(purchaseQuantity);
        BigDecimal remainAmount = totalAmount.subtract(promotionDiscount);
        BigInteger discount = remainAmount
                .multiply(BigDecimal.valueOf(0.3))
                .setScale(-3, RoundingMode.DOWN)
                .toBigInteger();
        return compareBetweenMaxMemberShipAnd(discount);
    }

    public boolean isProductName(String name) {
        return this.name.equals(name);
    }

    public long getQuantity() {
        return promotionQuantity + generalQuantity;
    }

    // TODO: null 포인터 고려!
    public int getGet() {
        return promotion.getBuy();
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getPromotionQuantity() {
        return promotionQuantity;
    }

    public Long getGeneralQuantity() {
        return generalQuantity;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotionQuantity(Long promotionQuantity) {
        this.promotionQuantity = promotionQuantity;
    }

    public void setGeneralQuantity(Long generalQuantity) {
        this.generalQuantity = generalQuantity;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    @Override
    public String toString() {
        return "Product{" +
               "name='" + name + '\'' +
               ", price=" + price +
               ", promotionQuantity=" + promotionQuantity +
               ", generalQuantity=" + generalQuantity +
               ", promotion=" + promotion +
               '}';
    }
}
