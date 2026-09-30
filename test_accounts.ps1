Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "--- TEST 1: FETCH DEFAULT ACCOUNTS ---"
$accounts = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts" -Method Get
$accounts | ConvertTo-Json -Depth 2
Write-Host "Total Accounts initially (Expected 6):" $accounts.Length

Write-Host "`n--- TEST 2: CREATE NEW ACCOUNT ---"
$newAccountBody = @"
{
    "accountCode": "3001",
    "accountName": "Owner Equity",
    "accountType": "LIABILITY"
}
"@
$newAccount = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts" -Method Post -Headers $headers -Body $newAccountBody
$newAccount | ConvertTo-Json

Write-Host "`n--- TEST 3: DUPLICATE ACCOUNT CODE ---"
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/accounts" -Method Post -Headers $headers -Body $newAccountBody
} catch {
    Write-Host "Failed as expected: $_"
}

Write-Host "`n--- TEST 4: FETCH ACCOUNT BY ID ---"
$acc = Invoke-RestMethod -Uri "http://localhost:8080/api/accounts/1" -Method Get
Write-Host "Account 1 Name:" $acc.accountName
