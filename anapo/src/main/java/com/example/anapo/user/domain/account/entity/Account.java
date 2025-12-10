package com.example.anapo.user.domain.account.entity;

import com.example.anapo.user.enums.AccountStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_number", nullable = false)
    @NotEmpty(message = "전화번호는 필수항목입니다.")
    @Pattern(regexp = "\\d{10,11}", message = "전화번호 형식은 01012345678이어야 합니다.")
    private String userNumber;

    @Column(name = "user_password", nullable = false)
    private String userPassword;

    @Column(nullable = false)
    private String birth;

    @Column(nullable = false)
    private String sex;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    public Account(String userPassword, String userName, String userId, @NotEmpty(message = "전화번호는 필수항목입니다.") @Pattern(regexp = "\\d{10,11}", message = "전화번호 형식은 01012345678이어야 합니다.") String userNumber, String birth, String sex) {
        this.userPassword = userPassword;
        this.userName = userName;
        this.userId = userId;
        this.userNumber = userNumber;
        this.birth = birth;
        this.sex = sex;

        this.status = AccountStatus.ACTIVE;
    }
}
