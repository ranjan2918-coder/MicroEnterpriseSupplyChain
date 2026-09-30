Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }

Write-Host "--- SETUP FOR CUSTOMER PAYMENT ---"
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

$invoice = Invoke-RestMethod -Uri "http://localhost:8080/api/invoices/from-order/$orderId" -Method Post
$invoiceId = $invoice.id

Write-Host "`n--- TEST 1: CUSTOMER PAYMENT AMOUNT MISMATCH ---"
$custPaymentFailBody = @"
{
    "invoiceId": $invoiceId,
    "amount": 999.00,
    "paymentType": "BANK_TRANSFER"
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/payments/customer" -Method Post -Headers $headers -Body $custPaymentFailBody
} catch {
    Write-Host "Failed as expected: $_"
}

Write-Host "`n--- TEST 2: SUCCESSFUL CUSTOMER PAYMENT ---"
$custPaymentBody = @"
{
    "invoiceId": $invoiceId,
    "amount": 20.00,
    "paymentType": "BANK_TRANSFER"
}
"@
$payment = Invoke-RestMethod -Uri "http://localhost:8080/api/payments/customer" -Method Post -Headers $headers -Body $custPaymentBody
$payment | ConvertTo-Json

$invoiceAfter = Invoke-RestMethod -Uri "http://localhost:8080/api/invoices/$invoiceId" -Method Get
Write-Host "Invoice Status after payment:" $invoiceAfter.status

Write-Host "`n--- TEST 3: DUPLICATE CUSTOMER PAYMENT ---"
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/payments/customer" -Method Post -Headers $headers -Body $custPaymentBody
} catch {
    Write-Host "Failed as expected: $_"
}


Write-Host "`n--- SETUP FOR ARTISAN PAYMENT ---"
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
$poAmount = $po.totalAmount

$billBody = @"
{
    "purchaseOrderId": $poId,
    "artisanId": $artisanId,
    "amount": $poAmount
}
"@
$bill = Invoke-RestMethod -Uri "http://localhost:8080/api/vendor-bills" -Method Post -Headers $headers -Body $billBody
$billId = $bill.id

Write-Host "`n--- TEST 4: ARTISAN PAYMENT AMOUNT MISMATCH ---"
$artPaymentFailBody = @"
{
    "vendorBillId": $billId,
    "amount": 1000.00,
    "paymentType": "BANK_TRANSFER"
}
"@
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/payments/artisan" -Method Post -Headers $headers -Body $artPaymentFailBody
} catch {
    Write-Host "Failed as expected: $_"
}

Write-Host "`n--- TEST 5: SUCCESSFUL ARTISAN PAYMENT ---"
$artPaymentBody = @"
{
    "vendorBillId": $billId,
    "amount": 50.00,
    "paymentType": "BANK_TRANSFER"
}
"@
$paymentArt = Invoke-RestMethod -Uri "http://localhost:8080/api/payments/artisan" -Method Post -Headers $headers -Body $artPaymentBody
$paymentArt | ConvertTo-Json

$billAfter = Invoke-RestMethod -Uri "http://localhost:8080/api/vendor-bills/$billId" -Method Get
Write-Host "Vendor Bill Status after payment:" $billAfter.status

Write-Host "`n--- TEST 6: DUPLICATE ARTISAN PAYMENT ---"
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/payments/artisan" -Method Post -Headers $headers -Body $artPaymentBody
} catch {
    Write-Host "Failed as expected: $_"
}
