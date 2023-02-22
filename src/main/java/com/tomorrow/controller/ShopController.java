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
import org.springframework.web.bind.annotation.ResponseBody;

import com.tomorrow.dto.MemShopMappingDto;
import com.tomorrow.dto.NoticeDto;
import com.tomorrow.dto.NoticeLikeDto;
import com.tomorrow.dto.ShopDto;
import com.tomorrow.entity.Member;
import com.tomorrow.entity.Notice;
import com.tomorrow.entity.NoticeLike;
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
		model.addAttribute("updateNoticeDto", new NoticeDto());
		return "shop/shopNoticeForm";
	}
	
	// POST매장공지 등록 시
	@PostMapping(value = "/shop/info")
	public String shopInfoUpdate(@Valid NoticeDto noticeDto, Model model, BindingResult bindingResult, Principal principal) {
		
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
	
	// 공지 수정 눌렀을때
	@PostMapping(value = "/shop/info/{noticeId}/update")
	public String updateNoticePage(@PathVariable("noticeId") Long noticeId, @Valid NoticeDto noticeDto, Model model, BindingResult bindingResult, Principal principal) {
		
		if (bindingResult.hasErrors()) {
			
			List<MemShopMappingDto> myShopList = shopService.getMyShop(principal.getName());
			
			model.addAttribute("myShopList", myShopList);
			return "shop/shopNoticeForm";
		}
		
		try {
			
			Notice notice = shopService.findNotice(noticeId);
			notice.setNoticeCont(noticeDto.getNoticeCont());
			
			
		} catch (Exception e) {
			
			model.addAttribute("errorMessage", "공지등록 중 에러가 발생했습니다.");
			List<MemShopMappingDto> myShopList = shopService.getMyShop(principal.getName());
			
			model.addAttribute("myShopList", myShopList);
			model.addAttribute("noticeDto", noticeDto);
			return "redirect:/shop/info/" + noticeDto.getShopDto().getShopId();
		}
		
		
		return "redirect:/shop/info/" + noticeDto.getShopDto().getShopId();
	}
	
	// 공지 삭제 눌렀을때
	@DeleteMapping(value = "/shop/notice/{noticeId}/delete")
	public @ResponseBody ResponseEntity deleteNotice(@PathVariable("noticeId") Long noticeId, Principal principal) {
		
		Notice notice = shopService.findNotice(noticeId);
		shopService.deleteNotice(notice);
		return new ResponseEntity<Long>(noticeId, HttpStatus.OK);
	}

	// GET근무일지폼
	@GetMapping(value = {"/shop/log", "/shop/log/{shop_id}"})
	public String shopLog(Model model, Principal principal) {
		
		// TODO 현재 로그인한 회원의 매장번호를 조회해서 매장코드로 업무내용 불러옴
		
		return "shop/workLogForm";
	}
	
	// POST근무일지폼
	@PostMapping(value = "/shop/log/{shop_id}")
	public String shopLogUpdate(Model model) {
		
		return "shop/workLogForm";
	}
	
	// 직원정보(employeeInfoForm.html - 수경)
	@GetMapping(value = "/shop/employeeInfo")
	public String employeeInfo(Model model) {

		return "shop/employeeInfoForm";
	}

	// 매장생성(shopCreateForm.html)
	@GetMapping(value = "/shop/shopCreate")
	public String createShop(Model model) {
		return "shop/shopCreateForm";
		
	}
	
	// 출퇴근조회(commuteListForAdmin.html - 수경)
	@GetMapping(value = "/shop/commuteList")
	public String commuteListForAdmin() {
		return "shop/commuteListForAdmin";
	}
	
	// 급여관리(payrollManagement.html - 수경)
	@GetMapping(value = "/shop/payroll")
	public String payrollManagement() {
		return "shop/payrollManagement";
	}
	
	// 매장정보(shopInfo.html - 수경)
	@GetMapping(value = "/shop/shopInfo")
	public String shopInfo() {
		return "shop/shopInfo";
	}
}
