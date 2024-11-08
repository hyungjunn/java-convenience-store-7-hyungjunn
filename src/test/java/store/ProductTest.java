package store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {
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
}
