# Script para configurar variável de ambiente FOOTBALL_API_KEY
# Execute este arquivo com: .\setup-env.ps1

Write-Host "Configurando variável de ambiente FOOTBALL_API_KEY..." -ForegroundColor Green
Write-Host ""
Write-Host "Sua chave API pode ser encontrada em: https://dashboard.api-football.com/soccer/tester" -ForegroundColor Cyan
Write-Host ""

$apiKey = Read-Host "Digite sua chave API"

if ([string]::IsNullOrWhiteSpace($apiKey)) {
    Write-Host "Chave vazia! Configure manualmente:" -ForegroundColor Red
    Write-Host '$env:FOOTBALL_API_KEY = "sua-chave-aqui"'
    exit
}

# Configurar no escopo da sessão atual
$env:FOOTBALL_API_KEY = $apiKey

# Configurar permanentemente no Windows
[Environment]::SetEnvironmentVariable("FOOTBALL_API_KEY", $apiKey, "User")

Write-Host ""
Write-Host "✓ Variável configurada com sucesso!" -ForegroundColor Green
Write-Host "  Chave: $($apiKey.Substring(0, [Math]::Min(10, $apiKey.Length)))..." -ForegroundColor Yellow
Write-Host ""
Write-Host "Você pode agora executar:" -ForegroundColor Cyan
Write-Host "  cd backend"
Write-Host "  .\mvnw spring-boot:run"
Write-Host ""
