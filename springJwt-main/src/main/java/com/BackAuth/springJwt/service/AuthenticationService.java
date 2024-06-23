package com.BackAuth.springJwt.service;

import com.BackAuth.springJwt.model.AuthenticationResponse;
import com.BackAuth.springJwt.model.Token;
import com.BackAuth.springJwt.model.User;
import com.BackAuth.springJwt.repository.TokenRepository;
import com.BackAuth.springJwt.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final TokenRepository tokenRepository;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository repository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 TokenRepository tokenRepository,
                                 AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(User request) {

        // check if user already exist. if exist than authenticate the user
        if(repository.findByUsername(request.getUsername()).isPresent()) {
            return new AuthenticationResponse(null, "User already exist");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountNonExpired(true);

        LocalDateTime now=LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setRole(request.getRole());

        user = repository.save(user);

        String jwt = jwtService.generateToken(user);

        saveUserToken(jwt, user);

        return new AuthenticationResponse(jwt, "User registration was successful");

    }

    public AuthenticationResponse authenticate(User request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = repository.findByUsername(request.getUsername()).orElseThrow();
        String jwt = jwtService.generateToken(user);

        revokeAllTokenByUser(user);
        saveUserToken(jwt,user);

        return new AuthenticationResponse(jwt,"User login was successful");

    }

    public AuthenticationResponse updateUser (User user,String userName ) {
        User ExistingUser = repository.findByUsername(userName).orElse(null);
        if(ExistingUser != null) {
            LocalDateTime now=LocalDateTime.now();
            ExistingUser.setFirstName(user.getFirstName());
            ExistingUser.setLastName(user.getLastName());
            ExistingUser.setEmail(user.getEmail());
            ExistingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            ExistingUser.setUpdatedAt(now);
            repository.save(ExistingUser);

            String jwt=jwtService.generateToken(ExistingUser);
            saveUserToken(jwt,ExistingUser);
            return new AuthenticationResponse(jwt,"ExistingUser Updated");
        }else {
            return new AuthenticationResponse(null,"User not found");
        }

    }

    public AuthenticationResponse deleteUser(String userName) {
        User existingUser = repository.findByUsername(userName).orElse(null);
        if(existingUser != null) {

/*
//            for deleting user from repo
            revokeAllTokenByUser(existingUser);
            repository.delete(existingUser);

*/
            
            existingUser.setAccountNonExpired(false);
            repository.save(existingUser);
            return new AuthenticationResponse(null, "User deleted successfully");
        } else {
            return new AuthenticationResponse(null, "User not found");
        }
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public Optional<User> displayUser(String username){
        User existinfUser=repository.findByUsername(username).orElse(null);
        if(existinfUser==null){
            return null;
        }else{
            return Optional.of(existinfUser);
        }
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getId());
        if(validTokens.isEmpty()) {
            System.out.println("No tokens found for user: " + user.getUsername());
            return;
        }
        System.out.println("Revoking tokens for user: " + user.getUsername());
        validTokens.forEach(t-> t.setLoggedOut(true));

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String jwt, User user) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }
}
