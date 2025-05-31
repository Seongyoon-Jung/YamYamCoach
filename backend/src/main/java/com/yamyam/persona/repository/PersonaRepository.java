package com.yamyam.persona.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yamyam.persona.entity.PersonaEntity;

public interface PersonaRepository extends JpaRepository<PersonaEntity, Integer>{
	
	PersonaEntity findByPersonaId(int personaId);
}
