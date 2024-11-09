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
    private static final Map<Long, Product> store = new HashMap<>();

    public void save() {
        long id = 0L;
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/products.md"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (isTitleRow(line)) {
                    continue;
                }
                List<String> columns = Arrays.asList(line.split(","));
                String name = columns.get(0);
                BigDecimal price = new BigDecimal(columns.get(1));
                long quantity = Long.parseLong(columns.get(2));
                String promotionName = columns.get(3);

                Product existProduct = findByName(name);
                if (existProduct == null) {
                    Product product = new Product.ProductBuilder()
                            .name(name)
                            .price(price)
                            .build();
                    store.put(++id, product);
                    existProduct = product;
                }
                storePromotionQuantity(promotionName, quantity, existProduct);
                storeGeneralQuantity(promotionName, quantity, existProduct);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return promotionName.equals("null");
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
