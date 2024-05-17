package com.BackAuth.springJwt.controller;

import com.BackAuth.springJwt.repository.UserRepository;
import com.BackAuth.springJwt.service.AuthenticationService;
import com.BackAuth.springJwt.model.AuthenticationResponse;
import com.BackAuth.springJwt.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthenticationController {

    private final AuthenticationService authService;
    private final UserRepository userRepository;

    public AuthenticationController(AuthenticationService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody User request
            ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> users() {
        List<User> list = authService.getAllUsers();
        return ResponseEntity.ok(list);
    }
    @PostMapping("/update/{username}")
    public ResponseEntity<AuthenticationResponse> update(@RequestBody User request, @PathVariable String username) {
        return  ResponseEntity.ok(authService.updateUser(request,username));

    }
    @DeleteMapping("/delete/{username}")
    public ResponseEntity<AuthenticationResponse> delete(@PathVariable String username) {
        return  ResponseEntity.ok(authService.deleteUser(username));
    }
    @GetMapping("/getuser/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {

        return authService.displayUser(username)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }
}
