package com.example.supplychain.service;

import com.example.supplychain.model.Account;
import com.example.supplychain.model.AccountType;
import com.example.supplychain.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @PostConstruct
    public void initDefaultAccounts() {
        createAccountIfNotExists("1001", "Finished Goods Stock", AccountType.ASSET);
        createAccountIfNotExists("1002", "Cash/Bank", AccountType.ASSET);
        createAccountIfNotExists("2001", "Artisan Payables", AccountType.LIABILITY);
        createAccountIfNotExists("4001", "Handicraft Sales Revenue", AccountType.INCOME);
        createAccountIfNotExists("5001", "Artisan Cost of Goods", AccountType.EXPENSE);
        createAccountIfNotExists("5002", "Freight Expenses", AccountType.EXPENSE);
    }
    
    private void createAccountIfNotExists(String code, String name, AccountType type) {
        if (accountRepository.findByAccountCode(code).isEmpty()) {
            Account account = new Account(code, name, type);
            accountRepository.save(account);
        }
    }

    public Account createAccount(Account account) {
        if (accountRepository.findByAccountCode(account.getAccountCode()).isPresent()) {
            throw new RuntimeException("Account code already exists: " + account.getAccountCode());
        }
        if (account.getBalance() == null) {
            account.setBalance(java.math.BigDecimal.ZERO);
        }
        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
}
