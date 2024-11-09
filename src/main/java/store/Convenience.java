package store;

import java.util.List;

public class Convenience {
    private final StoreRoom storeRoom;
    private final OutputView outputView;
    private final InputView inputView;

    public Convenience(StoreRoom storeRoom) {
        this.storeRoom = storeRoom;
        this.outputView = new OutputView();
        this.inputView = new InputView();
    }

    public void productGuide() {
        storeRoom.save();
        List<Product> products = storeRoom.readAll();
        outputView.printProductList(products);
        List<PurchaseProduct> purchaseProducts = inputView.readProductDetail(); // TODO: 디테일 입력 검증
    }
}
