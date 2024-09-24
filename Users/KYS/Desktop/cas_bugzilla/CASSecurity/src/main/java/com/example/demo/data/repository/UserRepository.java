package com.example.demo.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.data.entity.CasUser;

@Repository
public interface UserRepository extends JpaRepository<CasUser, Integer> {
	
	CasUser findByEmail (@Param("email") String email);

}
