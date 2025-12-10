package com.example.anapo.user.domain.hospital.entity;

import com.example.anapo.user.enums.AccountStatus;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.Collection;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hos_name", nullable = false)
    private String hosName;

    @Column(name = "hos_address", nullable = false)
    private String hosAddress;

    @Column(name = "hos_number", nullable = false)
    private String hosNumber;

    @Column(name = "hos_email")
    private String hosEmail;

    @Column(name = "hos_time", nullable = false)
    private String hosTime;

    @Column(name = "hos_lat", nullable = false)
    private double hosLat;      // 위도

    @Column(name = "hos_lng", nullable = false)
    private double hosLng;      // 경도

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    public Hospital(String hosName, String hosAddress, String hosNumber, String hosEmail,
                    String hosTime, double hosLat, double hosLng) {
        this.hosName = hosName;
        this.hosAddress = hosAddress;
        this.hosNumber = hosNumber;
        this.hosEmail = hosEmail;
        this.hosTime = hosTime;
        this.hosLat = hosLat;
        this.hosLng = hosLng;
    }


    public void updateInfo(String hosName, String hosAddress,String hosNumber,
                           String hosEmail, Double hosLat, Double hosLng) {

        if (hosName != null) this.hosName = hosName;
        if (hosAddress != null) this.hosAddress = hosAddress;
        if (hosEmail != null) this.hosEmail = hosEmail;
        if (hosNumber != null) this.hosNumber = hosNumber;
        if (hosLat != null) this.hosLat = hosLat;
        if (hosLng != null) this.hosLng = hosLng;
    }

    public void updateStatus(AccountStatus newStatus) {
        this.status = newStatus;
    }
}
