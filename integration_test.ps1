Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "1. Create rural artisan"
$artisanBody = '{"name": "Artisan A", "contactType": "RURAL_ARTISAN"}'
$artisan = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $artisanBody

Write-Host "2. Create merchant buyer"
$merchantBody = '{"name": "Merchant B", "contactType": "MERCHANT_BUYER"}'
$merchant = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $merchantBody

Write-Host "3. Create delivery partner"
$deliveryBody = '{"name": "Delivery C", "contactType": "DELIVERY_PARTNER"}'
$deliveryPartner = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $deliveryBody

Write-Host "4. Create terracotta vase"
$vaseBody = '{"name": "Terracotta Vase", "productType": "GOODS", "purchasePrice": 100, "sellingPrice": 200, "stockQty": 0, "artisanId": 1}'
$vase = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -Headers $headers -Body $vaseBody

Write-Host "5. Create silk shawl"
$shawlBody = '{"name": "Silk Shawl", "productType": "GOODS", "purchasePrice": 150, "sellingPrice": 300, "stockQty": 0, "artisanId": 1}'
Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -Headers $headers -Body $shawlBody | Out-Null

Write-Host "6. Create Southern Craft Cluster"
$clusterBody = '{"name": "Southern Craft Cluster", "region": "South"}'
$cluster = Invoke-RestMethod -Uri "http://localhost:8080/api/clusters" -Method Post -Headers $headers -Body $clusterBody

Write-Host "7. Create cluster budget"
$budgetBody = '{"clusterId": 1, "budgetYear": 2026, "plannedAmount": 10000, "actualAmount": 0}'
Invoke-RestMethod -Uri "http://localhost:8080/api/budgets" -Method Post -Headers $headers -Body $budgetBody | Out-Null

Write-Host "8. Create purchase order"
$poBody = '{"artisanId": 1, "items": [{"productId": 1, "quantity": 10, "unitPrice": 100}]}'
$po = Invoke-RestMethod -Uri "http://localhost:8080/api/purchase-orders" -Method Post -Headers $headers -Body $poBody

Write-Host "9. Receive purchase order"
$poUpdate = '{"status": "RECEIVED"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/purchase-orders/1/status" -Method Put -Headers $headers -Body $poUpdate | Out-Null

Write-Host "10. Verify inventory increased"
$vaseCheck = Invoke-RestMethod -Uri "http://localhost:8080/api/products/1" -Method Get
if ($vaseCheck.stockQty -eq 10) { Write-Host "Inventory increased successfully (10)." } else { Write-Host "ERROR: Inventory not 10!" }

Write-Host "11. Create vendor bill"
$billBody = '{"purchaseOrderId": 1, "artisanId": 1, "amount": 1000}'
$bill = Invoke-RestMethod -Uri "http://localhost:8080/api/vendor-bills" -Method Post -Headers $headers -Body $billBody

Write-Host "12. Pay artisan"
$paymentArtisan = '{"vendorBillId": 1, "amount": 1000}'
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/artisan" -Method Post -Headers $headers -Body $paymentArtisan | Out-Null

$journalPayout = '{"referenceType": "VENDOR_BILL", "referenceId": 1, "lines": [{"accountId": 5, "debit": 1000}, {"accountId": 2, "credit": 1000}]}'
Invoke-RestMethod -Uri "http://localhost:8080/api/journal-entries" -Method Post -Headers $headers -Body $journalPayout | Out-Null

$budgetUpdate = '{"amount": 1000}'
Invoke-RestMethod -Uri "http://localhost:8080/api/budgets/1/actual-amount" -Method Put -Headers $headers -Body $budgetUpdate | Out-Null

Write-Host "13. Create merchant sales order"
$soBody = '{"merchantId": 2, "items": [{"productId": 1, "quantity": 3, "unitPrice": 200}]}'
$so = Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers $headers -Body $soBody

Write-Host "14. Verify stock decreased atomically"
$vaseCheck2 = Invoke-RestMethod -Uri "http://localhost:8080/api/products/1" -Method Get
if ($vaseCheck2.stockQty -eq 7) { Write-Host "Inventory decreased successfully (7)." } else { Write-Host "ERROR: Inventory not 7!" }

Write-Host "15. Generate customer invoice"
$inv = Invoke-RestMethod -Uri "http://localhost:8080/api/invoices/from-order/1" -Method Post -Headers $headers

Write-Host "16. Assign delivery partner"
$deliveryAssign = '{"salesOrderId": 1, "deliveryPartnerId": 3}'
Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries" -Method Post -Headers $headers -Body $deliveryAssign | Out-Null

Write-Host "16.5. Mark order ASSIGNED"
$assign = '{"status": "ASSIGNED"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries/1/status" -Method Put -Headers $headers -Body $assign | Out-Null

Write-Host "17. Dispatch order"
$dispatch = '{"status": "DISPATCHED"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries/1/status" -Method Put -Headers $headers -Body $dispatch | Out-Null

Write-Host "18. Mark order IN_TRANSIT"
$transit = '{"status": "IN_TRANSIT"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries/1/status" -Method Put -Headers $headers -Body $transit | Out-Null

Write-Host "19. Mark order DELIVERED"
$delivered = '{"status": "DELIVERED"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries/1/status" -Method Put -Headers $headers -Body $delivered | Out-Null

Write-Host "20. Receive merchant payment"
$paymentCust = '{"invoiceId": 1, "amount": 600}'
Invoke-RestMethod -Uri "http://localhost:8080/api/payments/customer" -Method Post -Headers $headers -Body $paymentCust | Out-Null

$journalSales = '{"referenceType": "SALES_ORDER", "referenceId": 1, "lines": [{"accountId": 2, "debit": 600}, {"accountId": 4, "credit": 600}]}'
Invoke-RestMethod -Uri "http://localhost:8080/api/journal-entries" -Method Post -Headers $headers -Body $journalSales | Out-Null

Write-Host "21. Verify journal entries"
$journals = Invoke-RestMethod -Uri "http://localhost:8080/api/journal-entries" -Method Get
if ($journals.Count -eq 2) { Write-Host "Found 2 journal entries." } else { Write-Host "ERROR: Missing journal entries." }

Write-Host "22. Verify debit = credit"
$firstJournal = $journals[0]
$totalDebit = ($firstJournal.lines | Measure-Object -Property debit -Sum).Sum
$totalCredit = ($firstJournal.lines | Measure-Object -Property credit -Sum).Sum
if ($totalDebit -eq $totalCredit) { Write-Host "Debit matches Credit." } else { Write-Host "ERROR: Debit != Credit!" }

Write-Host "23. Verify budget actual amount"
$budgetCheck = Invoke-RestMethod -Uri "http://localhost:8080/api/budgets/1" -Method Get
if ($budgetCheck.actualAmount -eq 1000) { Write-Host "Budget actual amount is 1000." } else { Write-Host "ERROR: Budget actual amount is NOT 1000!" }

Write-Host "24. Generate Balance Sheet"
Invoke-RestMethod -Uri "http://localhost:8080/api/reports/balance-sheet" -Method Get | ConvertTo-Json

Write-Host "25. Generate Profit & Loss"
Invoke-RestMethod -Uri "http://localhost:8080/api/reports/profit-loss" -Method Get | ConvertTo-Json

Write-Host "26. Generate Budget Report"
Invoke-RestMethod -Uri "http://localhost:8080/api/reports/budget/1" -Method Get | ConvertTo-Json
