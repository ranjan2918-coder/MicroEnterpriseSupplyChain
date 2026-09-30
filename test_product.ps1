Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

# GOODS - Should keep stockQty
$bodyGoods = '{"name":"Handcrafted Terracotta Vase","description":"Beautiful handcrafted vase","productType":"GOODS","sellingPrice":45.99,"purchasePrice":20.00,"stockQty":5,"artisanId":1,"active":true}'
Write-Host "--- CREATE GOODS ---"
$res1 = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -Headers $headers -Body $bodyGoods
$res1 | ConvertTo-Json
$idGoods = $res1.id

# SERVICE - Should drop stockQty (set to null)
$bodyService = '{"name":"Last-Mile Delivery Service","description":"Delivery service","productType":"SERVICE","sellingPrice":15.00,"purchasePrice":0.00,"stockQty":100,"artisanId":1,"active":true}'
Write-Host "--- CREATE SERVICE ---"
$res2 = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Post -Headers $headers -Body $bodyService
$res2 | ConvertTo-Json
$idService = $res2.id

Write-Host "--- GET ALL ---"
$res3 = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Get
$res3 | ConvertTo-Json

Write-Host "--- GET BY ID ---"
$res4 = Invoke-RestMethod -Uri "http://localhost:8080/api/products/$idGoods" -Method Get
$res4 | ConvertTo-Json

Write-Host "--- LOW STOCK ---"
$res5 = Invoke-RestMethod -Uri "http://localhost:8080/api/products/low-stock?threshold=10" -Method Get
$res5 | ConvertTo-Json

Write-Host "--- UPDATE ---"
$updateBody = '{"name":"Woven Silk Shawl","description":"Premium shawl","productType":"GOODS","sellingPrice":150.00,"purchasePrice":80.00,"stockQty":15,"artisanId":2,"active":true}'
$res6 = Invoke-RestMethod -Uri "http://localhost:8080/api/products/$idGoods" -Method Put -Headers $headers -Body $updateBody
$res6 | ConvertTo-Json

Write-Host "--- DELETE ---"
Invoke-RestMethod -Uri "http://localhost:8080/api/products/$idGoods" -Method Delete
Invoke-RestMethod -Uri "http://localhost:8080/api/products/$idService" -Method Delete
Write-Host "Deleted"
