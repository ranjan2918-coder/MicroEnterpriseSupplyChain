package com.example.supplychain.service;

import com.example.supplychain.model.Account;
import com.example.supplychain.model.Budget;
import com.example.supplychain.model.Cluster;
import com.example.supplychain.repository.AccountRepository;
import com.example.supplychain.repository.BudgetRepository;
import com.example.supplychain.repository.ClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private ClusterRepository clusterRepository;

    private BigDecimal getAccountBalance(String code) {
        return accountRepository.findByAccountCode(code)
                .map(Account::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    public Map<String, Object> getBalanceSheet() {
        BigDecimal finishedGoods = getAccountBalance("1001");
        BigDecimal cash = getAccountBalance("1002");
        BigDecimal payables = getAccountBalance("2001");

        BigDecimal totalAssets = finishedGoods.add(cash);
        BigDecimal totalLiabilities = payables;

        Map<String, Object> report = new HashMap<>();
        report.put("Finished Goods Stock", finishedGoods);
        report.put("Cash/Bank", cash);
        report.put("Artisan Payables", payables);
        report.put("Total Assets", totalAssets);
        report.put("Total Liabilities", totalLiabilities);

        return report;
    }

    public Map<String, Object> getProfitLoss() {
        BigDecimal revenue = getAccountBalance("4001");
        BigDecimal cogs = getAccountBalance("5001");
        BigDecimal freight = getAccountBalance("5002");

        BigDecimal profit = revenue.subtract(cogs).subtract(freight);

        Map<String, Object> report = new HashMap<>();
        report.put("Handicraft Sales Revenue", revenue);
        report.put("Artisan Procurement Cost", cogs);
        report.put("Freight Expense", freight);
        report.put("Profit", profit);

        return report;
    }

    public Map<String, Object> getClusterBudget(Long clusterId) {
        Cluster cluster = clusterRepository.findById(clusterId)
            .orElseThrow(() -> new RuntimeException("Cluster not found"));

        List<Budget> budgets = budgetRepository.findAll().stream()
                .filter(b -> b.getClusterId().equals(clusterId))
                .collect(Collectors.toList());

        BigDecimal planned = BigDecimal.ZERO;
        BigDecimal actual = BigDecimal.ZERO;

        for (Budget b : budgets) {
            planned = planned.add(b.getPlannedAmount() != null ? b.getPlannedAmount() : BigDecimal.ZERO);
            actual = actual.add(b.getActualAmount() != null ? b.getActualAmount() : BigDecimal.ZERO);
        }

        BigDecimal remaining = planned.subtract(actual);
        BigDecimal utilization = BigDecimal.ZERO;

        if (planned.compareTo(BigDecimal.ZERO) > 0) {
            utilization = actual.divide(planned, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }

        Map<String, Object> report = new HashMap<>();
        report.put("Cluster", cluster.getName());
        report.put("Planned Budget", planned);
        report.put("Actual Spending", actual);
        report.put("Remaining Budget", remaining);
        report.put("Utilization Percentage", utilization);

        return report;
    }
}
