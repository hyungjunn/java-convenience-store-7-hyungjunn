package store;

import java.time.LocalDate;

public enum Promotion {
    CARBONATE("탄산2+1", 2, 1, LocalDate.of(2024, 01, 01), LocalDate.of(2024, 12, 31)),
    MD_RECOMMEND("MD추천상품", 1, 1, LocalDate.of(2024, 01, 01), LocalDate.of(2024, 12, 31)),
    FLASH_DISCOUNT("반짝할인", 1, 1, LocalDate.of(2024, 11, 01), LocalDate.of(2024, 11, 30));

    private final String name;
    private final int buy;
    private final int get;
    private final LocalDate startDate;
    private final LocalDate endDate;

    Promotion(String name, int buy, int get, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.buy = buy;
        this.get = get;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isPromotionPeriod(LocalDate date) {
        return date.isAfter(startDate) && date.isBefore(endDate);
    }

    public int extractBuyAndGet() {
        return buy + get;
    }

    public boolean canApplyPromotion(Long purchaseQuantity) {
        return purchaseQuantity % extractBuyAndGet()  == buy;
    }

    public boolean isApplyPromotion(Long purchaseQuantity) {
        return purchaseQuantity >= extractBuyAndGet();
    }

    // 증정할 수 있는 갯수
    public long countNumberOfGiveaway(long purchaseQuantity) {
        int buyAndGet = extractBuyAndGet();
        if (purchaseQuantity >= buyAndGet) {
            return purchaseQuantity / buyAndGet;
        }
        return get;
    }

    public static Promotion findByName(String promotionName) {
        Promotion promotion = null;
        Promotion[] values = Promotion.values();
        for (Promotion value : values) {
            if (value.name.equals(promotionName)) {
                promotion = value;
            }
        }
        return promotion;
    }

    public String getName() {
        return name;
    }

    public int getBuy() {
        return buy;
    }

    public int getGet() {
        return get;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
