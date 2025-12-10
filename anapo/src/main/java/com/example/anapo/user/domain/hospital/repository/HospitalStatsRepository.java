package com.example.anapo.user.domain.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.anapo.user.domain.reservation.entity.Reservation;

import java.util.List;

@Repository
public interface HospitalStatsRepository extends JpaRepository<Reservation, Long> {

    // 월간 예약 수
    @Query("SELECT COUNT(r) FROM Reservation r " +
            "WHERE r.hospital.id = :hospitalId " +
            "AND MONTH(r.reserDate) = MONTH(CURRENT_DATE) " +
            "AND YEAR(r.reserDate) = YEAR(CURRENT_DATE)")
    int countMonthlyReservations(Long hospitalId);

    // 인기 진료과
    @Query("SELECT r.department, COUNT(r) FROM Reservation r " +
            "WHERE r.hospital.id = :hospitalId " +
            "GROUP BY r.department ORDER BY COUNT(r) DESC")
    List<Object[]> getDepartmentRanking(Long hospitalId);

    // 시간대별 예약 패턴 (HOUR)
    @Query("SELECT FUNCTION('HOUR', r.reserDate), COUNT(r) FROM Reservation r " +
            "WHERE r.hospital.id = :hospitalId " +
            "GROUP BY FUNCTION('HOUR', r.reserDate) " +
            "ORDER BY FUNCTION('HOUR', r.reserDate)")
    List<Object[]> getReservationByHour(Long hospitalId);

    // 신규 환자 수
    @Query("SELECT COUNT(DISTINCT c.user.id) FROM Clinic c " +
            "WHERE c.hospital.id = :hospitalId " +
            "AND c.user.id NOT IN (" +
            "SELECT c2.user.id FROM Clinic c2 " +
            "WHERE c2.hospital.id = :hospitalId " +
            "GROUP BY c2.user.id HAVING COUNT(c2) > 1)")
    int countNewUsers(Long hospitalId);

    // 재방문 환자 수
    @Query("SELECT c.user.id FROM Clinic c " +
            "WHERE c.hospital.id = :hospitalId " +
            "GROUP BY c.user.id HAVING COUNT(c) > 1")
    List<Long> countReturnList(Long hospitalId);

    default int countReturn(Long hospitalId) {
        return countReturnList(hospitalId).size();
    }

    // 전체 예약 수
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.hospital.id = :hospitalId")
    int totalReservations(Long hospitalId);

    // 취소된 예약 수
    @Query("SELECT COUNT(r) FROM Reservation r " +
            "WHERE r.hospital.id = :hospitalId AND r.reserYesNo = false")
    int canceledReservations(Long hospitalId);
}
