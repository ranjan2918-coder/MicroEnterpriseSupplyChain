package com.example.supplychain.service;

import com.example.supplychain.model.Budget;
import com.example.supplychain.repository.BudgetRepository;
import com.example.supplychain.repository.ClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ClusterRepository clusterRepository;

    public Budget createBudget(Budget budget) {
        if (!clusterRepository.existsById(budget.getClusterId())) {
            throw new RuntimeException("Cluster does not exist");
        }
        
        if (budget.getPlannedAmount() == null || budget.getPlannedAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Planned amount must be non-negative");
        }
        
        if (budget.getActualAmount() == null) {
            budget.setActualAmount(BigDecimal.ZERO);
        }
        
        if (budget.getActualAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Actual amount must be non-negative");
        }
        
        return budgetRepository.save(budget);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    public Budget updateActualAmount(Long id, BigDecimal newAmount) {
        Budget budget = budgetRepository.findById(id).orElseThrow(() -> new RuntimeException("Budget not found"));
        budget.setActualAmount(newAmount);
        return budgetRepository.save(budget);
    }
}
