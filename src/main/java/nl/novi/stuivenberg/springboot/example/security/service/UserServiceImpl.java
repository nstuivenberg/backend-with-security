package nl.novi.stuivenberg.springboot.example.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import nl.novi.stuivenberg.springboot.example.security.domain.User;
import nl.novi.stuivenberg.springboot.example.security.payload.request.UpdateUserRequest;
import nl.novi.stuivenberg.springboot.example.security.payload.response.MessageResponse;
import nl.novi.stuivenberg.springboot.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Value("${novi.sec.jwtSecret}")
    private String jwtSecret;

    private static final String PREFIX = "Bearer ";

    private UserRepository userRepository;
    private PasswordEncoder encoder;

    @Override
    public ResponseEntity<?> getAllUsers() {

        List<User> users = userRepository.findAll();

        if(users.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("No Users found!"));
        }
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<?> updateUserById(String token, UpdateUserRequest userRequest) {
        if(token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid token"));
        }
        String username =  getUsernameFromToken(token);

        if(userExists(username) && updateRequestIsValid(userRequest)) {
            User updatedUser = findUserByUsername(username);
            if(!userRequest.getPassword().isEmpty() && !userRequest.getRepeatedPassword().isEmpty()) {
                updatedUser.setPassword(encoder.encode(userRequest.getPassword()));
            }
            if(userRequest.getEmail() != null && !userRequest.getEmail().isEmpty()) {
                updatedUser.setEmail(userRequest.getEmail());
            }
            return ResponseEntity.ok().body(userRepository.save(updatedUser));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("User cannot be updated with provided data."));
    }

    @Override
    public ResponseEntity<?> findUserByToken(String token) {
        String username = getUsernameFromToken(token);

        if(userExists(username)) {
            return ResponseEntity.ok(findUserByUsername(username));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
    }

    private String getUsernameFromToken(String token) {
        String tokenWithoutBearer = removePrefix(token);

        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecret))
                .parseClaimsJws(tokenWithoutBearer).getBody();

        return claims.getSubject();
    }

    private String removePrefix(String token) {
        return token.replace(PREFIX, "");
    }

    private boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    private boolean updateRequestIsValid(UpdateUserRequest updateUserRequest) {
        if(updateUserRequest.getPassword().equals(updateUserRequest.getRepeatedPassword())) {
            return true;
        }
        return false;
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username).get();
    }


    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

}
