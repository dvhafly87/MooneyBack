package com.MooneyB.Notification;


import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.MooneyB.Member.Member;
import com.MooneyB.Member.MemberRepo;
import com.MooneyB.common.exceptions.MemberNotFoundException;



@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepo memberRepository;

    public NotificationService(NotificationRepository notificationRepository, MemberRepo memberRepository) {
        this.notificationRepository = notificationRepository;
        this.memberRepository = memberRepository;
    }

	
	public List<Notification> getNotification(String memberId){
		return (List<Notification>) notificationRepository.findByMember_Mmemid(memberId);
	}
	
	public Notification createNotification(Notification notification, String memberId) {
		Member m = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + memberId));
		notification.setMember(m);
		return notificationRepository.save(notification);
	}
	
	@Transactional(readOnly = true)
	public Optional<Notification> getNotificationById(Long notificationId){
		return notificationRepository.findById(notificationId);
	}
	
	@Transactional(readOnly = true)
	public List<Notification> getNorificationByMemberId(String memberId){
		return notificationRepository.findByMember_Mmemid(memberId);
	}
	
	@Transactional(readOnly = true)
	public List<Notification> getUnreadNotificationByMemberId(String memberId){
		return notificationRepository.findByMember_MmemidAndMnofReadYnFalse(memberId);
	}
	
	public Optional<Notification> markNotificationAsRead(Long notificationId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if (notificationOptional.isPresent()) {
            Notification notification = notificationOptional.get();
            notification.setMnofReadYn(true); // 읽음 상태로 변경
            return Optional.of(notificationRepository.save(notification)); // 변경사항 저장
        }
        return Optional.empty(); // 해당 알림을 찾을 수 없음
    }
	
	@Transactional 
	public int markAllNotificationsAsRead(String memberId) {
        return notificationRepository.markAllAsMnofReadYnByMemberId(memberId);
    }
	
	public void deleteNotification(Long notificationId) {
		notificationRepository.deleteById(notificationId);
    }
}
