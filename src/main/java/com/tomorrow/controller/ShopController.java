package com.tomorrow.controller;


import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.tomorrow.dto.CreateShopFormDto;
import com.tomorrow.dto.MemShopMappingDto;
import com.tomorrow.dto.NoticeDto;
import com.tomorrow.dto.NoticeLikeDto;
import com.tomorrow.dto.ShopDto;
import com.tomorrow.entity.Member;
import com.tomorrow.entity.Notice;
import com.tomorrow.entity.NoticeLike;
import com.tomorrow.entity.Shop;
import com.tomorrow.service.ShopService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ShopController {

	private final ShopService shopService;

	// GET매장공지폼
	@GetMapping(value = "/shop/info")
	public String shopInfo(Model model, Principal principal) {

		List<MemShopMappingDto> myShopList = shopService.getMyShop(principal.getName());

		model.addAttribute("myShopList", myShopList);
		model.addAttribute("noticeDto", new NoticeDto());
		return "shop/shopNoticeForm";
	}

	// GET매장 선택 시 공지내역가져옴
	@GetMapping(value = "/shop/info/{shopId}")
	public String shopGetNoti(@PathVariable("shopId") String shopId, Model model, Principal principal) {

		List<NoticeDto> notiList = shopService.getNoticeList(shopId);
		List<MemShopMappingDto> myShopList = shopService.getMyShop(principal.getName());
		NoticeDto noticeDto = new NoticeDto();
		ShopDto shopDto = new ShopDto();
		shopDto.setShopId(Long.parseLong(shopId));
		noticeDto.setShopDto(shopDto);

		model.addAttribute("notiList", notiList);
		model.addAttribute("myShopList", myShopList);
		model.addAttribute("noticeDto", noticeDto);
		return "shop/shopNoticeForm";
	}

	// POST매장공지 등록 시
	@PostMapping(value = "/shop/info")
	public String shopInfoUpdate(@Valid NoticeDto noticeDto, Model model, BindingResult bindingResult,
			Principal principal) {

		if (bindingResult.hasErrors()) {

			List<MemShopMappingDto> myShopList = shopService.getMyShop(principal.getName());

			model.addAttribute("myShopList", myShopList);
			return "shop/shopNoticeForm";
		}

		try {

			Member member = shopService.findMember(principal.getName());
			Long ShopId = noticeDto.getShopDto().getShopId();
			Notice notice = Notice.createNotice(noticeDto, member, shopService.findShop(ShopId));
			shopService.saveNotice(notice);

		} catch (Exception e) {

			model.addAttribute("errorMessage", "공지등록 중 에러가 발생했습니다.");
			List<MemShopMappingDto> myShopList = shopService.getMyShop(principal.getName());

			model.addAttribute("myShopList", myShopList);
			model.addAttribute("noticeDto", noticeDto);
			return "redirect:/shop/info/" + noticeDto.getShopDto().getShopId();
		}

		return "redirect:/shop/info/" + noticeDto.getShopDto().getShopId();
	}

//	// 좋아요 눌렀을때
//	@PostMapping(value = "/shop/info/like/{memberId}/like")
//	public ResponseEntity shopLikeInsert(@PathVariable("memberId") Long memberId, Long noticeId, Model model, Principal principal) {
//		
//		Notice notice = shopService.findNotice(noticeId);
//		Member member = shopService.findMember(principal.getName());
//		NoticeLike noticeLike = NoticeLike.createNoticeLike(member, notice);
//		shopService.saveNoticeLike(noticeLike);
//		
//		return new ResponseEntity(memberId, HttpStatus.OK);
//	}
//	
//	// 좋아요 또 눌렀을때
//	@DeleteMapping(value = "/shop/info/{notiLikeId}/likeDel")
//	public ResponseEntity shopLikeDelete(@PathVariable("notiLikeId") Long notiLikeId, Long noticeId, Model model, Principal principal) {
//		
//		NoticeLike noticeLike = shopService.findNoticeLike(notiLikeId);
//		
//		return new ResponseEntity(notiLikeId, HttpStatus.OK);
//	}

	@GetMapping(value = "/shop/info/update/{noticeId}")
	public @ResponseBody ResponseEntity updateNotice(@PathVariable("noticeId") Long noticeId, Principal principal) {

		Notice notice = shopService.findNotice(noticeId);

		return new ResponseEntity<Long>(noticeId, HttpStatus.OK);
	}

	@DeleteMapping(value = "/shop/notice/{noticeId}/delete")
	public @ResponseBody ResponseEntity deleteNotice(@PathVariable("noticeId") Long noticeId, Principal principal) {

		Notice notice = shopService.findNotice(noticeId);
		shopService.deleteNotice(notice);
		return new ResponseEntity<Long>(noticeId, HttpStatus.OK);
	}

	// GET근무일지폼
	@GetMapping(value = { "/shop/log", "/shop/log/{shop_id}" })
	public String shopLog(Model model, Principal principal) {

		// TODO 현재 로그인한 회원의 매장번호를 조회해서 매장코드로 업무내용 불러옴

		return "shop/workLogForm";
	}

	// POST근무일지폼
	@PostMapping(value = "/shop/log/{shop_id}")
	public String shopLogUpdate(Model model) {

		return "shop/workLogForm";
	}

	// 직원정보 - 수경 2
	@GetMapping(value = "/shop/employeeInfo")
	public String employeeInfo(Model model) {

		return "shop/employeeInfoForm";
	}
<<<<<<< HEAD
	//매장생성(shopCreateForm.html) 들어가기
	@GetMapping(value="/shop/shopCreate")
	public String createShopForm(Model model) {
		model.addAttribute("createShopFormDto",new CreateShopFormDto());
		return "shop/shopCreateForm";
	}
	// 매장생성(shopCreateForm.html) 진짜 생성
	@PostMapping(value = "/shop/shopCreate")
	public String createShop(@Valid CreateShopFormDto createShopFormDto, BindingResult bindingResult, 
			Model model, @RequestParam("createShopImgFile") List<MultipartFile> createShopImgFileList) {
		
		if(bindingResult.hasErrors()) {
			return "shop/shopCreateForm";
		}
		if(createShopImgFileList.get(0).isEmpty() && createShopFormDto.getId() == null) {
			model.addAttribute("errorMessage","첫 번째 상품 이미지는 필수 입력 값 입니다.");
			return "shop/shopCreateForm";
		}
		
		try {
			shopService.saveShop(createShopFormDto, createShopImgFileList);
		}catch(Exception e) {
			model.addAttribute("errorMessage","상품 등록 중 에러가 발생했습니다.");
			return "shop/shopCreateForm";
		}
		
		return "redirect:/";
		
=======

	// 매장생성
	@GetMapping(value = "/shop/shopCreate")
	public String createShop(Model model) {
		return "shop/shopCreateForm";

>>>>>>> 99ccd3094291e36f5a06b9ad91c08fac7aff81be
	}

	// 출퇴근조회
	@GetMapping(value = "/shop/commuteList")
	public String commuteListForAdmin() {
		return "shop/commuteListForAdmin";
	}

	// 급여관리
	@GetMapping(value = "/shop/payroll")
	public String payrollManagement() {
		return "shop/payrollManagement";
	}

	/*
	 * TODO 1. 매장 정보 폼 가져오기 (SELECT) 2. 매장 정보 가져오기 3. 매장 정보 수정하기 4. '취소'버튼 누르면
	 * 마이페이지로 가게 하기
	 */

	// 매장정보폼 불러오기 - 수경 1
	@GetMapping(value = "/shop/shopInfo")
	public String shopInfoForAdmin(Model model, Principal principal) {
		List<MemShopMappingDto> myShopList = shopService.getMyShop(principal.getName());

		model.addAttribute("myShopList", myShopList);
		model.addAttribute("shopDto", new ShopDto());

		return "shop/shopInfo";
	}

	// 매장 선택 시 매장 정보 내역 가져옴
	@GetMapping(value = "/shop/shopInfo/{shopId}")
	public String getShopInfoForAdmin(@PathVariable("shopId") Long shopId, Model model, Principal principal) {
		Shop shop = shopService.findShop(shopId);
		ShopDto shopDto = shopService.getShop(shop);

		List<MemShopMappingDto> myShopList = shopService.getMyShop(principal.getName());

		model.addAttribute("myShopList", myShopList);
		model.addAttribute("shopDto", shopDto);

		return "shop/shopInfo";
	}

}
