package com.MooneyB.Notification;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // RESTful 웹 서비스를 위한 컨트롤러임을 나타냅니다.
@RequestMapping("/notifications") // 모든 엔드포인트의 기본 경로를 설정합니다.
public class NotificationController {

    private final NotificationService notificationService;

    // 의존성 주입 (생성자 주입 권장)
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    // ✨ 새 알림 생성

    @PostMapping("/member/{memberId}") // POST /notifications/member/{memberId}
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification, @PathVariable("memberId") String memberId) {
        Notification createdNotification = notificationService.createNotification(notification, memberId);
        return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
    }



    // 🔍 ID로 알림 조회

    @GetMapping("/{mnofId}") // GET /notifications/{mnofId}
    public ResponseEntity<Notification> getNotificationById(@PathVariable("mnofId") Long mnofId) {
        Optional<Notification> notification = notificationService.getNotificationById(mnofId);
        // Optional.ofNullable을 사용하면 값이 있으면 200 OK, 없으면 404 Not Found를 반환합니다.
        return notification.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                           .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    // 👤 회원 ID로 모든 알림 조회

    @GetMapping("/member/{memberId}") // GET /notifications/member/{memberId}
    public ResponseEntity<List<Notification>> getAllNotificationsByMemberId(@PathVariable("memberId") String memberId) {
        // 서비스 메서드 이름과 일치하도록 getNotification 또는 getNorificationByMemberId 중 하나를 사용합니다.
        // 서비스에 두 메서드가 중복되므로, 여기서는 getNotification을 사용합니다.
        List<Notification> notifications = notificationService.getNotification(memberId);
        if (notifications.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK); // 200 OK
    }



    // 📧 회원 ID로 읽지 않은 알림 조회

    @GetMapping("/member/{memberId}/unread") // GET /notifications/member/{memberId}/unread
    public ResponseEntity<List<Notification>> getUnreadNotificationsByMemberId(@PathVariable("memberId") String memberId) {
        List<Notification> unreadNotifications = notificationService.getUnreadNotificationByMemberId(memberId);
        if (unreadNotifications.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(unreadNotifications, HttpStatus.OK); // 200 OK
    }



    // ✅ 특정 알림 읽음 처리

    @PutMapping("/{mnofId}/read") // PUT /notifications/{mnofId}/read
    public ResponseEntity<Notification> markNotificationAsRead(@PathVariable("mnofId") Long id) {
        Optional<Notification> updatedNotification = notificationService.markNotificationAsRead(id);
        // 성공적으로 업데이트되면 200 OK와 업데이트된 알림 정보를 반환합니다.
        // 알림을 찾을 수 없으면 404 Not Found를 반환합니다.
        return updatedNotification.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                                  .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ✅ 모든 알림 읽음 처리

    @PutMapping("/member/{memberId}/read-all") // PUT /notifications/member/{memberId}/read-all
    public ResponseEntity<String> markAllNotificationsAsRead(@PathVariable("memberId") String memberId) {
        int updatedCount = notificationService.markAllNotificationsAsRead(memberId);
        return new ResponseEntity<>("Marked " + updatedCount + " notifications as read for member " + memberId + ".", HttpStatus.OK);
    }



   // 🗑️ 특정 알림 삭제

    @DeleteMapping("/{mnofId}") // DELETE /notifications/{mnofId}
    public ResponseEntity<Void> deleteNotification(@PathVariable("mnofId") Long mnofId) {
        notificationService.deleteNotification(mnofId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }
}