package nl.novi.stuivenberg.springboot.example.security.service;

import nl.novi.stuivenberg.springboot.example.security.domain.User;
import nl.novi.stuivenberg.springboot.example.security.payload.request.UpdateUserRequest;
import nl.novi.stuivenberg.springboot.example.security.payload.response.MessageResponse;
import nl.novi.stuivenberg.springboot.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

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
    public ResponseEntity<?> updateUserById(UpdateUserRequest userRequest) {
        String username =  getUsernameFromToken();

        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isPresent()) {
            User updatedUser = userOptional.get();
            if(!userRequest.getPassword().isEmpty() && !userRequest.getRepeatedPassword().isEmpty()
                    && isNewPasswordValid(userRequest)) {
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
    public ResponseEntity<?> findUserByToken() {
        String username = getUsernameFromToken();

        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        }
        return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
    }

    private String getUsernameFromToken() {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }

    private boolean isNewPasswordValid(UpdateUserRequest updateUserRequest) {
        return updateUserRequest.getPassword().equals(updateUserRequest.getRepeatedPassword());
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
