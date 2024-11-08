package store;

import java.math.BigDecimal;

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

    public void decreaseStock(Long purchaseQuantity) {
        // 만약 purchaseQuantity가 프로모션 갯수보다 많은 경우
        // 0. purchaseQuantity - 프로모션 갯수를 한다
        // 1. 프로모션 갯수를 0으로 만든다.
        // 2. 일반 갯수 - 0번 갯수를 한다.
        if (promotionQuantity < purchaseQuantity) {
            long exceedQuantity = purchaseQuantity - promotionQuantity;
            if (exceedQuantity > generalQuantity) {
                throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
            }
            generalQuantity -= exceedQuantity;
            promotionQuantity -= promotionQuantity;
            return;
        }
        promotionQuantity -= purchaseQuantity;
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
