package com.example.anapo.user.domain.hospital.repository;

import com.example.anapo.user.domain.hospital.entity.Hospital;
import com.example.anapo.user.domain.hospital.entity.HospitalDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findByHosNameContaining(String name);

    // 진료과 기준 병원 필터링
    @Query("SELECT h FROM Hospital h " +
            "JOIN HospitalDepartment hd ON h.id = hd.hospital.id " +
            "WHERE hd.department.id = :departmentId")
    List<Hospital> findByDepartment(@Param("departmentId") Long departmentId);
}