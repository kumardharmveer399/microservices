package com.BackAuth.springJwt.controller;

import com.BackAuth.springJwt.model.FileResponse;
import com.BackAuth.springJwt.model.User;
import com.BackAuth.springJwt.repository.UserRepository;
import com.BackAuth.springJwt.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class OtherController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${project.image}")
    private String path;

    @GetMapping("/demo")
    public ResponseEntity<String> demo() {
        return ResponseEntity.ok("Hello from secured url");
    }

    @GetMapping("/admin_only")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Hello from admin only url");
    }

    @PostMapping("/image")
    public ResponseEntity<FileResponse> fileUpload(
            @RequestParam("image") MultipartFile image, Authentication authentication
    ) throws IOException {

        User user = (User) authentication.getPrincipal();
        String fileName = this.fileStorageService.uploadFile(path, image); // 'path' is correctly used here

        user.setProfileImageUrl(fileName);
        userRepository.save(user);

        return new ResponseEntity<>(new FileResponse(fileName, "Image uploaded successfully"), HttpStatus.OK);
    }

}
