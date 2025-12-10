package com.example.anapo.user.application.account.service;

import com.example.anapo.user.application.account.dto.AccountUpdateDto;
import com.example.anapo.user.exception.DataNotFoundException;
import com.example.anapo.user.application.account.dto.AccountDto;
import com.example.anapo.user.domain.account.entity.Account;
import com.example.anapo.user.domain.account.repository.AccountRepository;
import com.example.anapo.user.exception.DuplicateUserIdException;
import com.example.anapo.user.exception.PasswordMismatchException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    // 암호화 도구
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final AccountRepository accountRepository;

    // ✅ [추가] 내 정보 가져오기 (정보 수정 페이지용)
    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
    }

    // 회원가입
    @Transactional
    public Account join(AccountDto accountDto){
        // 1. 아이디 중복 검사
        if (accountRepository.findByUserId(accountDto.getUserId()).isPresent()) {
            throw new DuplicateUserIdException("이미 존재하는 아이디입니다.");
        }

        // 2. 비밀번호 일치 검사
        if (!Objects.equals(accountDto.getUserPassword(), accountDto.getUserPassword2())) {
            throw new PasswordMismatchException("비밀번호가 서로 일치하지 않습니다.");
        }

        // 3. 회원 생성 (비밀번호 암호화)
        Account account = new Account(
                encoder.encode(accountDto.getUserPassword()),
                accountDto.getUserName(),
                accountDto.getUserId(),
                accountDto.getUserNumber(),
                accountDto.getBirth(),
                accountDto.getSex()
        );

        return accountRepository.save(account);
    }

    // 사용자 조회 (이름으로 찾기)
    public Account getUser(String userName) {
        return accountRepository.findByUserName(userName)
                .orElseThrow(() -> new DataNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 로그인
    public Account login(AccountDto accountDto) {
        if (accountDto.getUserId() == null || accountDto.getUserPassword() == null) {
            return null;
        }

        Optional<Account> userOp = accountRepository.findByUserId(accountDto.getUserId());

        if (userOp.isPresent()) {
            Account found = userOp.get();
            // 암호화된 비밀번호 비교
            if(encoder.matches(accountDto.getUserPassword(), found.getUserPassword())) {
                return found;
            }
        }
        return null;
    }

    // 아이디 중복 확인
    public boolean existsByUserId(String userId) {
        return accountRepository.existsByUserId(userId);
    }

    // ✅ [수정됨] 회원 정보 수정 (비밀번호 암호화 포함)
    @Transactional
    public Account updateAccount(Long accId, AccountUpdateDto dto) {
        Account account = accountRepository.findById(accId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 1. 이름 변경
        if (dto.getUserName() != null && !dto.getUserName().isEmpty()) {
            account.setUserName(dto.getUserName());
        }

        // 2. 전화번호 변경
        if (dto.getUserNumber() != null && !dto.getUserNumber().isEmpty()) {
            account.setUserNumber(dto.getUserNumber());
        }

        // 3. ★ 비밀번호 변경 (값이 있을 때만 + 암호화 필수!)
        if (dto.getUserPassword() != null && !dto.getUserPassword().isEmpty()) {
            String encodedPwd = encoder.encode(dto.getUserPassword());
            account.setUserPassword(encodedPwd);
        }

        // (생년월일, 성별은 DTO에 값이 있다면 변경)
        if (dto.getBirth() != null) account.setBirth(dto.getBirth());
        if (dto.getSex() != null) account.setSex(dto.getSex());

        return account; // JPA Dirty Checking으로 자동 저장
    }
}