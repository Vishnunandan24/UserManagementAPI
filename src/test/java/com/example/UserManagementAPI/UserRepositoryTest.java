package com.example.UserManagementAPI;
import com.example.UserManagementAPI.Model.User;
import com.example.UserManagementAPI.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;


import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@DataJpaTest
@AutoConfigureTestDatabase(replace =AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
            @Autowired
    private TestEntityManager entityManager;
    private User user1;
    private User user2;
    private User user3;
    @BeforeEach
    void setUp() {
        userRepository.deleteAllInBatch();
        user1 = new User("Alice Wonderland", "alice@example.com");
        user2 = new User("Bob the Builder", "bob@example.com");
        user3 = new User("kalyani", "kalyani@example.com");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
        entityManager.clear();
    }
    void testFindByEmailFound(){
        Optional<User> foundUser = userRepository.findByEmail(user1.getEmail());
        userRepository.findByEmail(user1.getEmail());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(user1.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(user1.getEmail());
        assertThat(foundUser.get().getId()).isEqualTo(user1.getId());
    }
    @Test
    void testFindByEmailNotFound(){
        String nonExistingMail = "xyz@gmail.com";
        Optional<User> foundUser = userRepository.findByEmail(nonExistingMail);
        assertThat(foundUser).isEmpty();
    }
    @Test
    void testSaveUser(){
        User newUser = new User("Charlie Chaplin","charlie@example.com");
        User savedUser = userRepository.save(newUser);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Charlie Chaplin");
    }
    @Test
    void testUpdateUser(){
        String newName = "kalyanbabu";
        String newEmail = "babulakebabu@example.com";
        User user3 = new User("kalyani", "kalyani@example.com");
        user3.setName(newName);
        user3.setEmail(newEmail);

        User updatedUser = userRepository.save(user3);
        assertThat(updatedUser).isNull();
        assertThat(updatedUser.getId()).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo(newName);
        assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
    }
}
