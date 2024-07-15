package codesquad.webserver.authentication;

import codesquad.application.domain.User;

public class AuthenticationHolder {
    private static final ThreadLocal<User> context = new ThreadLocal<>();

    private AuthenticationHolder() {}

    public static User getContext() {
        return context.get();
    }

    public static void setContext(User user) {
        context.set(user);
    }

    public static void clear() {
        context.remove();
    }
}
