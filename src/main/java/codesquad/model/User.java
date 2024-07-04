package codesquad.model;

public class User {
    private final String name;
    private final String email;
    private final String password;
    private final String nickname;

    public User(final String name, final String password, final String nickname, final String email) {
        this.name = name;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
