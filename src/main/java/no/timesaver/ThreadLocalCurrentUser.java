package no.timesaver;


import no.timesaver.domain.User;

public class ThreadLocalCurrentUser {
    private static ThreadLocal user = new ThreadLocal<>();

    private ThreadLocalCurrentUser() {
    }

    public static User get() {
        return (User) user.get();
    }

    public static void set(User inputUser) {
        user.set(inputUser);
    }

    public static void unset() {
        user.remove();
    }
}
