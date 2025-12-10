package com.example.anapo.user.application.hospital.service;

import com.example.anapo.user.domain.bookmark.repository.BookmarkRepository;
import com.example.anapo.user.domain.hospital.repository.HospitalStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HospitalStatsService {

    private final HospitalStatsRepository statsRepository;
    private final BookmarkRepository bookmarkRepository;


    public Map<String, Object> getHospitalStats(Long hospitalId) {

        int monthly = statsRepository.countMonthlyReservations(hospitalId);
        List<Object[]> deptRank = statsRepository.getDepartmentRanking(hospitalId);
        List<Object[]> hourly = statsRepository.getReservationByHour(hospitalId);

        int newUsers = statsRepository.countNewUsers(hospitalId);
        int returnUsers = statsRepository.countReturn(hospitalId);

        int bookmarkCount = bookmarkRepository.countByHospital_Id(hospitalId);;

        int total = statsRepository.totalReservations(hospitalId);
        int canceled = statsRepository.canceledReservations(hospitalId);

        return Map.of(
                "monthlyReservations", monthly,
                "popularDepartments", deptRank,
                "hourlyPattern", hourly,
                "newUser", newUsers,
                "returnUser", returnUsers,
                "bookmarkCount", bookmarkCount,
                "cancelRate", total == 0 ? 0 : (double) canceled / total
        );
    }
}
