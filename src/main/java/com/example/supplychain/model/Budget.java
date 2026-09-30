package com.example.supplychain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clusterId;
    private Integer budgetYear;
    private BigDecimal plannedAmount;
    private BigDecimal actualAmount;

    @Transient
    private BigDecimal remainingBudget;

    @Transient
    private BigDecimal utilizationPercentage;

    public BigDecimal getRemainingBudget() {
        if (plannedAmount != null && actualAmount != null) {
            return plannedAmount.subtract(actualAmount);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getUtilizationPercentage() {
        if (plannedAmount != null && actualAmount != null && plannedAmount.compareTo(BigDecimal.ZERO) > 0) {
            return actualAmount.divide(plannedAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClusterId() { return clusterId; }
    public void setClusterId(Long clusterId) { this.clusterId = clusterId; }

    public Integer getBudgetYear() { return budgetYear; }
    public void setBudgetYear(Integer budgetYear) { this.budgetYear = budgetYear; }

    public BigDecimal getPlannedAmount() { return plannedAmount; }
    public void setPlannedAmount(BigDecimal plannedAmount) { this.plannedAmount = plannedAmount; }

    public BigDecimal getActualAmount() { return actualAmount; }
    public void setActualAmount(BigDecimal actualAmount) { this.actualAmount = actualAmount; }
}
