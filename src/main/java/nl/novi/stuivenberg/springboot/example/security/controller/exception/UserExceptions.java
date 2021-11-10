package nl.novi.stuivenberg.springboot.example.security.controller.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptions {

    @ExceptionHandler(value = PasswordDoNotMatchException.class)
    public ResponseEntity<Object> exception() {
        return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Object> generateUserNotFoundExceptionMessage() {
        return new ResponseEntity<>("Could not find your data in the database", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UsersNotFoundException.class)
    public ResponseEntity<Object> generateUsersNotFoundExceptionMessage() {
        return new ResponseEntity<>("Could not find any users in the database", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RoleNotFoundException.class)
    public ResponseEntity<Object> generateRoleNotFoundExceptionMessage() {
        return new ResponseEntity<>("This role does not exist.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UsernameNotUniqueException.class)
    public ResponseEntity<Object> generateUsernameNotUniqueExceptionMessage() {
        return new ResponseEntity<>("This username is already in use", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UsernameNotUniqueException.class)
    public ResponseEntity<Object> generateEmailNotUniqueExceptionMessage() {
        return new ResponseEntity<>("This email is already in use", HttpStatus.BAD_REQUEST);
    }
}