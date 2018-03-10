package com.home.batch.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Claim {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id; // Autoincrement id of the table // exclude this from the comma seperated
	private int claimId;
	private int insuredId;
	private int amount;
	private String patientName;
	
	
	public synchronized int getId() {
		return id;
	}
	public synchronized void setId(int id) {
		this.id = id;
	}
	public int getClaimId() {
		return claimId;
	}
	public void setClaimId(int claimId) {
		this.claimId = claimId;
	}
	public int getInsuredId() {
		return insuredId;
	}
	public void setInsuredId(int insuredId) {
		this.insuredId = insuredId;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}	
	

}
