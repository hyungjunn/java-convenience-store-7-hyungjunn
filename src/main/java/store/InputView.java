package store;

import camp.nextstep.edu.missionutils.Console;

import java.util.ArrayList;
import java.util.List;

public class InputView {
    public List<PurchaseProduct> readProductDetail(Convenience convenience) {
        System.out.println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        String line = enterPurchaseProductDetail();
        List<PurchaseProduct> purchaseProducts = new ArrayList<>();
        String[] strings = line.split(",");
        for (String string : strings) {
            String nameAndQuantity = string.substring(1, string.length() -1);
            String[] productDetail = nameAndQuantity.split("-");
            String name = productDetail[0];
            Product product = convenience.findProduct(name);
            validateExist(product);
            long quantity = Long.parseLong(productDetail[1]);
            validateExceedQuantity(quantity, product);
            purchaseProducts.add(new PurchaseProduct(name, quantity));
        }
        return purchaseProducts;
    }

    private static void validateExceedQuantity(long quantity, Product product) {
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    private static void validateExist(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
    }

    private static String enterPurchaseProductDetail() {
        String line;
        while(true) {
            try {
                line = Console.readLine();
                validateProductDetail(line);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        return line;
    }

    private static void validateProductDetail(String line) {
        validateBlank(line);
        validateNotProductDetailFormat(line);
    }

    // TODO: [] [,] 이런 경우 고려하기!
    private static void validateNotProductDetailFormat(String line) {
        if (!(line.startsWith("[") && line.endsWith("]"))) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
    }

    private static void validateBlank(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
        }
    }

    public boolean readWantedNoPromotionBenefit(Product product, Long quantity) {
        System.out.println("현재 " + product.getName() + " " + product.countNoBenefitQuantity(quantity) + "개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)");
        String line = Console.readLine();
        if ("Y".equals(line)) {
            return true;
        }
        if ("N".equals(line)) {
            return false;
        }
        throw new IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
    }
}
