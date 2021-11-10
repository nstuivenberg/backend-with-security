package nl.novi.stuivenberg.springboot.example.security.controller;

import nl.novi.stuivenberg.springboot.example.security.payload.request.ImageRequest;
import nl.novi.stuivenberg.springboot.example.security.payload.request.UpdateUserRequest;
import nl.novi.stuivenberg.springboot.example.security.payload.response.ImageResponse;
import nl.novi.stuivenberg.springboot.example.security.payload.response.UserResponse;
import nl.novi.stuivenberg.springboot.example.security.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest updateRequest) {
        return ResponseEntity.ok().body(userService.updateUser(updateRequest));
    }

    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> findUserByToken() {
        return ResponseEntity.ok(userService.findUserByToken());
    }

    @PostMapping("/image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ImageResponse> addImageToProfile(ImageRequest base64Img) {
        return ResponseEntity.ok(userService.addImageToProfile(base64Img));
    }
}
