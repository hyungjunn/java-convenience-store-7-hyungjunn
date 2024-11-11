package store;

import java.math.BigDecimal;

public record PromotionBenefitResult(BigDecimal totalAmount, BigDecimal eventDiscountAmount) {
}
