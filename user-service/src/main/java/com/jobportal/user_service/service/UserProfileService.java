package com.jobportal.user_service.service;


import com.jobportal.user_service.dto.UserPartialUpdateDto;
import com.jobportal.user_service.dto.UserRequestDto;
import com.jobportal.user_service.dto.UserResponseDto;
import com.jobportal.user_service.entity.UserData;
import com.jobportal.user_service.mapper.UserMapper;
import com.jobportal.user_service.repository.UserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserDataRepository userDataRepository;
    private final UserMapper userMapper;

    public UserResponseDto createUserProfile(UserRequestDto dto) {
        UserData user = userMapper.toEntity(dto);
        UserData saved = userDataRepository.save(user);
        return userMapper.toResponseDto(saved);
    }

    public UserResponseDto getUserProfileById(Long id) {
        UserData user = userDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return userMapper.toResponseDto(user);
    }

    public List<UserResponseDto> getAllUserProfiles() {
        List<UserData> users = userDataRepository.findAll();
        return userMapper.toResponseDTOList(users);
    }

    public void deleteUserProfileById(Long id) {
        UserData user = userDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setIsActive(false);   // soft delete
        userDataRepository.save(user);
    }

    public UserResponseDto updateUserProfile(Long id, UserRequestDto dto) {
        UserData user = userDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // MapStruct partial update
        userMapper.updateEntityFromDto(dto, user);

        UserData updatedUser = userDataRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }

    public UserResponseDto partialUpdateUserProfile(Long id, UserPartialUpdateDto dto) {
        UserData user = userDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Partial update via MapStruct, only non-null fields from dto
        userMapper.patchEntityFromDto(dto, user);

        UserData updatedUser = userDataRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }

}
