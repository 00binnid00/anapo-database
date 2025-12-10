package com.example.anapo.user.application.hospital.service;

import com.example.anapo.user.application.hospital.dto.HosCreateDto;
import com.example.anapo.user.application.hospital.dto.HosUpdateDto;
import com.example.anapo.user.application.hospital.dto.HospitalDisDto;
import com.example.anapo.user.application.hospital.dto.HospitalDto;
import com.example.anapo.user.domain.department.entity.Department;
import com.example.anapo.user.domain.hospital.entity.HospitalDepartment;
import com.example.anapo.user.domain.department.repository.DepartmentRepository;
import com.example.anapo.user.domain.hospital.repository.HospitalDepartmentRepository;
import com.example.anapo.user.domain.hospital.entity.Hospital;
import com.example.anapo.user.domain.hospital.repository.HospitalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final HospitalDepartmentRepository hospitalDepartmentRepository;

    // 병원 전체 조회
    public List<HospitalDto> getAllHospitals() {
        return hospitalRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 병원 이름으로 검색
    public List<HospitalDto> searchByName(String name) {
        return hospitalRepository.findByHosNameContaining(name)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 엔티티 -> DTO 변환
    private HospitalDto convertToDto(Hospital hospital) {
        return HospitalDto.builder()
                .id(hospital.getId())
                .hosName(hospital.getHosName())
                .hosAddress(hospital.getHosAddress())
                .hosNumber(hospital.getHosNumber())
                .hosTime(hospital.getHosTime())
                .build();
    }

    /*------------------------------------------------------------------------------------------*/

    // 병원 정보 등록
    @Transactional
    public Hospital createHospital(HosCreateDto dto) {

        // 1) 병원 먼저 저장
        Hospital hospital = Hospital.builder()
                .hosName(dto.getHosName())
                .hosAddress(dto.getHosAddress())
                .hosNumber(dto.getHosNumber())
                .hosEmail(dto.getHosEmail())
                .hosTime(dto.getHosTime())
                .hosLat(dto.getHosLat())
                .hosLng(dto.getHosLng())
                .build();

        hospitalRepository.save(hospital);

        // 2) 진료과 매핑 저장
        if (dto.getDepartments() != null) {
            for (Long deptId : dto.getDepartments()) {

                Department dept = departmentRepository.findById(deptId)
                        .orElseThrow(() ->
                                new RuntimeException("존재하지 않는 진료과 ID: " + deptId));

                HospitalDepartment mapping =
                        new HospitalDepartment(hospital, dept);

                hospitalDepartmentRepository.save(mapping);
            }
        }

        return hospital;
    }

    // 병원에 진료과 추가
    @Transactional
    public void addDepartments(Long hosId, List<Long> departmentIds) {

        Hospital hospital = hospitalRepository.findById(hosId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 병원입니다."));

        for (Long deptId : departmentIds) {

            Department department = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 진료과 ID: " + deptId));

            HospitalDepartment hd = new HospitalDepartment(hospital, department);
            hospitalDepartmentRepository.save(hd);
        }
    }
    // HospitalService.java 안에 있어야 함
    public Hospital findById(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 병원이 없습니다."));
    }
    // 병원 정보 수정
    @Transactional
    public Hospital updateHospital(Long hosId, HosUpdateDto dto) {
        Hospital hospital = hospitalRepository.findById(hosId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 병원입니다."));

        hospital.updateInfo(
                dto.getHosName(),
                dto.getHosAddress(),
                dto.getHosEmail(),
                dto.getHosNumber(),
                dto.getHosLat(),
                dto.getHosLng()
        );

        return hospital;
    }
}