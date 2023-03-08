package com.mocha.shopwebsite.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String first_name;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private String username;
	
	private String last_name;

	public String getPassword() {
		return password;
	}

	private String password;
	
	public Integer getId() {
	    return id;
	  }

	public void setId(Integer id) {
	    this.id = id;
	  }

	public String getFirstName() {
	    return first_name;
	}

	public void setFirstName(String first_name) {
	    this.first_name = first_name;
	}
	

	public String getLastName() {
	    return last_name;
	}

	public void setLastName(String last_name) {
	    this.last_name = last_name;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
		
}
