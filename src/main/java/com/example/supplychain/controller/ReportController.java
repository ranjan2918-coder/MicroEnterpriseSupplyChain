package com.example.supplychain.controller;

import com.example.supplychain.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/balance-sheet")
    public ResponseEntity<Map<String, Object>> getBalanceSheet() {
        return ResponseEntity.ok(reportService.getBalanceSheet());
    }

    @GetMapping("/profit-loss")
    public ResponseEntity<Map<String, Object>> getProfitLoss() {
        return ResponseEntity.ok(reportService.getProfitLoss());
    }

    @GetMapping("/budget/{clusterId}")
    public ResponseEntity<?> getClusterBudget(@PathVariable Long clusterId) { return ResponseEntity.ok(reportService.getClusterBudget(clusterId));
    }
}

