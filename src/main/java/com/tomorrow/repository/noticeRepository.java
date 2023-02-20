package com.tomorrow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tomorrow.entity.Notice;

public interface noticeRepository extends JpaRepository<Notice, Long> {

	List<Notice> findByManagerId(Long id);

	List<Notice> findByShopId(Long shopId);
}
