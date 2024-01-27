package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
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
	@RequestMapping("/dashbord")
	public String dashbord(Model m,Principal principal) {
		
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
		m.addAttribute("title","View Contact");
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
			m.addAttribute("title",contact.getName());
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
		System.out.println("contact name "+contact.getName());
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
		
		
		
}