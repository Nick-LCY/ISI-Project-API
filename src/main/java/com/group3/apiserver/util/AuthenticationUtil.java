package com.group3.apiserver.util;

import com.group3.apiserver.entity.UserEntity;
import com.group3.apiserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class AuthenticationUtil {
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Boolean userAuthentication(Integer id, String token) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        return userOptional.filter(userEntity -> Objects.equals(userEntity.getToken(), token)).isPresent();
    }
}
