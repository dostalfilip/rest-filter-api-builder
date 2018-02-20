package dostal.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dostal.example.model.PetExemple;

public interface PetExempleJPARepository extends JpaRepository<PetExemple, Long> {

}
