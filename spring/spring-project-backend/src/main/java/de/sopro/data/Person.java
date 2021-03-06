package de.sopro.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "person")
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long personId;

	@NotNull
	@Column(nullable = false, unique = true)
	private String username;

	@NotNull
	private String password;

	private Role role;

	public Person(String username, String password, Role role) {
		this.role = role;
		this.username = username;
		this.password = password;
	}

	public Person() {

	}

	public Long getPersonId() {
		return personId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
