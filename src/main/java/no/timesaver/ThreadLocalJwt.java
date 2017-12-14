package no.timesaver;


public class ThreadLocalJwt {
    private static ThreadLocal jwt = new ThreadLocal<>();

    private ThreadLocalJwt() {
    }

    public static String get() {
        return (String) jwt.get();
    }

    public static void set(String inputJwt) {
        jwt.set(inputJwt);
    }

    public static void unset() {
        jwt.remove();
    }
}
