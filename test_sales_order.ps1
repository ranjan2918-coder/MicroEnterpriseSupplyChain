Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "--- SETUP ---"
$artisanBody = '{"name":"Artisan Vendor","contactType":"RURAL_ARTISAN","active":true}'
$artisan = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $artisanBody
$artisanId = $artisan.id

$merchantBody = '{"name":"Local Merchant","contactType":"MERCHANT_BUYER","active":true}'
$merchant = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $merchantBody
$merchantId = $merchant.id

$product1Body = @"
{"name":"Pot A","productType":"GOODS","sellingPrice":20,"purchasePrice":10,"stockQty":10,"artisanId":$artisanId,"active":true}
"@
$product1 = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -Headers $headers -Body $product1Body
$prod1Id = $product1.id

$product2Body = @"
{"name":"Pot B","productType":"GOODS","sellingPrice":30,"purchasePrice":15,"stockQty":10,"artisanId":$artisanId,"active":true}
"@
$product2 = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -Headers $headers -Body $product2Body
$prod2Id = $product2.id


Write-Host "`n--- TEST 1: SUCCESSFUL ORDER (Multiple Products) ---"
$orderBody = @"
{
    "merchantId": $merchantId,
    "items": [
        {
            "productId": $prod1Id,
            "quantity": 3,
            "unitPrice": 20.00
        },
        {
            "productId": $prod2Id,
            "quantity": 5,
            "unitPrice": 30.00
        }
    ]
}
"@
$order = Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers $headers -Body $orderBody
$order | ConvertTo-Json -Depth 5
$p1After = Invoke-RestMethod -Uri "http://localhost:8080/api/products/$prod1Id" -Method Get
$p2After = Invoke-RestMethod -Uri "http://localhost:8080/api/products/$prod2Id" -Method Get
Write-Host "Prod 1 Stock (Expected 7):" $p1After.stockQty
Write-Host "Prod 2 Stock (Expected 5):" $p2After.stockQty

Write-Host "`n--- TEST 2: INSUFFICIENT STOCK (Rollback) ---"
$orderFailBody = @"
{
    "merchantId": $merchantId,
    "items": [
        {
            "productId": $prod1Id,
            "quantity": 2,
            "unitPrice": 20.00
        },
        {
            "productId": $prod2Id,
            "quantity": 100,
            "unitPrice": 30.00
        }
    ]
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers $headers -Body $orderFailBody
} catch {
    Write-Host "Failed as expected: $_"
}
$p1Rollback = Invoke-RestMethod -Uri "http://localhost:8080/api/products/$prod1Id" -Method Get
Write-Host "Prod 1 Stock after rollback (Expected 7):" $p1Rollback.stockQty


Write-Host "`n--- TEST 3: INVALID PRODUCT ---"
$orderInvalidProd = @"
{
    "merchantId": $merchantId,
    "items": [
        {
            "productId": 9999,
            "quantity": 1,
            "unitPrice": 20.00
        }
    ]
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers $headers -Body $orderInvalidProd
} catch {
    Write-Host "Failed as expected: $_"
}


Write-Host "`n--- TEST 4: INVALID MERCHANT ---"
$orderInvalidMerchant = @"
{
    "merchantId": 9999,
    "items": [
        {
            "productId": $prod1Id,
            "quantity": 1,
            "unitPrice": 20.00
        }
    ]
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers $headers -Body $orderInvalidMerchant
} catch {
    Write-Host "Failed as expected: $_"
}
