package com.example.UserManagementAPI;

import com.example.UserManagementAPI.Model.User;
import com.example.UserManagementAPI.Repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {
        "http://localhost:8080",
        "http://127.0.0.1:5500"
})
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

//    @PostMapping
//    public ResponseEntity<List<User>> createUser(@Valid @RequestBody List<@Valid User> users){
//        List<User> savedUsers = userRepository.saveAll(users);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedUsers);
//    }

//    @GetMapping
//    public List<User> getAllUsers(){
//        return userRepository.findAll();
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable Long id){
//        Optional<User> user = userRepository.findById(id);
//        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
//    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails){
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setName(userDetails.getName());
            existingUser.setEmail(userDetails.getEmail());
            User updatedUser = userRepository.save(existingUser);
            if(userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()){
                existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            if(userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()){
                existingUser.setRoles(userDetails.getRoles());
            }
            User updatesUser = userRepository.save(existingUser);
            return ResponseEntity.ok(updatedUser);
        }).orElse(ResponseEntity.notFound().build());
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
//        if (userRepository.existsById(id)) {
//            userRepository.deleteById(id);
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @GetMapping("/page")
    public Page<User> getUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }

//    @PostMapping("/register")
//    public ResponseEntity<User> registerUser(@Valid @RequestBody User user){
//        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
//        }
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setRoles(Collections.singleton("ROLE_USER"));
//        User savedUser = userRepository.save(user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
//    }

    @GetMapping
    public List<User> getAllusers(){
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<List<User>> createUsers(@Valid @RequestBody List<User> users){
        users.forEach(user -> {
            if(user.getRoles() == null || user.getRoles().isEmpty()){
                user.setRoles(Collections.singleton("ROLE_USER"));
            }
        });
        List<User> savedUsers = userRepository.saveAll(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUsers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}