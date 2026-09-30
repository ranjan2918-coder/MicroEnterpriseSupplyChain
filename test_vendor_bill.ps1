Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "--- CREATE ARTISAN ---"
$artisanBody = '{"name":"Artisan Vendor","contactType":"RURAL_ARTISAN","active":true}'
$artisan = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $artisanBody
$artisanId = $artisan.id

Write-Host "--- CREATE PRODUCT (GOODS) ---"
$productBody = @"
{"name":"Clay Pot 2","productType":"GOODS","sellingPrice":20,"purchasePrice":10,"stockQty":0,"artisanId":$artisanId,"active":true}
"@
$product = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -Headers $headers -Body $productBody
$productId = $product.id

Write-Host "--- CREATE PO ---"
$poBody = @"
{
    "artisanId": $artisanId,
    "items": [
        {
            "productId": $productId,
            "quantity": 5,
            "unitPrice": 10.00
        }
    ]
}
"@
$po = Invoke-RestMethod -Uri "http://localhost:8080/api/purchase-orders" -Method Post -Headers $headers -Body $poBody
$poId = $po.id

Write-Host "--- CREATE VENDOR BILL ---"
$billBody = @"
{
    "purchaseOrderId": $poId,
    "artisanId": $artisanId,
    "amount": 50.00
}
"@
$bill = Invoke-RestMethod -Uri "http://localhost:8080/api/vendor-bills" -Method Post -Headers $headers -Body $billBody
$billId = $bill.id
$bill | ConvertTo-Json

Write-Host "--- PAY VENDOR BILL ---"
$paidBill = Invoke-RestMethod -Uri "http://localhost:8080/api/vendor-bills/$billId/pay" -Method Put -Headers $headers
$paidBill | ConvertTo-Json

Write-Host "--- ATTEMPT TO PAY AGAIN (EXPECTING FAILURE) ---"
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/vendor-bills/$billId/pay" -Method Put -Headers $headers
} catch {
    Write-Host "Failed as expected: $_"
}
