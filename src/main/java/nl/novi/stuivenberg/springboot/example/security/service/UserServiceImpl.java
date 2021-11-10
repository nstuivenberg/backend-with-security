package nl.novi.stuivenberg.springboot.example.security.service;

import nl.novi.stuivenberg.springboot.example.security.domain.User;
import nl.novi.stuivenberg.springboot.example.security.payload.request.UpdateUserRequest;
import nl.novi.stuivenberg.springboot.example.security.payload.response.ImageResponse;
import nl.novi.stuivenberg.springboot.example.security.payload.response.UserResponse;
import nl.novi.stuivenberg.springboot.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder encoder;

    @Override
    public List<UserResponse> getAllUsers() {

        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
            throw new RuntimeException("No users found");
        }

        List<UserResponse> userResponses = new ArrayList<>();
        for(User user : users) {
            userResponses.add(userToUserResponse(user));
        }

        return userResponses;
    }

    @Override
    public UserResponse updateUserById(UpdateUserRequest userRequest) {
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

            User savedUser = userRepository.save(updatedUser);
            return userToUserResponse(savedUser);
        }
        throw new RuntimeException("User cannot be updated with provided data");
    }

    @Override
    public UserResponse findUserByToken() {
        String username = getUsernameFromToken();

        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isPresent()) {
            User user = userOptional.get();

            return userToUserResponse(user);
        }
        throw new RuntimeException("User not found");
    }

    @Override
    public ImageResponse addImageToProfile(String base64Image) {
        String username = getUsernameFromToken();
        var optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isPresent()) {
            var user = optionalUser.get();
            user.setBase64ProfilePicture(base64Image);
            var savedUser = userRepository.save(user);
            return userToImageResponse(savedUser);
        }
        throw new RuntimeException("Could not find user!");
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

    private UserResponse userToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRoles(user.getRoles());
        if(user.getBase64ProfilePicture()!= null && !user.getBase64ProfilePicture().isEmpty()) {
            userResponse.setProfilePicture(user.getBase64ProfilePicture());
        }
        return userResponse;
    }

    private ImageResponse userToImageResponse(User user) {
        return new ImageResponse(user.getBase64ProfilePicture());
    }

}
