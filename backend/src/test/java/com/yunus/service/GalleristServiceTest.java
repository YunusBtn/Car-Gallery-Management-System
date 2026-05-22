package com.yunus.service;

import com.yunus.dto.DtoGallerist;
import com.yunus.dto.DtoGalleristIU;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.GalleristMapper;
import com.yunus.model.Address;
import com.yunus.model.Gallerist;
import com.yunus.model.Role;
import com.yunus.model.User;
import com.yunus.repository.AddressRepository;
import com.yunus.repository.GalleristRepository;
import com.yunus.repository.RoleRepository;
import com.yunus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GalleristService Unit Testleri")
class GalleristServiceTest {

    @InjectMocks
    private GalleristService galleristService;

    @Mock private GalleristRepository galleristRepository;
    @Mock private GalleristMapper galleristMapper;
    @Mock private AddressRepository addressRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;

    private static final Long ADDRESS_ID = 5L;
    private static final String USERNAME  = "galleristUser";

    private DtoGalleristIU dtoGalleristIU;
    private Address address;
    private User user;
    private Role galleristRole;
    private Gallerist gallerist;
    private DtoGallerist dtoGallerist;

    @BeforeEach
    void setUp() {
        dtoGalleristIU = new DtoGalleristIU("Ali", "Veli", ADDRESS_ID);

        address = new Address();
        address.setId(ADDRESS_ID);

        galleristRole = new Role();
        galleristRole.setName(Role.RoleName.GALLERIST);

        user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setRoles(new HashSet<>());

        gallerist = new Gallerist();
        gallerist.setId(1L);
        gallerist.setFirstName("Ali");

        dtoGallerist = new DtoGallerist();
        dtoGallerist.setFirstName("Ali");
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> mockedSecurity) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication auth = Mockito.mock(Authentication.class);
        mockedSecurity.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(USERNAME);
    }

    // ─────────────────────────────────────────────
    // saveGallerist
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("saveGallerist: Tüm bağımlılıklar mevcutsa galerici kaydedilmeli")
    void saveGallerist_shouldReturnDtoGallerist_whenAllFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            // Given
            mockSecurityContext(mockedSecurity);
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(address));
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(roleRepository.findByName(Role.RoleName.GALLERIST)).thenReturn(Optional.of(galleristRole));
            when(galleristMapper.toEntity(dtoGalleristIU)).thenReturn(gallerist);
            when(galleristRepository.save(gallerist)).thenReturn(gallerist);
            when(galleristRepository.findById(gallerist.getId())).thenReturn(Optional.of(gallerist));
            when(galleristMapper.toDto(gallerist)).thenReturn(dtoGallerist);

            // When
            DtoGallerist result = galleristService.saveGallerist(dtoGalleristIU);

            // Then
            assertNotNull(result);
            assertEquals("Ali", result.getFirstName());
            verify(userRepository, times(1)).save(user);
            verify(galleristRepository, times(1)).save(gallerist);
        }
    }

    @Test
    @DisplayName("saveGallerist: Adres bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void saveGallerist_shouldThrow_whenAddressNotFound() {
        // Given
        when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> galleristService.saveGallerist(dtoGalleristIU));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
        verify(galleristRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveGallerist: Kullanıcı bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void saveGallerist_shouldThrow_whenUserNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            // Given
            mockSecurityContext(mockedSecurity);
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(address));
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

            // When & Then
            BaseException ex = assertThrows(BaseException.class,
                    () -> galleristService.saveGallerist(dtoGalleristIU));
            assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
            verify(galleristRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("saveGallerist: Galerici rolü bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void saveGallerist_shouldThrow_whenGalleristRoleNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            // Given
            mockSecurityContext(mockedSecurity);
            when(addressRepository.findById(ADDRESS_ID)).thenReturn(Optional.of(address));
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(roleRepository.findByName(Role.RoleName.GALLERIST)).thenReturn(Optional.empty());

            // When & Then
            BaseException ex = assertThrows(BaseException.class,
                    () -> galleristService.saveGallerist(dtoGalleristIU));
            assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
            verify(galleristRepository, never()).save(any());
        }
    }

    // ─────────────────────────────────────────────
    // getAllGallerists
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getAllGallerists: Galerici listesi varsa DTO listesi dönmeli")
    void getAllGallerists_shouldReturnList() {
        // Given
        Gallerist g2 = new Gallerist();
        DtoGallerist dtoG2 = new DtoGallerist();

        when(galleristRepository.findAll()).thenReturn(List.of(gallerist, g2));
        when(galleristMapper.toDto(gallerist)).thenReturn(dtoGallerist);
        when(galleristMapper.toDto(g2)).thenReturn(dtoG2);

        // When
        List<DtoGallerist> result = galleristService.getAllGallerists();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getAllGallerists: Kayıt yoksa boş liste dönmeli")
    void getAllGallerists_shouldReturnEmptyList() {
        // Given
        when(galleristRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<DtoGallerist> result = galleristService.getAllGallerists();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ─────────────────────────────────────────────
    // getGalleristById
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getGalleristById: Galerici bulunduğunda DtoGallerist dönmeli")
    void getGalleristById_shouldReturnDto_whenFound() {
        // Given
        when(galleristRepository.findById(1L)).thenReturn(Optional.of(gallerist));
        when(galleristMapper.toDto(gallerist)).thenReturn(dtoGallerist);

        // When
        DtoGallerist result = galleristService.getGalleristById(1L);

        // Then
        assertNotNull(result);
    }

    @Test
    @DisplayName("getGalleristById: Galerici bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void getGalleristById_shouldThrow_whenNotFound() {
        // Given
        when(galleristRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> galleristService.getGalleristById(99L));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
    }

    // ─────────────────────────────────────────────
    // getMyProfile
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getMyProfile: Giriş yapan kullanıcının galerici profili dönmeli")
    void getMyProfile_shouldReturnDtoGallerist_whenFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            // Given
            mockSecurityContext(mockedSecurity);
            gallerist.setUser(user);

            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(galleristRepository.findAll()).thenReturn(List.of(gallerist));
            when(galleristMapper.toDto(gallerist)).thenReturn(dtoGallerist);

            // When
            DtoGallerist result = galleristService.getMyProfile();

            // Then
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("getMyProfile: Galerici kaydı yoksa null dönmeli")
    void getMyProfile_shouldReturnNull_whenNoGalleristForUser() {
        try (MockedStatic<SecurityContextHolder> mockedSecurity =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            // Given
            mockSecurityContext(mockedSecurity);
            when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
            when(galleristRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            DtoGallerist result = galleristService.getMyProfile();

            // Then
            assertNull(result);
        }
    }
}
