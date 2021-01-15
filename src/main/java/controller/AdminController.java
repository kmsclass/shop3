package controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import exception.LoginException;
import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("admin")
public class AdminController {
	
	@Autowired
	private ShopService service;
	
	@RequestMapping("list")
	public ModelAndView adminCheckList(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		List<User> userList = service.getUserList();
		mav.addObject("list", userList);
		return mav;
	}
	@RequestMapping("mailForm")
	public ModelAndView mailform (String[] idchks, HttpSession session) {
		ModelAndView mav = new ModelAndView("admin/mail");
		if(idchks == null || idchks.length==0) {
			throw new LoginException
			("메일을 보낼 대상자를 선택하세요","list.shop");
		}
		List<User> list = service.getUserList(idchks);
		//select * from useraccount where userid in ('test1','test2')
		mav.addObject("list",list);
		return mav;
	}	
}
