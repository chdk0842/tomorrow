package com.tomorrow.controller;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.tomorrow.dto.HireDto;
import com.tomorrow.dto.MemShopMappingDto;
import com.tomorrow.dto.MemberFormDto;
import com.tomorrow.dto.PayListDto;
import com.tomorrow.dto.ShopDto;
import com.tomorrow.entity.Hire;
import com.tomorrow.entity.Member;
import com.tomorrow.entity.Shop;
import com.tomorrow.service.JobService;
import com.tomorrow.service.MemberService;
import com.tomorrow.service.ShopInfoService;
import com.tomorrow.service.ShopService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class JobController {
	private final ShopService shopService;
	private final MemberService memberService;
	private final ShopInfoService shopInfoService;
	private final JobService jobService;

	// 사이드바 프로필정보 가져옴
	public Model getSideImg(Model model, Principal principal) {
		MemberFormDto memberFormDto = memberService.getIdImgUrl(principal.getName());
		return model.addAttribute("member", memberFormDto);
	}

	// 구인공고 등록 페이지
	@GetMapping(value = "/admin/job/new")
	public String jobNew(Model model, Principal principal) {
		List<MemShopMappingDto> myShopList = jobService.getMyShop(principal.getName());

		getSideImg(model, principal);

		model.addAttribute("myShopList", myShopList);
		model.addAttribute("shopDto", new ShopDto());
		model.addAttribute("hireDto", new HireDto());

		return "job/jobNew";
	}

	//TODO 셀렉트 매장 정보 불러오기 에러,공고 등록 유효성검사 완료
	// 구인공고 등록 매장 정보 불러오기
	@GetMapping(value = "/admin/job/new/{shopId}")
	public String getShopJobNew(@PathVariable("shopId") Long shopId, Model model, Principal principal) {
		
		getSideImg(model, principal);
		
		List<MemShopMappingDto> myShopList = jobService.getMyShop(principal.getName());

        Shop shop = shopInfoService.findShop(shopId);
        ShopDto shopDto = jobService.getShop(shop);
		
        HireDto hireDto = new HireDto();
        hireDto.setShopDto(shopDto);
        
		model.addAttribute("myShopList", myShopList);
	    model.addAttribute("shopDto", shopDto);
	    model.addAttribute("hireDto", hireDto);
		

		return "job/jobNew";
	}

	// 구인공고 등록하기
    @PostMapping(value = "/admin/job/new")
    public String jobHireNew(@Valid HireDto hireDto, BindingResult bindingResult, Model model, Principal principal) {
	
    	if (bindingResult.hasErrors()) {
    		
    		List<MemShopMappingDto> myShopList = jobService.getMyShop(principal.getName());
    		getSideImg(model, principal);
    		model.addAttribute("myShopList", myShopList); 
    		model.addAttribute("hireDto", hireDto);
    		return "job/jobNew";
    	}
    	
    	try {
    		getSideImg(model, principal);
         	Shop shop = shopService.findShop(hireDto.getShopDto().getShopId());
    		Member member = shopService.findMember(principal.getName());
    		Hire hire = Hire.createHire(hireDto, member, shop);
			jobService.saveHire(hire);
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "구인 등록 중 에러가 발생했습니다.");
			return "job/jobNew";
		}
    	
    	return "job/jobOpeningList";
 
    	
    }

	// 구인공고뷰 페이지
	@GetMapping(value = "/job/view")
	public String jobView(Model model, Principal principal) {
		List<MemShopMappingDto> myShopList = shopService.getMyShop(principal.getName());

		getSideImg(model, principal);

		model.addAttribute("myShopList", myShopList);

		return "job/jobView";
	}

	// 알바신청리스트 페이지
	@GetMapping(value = "admin/job/list")
	public String jobList(Model model, Principal principal) {
		List<MemShopMappingDto> myShopList = shopService.getMyShop(principal.getName());

		getSideImg(model, principal);

		model.addAttribute("myShopList", myShopList);
		model.addAttribute("payListDto", new PayListDto());

		return "job/jobList";
	}

	// 채용등록리스트 페이지
	@GetMapping(value = "/admin/job/openingList")
	public String jobOpeningList(Model model, Principal principal) {
		List<MemShopMappingDto> myShopList = shopService.getMyShop(principal.getName());

		getSideImg(model, principal);
		System.out.println(myShopList.toString());

		model.addAttribute("myShopList", myShopList);
//		model.addAttribute("payListDto", new PayListDto());
		return "job/jobOpeningList";
	}

}
