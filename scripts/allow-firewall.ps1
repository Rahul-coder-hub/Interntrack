$ErrorActionPreference = "Stop"

$ruleName = "InternTrack Java App Port 8080"

Write-Host "Creating Windows Firewall rule for InternTrack on TCP port 8080..."

if (Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue) {
    Write-Host "Firewall rule already exists: $ruleName"
} else {
    New-NetFirewallRule `
        -DisplayName $ruleName `
        -Direction Inbound `
        -Action Allow `
        -Protocol TCP `
        -LocalPort 8080 `
        -Profile Private | Out-Null

    Write-Host "Firewall rule created successfully."
}

Write-Host ""
Write-Host "Now run scripts\run.bat and share the Network link shown in that window."
