package com.softgallery.story_playground_server.service.user;

import com.softgallery.story_playground_server.config.WebClientConfig;
import com.softgallery.story_playground_server.dto.user.UserIdDTO;
import com.softgallery.story_playground_server.dto.user.UserInfoDTO;
import com.softgallery.story_playground_server.dto.user.UserInsertDTO;
import com.softgallery.story_playground_server.entity.UserEntity;
import com.softgallery.story_playground_server.global.error.exception.EntityNotFoundException;
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

    // no use maybe
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

    public UserInfoDTO getUserInfo() {
        String userEmail = WebClientConfig.getCurrentUserEmail();

        Optional<UserEntity> safeUser = userRepository.findByEmail(userEmail);
        if(safeUser.isEmpty()) throw new EntityNotFoundException();

        return UserInfoDTO.builder()
                .userId(safeUser.get().getUserId())
                .name(safeUser.get().getName())
                .email(safeUser.get().getEmail())
                .picture(safeUser.get().getPicture())
                .social(safeUser.get().getSocial())
                .build();
    }
}
