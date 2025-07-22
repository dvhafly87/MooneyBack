package com.MooneyB.Challenge;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.MooneyB.common.exceptions.ChallengeNotFoundException;
import com.MooneyB.common.exceptions.MemberNotFoundException;

import java.util.List;

@RestController 
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    // 의존성 주입 (생성자 주입 권장)
    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    

    // ✨ 새 챌린지 생성

    @PostMapping("/member/{memberId}") // POST /challenges/member/{memberId}
    public ResponseEntity<Challenge> createChallenge(
            @PathVariable("memberId") String memberId,
            @RequestBody Challenge challenge) {
        Challenge createdChallenge = challengeService.createChallenge(challenge, memberId);
        return new ResponseEntity<>(createdChallenge, HttpStatus.CREATED); // 201 Created
    }

    

    // 🔍 ID로 챌린지 조회

    @GetMapping("/{challengeId}") // GET /challenges/{challengeId}
    public ResponseEntity<Challenge> getChallengeById(@PathVariable("challengeId") Long challengeId) {
        Challenge challenge = challengeService.getChallengeById(challengeId);
        return new ResponseEntity<>(challenge, HttpStatus.OK); // 200 OK
    }

    

    // 📄 회원 ID로 모든 챌린지 조회

    @GetMapping("/member/{memberId}") // GET /api/challenges/member/{memberId}
    public ResponseEntity<List<Challenge>> getChallengesByMemberId(@PathVariable("memberId") String memberId) {
        List<Challenge> challenges = challengeService.getChallengesByMemberId(memberId);
        if (challenges.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(challenges, HttpStatus.OK); // 200 OK
    }

    

    // 🔄 챌린지 업데이트

    @PutMapping("/{challengeId}") // PUT /challenges/{challengeId}
    public ResponseEntity<Challenge> updateChallenge(
            @PathVariable("challengeId") Long challengeId,
            @RequestBody Challenge challenge) {
        challenge.setMchlId(challengeId);
        Challenge updatedChallenge = challengeService.updateChallenge(challenge);
        return new ResponseEntity<>(updatedChallenge, HttpStatus.OK); // 200 OK
    }

    

    // 🗑️ 챌린지 삭제

    @DeleteMapping("/{challengeId}") // DELETE /challenges/{challengeId}
    public ResponseEntity<Void> deleteChallenge(@PathVariable("challengeId") Long challengeId) {
        challengeService.deleteChallenge(challengeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }

    

    // 🚨 예외 처리 핸들러

    // 챌린지를 찾지 못했을 때 발생하는 예외 처리
    @ExceptionHandler(ChallengeNotFoundException.class)
    public ResponseEntity<String> handleChallengeNotFoundException(ChallengeNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
    }

    // 회원을 찾지 못했을 때 발생하는 예외 처리
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handleMemberNotFoundException(MemberNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
    }

    // 유효하지 않은 인자(예: 날짜 순서 오류)가 전달되었을 때 발생하는 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
    }
}