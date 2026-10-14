package com.example.petclinic.repository;

import com.example.petclinic.entity.Owner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    /**
     * 성 접두사 검색(대소문자 무시). {@code escapedPrefix}는 LIKE 와일드카드(%, _, \)가 이스케이프된 값이어야 한다.
     */
    @Query("select o from Owner o where lower(o.lastName) like lower(concat(:escapedPrefix, '%')) escape '\\' order by o.lastName, o.id")
    List<Owner> findByLastNamePrefix(@Param("escapedPrefix") String escapedPrefix, Pageable pageable);
}
