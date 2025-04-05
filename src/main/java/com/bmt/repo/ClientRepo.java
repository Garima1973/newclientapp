package com.bmt.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bmt.model.Client;

public interface ClientRepo extends JpaRepository<Client, Integer> {
	
	public Client findByEmail (String email);

}
