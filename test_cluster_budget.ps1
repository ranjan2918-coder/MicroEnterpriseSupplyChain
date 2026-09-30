Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "--- TEST 1: CREATE CLUSTER ---"
$clusterBody = @"
{
    "name": "Southern Craft Cluster",
    "region": "South",
    "description": "Artisans specialized in pottery and weaving"
}
"@
$cluster = Invoke-RestMethod -Uri "http://localhost:8080/api/clusters" -Method Post -Headers $headers -Body $clusterBody
$clusterId = $cluster.id
$cluster | ConvertTo-Json

Write-Host "`n--- TEST 2: CREATE BUDGET (VALID) ---"
$budgetBody = @"
{
    "clusterId": $clusterId,
    "budgetYear": 2026,
    "plannedAmount": 10000.00,
    "actualAmount": 2500.00
}
"@
$budget = Invoke-RestMethod -Uri "http://localhost:8080/api/budgets" -Method Post -Headers $headers -Body $budgetBody
$budget | ConvertTo-Json

Write-Host "`n--- VERIFY CALCULATIONS ---"
Write-Host "Remaining Budget (Expected 7500.00):" $budget.remainingBudget
Write-Host "Utilization Percentage (Expected 25.00):" $budget.utilizationPercentage

Write-Host "`n--- TEST 3: INVALID CLUSTER ID ---"
$invalidClusterBody = @"
{
    "clusterId": 9999,
    "budgetYear": 2026,
    "plannedAmount": 10000.00,
    "actualAmount": 0
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/budgets" -Method Post -Headers $headers -Body $invalidClusterBody
} catch {
    Write-Host "Failed as expected: $_"
}

Write-Host "`n--- TEST 4: NEGATIVE PLANNED AMOUNT ---"
$negPlannedBody = @"
{
    "clusterId": $clusterId,
    "budgetYear": 2026,
    "plannedAmount": -500.00,
    "actualAmount": 0
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/budgets" -Method Post -Headers $headers -Body $negPlannedBody
} catch {
    Write-Host "Failed as expected: $_"
}

Write-Host "`n--- TEST 5: NEGATIVE ACTUAL AMOUNT ---"
$negActualBody = @"
{
    "clusterId": $clusterId,
    "budgetYear": 2026,
    "plannedAmount": 10000.00,
    "actualAmount": -100.00
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/budgets" -Method Post -Headers $headers -Body $negActualBody
} catch {
    Write-Host "Failed as expected: $_"
}
