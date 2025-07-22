//package com.MooneyB.Notification;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import java.time.LocalDateTime;
//import java.util.Date;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.test.context.ActiveProfiles;
//
//import com.MooneyB.Member.Member;
//import com.MooneyB.Member.MemberRepo;
//
//import jakarta.transaction.Transactional;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//public class MooneyBackNotification {
//	
//	@Autowired
//	private NotificationService notiService; 
//	
//	@Autowired
//	private NotificationRepository notiRepo;
//	
//	@Autowired 
//	private MemberRepo mr;
// 
//
//	private Member m;
//	
//	@BeforeEach
//	void setUp() {
//		Optional<Member> d = mr.findById("uiuiui234");
//        if (d.isPresent()) {
//        	m = d.get();
//        } else {
//        	throw new NoSuchElementException("uiuiui234을 찾을 수 없었습니다.");
//        }
//        
//        List<Notification> existingNoti = notiService.getNorificationByMemberId(m.getMmemid());
//        notiRepo.deleteAll(existingNoti);
//    }
//	
//	@AfterEach // 각 테스트 메서드 실행 후 정리 작업을 수행합니다.
//    void tearDown() {
//        // 현재 테스트에서 'm'가 생성한 알림만 삭제합니다.
//        // (m 자체는 삭제하지 않습니다.)
//        List<Notification> notificationsToDelete = notiService.getNorificationByMemberId(m.getMmemid());
//        notiRepo.deleteAll(notificationsToDelete);
//    }
//	@Test
//    @DisplayName("알림 생성 성공 - 기본적으로 읽지 않은 상태로 저장")
//    void testCreateNotificationSuccess() {
//        // Given: 새로운 알림 객체 생성
//        Notification newNotification = new Notification();
//        newNotification.setMember(m);
//        newNotification.setMnofContent("새로운 공지사항입니다.");
//        newNotification.setMnofType("NOTICE");
//        newNotification.setMnofDt(LocalDateTime.now());
//
//        // When: 서비스 메서드 호출
//        Notification createdNotification = notiService.createNotification(newNotification);
//
//        // Then: 반환된 알림이 null이 아니고, ID가 생성되었으며, 읽지 않은 상태(false)인지 확인
//        assertThat(createdNotification).isNotNull();
//        assertThat(createdNotification.getMnofId()).isNotNull();
//        assertThat(createdNotification.isMnofReadYn()).isFalse();
//
//        Optional<Notification> foundInDb = notiRepo.findById(createdNotification.getMnofId());
//        assertThat(foundInDb).isPresent();
//        assertThat(foundInDb.get().getMnofContent()).isEqualTo("새로운 공지사항입니다.");
//    }
//
//    @Test
//    @DisplayName("ID로 알림 조회 성공 - 알림이 존재할 때")
//    void testGetNotificationByIdFound() {
//        Notification savedNotification = notiService.createNotification(createTestNotification(m, "조회용 알림", "INFO", false));
//        Optional<Notification> foundNotification = notiService.getNotificationById(savedNotification.getMnofId());
//        assertThat(foundNotification).isPresent();
//        assertThat(foundNotification.get().getMnofId()).isEqualTo(savedNotification.getMnofId());
//        assertThat(foundNotification.get().getMnofContent()).isEqualTo("조회용 알림");
//    }
//
//    @Test
//    @DisplayName("ID로 알림 조회 실패 - 알림이 존재하지 않을 때")
//    void testGetNotificationByIdNotFound() {
//        Optional<Notification> foundNotification = notiService.getNotificationById(999L);
//        assertThat(foundNotification).isNotPresent();
//    }
//
//    @Test
//    @DisplayName("회원 ID로 모든 알림 조회 성공")
//    void testGetNotificationsByMemberId() {
//        notiService.createNotification(createTestNotification(m, "알림1", "TYPE1", false));
//        notiService.createNotification(createTestNotification(m, "알림2", "TYPE2", true));
//        notiService.createNotification(createTestNotification(m, "알림3", "TYPE3", false));
//
//        List<Notification> result = notiService.getNorificationByMemberId(m.getMmemid());
//        assertThat(result).hasSize(3);
//        assertThat(result).extracting(Notification::getMnofContent)
//                          .containsExactlyInAnyOrder("알림1", "알림2", "알림3");
//    }
//
//    @Test
//    @DisplayName("회원 ID로 읽지 않은 알림만 조회 성공")
//    void testGetUnreadNotificationsByMemberId() {
//        notiService.createNotification(createTestNotification(m, "읽지 않은 알림1", "TYPE_A", false));
//        notiService.createNotification(createTestNotification(m, "읽은 알림1", "TYPE_B", true));
//        notiService.createNotification(createTestNotification(m, "읽지 않은 알림2", "TYPE_C", false));
//
//        List<Notification> result = notiService.getUnreadNotificationByMemberId(m.getMmemid());
//        System.out.println(result.toString());
//        assertThat(result).hasSize(2);
//        assertThat(result).extracting(Notification::getMnofContent)
//                          .containsExactlyInAnyOrder("읽지 않은 알림1", "읽지 않은 알림2");
//        assertThat(result).allMatch(notification -> !notification.isMnofReadYn());
//    }
//
//    @Test
//    @DisplayName("특정 알림을 읽음 상태로 변경 성공")
//    void testMarkNotificationAsReadSuccess() {
//        Notification notificationToMark = notiService.createNotification(createTestNotification(m, "읽음 처리할 알림", "ACTION", false));
//        assertThat(notificationToMark.isMnofReadYn()).isFalse();
//
//        Optional<Notification> updatedNotificationOptional = notiService.markNotificationAsRead(notificationToMark.getMnofId());
//        assertThat(updatedNotificationOptional).isPresent();
//        assertThat(updatedNotificationOptional.get().isMnofReadYn()).isTrue();
//
//        Optional<Notification> foundInDb = notiRepo.findById(notificationToMark.getMnofId());
//        assertThat(foundInDb).isPresent();
//        assertThat(foundInDb.get().isMnofReadYn()).isTrue();
//    }
//
//    @Test
//    @DisplayName("특정 알림을 읽음 상태로 변경 실패 - 알림이 존재하지 않을 때")
//    void testMarkNotificationAsReadNotFound() {
//        Optional<Notification> updatedNotificationOptional = notiService.markNotificationAsRead(999L);
//        assertThat(updatedNotificationOptional).isNotPresent();
//    }
//
//    @Test
//    @DisplayName("회원의 모든 알림을 읽음 상태로 변경 성공")
//    void testMarkAllNotificationsAsReadSuccess() {
//        notiService.createNotification(createTestNotification(m, "모두 읽음1", "TYPE_D", false));
//        notiService.createNotification(createTestNotification(m, "모두 읽음2", "TYPE_E", false));
//        notiService.createNotification(createTestNotification(m, "이미 읽음", "TYPE_F", true));
//
//        int updatedCount = notiService.markAllNotificationsAsRead(m.getMmemid());
//        assertThat(updatedCount).isEqualTo(2);
//
//        List<Notification> allNotifications = notiRepo.findByMember_Mmemid(m.getMmemid());
//        assertThat(allNotifications).allMatch(Notification::isMnofReadYn);
//    }
//
//    @Test
//    @DisplayName("알림 삭제 성공")
//    void testDeleteNotificationSuccess() {
//        Notification notificationToDelete = notiService.createNotification(createTestNotification(m, "삭제할 알림", "DELETE", false));
//        Long notificationId = notificationToDelete.getMnofId();
//        assertThat(notiRepo.findById(notificationId)).isPresent();
//
//        notiService.deleteNotification(notificationId);
//        assertThat(notiRepo.findById(notificationId)).isNotPresent();
//    }
//
//    // 테스트용 Notification 객체를 생성하는 헬퍼 메서드
//    private Notification createTestNotification(Member member, String content, String type, boolean readStatus) {
//        Notification notification = new Notification();
//        notification.setMember(member);
//        notification.setMnofContent(content);
//        notification.setMnofType(type);
//        notification.setMnofDt(LocalDateTime.now());
//        notification.setMnofReadYn(readStatus);
//        return notification;
//    }
//
//}
