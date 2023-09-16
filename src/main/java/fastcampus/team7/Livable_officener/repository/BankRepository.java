package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankRepository extends JpaRepository<Bank, Long> {

    List<Bank> findAll();
}
