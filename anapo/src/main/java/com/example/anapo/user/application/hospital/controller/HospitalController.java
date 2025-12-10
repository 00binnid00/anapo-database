package com.example.anapo.user.application.hospital.controller;

import com.example.anapo.user.application.hospital.dto.HosCreateDto;
import com.example.anapo.user.application.hospital.dto.HosUpdateDto;
import com.example.anapo.user.application.hospital.dto.HospitalDisDto;
import com.example.anapo.user.application.hospital.dto.HospitalDto;
import com.example.anapo.user.application.hospital.service.HospitalSearchService;
import com.example.anapo.user.application.hospital.service.HospitalService;
import com.example.anapo.user.domain.hospital.entity.Hospital;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hospitals")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // ✅ 1. React 연결 허용 (필수!)
public class HospitalController {

    private final HospitalService hospitalService;
    private final HospitalSearchService hospitalSearchService;

    // ✅ 2. [추가됨] 병원 상세 정보 조회 (ID로 찾기)
    // 프론트엔드 예약 페이지에서 병원 이름을 불러올 때 사용됩니다.
    @GetMapping("/{id}")
    public ResponseEntity<?> getHospitalDetail(@PathVariable Long id) {
        try {
            // Service 파일에 findById 메서드가 있어야 작동합니다.
            Hospital hospital = hospitalService.findById(id);
            return ResponseEntity.ok(hospital);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("병원 정보를 찾을 수 없습니다.");
        }
    }

    // -------------------------------------------------------------------------
    // 👇 기존 코드 (그대로 유지)
    // -------------------------------------------------------------------------

    // 병원 정보 등록
    @PostMapping
    public ResponseEntity<?> createHospital(@RequestBody HosCreateDto dto) {
        Hospital saved = hospitalService.createHospital(dto);

        return ResponseEntity.ok(Map.of(
                "message", "병원 등록 완료",
                "id", saved.getId()
        ));
    }

    // 병원 정보 수정
    @PatchMapping("/HosUpdate/{hosId}")
    public ResponseEntity<?> updateHospital(
            @PathVariable Long hosId,
            @RequestBody HosUpdateDto dto
    ) {
        Hospital updated = hospitalService.updateHospital(hosId, dto);

        return ResponseEntity.ok(
                Map.of(
                        "message", "병원 정보가 수정되었습니다.",
                        "id", updated.getId(),
                        "hosName", updated.getHosName(),
                        "hosAddress", updated.getHosAddress(),
                        "hosEmail", updated.getHosEmail(),
                        "hosNumber", updated.getHosNumber(),
                        "hosLat", updated.getHosLat(),
                        "hosLng", updated.getHosLng()
                )
        );
    }

    // 병원 진료과목 추가
    @PostMapping("/{hosId}/departments")
    public ResponseEntity<?> addDepartmentsToHospital(
            @PathVariable Long hosId,
            @RequestBody Map<String, List<Long>> request
    ) {
        List<Long> departmentIds = request.get("departments");
        hospitalService.addDepartments(hosId, departmentIds);

        return ResponseEntity.ok(Map.of(
                "message", "진료과가 병원에 추가되었습니다.",
                "hospitalId", hosId,
                "departments", departmentIds
        ));
    }

    // 전체 병원 목록 조회
    @GetMapping
    public List<HospitalDto> getAllHospitals() {
        return hospitalService.getAllHospitals();
    }

    // 병원 위도, 경도 구하기
    @GetMapping("/near")
    public ResponseEntity<List<HospitalDisDto>> getNearby(
            @RequestParam double lat,
            @RequestParam double lng
    ) {
        List<HospitalDisDto> result = hospitalSearchService.getNearbyHospitals(lat, lng);
        return ResponseEntity.ok(result);
    }
}