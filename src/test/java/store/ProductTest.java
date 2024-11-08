package store;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {
    // TODO: 유지 보수 하기 힘든 코드. 한가지만 테스트하도록 분리하기!
    @DisplayName("상품 수량이 결제된 수량만큼 차감한다.")
    @Test
    void decreaseStockTest() {
        Product coke = new Product("콜라", BigDecimal.valueOf(1_000L), 10L, 10L, Promotion.CARBONATE);
        Product orangeJuice = new Product("오렌지주스", BigDecimal.valueOf(1_800L), 9L, 0L, Promotion.MD_RECOMMEND);
        Product chocoBar = new Product("초코바", BigDecimal.valueOf(2_000L), 0L, 5L, null);
        LocalDate date = LocalDate.of(2024, 11, 11);

        coke.decreaseStock(3L, date);
        coke.decreaseStock(10L, date);
        orangeJuice.decreaseStock(8L, date);
        chocoBar.decreaseStock(3L, date);

        assertThat(coke.getPromotionQuantity()).isEqualTo(0L);
        assertThat(coke.getGeneralQuantity()).isEqualTo(7L);
        assertThat(orangeJuice.getPromotionQuantity()).isEqualTo(1L);
        assertThat(chocoBar.getPromotionQuantity()).isEqualTo(0L);
        assertThat(chocoBar.getGeneralQuantity()).isEqualTo(2L);

        assertThatThrownBy(() -> coke.decreaseStock(10L, date))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        assertThatThrownBy(() -> orangeJuice.decreaseStock(3L, date))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        assertThatThrownBy(() -> chocoBar.decreaseStock(10L, date))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
    }

    @DisplayName("프로모션이 가능한 상품인지 판별한다.")
    @Test
    void canAppliedPromotion() {
        Product coke = new Product("콜라", BigDecimal.valueOf(1_000L), 10L, 10L, Promotion.CARBONATE);
        Product orangeJuice = new Product("오렌지주스", BigDecimal.valueOf(1_800L), 9L, 0L, Promotion.MD_RECOMMEND);
        Product chocoBar = new Product("초코바", BigDecimal.valueOf(2_000L), 0L, 5L, null);

        boolean cannotApply = coke.canApplyPromotion(3);
        boolean cannotApply2 = coke.canApplyPromotion(4);
        boolean canApply = coke.canApplyPromotion(5);

        boolean cannotApply3 = orangeJuice.canApplyPromotion(4);
        boolean canApply2 = orangeJuice.canApplyPromotion(5);

        // 프로모션이 없는 경우
        boolean canApplyPromotion = chocoBar.canApplyPromotion(3);

        assertThat(cannotApply).isFalse();
        assertThat(cannotApply2).isFalse();
        assertThat(canApply).isTrue();

        assertThat(cannotApply3).isFalse();
        assertThat(canApply2).isTrue();

        assertThat(canApplyPromotion).isFalse();
    }

    @DisplayName("프로모션 할인을 적용한다.")
    @ParameterizedTest
    @CsvSource({"3, 1_000", "4, 1_000", "6, 2_000", "9, 3_000", "10, 3_000", "15, 3_000"})
    void applyPromotionDiscountTest(long purchaseQuantity, long value) {
        Product coke = new Product("콜라", BigDecimal.valueOf(1_000L), 10L, 10L, Promotion.CARBONATE);

        BigDecimal discount = coke.applyPromotionDiscount(purchaseQuantity);

        assertThat(discount).isEqualTo(BigDecimal.valueOf(value));
    }

    @DisplayName("프로모션 혜택없이 결제해야 하는 수량을 계산한다.")
    @Test
    void countNoBenefitQuantityTest() {
        Product coke = new Product("콜라", BigDecimal.valueOf(1_000L), 10L, 10L, Promotion.CARBONATE);
        Product NoPromotionalCoke = new Product("콜라", BigDecimal.valueOf(1_000L), 0L, 10L, Promotion.CARBONATE);

        long count = coke.countNoBenefitQuantity(11L);
        long count1 = NoPromotionalCoke.countNoBenefitQuantity(5L);

        assertThat(count).isEqualTo(2L);
        assertThat(count1).isEqualTo(5L);
    }

}
