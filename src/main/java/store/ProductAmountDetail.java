package store;

import java.math.BigDecimal;

public class ProductAmountDetail {
    BigDecimal totalAmount;
    BigDecimal eventDiscountAmount;

    public ProductAmountDetail(BigDecimal totalAmount, BigDecimal eventDiscountAmount) {
        this.totalAmount = totalAmount;
        this.eventDiscountAmount = eventDiscountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getEventDiscountAmount() {
        return eventDiscountAmount;
    }
}
