package com.cyclicgraph.masterymath.user.model;

import com.cyclicgraph.masterymath.game.model.Game;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"username"}), @UniqueConstraint(columnNames = {"email"})})
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;
    private String password;
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles = new HashSet<>();
    @OneToMany
    private List<Game> games = new ArrayList<>();
    private double challengeRating = 1000;

    public UserEntity(UUID id, String username, String password, String email, Collection<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getPermissions().stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Double.compare(that.challengeRating, challengeRating) == 0 && Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(email, that.email) && Objects.equals(roles, that.roles) && Objects.equals(games, that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, email, roles, games, challengeRating);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", games=" + games +
                ", challengeRating=" + challengeRating +
                '}';
    }

    private List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        for (Role role : roles) {
            for (Permission item : role.getPermissions()) {
                permissions.add(item.getName());
            }
            permissions.add(role.getName());
        }

        return permissions;
    }
}
