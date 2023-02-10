package com.csvToBD.repository;

import com.csvToBD.model.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;


public interface AthleteRepository extends JpaRepository<Athlete, Long> {
}
