/**
 * Пример программы на Java
 *
 */
// назавние главного класса, обычно совпадает с названием программы
public class HelloWorld {
    public static void main (String[] args) {
        String helloMessage = "Hello World!";
        System.out.println(helloMessage);
        if (args.length > 0) {
            System.out.println(args[0]);
        }
    }
}
