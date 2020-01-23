package com.ipiecoles.java.java230.repository;

import com.ipiecoles.java.java230.model.Manager;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends BaseEmployeRepository<Manager> {
    @EntityGraph(attributePaths = "equipe")
    Manager findOneWithEquipeById(Long id);
    
    Manager findByMatricule(String Matricule);
}
