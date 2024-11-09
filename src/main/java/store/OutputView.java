package store;

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
}
