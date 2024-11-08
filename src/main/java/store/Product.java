package store;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Product {
    private final String name;
    private final BigDecimal price;
    private Long promotionQuantity;
    private Long generalQuantity;
    private final Promotion promotion;

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

    public boolean canApplyPromotion(int purchaseQuantity) {
        if (promotion == null) {
            return false;
        }
        return promotion.canApplyPromotion(purchaseQuantity);
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
}
