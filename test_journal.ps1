Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "--- TEST 1: CREATE JOURNAL ENTRY FOR SALES (BALANCED) ---"
$salesEntryBody = @"
{
    "referenceType": "SALES_ORDER",
    "referenceId": 1,
    "description": "Sale of Handicrafts",
    "lines": [
        {
            "accountId": 2,
            "debit": 500.00
        },
        {
            "accountId": 4,
            "credit": 500.00
        }
    ]
}
"@
$salesEntry = Invoke-RestMethod -Uri "http://localhost:8080/api/journal-entries" -Method Post -Headers $headers -Body $salesEntryBody
$salesEntry | ConvertTo-Json -Depth 5

Write-Host "`n--- TEST 2: CREATE JOURNAL ENTRY FOR ARTISAN PAYOUT (BALANCED) ---"
$artisanEntryBody = @"
{
    "referenceType": "VENDOR_BILL",
    "referenceId": 1,
    "description": "Payout to Artisan",
    "lines": [
        {
            "accountId": 5,
            "debit": 300.00
        },
        {
            "accountId": 2,
            "credit": 300.00
        }
    ]
}
"@
$artisanEntry = Invoke-RestMethod -Uri "http://localhost:8080/api/journal-entries" -Method Post -Headers $headers -Body $artisanEntryBody
$artisanEntry | ConvertTo-Json -Depth 5

Write-Host "`n--- TEST 3: CREATE JOURNAL ENTRY FOR FREIGHT EXPENSE (BALANCED) ---"
$freightEntryBody = @"
{
    "referenceType": "DELIVERY",
    "referenceId": 1,
    "description": "Delivery logistics expense",
    "lines": [
        {
            "accountId": 6,
            "debit": 50.00
        },
        {
            "accountId": 2,
            "credit": 50.00
        }
    ]
}
"@
$freightEntry = Invoke-RestMethod -Uri "http://localhost:8080/api/journal-entries" -Method Post -Headers $headers -Body $freightEntryBody
$freightEntry | ConvertTo-Json -Depth 5

Write-Host "`n--- TEST 4: INVALID JOURNAL ENTRY (UNBALANCED) ---"
$unbalancedBody = @"
{
    "description": "Unbalanced entry",
    "lines": [
        {
            "accountId": 2,
            "debit": 100.00
        },
        {
            "accountId": 4,
            "credit": 50.00
        }
    ]
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/journal-entries" -Method Post -Headers $headers -Body $unbalancedBody
} catch {
    Write-Host "Failed as expected: $_"
}

Write-Host "`n--- TEST 5: INVALID JOURNAL ENTRY (BOTH DEBIT & CREDIT ON SAME LINE) ---"
$sameLineBody = @"
{
    "description": "Bad line entry",
    "lines": [
        {
            "accountId": 2,
            "debit": 100.00,
            "credit": 100.00
        }
    ]
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/journal-entries" -Method Post -Headers $headers -Body $sameLineBody
} catch {
    Write-Host "Failed as expected: $_"
}

Write-Host "`n--- TEST 6: VERIFY ACCOUNT BALANCES ---"
$acc2 = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/2" -Method Get
$acc4 = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/4" -Method Get
$acc5 = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/5" -Method Get
$acc6 = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/6" -Method Get

Write-Host "Cash/Bank (Expected 150):" $acc2.balance
Write-Host "Sales Revenue (Expected 500):" $acc4.balance
Write-Host "Artisan COGS (Expected 300):" $acc5.balance
Write-Host "Freight (Expected 50):" $acc6.balance
