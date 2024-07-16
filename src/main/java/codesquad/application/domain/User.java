package codesquad.application.domain;

import java.util.Objects;

public class User {
    private final String name;
    private final String email;
    private final String password;
    private final String nickname;

    public User(final String name, final String password, final String nickname, final String email) {
        if (name == null || password == null || nickname == null || email == null) {
            throw new IllegalArgumentException("User 생성시 null이 존재하면 안됩니다.");
        }

        this.name = name;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(nickname, user.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, password, nickname);
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
