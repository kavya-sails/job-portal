package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.UserProfilePartialUpdateDto;
import com.jobportal.user_service.dto.UserProfileRequestDto;
import com.jobportal.user_service.dto.UserProfileResponseDto;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.entity.UserEducation;
import com.jobportal.user_service.entity.UserProfile;
import com.jobportal.user_service.exception.DataIntegrityViolationException;
import com.jobportal.user_service.exception.ForbiddenException;
import com.jobportal.user_service.exception.UserNotFound;
import com.jobportal.user_service.exception.UserProfileNotFoundException;
import com.jobportal.user_service.mapper.UserEducationMapper;
import com.jobportal.user_service.mapper.UserProfileMapper;
import com.jobportal.user_service.repository.UserCredentialRepository;
import com.jobportal.user_service.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    private UserProfileRepository userProfileRepository;
    private UserCredentialRepository userCredentialRepository;
    private UserProfileMapper userProfileMapper;
    private UserEducationMapper userEducationMapper;

    private UserProfileService service;

    @BeforeEach
    void setUp() {
        userProfileRepository = mock(UserProfileRepository.class);
        userCredentialRepository = mock(UserCredentialRepository.class);
        userProfileMapper = mock(UserProfileMapper.class);
        userEducationMapper = mock(UserEducationMapper.class);

        service = new UserProfileService(
                userProfileRepository,
                userCredentialRepository,
                userProfileMapper,
                userEducationMapper
        );
    }

    // createUserProfile

    @Test
    void createUserProfile_whenProfileAlreadyExists_shouldThrowDataIntegrityViolation() {
        Long headerUserId = 1L;
        when(userProfileRepository.existsById(headerUserId)).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class,
                () -> service.createUserProfile(new UserProfileRequestDto(), headerUserId));

        verify(userProfileRepository, never()).save(any());
        verify(userCredentialRepository, never()).findById(anyLong());
        verify(userProfileMapper, never()).toEntity(any());
    }

    @Test
    void createUserProfile_whenAuthUserMissing_shouldThrowUserNotFound() {
        Long headerUserId = 1L;
        when(userProfileRepository.existsById(headerUserId)).thenReturn(false);
        when(userCredentialRepository.findById(headerUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class,
                () -> service.createUserProfile(new UserProfileRequestDto(), headerUserId));

        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void createUserProfile_withoutEducation_shouldSaveAndReturnEmail() {
        Long headerUserId = 1L;

        when(userProfileRepository.existsById(headerUserId)).thenReturn(false);

        UserCredential auth = new UserCredential();
        auth.setUserId(headerUserId);
        auth.setEmail("u@test.com");
        when(userCredentialRepository.findById(headerUserId)).thenReturn(Optional.of(auth));

        UserProfile mappedEntity = new UserProfile();
        mappedEntity.setFirstName("A"); // some fields for completion calc
        mappedEntity.setLastName("B");
        when(userProfileMapper.toEntity(any(UserProfileRequestDto.class))).thenReturn(mappedEntity);

        // repository save returns same object
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfileResponseDto mappedResponse = new UserProfileResponseDto();
        when(userProfileMapper.toResponseDto(any(UserProfile.class))).thenReturn(mappedResponse);

        UserProfileRequestDto dto = new UserProfileRequestDto(); // education null
        UserProfileResponseDto out = service.createUserProfile(dto, headerUserId);

        assertNotNull(out);
        assertEquals("u@test.com", out.getEmail());

        // id must be set from auth user
        assertEquals(headerUserId, mappedEntity.getId());

        // completion should be set
        assertTrue(mappedEntity.getProfileCompletionPercentage() >= 0);

        verify(userProfileRepository, times(1)).save(mappedEntity);
    }

    @Test
    void createUserProfile_withEducation_shouldAttachEducationAndSave() {
        Long headerUserId = 1L;

        when(userProfileRepository.existsById(headerUserId)).thenReturn(false);

        UserCredential auth = new UserCredential();
        auth.setUserId(headerUserId);
        auth.setEmail("u@test.com");
        when(userCredentialRepository.findById(headerUserId)).thenReturn(Optional.of(auth));

        UserProfile mappedEntity = new UserProfile();
        when(userProfileMapper.toEntity(any(UserProfileRequestDto.class))).thenReturn(mappedEntity);

        com.jobportal.user_service.dto.EducationDto educationDto =
                new com.jobportal.user_service.dto.EducationDto();

        UserProfileRequestDto dto = new UserProfileRequestDto();
        dto.setEducation(educationDto);

        UserEducation eduEntity = new UserEducation();
        when(userEducationMapper.toEntity(educationDto)).thenReturn(eduEntity);

        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfileResponseDto mappedResponse = new UserProfileResponseDto();
        when(userProfileMapper.toResponseDto(any(UserProfile.class))).thenReturn(mappedResponse);

        UserProfileResponseDto out = service.createUserProfile(dto, headerUserId);

        assertNotNull(out);
        assertEquals("u@test.com", out.getEmail());

        assertNotNull(mappedEntity.getEducation());
        assertEquals(eduEntity, mappedEntity.getEducation());
        assertEquals(mappedEntity, eduEntity.getUserProfile());

        verify(userEducationMapper, times(1)).toEntity(educationDto);
        verify(userProfileRepository, times(1)).save(mappedEntity);
    }

    // getUserProfileById

    @Test
    void getUserProfileById_whenNotOwner_shouldThrowForbidden() {
        assertThrows(ForbiddenException.class, () -> service.getUserProfileById(2L, 1L));
    }

    @Test
    void getUserProfileById_whenNotFound_shouldThrowUserProfileNotFound() {
        Long id = 1L;

        when(userProfileRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserProfileNotFoundException.class,
                () -> service.getUserProfileById(id, id));
    }

    @Test
    void getUserProfileById_success_shouldReturnDtoWithEmail() {
        Long id = 1L;

        UserProfile profile = new UserProfile();
        profile.setId(id);

        when(userProfileRepository.findById(id)).thenReturn(Optional.of(profile));

        UserCredential auth = new UserCredential();
        auth.setUserId(id);
        auth.setEmail("u@test.com");
        when(userCredentialRepository.findById(id)).thenReturn(Optional.of(auth));

        UserProfileResponseDto mapped = new UserProfileResponseDto();
        when(userProfileMapper.toResponseDto(profile)).thenReturn(mapped);

        UserProfileResponseDto out = service.getUserProfileById(id, id);

        assertNotNull(out);
        assertEquals("u@test.com", out.getEmail());
    }

    // getAllUserProfiles

    @Test
    void getAllUserProfiles_shouldReturnListWithEmails() {
        UserProfile p1 = new UserProfile();
        p1.setId(1L);
        UserProfile p2 = new UserProfile();
        p2.setId(2L);

        when(userProfileRepository.findAll()).thenReturn(List.of(p1, p2));

        UserCredential c1 = new UserCredential();
        c1.setUserId(1L);
        c1.setEmail("a@test.com");
        when(userCredentialRepository.findById(1L)).thenReturn(Optional.of(c1));

        UserCredential c2 = new UserCredential();
        c2.setUserId(2L);
        c2.setEmail("b@test.com");
        when(userCredentialRepository.findById(2L)).thenReturn(Optional.of(c2));

        when(userProfileMapper.toResponseDto(p1)).thenReturn(new UserProfileResponseDto());
        when(userProfileMapper.toResponseDto(p2)).thenReturn(new UserProfileResponseDto());

        List<UserProfileResponseDto> out = service.getAllUserProfiles();

        assertEquals(2, out.size());
        assertEquals("a@test.com", out.get(0).getEmail());
        assertEquals("b@test.com", out.get(1).getEmail());
    }

    //  deleteUserProfileById

    @Test
    void deleteUserProfileById_whenNotOwner_shouldThrowForbidden() {
        assertThrows(ForbiddenException.class, () -> service.deleteUserProfileById(2L, 1L));
    }

    @Test
    void deleteUserProfileById_whenNotFound_shouldThrowUserProfileNotFound() {
        Long id = 1L;
        when(userProfileRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserProfileNotFoundException.class,
                () -> service.deleteUserProfileById(id, id));

        verify(userProfileRepository, never()).delete(any());
    }

    @Test
    void deleteUserProfileById_success_shouldDelete() {
        Long id = 1L;

        UserProfile profile = new UserProfile();
        profile.setId(id);

        when(userProfileRepository.findById(id)).thenReturn(Optional.of(profile));

        service.deleteUserProfileById(id, id);

        verify(userProfileRepository, times(1)).delete(profile);
    }

    //  updateUserProfile

    @Test
    void updateUserProfile_whenNotOwner_shouldThrowForbidden() {
        assertThrows(ForbiddenException.class,
                () -> service.updateUserProfile(2L, 1L, new UserProfileRequestDto()));
    }

    @Test
    void updateUserProfile_whenProfileNotFound_shouldThrowUserProfileNotFound() {
        Long id = 1L;
        when(userProfileRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserProfileNotFoundException.class,
                () -> service.updateUserProfile(id, id, new UserProfileRequestDto()));
    }

    @Test
    void updateUserProfile_shouldUpdateResumeTimestampWhenChanged() {
        Long id = 1L;

        UserProfile profile = new UserProfile();
        profile.setId(id);
        profile.setResumeUrl("old.pdf");

        when(userProfileRepository.findById(id)).thenReturn(Optional.of(profile));

        UserCredential auth = new UserCredential();
        auth.setUserId(id);
        auth.setEmail("u@test.com");
        when(userCredentialRepository.findById(id)).thenReturn(Optional.of(auth));

        // simulate mapper changing resumeUrl
        doAnswer(inv -> {
            UserProfile target = inv.getArgument(1);
            target.setResumeUrl("new.pdf");
            return null;
        }).when(userProfileMapper).updateEntityFromDto(any(UserProfileRequestDto.class), any(UserProfile.class));

        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UserProfileResponseDto mapped = new UserProfileResponseDto();
        when(userProfileMapper.toResponseDto(profile)).thenReturn(mapped);

        assertNull(profile.getResumeUploadedAt());

        service.updateUserProfile(id, id, new UserProfileRequestDto());

        assertNotNull(profile.getResumeUploadedAt());
    }

    @Test
    void updateUserProfile_shouldNotUpdateResumeTimestampWhenSameUrl() {
        Long id = 1L;

        UserProfile profile = new UserProfile();
        profile.setId(id);
        profile.setResumeUrl("same.pdf");

        when(userProfileRepository.findById(id)).thenReturn(Optional.of(profile));

        UserCredential auth = new UserCredential();
        auth.setUserId(id);
        auth.setEmail("u@test.com");
        when(userCredentialRepository.findById(id)).thenReturn(Optional.of(auth));

        // mapper keeps same resume
        doAnswer(inv -> {
            UserProfile target = inv.getArgument(1);
            target.setResumeUrl("same.pdf");
            return null;
        }).when(userProfileMapper).updateEntityFromDto(any(UserProfileRequestDto.class), any(UserProfile.class));

        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userProfileMapper.toResponseDto(profile)).thenReturn(new UserProfileResponseDto());

        service.updateUserProfile(id, id, new UserProfileRequestDto());

        assertNull(profile.getResumeUploadedAt());
    }

    // partialUpdateUserProfile

    @Test
    void partialUpdateUserProfile_whenNotOwner_shouldThrowForbidden() {
        assertThrows(ForbiddenException.class,
                () -> service.partialUpdateUserProfile(2L, 1L, new UserProfilePartialUpdateDto()));
    }

    @Test
    void partialUpdateUserProfile_whenProfileNotFound_shouldThrowUserProfileNotFound() {
        Long id = 1L;
        when(userProfileRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserProfileNotFoundException.class,
                () -> service.partialUpdateUserProfile(id, id, new UserProfilePartialUpdateDto()));
    }

    @Test
    void partialUpdateUserProfile_shouldUpdateResumeTimestampWhenChanged() {
        Long id = 1L;

        UserProfile profile = new UserProfile();
        profile.setId(id);
        profile.setResumeUrl("old.pdf");

        when(userProfileRepository.findById(id)).thenReturn(Optional.of(profile));

        UserCredential auth = new UserCredential();
        auth.setUserId(id);
        auth.setEmail("u@test.com");
        when(userCredentialRepository.findById(id)).thenReturn(Optional.of(auth));

        // patch changes resumeUrl
        doAnswer(inv -> {
            UserProfile target = inv.getArgument(1);
            target.setResumeUrl("new.pdf");
            return null;
        }).when(userProfileMapper).patchEntityFromDto(any(UserProfilePartialUpdateDto.class), any(UserProfile.class));

        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userProfileMapper.toResponseDto(profile)).thenReturn(new UserProfileResponseDto());

        assertNull(profile.getResumeUploadedAt());

        service.partialUpdateUserProfile(id, id, new UserProfilePartialUpdateDto());

        assertNotNull(profile.getResumeUploadedAt());
    }

    //  checkUserExists

    @Test
    void checkUserExists_whenNotExists_shouldThrowUserNotFound() {
        when(userProfileRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFound.class, () -> service.checkUserExists(1L));
    }

    @Test
    void checkUserExists_whenExists_shouldNotThrow() {
        when(userProfileRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> service.checkUserExists(1L));
    }
}
