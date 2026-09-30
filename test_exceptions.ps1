Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "--- TEST 1: RESOURCE NOT FOUND EXCEPTION (404) ---"
$orderBody = @"
{
    "merchantId": 999,
    "items": []
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers $headers -Body $orderBody
} catch {
    Write-Host "Exception triggered! Response format:"
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $responseBody = $reader.ReadToEnd()
    Write-Host $responseBody
}

Write-Host "`n--- SEEDING A MERCHANT ---"
$merchantBody = '{"name": "Alice", "contactType": "MERCHANT_BUYER"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $merchantBody | Out-Null

Write-Host "`n--- TEST 2: INVALID OPERATION EXCEPTION (400) ---"
$noItemsOrderBody = @"
{
    "merchantId": 1,
    "items": []
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers $headers -Body $noItemsOrderBody
} catch {
    Write-Host "Exception triggered! Response format:"
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $responseBody = $reader.ReadToEnd()
    Write-Host $responseBody
}

Write-Host "`n--- SEEDING A PRODUCT WITH 5 STOCK ---"
$productBody = '{"name": "Vase", "productType": "GOODS", "stockQty": 5, "unitPrice": 100}'
Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -Headers $headers -Body $productBody | Out-Null

Write-Host "`n--- TEST 3: INSUFFICIENT STOCK EXCEPTION (409) ---"
$badOrderBody = @"
{
    "merchantId": 1,
    "items": [
        {
            "productId": 1,
            "quantity": 10,
            "unitPrice": 100
        }
    ]
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers $headers -Body $badOrderBody
} catch {
    Write-Host "Exception triggered! Response format:"
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $responseBody = $reader.ReadToEnd()
    Write-Host $responseBody
}
