package nl.novi.stuivenberg.springboot.example.security.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import nl.novi.stuivenberg.springboot.example.security.domain.Role;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private long id;
    private String username;
    private String email;
    private String profilePicture;
    private String info;

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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
