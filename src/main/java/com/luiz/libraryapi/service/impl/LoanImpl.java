package com.luiz.libraryapi.service.impl;

import com.luiz.libraryapi.api.domain.Loan;
import com.luiz.libraryapi.repository.LoanRepository;
import com.luiz.libraryapi.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;

public class LoanImpl implements LoanService {

    @Autowired
    public LoanRepository loanRepository;

    @Override
    public Loan save(Loan any) {
        return loanRepository.save(any);
    }
}
