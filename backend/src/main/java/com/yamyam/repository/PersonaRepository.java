package com.yamyam.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.yamyam.entity.PersonaEntity;

public interface PersonaRepository extends JpaRepository<PersonaEntity, Integer>{
	
	PersonaEntity findByPersonaId(int personaId);
}
