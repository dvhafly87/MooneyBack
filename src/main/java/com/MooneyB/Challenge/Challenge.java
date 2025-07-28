package com.MooneyB.Challenge; // 적절한 패키지명으로 변경해주세요.

import com.MooneyB.Member.Member; // Member 엔티티의 패키지 경로를 정확히 입력해주세요.
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "MOONEY_CHALLENGE")
@SequenceGenerator(
        name = "CHALLENGE_SEQ_GENERATOR", // 시퀀스 제너레이터 이름
        sequenceName = "MOONEY_CHALLENGE_SEQ", // DB 시퀀스 이름
        initialValue = 1, // 시퀀스 시작 값 (DB DDL과 일치)
        allocationSize = 1 // 시퀀스 할당 크기 (DB DDL과 일치)
)
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHALLENGE_SEQ_GENERATOR")
    @Column(name = "MCHL_ID")
    private Long mchlId;

    @Column(name = "MCHL_NAME", nullable = false, length = 100)
    private String mchlName; 

    @Column(name = "MCHL_TARGET_AMOUNT", nullable = false)
    private Long mchlTargetAmount; 

    @Column(name = "MCHL_START_DATE", nullable = false)
    private LocalDate mchlStartDate; 

    @Column(name = "MCHL_END_DATE", nullable = false)
    private LocalDate mchlEndDate; 

    @Column(name = "MCHL_REWARD", nullable = false)
    private Long mchlReward = 0L; 

    @Column(name = "MCHL_CONTENTS", length = 3000)
    private String mchlContents; 

    @ManyToOne
    @JoinColumn(name = "MCHL_MMEM_ID", referencedColumnName = "MMEM_ID", nullable = false)
    @JsonIgnore
    private Member member; 
}