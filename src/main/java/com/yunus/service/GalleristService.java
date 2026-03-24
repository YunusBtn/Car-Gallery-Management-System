package com.yunus.service;

import com.yunus.dto.DtoGallerist;
import com.yunus.dto.DtoGalleristIU;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.mapper.GalleristMapper;
import com.yunus.model.Address;
import com.yunus.model.Gallerist;
import com.yunus.repository.AddressRepository;
import com.yunus.repository.GalleristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.yunus.model.User;
import com.yunus.model.Role;
import com.yunus.repository.UserRepository;
import com.yunus.repository.RoleRepository;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class GalleristService {

    private final GalleristRepository galleristRepository;
    private final GalleristMapper galleristMapper;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public DtoGallerist saveGallerist(DtoGalleristIU dtoGalleristIU) {

        Optional<Address> optionalAddress = addressRepository.findById(dtoGalleristIU.getAddressId());
        if (optionalAddress.isEmpty()) {
            throw new BaseException(ErrorType.NOT_FOUND, "Adress Bulunamadı");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, "Kullanıcı bulunamadı: " + username));
        
        Role galleristRole = roleRepository.findByName(Role.RoleName.GALLERIST)
                .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, "Galerici rolü bulunamadı"));
        
        user.getRoles().add(galleristRole);
        userRepository.save(user);

        Gallerist gallerist = galleristMapper.toEntity(dtoGalleristIU);
        gallerist.setAddress(optionalAddress.get());
        gallerist.setUser(user);
        Gallerist savedGallerist = galleristRepository.save(gallerist);

        Gallerist fullGallerist = galleristRepository.findById(savedGallerist.getId()).orElseThrow();

        return galleristMapper.toDto(fullGallerist);
    }

    public List<DtoGallerist> getAllGallerists() {
        return galleristRepository.findAll()
                .stream()
                .map(galleristMapper::toDto)
                .collect(Collectors.toList());
    }

    public DtoGallerist getGalleristById(Long id) {
        Gallerist gallerist = galleristRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, id.toString()));
        return galleristMapper.toDto(gallerist);
    }

    public DtoGallerist getMyProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, "Kullanıcı bulunamadı"));

        Optional<Gallerist> gallerist = galleristRepository.findAll().stream()
                .filter(g -> g.getUser() != null && g.getUser().getId().equals(user.getId()))
                .findFirst();

        return gallerist.map(galleristMapper::toDto).orElse(null);
    }
}
