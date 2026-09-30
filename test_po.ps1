Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "--- CREATE ARTISAN ---"
$artisanBody = '{"name":"Artisan PO","contactType":"RURAL_ARTISAN","active":true}'
$artisan = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $artisanBody
$artisanId = $artisan.id

Write-Host "--- CREATE PRODUCT (GOODS) ---"
$productBody = @"
{"name":"Clay Pot","productType":"GOODS","sellingPrice":20,"purchasePrice":10,"stockQty":0,"artisanId":$artisanId,"active":true}
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
            "quantity": 10,
            "unitPrice": 10.00
        }
    ]
}
"@
$po = Invoke-RestMethod -Uri "http://localhost:8080/api/purchase-orders" -Method Post -Headers $headers -Body $poBody
$poId = $po.id
$po | ConvertTo-Json -Depth 5

Write-Host "--- CHECK STOCK BEFORE RECEIVED (Should be 0) ---"
$prodBefore = Invoke-RestMethod -Uri "http://localhost:8080/api/products/$productId" -Method Get
Write-Host "Stock:" $prodBefore.stockQty

Write-Host "--- UPDATE PO TO RECEIVED ---"
$statusBody = '{"status":"RECEIVED"}'
$poUpdated = Invoke-RestMethod -Uri "http://localhost:8080/api/purchase-orders/$poId/status" -Method Put -Headers $headers -Body $statusBody
$poUpdated | ConvertTo-Json -Depth 5

Write-Host "--- CHECK STOCK AFTER RECEIVED (Should be 10) ---"
$prodAfter = Invoke-RestMethod -Uri "http://localhost:8080/api/products/$productId" -Method Get
Write-Host "Stock:" $prodAfter.stockQty

Write-Host "--- UPDATE PO TO RECEIVED AGAIN ---"
$poUpdatedAgain = Invoke-RestMethod -Uri "http://localhost:8080/api/purchase-orders/$poId/status" -Method Put -Headers $headers -Body $statusBody

Write-Host "--- CHECK STOCK AFTER SECOND RECEIVED (Should STILL be 10) ---"
$prodFinal = Invoke-RestMethod -Uri "http://localhost:8080/api/products/$productId" -Method Get
Write-Host "Stock:" $prodFinal.stockQty
