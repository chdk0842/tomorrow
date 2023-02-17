package com.tomorrow.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.tomorrow.constant.Role;
import com.tomorrow.dto.MemberFormDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "manager")
@Getter
@Setter
@ToString
public class Manager {
	
	@Id
	@Column(name = "manager_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;					// 회원 식별자
	
	@Column(length = 25, nullable = false)
	private String userId;				// 유저 회원가입/로그인 시 필요한 id
	
	@Column(length = 20, nullable = false)
	private String userNm;				// 유저 회원가입 시 필요한 이름
	
	@Column(nullable = false)
	private String password;			// 유저 회원가입/로그인 시 필요한 비밀번호
	
	@Column(length = 13, nullable = false)
	private String pNum;				// 유저 회원가입 시 필요한 전화번호
	
	@Enumerated(EnumType.STRING)
	private Role role;					// 유저 권한등급 > 기본 회원가입 시 무조건 USER 등급
	
	private String userProfile;			// 유저 회원가입 시 선택적으로 입력하는 프로필 이미지
	
	public static Manager createManager(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
		
		Manager manager = new Manager();
		
		manager.setUserNm(memberFormDto.getUserNm());
		manager.setUserId(memberFormDto.getUserId());
		manager.setPNum(memberFormDto.getPNum());
		
		String password = passwordEncoder.encode(memberFormDto.getPassword()); //비밀번호 암호화
		manager.setPassword(password);
		
		manager.setRole(Role.ADMIN);
		
		return manager;
	}
}
