package clase03;
@FunctionalInterface
public interface Operator <T>{
    T apply(T a, T b);
}