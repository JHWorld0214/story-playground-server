package com.softgallery.story_playground_server.service.user;

import com.softgallery.story_playground_server.dto.user.UserIdDTO;
import com.softgallery.story_playground_server.dto.user.UserInfoDTO;
import com.softgallery.story_playground_server.dto.user.UserInsertDTO;
import com.softgallery.story_playground_server.entity.UserEntity;
import com.softgallery.story_playground_server.global.error.exception.EntityNotFoundException;
import com.softgallery.story_playground_server.repository.UserRepository;
import com.softgallery.story_playground_server.service.auth.OAuth2Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final OAuth2Service oAuth2Service;

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

    public UserInfoDTO getUserInfo(String token) {
        UUID userId = OAuth2Service.extractMemberId(OAuth2Service.getOnlyToken(token));

        Optional<UserEntity> safeUser = userRepository.findById(userId);
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
