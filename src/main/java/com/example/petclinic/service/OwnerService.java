package com.example.petclinic.service;

import com.example.petclinic.entity.Owner;
import com.example.petclinic.repository.OwnerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class OwnerService {

    static final int MAX_SEARCH_RESULTS = 100;

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    public List<Owner> findOwners() {
        return ownerRepository.findAll();
    }

    /**
     * 성(lastName) 접두사로 owner를 검색한다(대소문자 무시, 최대 {@value #MAX_SEARCH_RESULTS}건).
     * 검색어가 null이면 전체 목록을 반환하고, 공백뿐이면 {@link IllegalArgumentException}을 던진다.
     */
    public List<Owner> findOwners(String lastName) {
        if (lastName == null) {
            return findOwners();
        }
        String keyword = lastName.trim();
        if (keyword.isEmpty()) {
            throw new IllegalArgumentException("lastName must not be blank");
        }
        return ownerRepository.findByLastNamePrefix(
                escapeLike(keyword), PageRequest.of(0, MAX_SEARCH_RESULTS));
    }

    private String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    public Owner findOwner(Long ownerId) {
        return verifyOwnerExists(ownerId);
    }

    private Owner verifyOwnerExists(Long ownerId) {
        return ownerRepository.findById(ownerId).orElseThrow(
                () -> new NoSuchElementException("Owner does not exist")
        );
    }
}
