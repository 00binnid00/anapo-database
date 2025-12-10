package com.example.anapo.user.application.account.controller;

import com.example.anapo.user.application.account.dto.AccountDto;
import com.example.anapo.user.application.account.dto.AccountUpdateDto;
import com.example.anapo.user.application.account.service.AccountService;
import com.example.anapo.user.domain.account.entity.Account;
import com.example.anapo.user.exception.DuplicateUserIdException;
import com.example.anapo.user.exception.PasswordMismatchException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AccountController {

    private final AccountService accountService;

    // ✅ [추가됨] 내 정보 불러오기 (정보 수정 페이지 접속 시 사용)
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountInfo(@PathVariable Long id) {
        try {
            // Service에 추가한 getAccount 메서드 사용
            Account account = accountService.getAccount(id);

            // 프론트엔드에 필요한 정보만 골라서 줍니다 (비밀번호 제외)
            Map<String, Object> response = new HashMap<>();
            response.put("userName", account.getUserName());
            response.put("userId", account.getUserId());     // 아이디(이메일)
            response.put("userNumber", account.getUserNumber()); // 전화번호
            response.put("birth", account.getBirth());       // 생년월일
            response.put("sex", account.getSex());           // 성별

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 정보를 불러올 수 없습니다: " + e.getMessage());
        }
    }

    // ✅ [수정됨] 회원 정보 수정 (비밀번호 변경 등)
    @PatchMapping("/accUpdate/{accId}")
    public ResponseEntity<?> updateAccount(@PathVariable Long accId, @RequestBody AccountUpdateDto dto) {
        try {
            // Service의 updateAccount 메서드 호출 (비밀번호 암호화 로직 포함됨)
            Account updated = accountService.updateAccount(accId, dto);

            return ResponseEntity.ok(Map.of(
                    "message", "회원 정보가 성공적으로 수정되었습니다.",
                    "userName", updated.getUserName()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("정보 수정 실패: " + e.getMessage());
        }
    }

    // ========================================================
    // 👇 기존 기능들 (로그인, 회원가입 등은 그대로 유지)
    // ========================================================

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> joinUser(@RequestBody AccountDto accountDto) {
        try {
            return ResponseEntity.ok(accountService.join(accountDto));
        } catch (DuplicateUserIdException | PasswordMismatchException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("회원가입 오류: " + e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AccountDto accountDto, HttpServletRequest request) {
        try {
            System.out.println("로그인 시도 중... ID: " + accountDto.getUserId());

            Account user = accountService.login(accountDto);

            if (user != null) {
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) oldSession.invalidate();

                HttpSession newSession = request.getSession(true);
                newSession.setAttribute("loggedInUser", user);
                newSession.setMaxInactiveInterval(1800);

                System.out.println("로그인 성공! User DB ID: " + user.getId());

                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "로그인 성공");
                responseMap.put("id", user.getId());         // 프론트엔드 저장용 ID
                responseMap.put("userId", user.getUserId());
                responseMap.put("userName", user.getUserName());

                return ResponseEntity.ok(responseMap);

            } else {
                System.out.println("로그인 실패: 아이디/비번 불일치");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
            }
        } catch (Exception e) {
            System.err.println("!!! 로그인 에러 발생 !!!");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 에러 내용: " + e.toString());
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    // 로그인 상태 확인 (세션 방식용 - 참고용으로 유지)
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(HttpSession session) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보가 없습니다.");
        }
        try {
            return ResponseEntity.ok((Account) loggedInUser);
        } catch (Exception e) {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션 오류");
        }
    }
}