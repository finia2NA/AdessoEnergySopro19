package de.sopro.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Reading {
	
public Reading() {
	 readingValues = new ArrayList<ReadingValue>();
}
	
@OneToMany(mappedBy = "reading",cascade = CascadeType.ALL)
private List <ReadingValue> readingValues;

@Id @GeneratedValue(strategy = GenerationType.AUTO)
private String readingID;

public String getReadingID() {
	return readingID;
}

public void setReadingID(String readingID) {
	this.readingID = readingID;
}

public List<ReadingValue> getReadingValues() {
	return readingValues;
}

public void setReadingValues(List<ReadingValue> readingValues) {
	this.readingValues = readingValues;
}
}
