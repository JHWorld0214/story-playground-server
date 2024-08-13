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

        if(user.isPresent()) {
            return new UserIdDTO(
                    user.get().getUserId(),
                    UserAgreementDTO.builder()
                            .tosAgree(user.get().getUserAgreement().getTosAgree())
                            .tosAgreeLog(user.get().getUserAgreement().getTosAgreeLog())
                            .privacyPolicyAgree(user.get().getUserAgreement().getPrivacyPolicyAgree())
                            .privacyPolicyAgreeLog(user.get().getUserAgreement().getPrivacyPolicyAgreeLog())
                            .build()
            );
        }

        User newUser = userRepository.save(
                new User(
                        userInsertDTO.getEmail(),
                        userInsertDTO.getPicture(),
                        userInsertDTO.getName(),
                        userInsertDTO.getSocial()
                )
        );

        return new UserIdResponseDTO(
                newUser.getUserId(),
                UserAgreementDTO.builder()
                        .tosAgree(newUser.getUserAgreement().getTosAgree())
                        .tosAgreeLog(newUser.getUserAgreement().getTosAgreeLog())
                        .privacyPolicyAgree(newUser.getUserAgreement().getPrivacyPolicyAgree())
                        .privacyPolicyAgreeLog(newUser.getUserAgreement().getPrivacyPolicyAgreeLog())
                        .build()
        );
    }
}
