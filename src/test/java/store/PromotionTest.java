package store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionTest {
    @DisplayName("프로모션 기간 중인지 판별한다.")
    @Test
    void isPromotionPeriodTest() {
        LocalDate date = LocalDate.of(2024, 12, 8);
        boolean isDuringPromotion1 = Promotion.CARBONATE.isPromotionPeriod(date);
        boolean isDuringPromotion2 = Promotion.MD_RECOMMEND.isPromotionPeriod(date);
        boolean isDuringPromotion3 = Promotion.FLASH_DISCOUNT.isPromotionPeriod(date);

        assertThat(isDuringPromotion1).isTrue();
        assertThat(isDuringPromotion2).isTrue();
        assertThat(isDuringPromotion3).isFalse();
    }
}
