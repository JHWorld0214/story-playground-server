package com.softgallery.story_playground_server.service.user;

import com.softgallery.story_playground_server.dto.UserIdDTO;
import com.softgallery.story_playground_server.dto.UserInsertDTO;
import com.softgallery.story_playground_server.entity.UserEntity;
import com.softgallery.story_playground_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserIdDTO signIn(UserInsertDTO userInsertDTO) {

        Optional<UserEntity> user = userRepository.findByEmail(userInsertDTO.getEmail());

        if(user.isPresent()) return new UserIdDTO(user.get().getUserId());

        UserEntity newUser = userRepository.save(
                UserEntity.builder()
                        .email(userInsertDTO.getEmail())
                        .name(userInsertDTO.getName())
                        .picture(userInsertDTO.getPicture())
                        .social(userInsertDTO.getSocial())
                        .build()
        );

        return new UserIdDTO(newUser.getUserId());
    }
}
