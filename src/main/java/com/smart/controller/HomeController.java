package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private PasswordEncoder password;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String home(Model m) {
		m.addAttribute("title", "Home-Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "About-Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model m, HttpSession session,@ModelAttribute User user) {
		m.addAttribute("title", "SignUp-Smart Contact Manager");
		m.addAttribute("user", new User());
		if (session.getAttribute("message") != null) {
			m.addAttribute("message", session.getAttribute("message"));
			session.removeAttribute("message");
		}
		// Set default image URL if the user did not provide a profile picture
		if (user.getImageurl() == null || user.getImageurl().isEmpty()) {
		    user.setImageurl("default1.png");
		    System.out.println("Setting default image URL: " + user.getImageurl());
		} else {
		    // Extract the file name from the full path
		    String fileName = user.getImageurl().substring(user.getImageurl().lastIndexOf("/") + 1);
		    
		    // Set only the file name without the path
		    user.setImageurl(fileName);
		    System.out.println("Setting image URL with file name: " + user.getImageurl());
		}

		return "signup";
	}

	// Handling for Registering user
	@PostMapping("/do_register")
	public String register(@Valid @ModelAttribute("user") User users,BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			 HttpSession session) {
		try {
			if (!agreement) {
				System.out.println("User has not agreed the terms and conditions");
				throw new Exception("User has not agreed the terms and conditions");
			}

			if (result1.hasErrors()) {
				System.out.println("Result "+result1);
                model.addAttribute("user",users);
				return "signup";
			}

			users.setRole("ROLE_USER");
			users.setEnable(true);
			users.setPassword(password.encode(users.getPassword()));
			
			System.out.println("Agreement " + agreement);
			System.out.println("User" + users);
			User result = this.userRepository.save(users);
			model.addAttribute("User", new User());
			session.setAttribute("message", new Message("Successfully registered !!", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user" + users);
			session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}
	
	@GetMapping("/signin")
	public String customlogin(Model m) {
		m.addAttribute("title", "Login Contact Manager");
		return "login";
	}
}
