package com.yunus.service;

import com.yunus.dto.DtoAddress;
import com.yunus.dto.DtoAddressIU;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.AddressMapper;
import com.yunus.model.Address;
import com.yunus.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Unit Testleri")
class AddressServiceTest {

    @InjectMocks
    private AddressService addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    private DtoAddressIU dtoAddressIU;
    private Address address;
    private DtoAddress dtoAddress;

    @BeforeEach
    void setUp() {
        dtoAddressIU = new DtoAddressIU();
        dtoAddressIU.setCity("İstanbul");
        dtoAddressIU.setDistrict("Kadıköy");
        dtoAddressIU.setNeighborhood("Moda");
        dtoAddressIU.setStreet("Atatürk Caddesi");

        address = new Address();
        address.setId(1L);
        address.setCity("İstanbul");

        dtoAddress = new DtoAddress();
        dtoAddress.setId(1L);
        dtoAddress.setCity("İstanbul");
    }

    // ─────────────────────────────────────────────
    // saveAddress
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("saveAddress: Geçerli DTO ile adres kaydedilmeli ve DtoAddress dönmeli")
    void saveAddress_shouldReturnDtoAddress() {
        // Given
        when(addressMapper.toEntity(dtoAddressIU)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(dtoAddress);

        // When
        DtoAddress result = addressService.saveAddress(dtoAddressIU);

        // Then
        assertNotNull(result);
        assertEquals("İstanbul", result.getCity());
        verify(addressRepository, times(1)).save(address);
        verify(addressMapper, times(1)).toDto(address);
    }

    @Test
    @DisplayName("saveAddress: Mapper ve repository doğru sırayla çağrılmalı")
    void saveAddress_shouldCallMapperAndRepositoryInOrder() {
        // Given
        when(addressMapper.toEntity(dtoAddressIU)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(dtoAddress);

        // When
        addressService.saveAddress(dtoAddressIU);

        // Then
        var inOrder = inOrder(addressMapper, addressRepository);
        inOrder.verify(addressMapper).toEntity(dtoAddressIU);
        inOrder.verify(addressRepository).save(address);
        inOrder.verify(addressMapper).toDto(address);
    }

    // ─────────────────────────────────────────────
    // getAddressById
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("getAddressById: Adres bulunduğunda DtoAddress dönmeli")
    void getAddressById_shouldReturnDtoAddress_whenFound() {
        // Given
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(addressMapper.toDto(address)).thenReturn(dtoAddress);

        // When
        DtoAddress result = addressService.getAddressById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("İstanbul", result.getCity());
    }

    @Test
    @DisplayName("getAddressById: Adres bulunamazsa NOT_FOUND hatası fırlatılmalı")
    void getAddressById_shouldThrow_whenNotFound() {
        // Given
        when(addressRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        BaseException ex = assertThrows(BaseException.class,
                () -> addressService.getAddressById(99L));
        assertEquals(ErrorType.NOT_FOUND, ex.getErrorType());
        verify(addressMapper, never()).toDto(any());
    }
}
