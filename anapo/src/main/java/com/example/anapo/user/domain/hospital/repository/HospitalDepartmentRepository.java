package com.example.anapo.user.domain.hospital.repository;

import com.example.anapo.user.domain.hospital.entity.Hospital;
import com.example.anapo.user.domain.hospital.entity.HospitalDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HospitalDepartmentRepository extends JpaRepository<HospitalDepartment, Long> {
    boolean existsByHospital_IdAndDepartment_DeptName(Long hospitalId, String deptName);

    List<HospitalDepartment> findByHospitalId(Long hospitalId);
}