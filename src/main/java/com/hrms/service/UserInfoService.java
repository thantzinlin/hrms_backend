package com.hrms.service;

import com.hrms.model.User;
import com.hrms.repository.UserRepository;
import com.hrms.security.services.UserDetailsImpl;
import com.hrms.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserRepository repository;
    @Value("${resetpasswordlink}")
    private String resetPasswordLink;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;
    // private final User userInfo;

    // public User getUserInfo() {
    // return userInfo;
    // }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = repository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserDetailsImpl(user);
        // Optional<User> userDetail =
        // repository.findByUserIdAndIsDeletedFalse(username);

        // return userDetail.map(UserInfoDetails::new)
        // .orElseThrow(() -> new UsernameNotFoundException("User not found: " +
        // username));
    }

    public User addUser(User userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        return repository.save(userInfo);
    }

    // public boolean verifyUser(String token) {
    // User existingUser = repository.findByResettoken(token)
    // .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired
    // verification token"));
    // existingUser.setIsVerified(true);
    // // existingUser.setResetToken("");
    // repository.save(existingUser);
    // return true;
    // }

    public boolean checkPassword(User user, String currentPassword) {
        return encoder.matches(currentPassword, user.getPassword());
    }

    public void changePassword(User user, String newPassword) {
        String hashedPassword = encoder.encode(newPassword);
        user.setPassword(hashedPassword);
        repository.save(user);
    }

    public String forgotPassword(String email) {
        User user = repository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateResetToken(user.getEmail());

        user.setResettoken(token);
        repository.save(user);

        String resetLink = resetPasswordLink + "?token=" + token;
        emailService.sendEmail(user.getEmail(), "Password Reset Request",
                "Click the link to reset your password: " + resetLink);
        return token;
    }

    public String resetPassword(String token) {
        String email = jwtService.validateResetToken(token);

        User user = repository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String newPassword = PasswordUtil.generateRandomPassword();
        user.setPassword(encoder.encode(newPassword));
        user.setResettoken("");
        repository.save(user);
        emailService.sendNewPasswordEmail(user.getEmail(), newPassword);
        return newPassword;

    }

    public User findByEmail(String email) {
        return repository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findByUserId(String userId) {
        return repository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}