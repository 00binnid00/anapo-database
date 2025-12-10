package com.example.anapo.user.application.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountUpdateDto {
    private String userName;     // 이름
    private String userNumber;   // 전화번호
    private String birth;        // 생년월일
    private String sex;          // 성별

    // ✅ [추가] 비밀번호 변경을 위해 꼭 필요합니다!
    private String userPassword;
}