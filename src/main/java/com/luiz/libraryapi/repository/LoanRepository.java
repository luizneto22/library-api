package com.luiz.libraryapi.repository;

import com.luiz.libraryapi.api.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
