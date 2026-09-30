Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "--- SETUP ---"
$artisanBody = '{"name":"Artisan Vendor","contactType":"RURAL_ARTISAN","active":true}'
$artisan = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $artisanBody
$artisanId = $artisan.id

$merchantBody = '{"name":"Local Merchant","contactType":"MERCHANT_BUYER","active":true}'
$merchant = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $merchantBody
$merchantId = $merchant.id

$productBody = @"
{"name":"Pot A","productType":"GOODS","sellingPrice":20,"purchasePrice":10,"stockQty":10,"artisanId":$artisanId,"active":true}
"@
$product = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -Headers $headers -Body $productBody
$productId = $product.id

Write-Host "`n--- CREATE SALES ORDER ---"
$orderBody = @"
{
    "merchantId": $merchantId,
    "items": [
        {
            "productId": $productId,
            "quantity": 1,
            "unitPrice": 20.00
        }
    ]
}
"@
$order = Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers $headers -Body $orderBody
$orderId = $order.id

Write-Host "`n--- TEST 1: GENERATE INVOICE SUCCESS ---"
$invoice1 = Invoke-RestMethod -Uri "http://localhost:8080/api/invoices/from-order/$orderId" -Method Post
$invoice1 | ConvertTo-Json

Write-Host "`n--- TEST 2: GENERATE INVOICE AGAIN (CONFLICT) ---"
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/invoices/from-order/$orderId" -Method Post
} catch {
    Write-Host "Failed as expected: $_"
}

Write-Host "`n--- TEST 3: INVALID SALES ORDER ---"
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/invoices/from-order/9999" -Method Post
} catch {
    Write-Host "Failed as expected: $_"
}

Write-Host "`n--- TEST 4: CANCELLED ORDER ---"
$order2 = Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers $headers -Body $orderBody
$order2Id = $order2.id
# Cancel order
$cancelBody = '{"status":"CANCELLED"}'
Invoke-RestMethod -Uri "http://localhost:8080/api/orders/$order2Id/status" -Method Put -Headers $headers -Body $cancelBody

try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/invoices/from-order/$order2Id" -Method Post
} catch {
    Write-Host "Failed as expected: $_"
}
