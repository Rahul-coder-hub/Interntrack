$ErrorActionPreference = "SilentlyContinue"

Write-Host ""
Write-Host "Checking InternTrack on http://localhost:8080 ..."

$response = Invoke-WebRequest -UseBasicParsing http://localhost:8080 -TimeoutSec 5

if ($response.StatusCode -eq 200) {
    Write-Host ""
    Write-Host "SUCCESS: InternTrack is running."
    Write-Host "Open this on your computer: http://localhost:8080"
    Write-Host "Open this on same Wi-Fi:    http://192.168.29.200:8080"
} else {
    Write-Host ""
    Write-Host "InternTrack is not responding on port 8080."
    Write-Host "Start it first by double-clicking START_INTERNTRACK.bat."
}

Write-Host ""
pause
