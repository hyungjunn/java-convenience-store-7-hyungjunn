package store;

public class Application {
    public static void main(String[] args) {
        ConvenienceSystem convenienceSystem = new ConvenienceSystem(new Convenience(new StoreRoom()));
        convenienceSystem.productGuide();
    }
}
