package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.*;
import com.razorpay.*;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Order;
import com.razorpay.Payment;
//import com.razorpay.PaymentOrderRequest;
import com.razorpay.Refund;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;


import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
//import org.springframework.data.jpa.repository.query.EqlParser.New_valueContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;




@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private MyOrderRepository myOrderRepository;
	
	//Method for adding common data eg: User name
	@ModelAttribute
	public void addCommondata(Model m,Principal principal) {
		String name = principal.getName();
		System.out.println("UserName "+name);
		User username = repository.getUserByUserName(name);
		System.out.println("User "+username);
		m.addAttribute("user",username);
	}
	
	// Dashbord controller
	@GetMapping("/dashbord/{page}")
	public String dashbord(@PathVariable("page") Integer page, Model m,Principal principal) {
		m.addAttribute("title","Dashbord");
		Pageable pageable = PageRequest.of(page, 10);
		Page<Contact> data = this.contactRepository.findAll(pageable);
		long totalContacts = this.contactRepository.count();
		// Get the content as a list  use chatgpt
	    List<Contact> reversedList = new ArrayList<>(data.getContent());
	    Collections.reverse(reversedList);

	    // Create a new Page with the reversed list
	    Page<Contact> reversedPage = new PageImpl<>(reversedList, pageable, totalContacts);
	    
		m.addAttribute("contacts",reversedPage);
		m.addAttribute("currentpage",page);
		m.addAttribute("totalpage",data.getTotalPages());
		return "Normal/dashbord";
	}
	
	
	//Add Contact Controller
	@GetMapping("/addContact")
	public String addContactStringForm(Model m) {
		m.addAttribute("title","Add Contact");
		m.addAttribute("contact",new Contact());
		return "Normal/AddContact";
	}
	
	
	//Processing Add contact form
	@PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 Principal principal, Model model) throws IOException {
        try {
            String name = principal.getName();
            User user = this.repository.getUserByUserName(name);

            // Processing and uploading file
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get("src/main/resources/static/image/", fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(fileName);  // Set the file name to the "image" field
            }
            else {
            	 System.out.println("File is empty");
            	 contact.setImage("contact.png");
            }

            contact.setUser(user);
            user.getContacts().add(contact);
            this.repository.save(user);
            System.out.println("Contact Data " + contact);
            System.out.println("Contact added to the database");

            // Success validation for add contact
            model.addAttribute("message", new Message("Your contact has been successfully added...!!", "success"));
        } catch (Exception e) {
            // Handle exceptions appropriately
            System.out.println("ERROR " + e.getMessage());
            e.printStackTrace();

            // Error validation for add contact
            model.addAttribute("message", new Message("Something went wrong!!..Try Again", "danger"));
        }
        return "Normal/AddContact";
    }
	
	
	//Show Contacts handler
	//Per page = 5 Contacts
	//Current page = 0 {page}
	@GetMapping("/show-contact/{page}")
	public String showcontact(@PathVariable("page") Integer page ,Model m,Principal principal) {
		m.addAttribute("title","View Job Posts");
		String userEmail = principal.getName();
		User username = this.repository.getUserByUserName(userEmail);
		
		//Current page : page
		//Contact Per page : 5
		Pageable pageable = PageRequest.of(page, 8);
		Page<Contact> contacts = this.contactRepository.findContactsByUserId(username.getId(),pageable);
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentpage",page);
		m.addAttribute("totalpage",contacts.getTotalPages());
		return "Normal/showContact";
	}
	
	
	//Show perticular contact details
	@GetMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") @NonNull Integer cid , Model m,Principal principal) {
		System.out.println("CID "+cid);
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		String Username = principal.getName();
		User user = this.repository.getUserByUserName(Username);
		
		if(user.getId()==contact.getUser().getId()) {
			m.addAttribute("contact",contact);
			m.addAttribute("title",contact.getCompany());
		}
		
		return "Normal/contact_detail";
	}
	
	
	//Delete contact
	@GetMapping("/delete/{cid}")
	public String deletecontact(@PathVariable("cid") Integer cid,Model m,HttpSession session ,Principal principal) {
		Optional<Contact> contactoptional = this.contactRepository.findById(cid);
		Contact contact = contactoptional.get();
		String username = principal.getName();
		User user = this.repository.getUserByUserName(username);
		
		if(user.getId()==contact.getUser().getId()) {
			String filename=contact.getImage();
			
			//Delete the image
			if(!filename.equals("contact.png")) {
				Path path = Paths.get("src/main/resources/static/image/", filename);
				try {
					Files.delete(path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		this.contactRepository.delete(contact);
		}
		
		return "redirect:/user/show-contact/0";
	}
	
	
	//Update contact
	@GetMapping("/update-contact/{cid}")
	public String updateform(@PathVariable("cid") Integer cid,Model m,Principal principal) {
		m.addAttribute("title","Update Contact");
		String username = principal.getName();
		User user = this.repository.getUserByUserName(username);
		  Optional<Contact> contactoptional = this.contactRepository.findById(cid);
		 Contact contact = contactoptional.get();
		
		if(user.getId()==contact.getUser().getId()) {
		m.addAttribute("contact",contact);
		}
		
		return "Normal/update_form";
	}
	
	
	//Update contact in database
	@PostMapping("/process-update")
	public String updatehandler(@ModelAttribute Contact contact,@RequestParam("imageFile") MultipartFile imageFile,Model m,RedirectAttributes redirectAttributes,HttpSession session,Principal principal ) {
		System.out.println("contact name "+contact.getCompany());
		System.out.println("Contact id "+contact.getCid());
		
		try {
			//old contact details
			Contact olddetails = this.contactRepository.findById(contact.getCid()).get();
			
			//Image handle(Delete & update)
			if(!imageFile.isEmpty()) {
				
				//Delete old photo
				// Delete old photo
				String oldfileName = olddetails.getImage(); // Use the existing image file name
				Path oldfilePath = Paths.get("src/main/resources/static/image/", oldfileName);
				Files.delete(oldfilePath);

				
				//Update new photo
				String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get("src/main/resources/static/image/", fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(fileName);
			}else {
				//if image is not present then set an new image
				contact.setImage(olddetails.getImage());
			}
			User user = this.repository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			//session.setAttribute("message", new Message("Your Job Description is updated...!", "success"));
			redirectAttributes.addFlashAttribute("message", new Message("Your Job Description is updated...!", "success"));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "redirect:/user/"+contact.getCid()+"/contact";
			
	}
	
	
	        //Profile update
			@GetMapping("/profile")
			public String yourprofile(Model m) {
			
				m.addAttribute("title","Profile Page");
				return "Normal/profilePage";
			}
			
			
		//Setting 
		@GetMapping("/setting")
		public String setting(Model m) {
			m.addAttribute("title","User Settings");
			return "Normal/setting";
		}
		
		
		//change pasword
		@PostMapping("/change-password")
		public String changepassword(@RequestParam("oldpassword") String oldpassword,@RequestParam("newpassword") String newpassword,@RequestParam("confirmpassword") String confirmpassword,Principal principal,Model model) {
			System.out.println("Old Password: "+oldpassword);
			System.out.println("New Password: "+newpassword);
			System.out.println("Confirm Password: "+confirmpassword);
			String username = principal.getName();
			User userByUserName = this.repository.getUserByUserName(username);
			System.out.println(userByUserName.getPassword());
			
			if(this.bCryptPasswordEncoder.matches(oldpassword, userByUserName.getPassword()) && newpassword.equals(confirmpassword)) {
				userByUserName.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
				this.repository.save(userByUserName);
	            model.addAttribute("message", new Message("Password updated successfully", "success"));

			}else {
	            model.addAttribute("message", new Message("Password doesn't match", "danger"));
                return "Normal/setting";
			}
			return "login";
		} 
		
		
		
		
		//Donation
		@GetMapping("/donation")
		public String donation(Model m) {
			m.addAttribute("title","Donation");
			return "Normal/donation";
		}
		
		//Payment order request handler
		@PostMapping("/create_order")
		@ResponseBody
		public String createOrder(@RequestBody Map<String, Object> data,Principal principal) throws Exception {
			System.out.println(data);
			int amount=Integer.parseInt(data.get("amount").toString());
			var client=new RazorpayClient("rzp_test_ditYT5Sj8tEIwg", "6Erfh2TdIauWsCdKWu688s7Y");
			
			JSONObject options = new JSONObject();
		    options.put("amount", amount*100); // Note: The amount should be in paise.
		    options.put("currency", "INR");
		    options.put("receipt", "txn_123456");
		    Order order = client.orders.create(options);
		    System.out.println(order);
		    
		    //Save order in database
		    MyOrder myOrder=new MyOrder();
		    myOrder.setAmount(order.get("amount").toString());
			myOrder.setOrderID(order.get("id"));
			myOrder.setPaymentID(null);
			myOrder.setStatus("created");
			myOrder.setUser(this.repository.getUserByUserName(principal.getName()));
			myOrder.setReceipt(order.get("receipt"));
			
			this.myOrderRepository.save(myOrder);
		    
		    return order.toString();
		}
		
		
		@PostMapping("/update_order")
		public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data){
			String orderID = (data.get("orderid") != null) ? data.get("orderid").toString() : null;
		    System.out.println("Received orderID: " + orderID);
		    
			MyOrder myOrder = this.myOrderRepository.findByOrderID(orderID);
			System.out.println("PID "+data.get("paymentid").toString());
			System.out.println(data.get("status").toString());
			myOrder.setPaymentID(data.get("paymentid").toString());
			myOrder.setStatus(data.get("status").toString());
			this.myOrderRepository.save(myOrder);
			
			System.out.println(data);
			return ResponseEntity.ok(Map.of("msg","updated"));
		}
		
		
}