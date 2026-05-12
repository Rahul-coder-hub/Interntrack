$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$classes = Join-Path $root "out"
$jar = Join-Path $root "lib\h2-2.3.232.jar"

Set-Location $root

if (-not (Test-Path $jar)) {
    throw "Missing H2 jar at $jar"
}

if (-not (Test-Path $classes)) {
    New-Item -ItemType Directory -Force -Path $classes | Out-Null
}

$sources = Get-ChildItem -Path (Join-Path $root "src\main\java") -Recurse -Filter *.java | ForEach-Object { $_.FullName }

Write-Host ""
Write-Host "Compiling InternTrack..."
javac -encoding UTF-8 -d $classes $sources
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed"
}

Write-Host ""
Write-Host "Starting InternTrack..."
java -cp "$classes;$jar" com.interntrack.App
