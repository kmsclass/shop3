
package controller;


import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import logic.Item;
import logic.Sale;
import logic.SaleItem;
import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("user")
public class UserController {
	@Autowired
	private ShopService service;
	
	@GetMapping("*")
//	public ModelAndView user() {
//		ModelAndView mav = new ModelAndView();
//		mav.addObject(new User());
//		return mav;
//	}
	public String user(Model model) {
		model.addAttribute(new User());
		return null;
	}
	@RequestMapping("main") //login되어야 하는 메서드이름을 loginCheckXXX로 지정
	public String loginCheckmain(HttpSession session) {
		return null;
	}
	
	@PostMapping("userEntry")
	public ModelAndView userAdd(@Valid User user, BindingResult bindingResult) {
		ModelAndView mav = new ModelAndView();
		if(bindingResult.hasErrors()) {
			mav.getModel().putAll(bindingResult.getModel());
			bindingResult.reject("error.input.user");
			return mav;
		}
		//db에 user 정보 useraccount 테이블에 저장.
		try {
			service.insert(user);
			mav.addObject("user",user);
		}catch(DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("error.duplicate.user");
			mav.getModel().putAll(bindingResult.getModel());
			return mav;
		}
		mav.setViewName("redirect:login.shop");
		return mav;
	}
	@PostMapping("login")
	public ModelAndView login(@Valid User user, BindingResult bresult, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		if(bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			bresult.reject("error.input.user");
			return mav;
		}
		//userid 맞는 User 객체 조회.
		//아이디가 없는 경우 아이디없음 화면에 출력
		//비밀번호 틀린경우 비밀번호 오류 화면에 출력
		//정상 : session.setAttribute("loginUser",User객체)
		//      main.shop으로 페이지 이동.
		try {
			User dbuser = service.selectUserOne(user.getUserid());
			//user.getPassword() : 입력비밀번호
			//dbuser.getPassword() : 등록된 비밀번호
			if(user.getPassword().equals(dbuser.getPassword())) { //정상 로그인
				session.setAttribute("loginUser", dbuser);
				mav.setViewName("redirect:main.shop");
			}else {   //비밀번호 오류
				bresult.reject("error.login.password");
				mav.getModel().putAll(bresult.getModel());
			}
		} catch(EmptyResultDataAccessException e) { //해당 아이디 없는 경우 예외발생
			bresult.reject("error.login.id");
			mav.getModel().putAll(bresult.getModel());
		}
		return mav;
	}
	@RequestMapping("logout")
	public String loginChecklogout(HttpSession session) {
		session.invalidate(); //loginUser속성, CART 속성 제거. 새로운 session 객체변경
		return "redirect:login.shop";
	}
	/*
	 * AOP 설정하기
	 * 1. UserController의 idCheck로 시작하는 메서드 +
	 *    매개변수가 id, session 인 경우를 pointcut으로 설정
	 * 2. 로그인 안된경우 : 로그인하세요 메세지 출력.  login.shop 페이지 이동
	 *    admin이 아니고, 다른 아이디 정보 조회시 :
	 *                  본인만 조회가능합니다. main.shop페이지 이동    
	 */
	@RequestMapping("mypage")
	public ModelAndView idCheckmypage(String id, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		//db에서 userid에 맞는 User 정보 한개리턴 
		User user = service.selectUserOne(id); 
		//userid가 주문한 주문 정보 목록 리턴
		List<Sale> salelist = service.salelist(id); 
		for(Sale sa : salelist) {
			//주문번호에 주문한 주문상품 목록 리턴
			List<SaleItem> saleitemlist = service.saleItemList(sa.getSaleid());
			for(SaleItem si : saleitemlist) {
				//주문상품id에 해당하는 Item 객체를 리턴
				Item item = service.getItem(Integer.parseInt(si.getItemid()));
				si.setItem(item);
			}
			sa.setItemList(saleitemlist); //한개의 주문에 해당하는 주문상품 목록 추가
		}
		mav.addObject("user",user); //회원정보
		mav.addObject("salelist", salelist); //회원의 주문 정보
		return mav;
	}
	
	@GetMapping("update")
	public ModelAndView idCheckupdate(String id, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		User user = service.selectUserOne(id);
		mav.addObject("user",user);
		return mav;
	}
	
	@PostMapping("update")
	public ModelAndView update(@Valid User user, BindingResult bindResult) {
		ModelAndView mav = new ModelAndView();
		if(bindResult.hasErrors()) {
			mav.getModel().putAll(bindResult.getModel());
			bindResult.reject("error.input.user");
			return mav;
		}
		//비밀번호 검증. 비밀번호가 일치하는 경우 useraccount 테이블 수정
		//로그인한 사용자의 비밀번호와, 입력된 비밀번호가 일치 검증
		//비밀번호가 일치하면 user 정보로 db 수정.
		// 일치하지 않으면 : error.login.password 코드를 입력하여 update.jsp 페이지에
		//                글로벌 오류 메시지 출력. 
		
		return mav;
	}
	
}

