/* CLASE 03 */

import clase03.Operator;

public class MainCalculadora {
    public static void main(String[] args) {

        System.out.println(ejecutarOp(1,1, (a, b) -> a + b));
        System.out.println(ejecutarOp(2L,3L, (a, b) -> a - b));
        System.out.println(ejecutarOp(2L,3L, (a, b) -> a * b));
        System.out.println(ejecutarOp(4F,5F, (a, b) -> a / b));

    }

    public static <T extends Number> T ejecutarOp(T a, T b, Operator<T> operacion) {
        return operacion.apply(a, b);
    }
}
