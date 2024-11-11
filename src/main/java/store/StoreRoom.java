package store;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreRoom {
    private static final String FIRST_ROW_PREFIX = "name";
    private static final String PRODUCTS_FILE_LOCATION = "src/main/resources/products.md";
    private static final String ROW_SPLIT_REGEX = ",";
    private static final byte PRICE_INDEX = 1;
    private static final byte QUANTITY_INDEX = 2;
    private static final byte PROMOTION_NAME_INDEX = 3;
    private static final String NO_PROMOTION = "null";

    private static final Map<Long, Product> store = new HashMap<>();

    public void save() {
        long id = 0L;
        try (BufferedReader br = new BufferedReader(new FileReader(PRODUCTS_FILE_LOCATION))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (isTitleRow(line)) {
                    continue;
                }
                List<String> columns = Arrays.asList(line.split(ROW_SPLIT_REGEX));
                ProductRow row = getProductRow(columns);
                Product existProduct = findByName(row.name());
                if (existProduct == null) {
                    Product product = new Product.ProductBuilder()
                            .name(row.name())
                            .price(row.price())
                            .build();
                    store.put(++id, product);
                    existProduct = product;
                }
                storePromotionQuantity(row.promotionName(), row.quantity(), existProduct);
                storeGeneralQuantity(row.promotionName(), row.quantity(), existProduct);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ProductRow getProductRow(List<String> columns) {
        String name = columns.getFirst();
        BigDecimal price = new BigDecimal(columns.get(PRICE_INDEX));
        long quantity = Long.parseLong(columns.get(QUANTITY_INDEX));
        String promotionName = columns.get(PROMOTION_NAME_INDEX);
        return new ProductRow(name, price, quantity, promotionName);
    }

    private record ProductRow(String name, BigDecimal price, long quantity, String promotionName) {
    }

    private void storePromotionQuantity(String promotionName, long quantity, Product existProduct) {
        long promotionQuantity;
        if (isPromotionProduct(promotionName)) {
            promotionQuantity = quantity;
            // TODO: setter 최선인지 고려하기.
            existProduct.setPromotionQuantity(promotionQuantity);
            existProduct.setPromotion(Promotion.findByName(promotionName));
        }
    }

    private void storeGeneralQuantity(String promotionName, long quantity, Product existProduct) {
        long generalQuantity;
        if (isGeneralProduct(promotionName)) {
            generalQuantity = quantity;
            existProduct.setGeneralQuantity(generalQuantity);
        }
    }

    private boolean isPromotionProduct(String promotionName) {
        return !isGeneralProduct(promotionName);
    }

    private boolean isGeneralProduct(String promotionName) {
        return promotionName.equals(NO_PROMOTION);
    }

    private boolean isTitleRow(String line) {
        return line.startsWith(FIRST_ROW_PREFIX);
    }

    // 없을 때(null)일 때 상품을 추가해야 되는 로직을 위해 `orElse(null)`
    public Product findByName(String name) {
        return store.values().stream()
                .filter(it -> it.isProductName(name))
                .findFirst()
                .orElse(null);
    }

    public List<Product> readAll() {
        return store.values()
                .stream()
                .toList();
    }

}
