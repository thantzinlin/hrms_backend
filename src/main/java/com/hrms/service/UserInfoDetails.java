// package com.hrms.service;

// import com.hrms.model.User;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;

// import java.util.Collection;
// import java.util.List;
// import java.util.stream.Collectors;

// public class UserInfoDetails implements UserDetails {

// private final String userId;
// private final String password;

// private String email;

// // @JsonIgnore
// // private String password;
// private final List<GrantedAuthority> authorities;

// public UserInfoDetails(User user) {
// this.userId = user.getUserId();
// this.password = user.getPassword();

// this.authorities = user.getRoles()
// .stream()
// .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
// .collect(Collectors.toList());
// }

// public static UserInfoDetails build(User user) {
// List<GrantedAuthority> authorities = user.getRoles().stream()
// .map(role -> new SimpleGrantedAuthority(role.getName().name()))
// .collect(Collectors.toList());

// return new UserInfoDetails(user);
// user.getUserId(),
// user.getUsername(),
// user.getEmail(),
// user.getPassword(),
// authorities);
// }

// public String getUserId() {
// return userId;
// }

// /**
// * userId is the principal identity
// */
// @Override
// public String getUsername() {
// return userId;
// }

// @Override
// public Collection<? extends GrantedAuthority> getAuthorities() {
// return authorities;
// }

// @Override
// public String getPassword() {
// return password;
// }

// @Override
// public boolean isAccountNonExpired() {
// return true;
// }

// @Override
// public boolean isAccountNonLocked() {
// return true;
// }

// @Override
// public boolean isCredentialsNonExpired() {
// return true;
// }

// @Override
// public boolean isEnabled() {
// return true;
// }

// public String getEmail() {
// return email;
// }
// }
