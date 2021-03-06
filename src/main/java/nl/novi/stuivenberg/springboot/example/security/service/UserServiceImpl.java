package nl.novi.stuivenberg.springboot.example.security.service;

import nl.novi.stuivenberg.springboot.example.security.controller.exception.PasswordDoNotMatchException;
import nl.novi.stuivenberg.springboot.example.security.controller.exception.UserNotFoundException;
import nl.novi.stuivenberg.springboot.example.security.controller.exception.UsersNotFoundException;
import nl.novi.stuivenberg.springboot.example.security.domain.User;
import nl.novi.stuivenberg.springboot.example.security.payload.request.ImageRequest;
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
            throw new UsersNotFoundException();
        }

        List<UserResponse> userResponses = new ArrayList<>();
        for(User user : users) {
            userResponses.add(userToUserResponse(user));
        }

        return userResponses;
    }

    @Override
    public UserResponse updateUser(UpdateUserRequest userRequest) {
        String username =  getUsernameFromToken();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isPresent()) {
            User updatedUser = userOptional.get();
            if(userRequest.getPassword() != null && userRequest.getRepeatedPassword() != null &&
                    !userRequest.getPassword().isEmpty() && !userRequest.getRepeatedPassword().isEmpty()
                    && isNewPasswordValid(userRequest)) {
                updatedUser.setPassword(encoder.encode(userRequest.getPassword()));
            }
            if(userRequest.getEmail() != null && !userRequest.getEmail().isEmpty()) {
                updatedUser.setEmail(userRequest.getEmail());
            }
            if(userRequest.getBase64Image() != null && !userRequest.getBase64Image().isEmpty()) {
                updatedUser.setBase64ProfilePicture(userRequest.getBase64Image());
            }
            if(userRequest.getInfo() != null && !userRequest.getInfo().isEmpty()) {
                updatedUser.setInfo(updatedUser.getInfo());
            }
            User savedUser = userRepository.save(updatedUser);
            return userToUserResponse(savedUser);
        }
        throw new UserNotFoundException();
    }

    @Override
    public UserResponse findUserByToken() {
        String username = getUsernameFromToken();

        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isPresent()) {
            User user = userOptional.get();

            return userToUserResponse(user);
        }
        throw new UserNotFoundException();
    }

    @Override
    public ImageResponse addImageToProfile(ImageRequest base64Image) {
        String username = getUsernameFromToken();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setBase64ProfilePicture(base64Image.getBase64Image());
            User savedUser = userRepository.save(user);
            return userToImageResponse(savedUser);
        }
        throw new UserNotFoundException();
    }

    private String getUsernameFromToken() {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }

    private boolean isNewPasswordValid(UpdateUserRequest updateUserRequest) {
        if(updateUserRequest.getPassword().equals(updateUserRequest.getRepeatedPassword())) {
            return true;
        } else {
            throw new PasswordDoNotMatchException();
        }
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
        if(user.getInfo() != null && !user.getInfo().isEmpty()) {
            userResponse.setInfo(user.getInfo());
        }
        return userResponse;
    }
    private ImageResponse userToImageResponse(User user) {
        return new ImageResponse(user.getBase64ProfilePicture());
    }
}
