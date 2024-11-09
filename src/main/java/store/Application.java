package store;

public class Application {
    public static void main(String[] args) {
        // TODO: 프로그램 구현
        ConvenienceSystem convenienceSystem = new ConvenienceSystem(new Convenience(new StoreRoom()));
        convenienceSystem.productGuide();
    }
}
