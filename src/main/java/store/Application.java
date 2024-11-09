package store;

public class Application {
    public static void main(String[] args) {
        // TODO: 프로그램 구현
        Convenience convenience = new Convenience(new StoreRoom());
        convenience.productGuide();
    }
}
