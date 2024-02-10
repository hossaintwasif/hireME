package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;

@Controller
@RequestMapping("/user")
public class donationcontroller {

	@Autowired
	private ContactRepository contactRepository;
	
	
	@Autowired
	private UserRepository repository;
	
	
}
