package com.example.anapo.user.domain.bookmark.repository;

import com.example.anapo.user.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 특정 사용자의 전체 즐겨찾기 조회
    List<Bookmark> findByUserId(Long userId);

    // 중복 저장 방지 (이미 즐겨찾기한 병원인지 체크)
    Optional<Bookmark> findByUserIdAndHospitalId(Long userId, Long hospitalId);

    int countByHospital_Id(Long hospitalId);
}
