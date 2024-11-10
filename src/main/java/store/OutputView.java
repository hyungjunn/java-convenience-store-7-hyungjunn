package store;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;

public class OutputView {
    private static final DecimalFormat df = new DecimalFormat("#,###");

    public void printProductList(List<Product> products) {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.\n");
        for (Product product : products) {
            if (product.getPromotion() != null) {
                System.out.println("- " + product.getName() + " " + df.format(product.getPrice()) + "원 " + product.getPromotionQuantity() + "개 " + product.getPromotion().getName());
            }
            if (product.getGeneralQuantity() > 0) {
                System.out.println("- " + product.getName() + " " + df.format(product.getPrice()) + "원 " + product.getGeneralQuantity() + "개");
            }
            if (product.getGeneralQuantity() == 0) {
                System.out.println("- " + product.getName() + " " + df.format(product.getPrice()) + "원 " + "재고 없음");
            }
        }
    }

    // private void printReceipt(List<PurchaseProduct> purchaseProducts, long totalQuantity, BigDecimal totalAmount, BigDecimal eventDiscountAmount, BigInteger membershipDiscountAmount, BigDecimal finalAmount) {
    //     System.out.println("==============W 편의점================");
    //     printThreeTitle("상품명", "수량", "금액");
    //     for (PurchaseProduct purchaseProduct : purchaseProducts) {
    //         String purchaseProductName = purchaseProduct.getName();
    //         Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
    //         totalQuantity += purchaseQuantity;
    //         BigDecimal totalPrice = convenience.determineTotalPriceForPurchaseQuantity(purchaseProductName, purchaseQuantity);
    //         printThreeColumn(purchaseProductName, purchaseQuantity, totalPrice);
    //     }
    //     System.out.println("=============증     정===============");
    //     for (PurchaseProduct purchaseProduct : purchaseProducts) {
    //         String purchaseProductName = purchaseProduct.getName();
    //         Long purchaseQuantity = purchaseProduct.getPurchaseQuantity();
    //         long numberOfGiveaway = convenience.determineGiftItemCount(purchaseProductName, purchaseQuantity);
    //         if (numberOfGiveaway != 0) {
    //             System.out.printf("%-17s %-8s%n", purchaseProductName, numberOfGiveaway);
    //         }
    //     }
    //     System.out.println("====================================");
    //     printThreeColumn("총구매액", totalQuantity, totalAmount);
    //     System.out.printf("%-17s %s%n", "행사할인", "-" + df.format(eventDiscountAmount));
    //     System.out.printf("%-17s %s%n", "멤버십할인", "-" + df.format(membershipDiscountAmount));
    //     System.out.printf("%-17s %s%n", "내실돈", df.format(finalAmount));
    // }

    public void printAboutAmount(
            long totalQuantity,
            BigDecimal totalAmount,
            BigDecimal eventDiscountAmount,
            BigInteger membershipDiscountAmount,
            BigDecimal finalAmount
    ) {
        System.out.println("====================================");
        printThreeColumn("총구매액", totalQuantity, totalAmount);
        printDiscountAmount("행사할인", eventDiscountAmount);
        printDiscountAmount("멤버십할인", new BigDecimal(membershipDiscountAmount));
        printTwoColumn("내실돈", finalAmount);
    }

    public void printThreeColumn(String first, long second, BigDecimal third) {
        System.out.format("%-17s %-8s %-6s%n", first, second, df.format(third));
    }

    public void printThreeTitle(String first, String second, String third) {
        System.out.format("%-17s %-8s %-6s%n", first, second, third);
    }

    public void printTwoColumn(String first, BigDecimal second) {
        System.out.printf("%-17s %16s%n", first, df.format(second));
    }

    public void printFreeGift(String first, long second) {
        System.out.printf("%-17s %16s%n", first, df.format(second));
    }

    public void printDiscountAmount(String first, BigDecimal second) {
        System.out.printf("%-17s %16s%n", first, "-" + df.format(second));
    }
}
