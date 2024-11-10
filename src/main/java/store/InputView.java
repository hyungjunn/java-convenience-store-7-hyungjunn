package store;

import camp.nextstep.edu.missionutils.Console;

import java.util.ArrayList;
import java.util.List;

public class InputView {
    public List<PurchaseProduct> readProductDetail(Convenience convenience) {
        System.out.println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        while (true) {
            try {
                List<PurchaseProduct> purchaseProducts = new ArrayList<>();
                String line = Console.readLine();
                validateProductDetail(line);
                String[] strings = line.split(",");
                for (String string : strings) {
                    PurchaseProduct purchaseProduct = getPurchaseProduct(convenience, string);
                    purchaseProducts.add(purchaseProduct);
                }
                return purchaseProducts;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static PurchaseProduct getPurchaseProduct(Convenience convenience, String string) {
        String nameAndQuantity = string.substring(1, string.length() - 1);
        String[] productDetail = nameAndQuantity.split("-");
        String name = productDetail[0];
        Product product = convenience.findProduct(name);
        validateExist(product);
        long quantity = Long.parseLong(productDetail[1]);
        validateExceedQuantity(quantity, product);
        return new PurchaseProduct(name, quantity);
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
        return yesOrNo();
    }

    private boolean yesOrNo() {
        while (true) {
            try {
                String line = Console.readLine();
                if ("Y".equals(line)) {
                    return true;
                }
                if ("N".equals(line)) {
                    return false;
                }
                throw new IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public boolean readWantedAddBenefitProduct(String name, long promotionGetQuantity) {
        System.out.println("현재 " + name + "은(는) " + promotionGetQuantity + "개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)");
        return yesOrNo();
    }

    public boolean readWantedMembershipDiscount() {
        System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");
        return yesOrNo();
    }

    public boolean readWantedPurchaseOther() {
        System.out.println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
        return yesOrNo();
    }
}
