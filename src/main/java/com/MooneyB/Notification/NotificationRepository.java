package com.MooneyB.Notification;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends CrudRepository<Notification, Long>{
	
	public List<Notification> findByMember_Mmemid(String memberId);
	
	public List<Notification> findByMember_MmemidAndMnofReadYnFalse(String memberId);
	
	@Modifying 
    @Query("UPDATE Notification n SET n.mnofReadYn = true WHERE n.member.mmemid = :memberId AND n.mnofReadYn = false")
    int markAllAsMnofReadYnByMemberId(@Param("memberId") String memberId);

	
	@Modifying 
    @Query("UPDATE Notification n SET n.mnofReadYn = true WHERE n.mnofId = :notificationId")
    int markAsMnofReadYnById(@Param("notificationId") Long notificationId);
}
