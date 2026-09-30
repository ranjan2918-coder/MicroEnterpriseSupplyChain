Start-Sleep -Seconds 15
$headers = @{ "Content-Type" = "application/json" }
$body = '{"name":"John Doe","contactType":"RURAL_ARTISAN","phone":"1234567890","email":"john@example.com","address":"123 Street","active":true}'
Write-Host "--- CREATE ---"
try {
    $res1 = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Post -Headers $headers -Body $body
    $res1 | ConvertTo-Json
    $id = $res1.id

    Write-Host "--- GET ALL ---"
    $res2 = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts" -Method Get
    $res2 | ConvertTo-Json

    Write-Host "--- GET BY ID ---"
    $res3 = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts/$id" -Method Get
    $res3 | ConvertTo-Json

    Write-Host "--- GET BY TYPE ---"
    $res4 = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts/type/RURAL_ARTISAN" -Method Get
    $res4 | ConvertTo-Json

    Write-Host "--- UPDATE ---"
    $updateBody = '{"name":"John Doe Updated","contactType":"RURAL_ARTISAN","phone":"0987654321","email":"john_new@example.com","address":"456 Avenue","active":true}'
    $res5 = Invoke-RestMethod -Uri "http://localhost:8080/api/contacts/$id" -Method Put -Headers $headers -Body $updateBody
    $res5 | ConvertTo-Json

    Write-Host "--- DELETE ---"
    Invoke-RestMethod -Uri "http://localhost:8080/api/contacts/$id" -Method Delete
    Write-Host "Deleted"
} catch {
    Write-Host "Error: $_"
}
