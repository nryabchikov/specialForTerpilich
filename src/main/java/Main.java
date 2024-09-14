public class Main {
    public static void main(String[] args) throws Exception {
        Halstead halstead = new Halstead(
                "src/main/resources/code.rb",
                "src/main/resources/index.html");
        halstead.calculate();
    }
}
