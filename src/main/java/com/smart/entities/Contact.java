package com.smart.entities;



import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "Job_Post_Details")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;
	private String company;
	private String position;
	private String location;
	private String experience;
	private String link;
	 
	private String  image;
	
	@Transient
    private MultipartFile imageFile;
	
	@Column(length = 100000)
	private String description;
	
	
	@ManyToOne
	@JsonIgnore
	private User user;
	
	

	public MultipartFile getImageFile() {
		return imageFile;
	}
	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}
	
	public String  getImage() {
		return image;
	}
	public void setImage(String  image) {
		this.image = image;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getExperience() {
		return experience;
	}
	public void setExperience(String experience) {
		this.experience = experience;
	}
	
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Contact(int cid, String company, String position, String location, String experience, String link, String  image,
			String description) {
		super();
		this.cid = cid;
		this.company = company;
		this.position = position;
		this.location = location;
		this.experience = experience;
		this.link = link;
		this.image = image;
		this.description = description;
	}
	
	public Contact() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Contact [cid=" + cid + ", company=" + company + ", position=" + position + ", location=" + location
				+ ", experience=" + experience + ", link=" + link + ", image=" + image + ", imageFile=" + imageFile
				+ ", description=" + description + ", user=" + user + "]";
	}
	
	
}
