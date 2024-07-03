package codesquad.model;

public class User {
    private final String name;
    private final String password;
    private final String nickname;

    public User(final String name, final String password, final String nickname) {
        this.name = name;
        this.password = password;
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "User{" +
            "name='" + name + '\'' +
            ", password='" + password + '\'' +
            ", nickname='" + nickname + '\'' +
            '}';
    }
}
