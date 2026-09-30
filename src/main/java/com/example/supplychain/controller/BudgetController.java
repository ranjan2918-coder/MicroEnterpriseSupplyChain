package com.example.supplychain.controller;

import com.example.supplychain.model.Budget;
import com.example.supplychain.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping
    public ResponseEntity<?> createBudget(@RequestBody Budget budget) { return ResponseEntity.ok(budgetService.createBudget(budget));
    }

    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetService.getAllBudgets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        return budgetService.getBudgetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/actual-amount")
    public ResponseEntity<?> updateActualAmount(@PathVariable Long id, @RequestBody java.util.Map<String, java.math.BigDecimal> body) { return ResponseEntity.ok(budgetService.updateActualAmount(id, body.get("amount")));
    }
}

