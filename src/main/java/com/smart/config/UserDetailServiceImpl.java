package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
@Component
public class UserDetailServiceImpl implements UserDetailsService{
	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.getUserByUserName(username);
		if(user==null) {
			throw new UsernameNotFoundException("Could not found user name !!");
		}
		
		CustomerUserDetails customerUserDetails = new CustomerUserDetails(user);
		return customerUserDetails;
	}

}
