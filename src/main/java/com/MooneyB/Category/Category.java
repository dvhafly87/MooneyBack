package com.MooneyB.Category; // 적절한 패키지명으로 변경해주세요.

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
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; 

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Entity
@Table(name = "MOONEY_CATEGORY")
@SequenceGenerator(
        name = "CATEGORY_SEQ_GENERATOR", // 시퀀스 제너레이터 이름
        sequenceName = "MOONEY_CATEGORY_SEQ", // DB 시퀀스 이름
        initialValue = 1, // 시퀀스 시작 값 (DB DDL과 일치)
        allocationSize = 1 // 시퀀스 할당 크기 (DB DDL과 일치)
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY_SEQ_GENERATOR")
    @Column(name = "MCAT_ID")
    private Long mcatId;

    @Column(name = "MCAT_NAME", nullable = false, length = 30)
    private String mcatName; 

    @Column(name = "MCAT_COLOR", nullable = false, length = 10)
    private String mcatColor; 

    @ManyToOne
    @JoinColumn(name = "MCAT_MMEM_ID", referencedColumnName = "MMEM_ID", nullable = false)
    @JsonIgnore
    private Member member; 
}