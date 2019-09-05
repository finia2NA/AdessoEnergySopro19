package de.sopro.data;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class User extends Person{

public User(String name, 
			String surname, 
			String eMailAddress, 
			String userNumber,
			String username, 
			String password, 
			Role role) {
	super(username, password, role);
	this.name = name;
	this.surname = surname;
	this.eMailAddress = eMailAddress;
	this.userNumber = userNumber;
	// createdAt = now;
}

private String name;

private String surname;

private String eMailAddress;

private String userNumber;

private Date createdAt;

private Date deletedAt;

private Date updatedAt;

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getSurname() {
	return surname;
}

public void setSurname(String surname) {
	this.surname = surname;
}

public String getEMailAddress() {
	return eMailAddress;
}

public void setEMailAddress(String eMailAddress) {
	this.eMailAddress = eMailAddress;
}

public String getUserNumber() {
	return userNumber;
}

public void setUserNumber(String userNumber) {
	this.userNumber = userNumber;
}

public Date getCreatedAt() {
	return createdAt;
}

public Date getDeletedAt() {
	return deletedAt;
}

public void setDeletedAt(Date deletedAt) {
	this.deletedAt = deletedAt;
}

public Date getUpdatedAt() {
	return updatedAt;
}

public void setUpdatedAt(Date updatedAt) {
	this.updatedAt = updatedAt;
}
}
