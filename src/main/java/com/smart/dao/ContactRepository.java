package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import com.smart.entities.Contact;
import com.smart.entities.User;



public interface ContactRepository extends JpaRepository<Contact, Integer> {
	//Implementation of Pagination
	//Current page : page
	//Contact Per page : 5
	@Query("from Contact as c where c.user.id=:UserId")
	public Page<Contact> findContactsByUserId(@Param("UserId") int UserId, Pageable pg);

	//Search functionality
	public List<Contact> findBycompanyContainingAndUser(String company, User user);
	
	@Query("FROM Contact")
	public Page<Contact> findAll(Pageable pageable);


}
