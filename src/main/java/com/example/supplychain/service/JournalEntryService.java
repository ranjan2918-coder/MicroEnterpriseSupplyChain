package com.example.supplychain.service;

import com.example.supplychain.model.*;
import com.example.supplychain.repository.AccountRepository;
import com.example.supplychain.repository.JournalEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;
    
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public JournalEntry createJournalEntry(JournalEntry entry) {
        if (entry.getLines() == null || entry.getLines().isEmpty()) {
            throw new RuntimeException("Journal entry must have lines");
        }

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (JournalLine line : entry.getLines()) {
            if (line.getDebit() == null) line.setDebit(BigDecimal.ZERO);
            if (line.getCredit() == null) line.setCredit(BigDecimal.ZERO);
            
            if (line.getDebit().compareTo(BigDecimal.ZERO) < 0 || line.getCredit().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Debit and Credit amounts must be non-negative");
            }
            if (line.getDebit().compareTo(BigDecimal.ZERO) > 0 && line.getCredit().compareTo(BigDecimal.ZERO) > 0) {
                throw new RuntimeException("A single line cannot have both Debit and Credit amounts");
            }

            Account account = accountRepository.findById(line.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found for ID: " + line.getAccountId()));

            totalDebit = totalDebit.add(line.getDebit());
            totalCredit = totalCredit.add(line.getCredit());

            if (account.getAccountType() == AccountType.ASSET || account.getAccountType() == AccountType.EXPENSE) {
                account.setBalance(account.getBalance().add(line.getDebit()).subtract(line.getCredit()));
            } else {
                account.setBalance(account.getBalance().add(line.getCredit()).subtract(line.getDebit()));
            }
            accountRepository.save(account);

            line.setJournalEntry(entry);
        }

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new RuntimeException("Invalid journal entry: TOTAL DEBIT (" + totalDebit + ") != TOTAL CREDIT (" + totalCredit + ")");
        }
        
        if (totalDebit.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("Invalid journal entry: Amounts cannot be zero");
        }

        if (entry.getEntryDate() == null) {
            entry.setEntryDate(LocalDateTime.now());
        }

        return journalEntryRepository.save(entry);
    }

    public List<JournalEntry> getAllJournalEntries() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> getJournalEntryById(Long id) {
        return journalEntryRepository.findById(id);
    }
}
