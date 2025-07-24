package com.MooneyB.Diary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.MooneyB.common.exceptions.*;

import java.time.LocalDate;
import java.util.List;

@RestController 
@RequestMapping("/diaries") 
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    

    // ✍️ 새 일기 생성

    @PostMapping("/member/{memberId}") // POST /diaries/member/{memberId}
    public ResponseEntity<Diary> createDiary(
            @PathVariable("memberId") String memberId,
            @RequestBody Diary diary) {
        Diary createdDiary = diaryService.createDiary(diary, memberId);
        return new ResponseEntity<>(createdDiary, HttpStatus.CREATED);
    }

    

    // 🔍 ID로 일기 조회

    @GetMapping("/{diaryId}") // GET /diaries/{diaryId}
    public ResponseEntity<Diary> getDiaryById(@PathVariable("diaryId") Long diaryId) {
        Diary diary = diaryService.getDiaryById(diaryId);
        return new ResponseEntity<>(diary, HttpStatus.OK); // 200 OK
    }

    

    // 📄 특정 회원의 모든 일기 조회

    @GetMapping("/member/{memberId}/all") // GET /diaries/member/{memberId}/all
    public ResponseEntity<List<Diary>> getDiaryListByMemberId(@PathVariable("memberId") String memberId) {
        List<Diary> diaries = diaryService.getDiaryList(memberId);
        if (diaries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(diaries, HttpStatus.OK); // 200 OK
    }

    

    // 📄 특정 회원의 일기 페이징 조회

//    @GetMapping("/member/{memberId}/paged") // GET /diaries/member/{memberId}/paged
//    public ResponseEntity<Page<Diary>> getDiaryListPaging(
//            @PathVariable("memberId") String memberId,
//            @RequestParam(defaultValue = "0") int page, // 현재 페이지 (0부터 시작)
//            @RequestParam(defaultValue = "10") int size, // 한 페이지당 항목 수
//            @RequestParam(defaultValue = "mdiaDate,desc") String[] sort // 정렬 기준 (예: "mdiaDate,desc" -> 날짜 내림차순)
//    ) {
//        Sort.Direction direction = sort[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
//
//        Page<Diary> diaryPage = diaryService.diaryListPaging(pageable, memberId);
//        
//        if (diaryPage.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(diaryPage, HttpStatus.OK);
//    }

    

    // 🔄 일기 업데이트

    @PutMapping("/{diaryId}") // PUT /diaries/{diaryId}
    public ResponseEntity<Diary> updateDiary(
            @PathVariable("diaryId") Long diaryId,
            @RequestBody Diary diary) {
        // PathVariable의 diaryId와 RequestBody의 mdiaId가 일치하는지 확인하거나,
        // RequestBody의 mdiaId를 우선시하여 서비스에 전달할 수 있습니다.
        // 여기서는 RequestBody의 diary 객체에 mdiaId를 설정하여 서비스에 전달합니다.
        diary.setMdiaId(diaryId); 
        Diary updatedDiary = diaryService.updateDiary(diary);
        return new ResponseEntity<>(updatedDiary, HttpStatus.OK); // 200 OK
    }

    

    // 🗑️ 일기 삭제

    @DeleteMapping("/{diaryId}") // DELETE /diaries/{diaryId}
    public ResponseEntity<Void> deleteDiary(@PathVariable("diaryId") Long diaryId) {
        diaryService.deleteDiary(diaryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }

    

    // 🚨 예외 처리 핸들러

    // DiaryService에서 발생할 수 있는 EntityNotFoundException을 처리.
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        // 404 Not Found 상태 코드와 예외 메시지를 반환합니다.
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}