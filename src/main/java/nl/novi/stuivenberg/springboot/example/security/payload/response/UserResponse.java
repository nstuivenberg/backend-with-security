package nl.novi.stuivenberg.springboot.example.security.payload.response;

import nl.novi.stuivenberg.springboot.example.security.domain.Role;

import java.util.Set;

public class UserResponse {

    private long id;
    private String username;
    private String email;

    private Set<Role> roles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
