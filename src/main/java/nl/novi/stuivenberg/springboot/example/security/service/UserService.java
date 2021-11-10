package nl.novi.stuivenberg.springboot.example.security.service;

import nl.novi.stuivenberg.springboot.example.security.payload.request.ImageRequest;
import nl.novi.stuivenberg.springboot.example.security.payload.request.UpdateUserRequest;
import nl.novi.stuivenberg.springboot.example.security.payload.response.ImageResponse;
import nl.novi.stuivenberg.springboot.example.security.payload.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse updateUser(UpdateUserRequest userRequest);
    UserResponse findUserByToken();
    ImageResponse addImageToProfile(ImageRequest base64Image);
}
