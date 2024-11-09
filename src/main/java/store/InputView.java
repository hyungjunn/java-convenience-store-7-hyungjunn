package store;

import camp.nextstep.edu.missionutils.Console;

import java.util.ArrayList;
import java.util.List;

public class InputView {
    public List<PurchaseProduct> readProductDetail(StoreRoom storeRoom) {
        System.out.println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        String line = enterPurchaseProductDetail();
        List<PurchaseProduct> purchaseProducts = new ArrayList<>();
        String[] strings = line.split(",");
        for (String string : strings) {
            String nameAndQuantity = string.substring(1, string.length() -1);
            String[] productDetail = nameAndQuantity.split("-");
            String name = productDetail[0];
            validateExistProduct(storeRoom, name);
            long quantity = Long.parseLong(productDetail[1]);
            purchaseProducts.add(new PurchaseProduct(name, quantity));
        }
        return purchaseProducts;
    }

    private static void validateExistProduct(StoreRoom storeRoom, String name) {
        if (storeRoom.findByName(name) == null) {
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
}
