Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "--- SETUP ---"
$artisanBody = '{"name":"Artisan Vendor","contactType":"RURAL_ARTISAN","active":true}'
$artisan = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $artisanBody
$artisanId = $artisan.id

$merchantBody = '{"name":"Local Merchant","contactType":"MERCHANT_BUYER","active":true}'
$merchant = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $merchantBody
$merchantId = $merchant.id

$partnerBody = '{"name":"Fast Deliveries","contactType":"DELIVERY_PARTNER","active":true}'
$partner = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $partnerBody
$partnerId = $partner.id

$productBody = @"
{"name":"Pot A","productType":"GOODS","sellingPrice":20,"purchasePrice":10,"stockQty":10,"artisanId":$artisanId,"active":true}
"@
$product = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -Headers $headers -Body $productBody
$productId = $product.id

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

Write-Host "`n--- TEST 1: CREATE DELIVERY ---"
$deliveryBody = @"
{
    "salesOrderId": $orderId,
    "deliveryPartnerId": $partnerId
}
"@
$delivery = Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries" -Method Post -Headers $headers -Body $deliveryBody
$deliveryId = $delivery.id
$delivery | ConvertTo-Json

Write-Host "`n--- TEST 2: VALID TRANSITIONS ---"
Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries/$deliveryId/status" -Method Put -Headers $headers -Body '{"status":"ASSIGNED"}' | Out-Null
Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries/$deliveryId/status" -Method Put -Headers $headers -Body '{"status":"DISPATCHED"}' | Out-Null
Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries/$deliveryId/status" -Method Put -Headers $headers -Body '{"status":"IN_TRANSIT"}' | Out-Null
$finalDelivery = Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries/$deliveryId/status" -Method Put -Headers $headers -Body '{"status":"DELIVERED"}'
$finalDelivery | ConvertTo-Json

Write-Host "`n--- TEST 3: INVALID TRANSITION (DELIVERED -> PENDING) ---"
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries/$deliveryId/status" -Method Put -Headers $headers -Body '{"status":"PENDING"}'
} catch {
    Write-Host "Failed as expected: $_"
}

Write-Host "`n--- TEST 4: CREATE DELIVERY WITH INVALID ORDER ---"
$invalidDeliveryBody = @"
{
    "salesOrderId": 9999,
    "deliveryPartnerId": $partnerId
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries" -Method Post -Headers $headers -Body $invalidDeliveryBody
} catch {
    Write-Host "Failed as expected: $_"
}

Write-Host "`n--- TEST 5: CREATE DELIVERY WITH INVALID PARTNER ---"
$invalidPartnerBody = @"
{
    "salesOrderId": $orderId,
    "deliveryPartnerId": 9999
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/deliveries" -Method Post -Headers $headers -Body $invalidPartnerBody
} catch {
    Write-Host "Failed as expected: $_"
}
