# ShareSpace Execution Script for PowerShell
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "       Compiling ShareSpace...        " -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan

if (!(Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sources = Get-ChildItem -Recurse -Filter "*.java" src/main/java | ForEach-Object { $_.FullName }
javac -cp "lib/mysql-connector-j-9.1.0.jar" -d bin $sources

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nCompilation successful! Launching ShareSpace...`n" -ForegroundColor Green
    java -cp "bin;lib/mysql-connector-j-9.1.0.jar" Main
} else {
    Write-Host "`nCompilation failed. Please check the errors above." -ForegroundColor Red
}
