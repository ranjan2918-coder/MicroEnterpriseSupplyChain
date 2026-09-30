Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "--- SEEDING CLUSTER AND BUDGET ---"
$clusterBody = '{"name": "Northern Craft Cluster", "region": "North", "description": "Textiles"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/clusters" -Method Post -Headers $headers -Body $clusterBody | Out-Null

$budgetBody = '{"clusterId": 1, "budgetYear": 2026, "plannedAmount": 10000.00, "actualAmount": 2500.00}'
Invoke-RestMethod -Uri "http://localhost:8080/api/budgets" -Method Post -Headers $headers -Body $budgetBody | Out-Null


Write-Host "`n--- SEEDING JOURNAL ENTRIES (Sales, COGS, Freight) ---"
$salesBody = '{"referenceType": "SALES", "lines": [{"accountId": 2, "debit": 500}, {"accountId": 4, "credit": 500}]}'
Invoke-RestMethod -Uri "http://localhost:8080/api/journal-entries" -Method Post -Headers $headers -Body $salesBody | Out-Null

$payoutBody = '{"referenceType": "PAYOUT", "lines": [{"accountId": 5, "debit": 300}, {"accountId": 2, "credit": 300}]}'
Invoke-RestMethod -Uri "http://localhost:8080/api/journal-entries" -Method Post -Headers $headers -Body $payoutBody | Out-Null

$freightBody = '{"referenceType": "FREIGHT", "lines": [{"accountId": 6, "debit": 50}, {"accountId": 2, "credit": 50}]}'
Invoke-RestMethod -Uri "http://localhost:8080/api/journal-entries" -Method Post -Headers $headers -Body $freightBody | Out-Null


Write-Host "`n--- TEST 1: BALANCE SHEET ---"
$balanceSheet = Invoke-RestMethod -Uri "http://localhost:8080/api/reports/balance-sheet" -Method Get
$balanceSheet | ConvertTo-Json

Write-Host "`n--- TEST 2: PROFIT & LOSS ---"
$profitLoss = Invoke-RestMethod -Uri "http://localhost:8080/api/reports/profit-loss" -Method Get
$profitLoss | ConvertTo-Json

Write-Host "`n--- TEST 3: CLUSTER BUDGET REPORT ---"
$budgetReport = Invoke-RestMethod -Uri "http://localhost:8080/api/reports/budget/1" -Method Get
$budgetReport | ConvertTo-Json
