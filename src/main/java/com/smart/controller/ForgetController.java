package com.smart.controller;

import java.util.Random;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;


@Controller
public class ForgetController {
	
	
	
	@Autowired
	private UserRepository userRepository;

	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService; 

	@Autowired
	private PasswordEncoder bCrypt;
	
	
	//Email id form open handler
	@RequestMapping("/forget")
	public String openEmailForm() {
		
		return "forget_email";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,Model m,HttpSession session) {
		System.out.println("Email "+email);
		
		//Generate otp for 5 digit
		int otp = random.nextInt(99999);
		System.out.println("OTP "+otp);
		
		//Write code for send otp to email
		String subject="OTP verification";
		String message = "<h1>Welcome to the HireMe, One-stop solution for your dream job</h1>"
		        + "<br>"
		        + "<div style='border: 1px solid #e2e2e2; padding:1px'>"
		        + "<h3>OTP="
		        + "<b>" + otp + "</b>"
		        + "</h3>"
		        + "</div>";

		
		
		String to=email;
		boolean flag = this.emailService.sendEmail(message, subject, to);
		
		if(flag=true) {
			session.setAttribute("MyOTP", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else {
			m.addAttribute("message","OTP doesn't match....!! Please check your Gmail");
			 return "forget_email";
		}
		
		
		
	}
	
	//Verifying otp
	@PostMapping("/verify_otp")
	public String verifyotp(@RequestParam("otp") Integer otp,HttpSession session,Model m) {
		Integer myotp = (int) session.getAttribute("MyOTP");
		String email = (String) session.getAttribute("email");
		System.out.println("myot "+myotp);
		System.out.println("otp "+otp);
		
		if(myotp.equals(otp)) {
			
			User user = this.userRepository.getUserByUserName(email);
			if(user==null) {
				//Send error message
				m.addAttribute("message", new Message("User doesn't exist with this email id..!!", "danger"));
		        
				return "forget_email";
			}
			else {
				//Send change password form
				
			}
			
			return "password_change_form";
		}else {
		m.addAttribute("message", new Message("OTP does not match", "danger"));
        
		return "verify_otp";
		
		}
	}
	
	
	//Change password form
	@PostMapping("/Change-Password")
	public String change_password(@RequestParam("newpassword") String newpassword,@RequestParam("confirmpassword") String confirmpassword,HttpSession session,Model m) {
		
		String email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCrypt.encode(newpassword));
		
		if(newpassword.equals(confirmpassword)) {
			
			this.userRepository.save(user);
			m.addAttribute("message", new Message("Password changed successfully", "success"));
			return "login";
		}else {
			m.addAttribute("message", new Message("Password doesn't match with each other...!!", "danger"));
	        
			return "password_change_form";
		}
		
	}
	
			
}
