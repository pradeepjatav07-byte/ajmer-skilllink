# Run from the project root: . .\scripts\make-submission-zips.ps1
$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$out = Join-Path $repo 'submission-artifacts'
New-Item -ItemType Directory -Force -Path $out | Out-Null
$stageRoot = Join-Path $env:TEMP ('asl-submission-stage-' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force -Path $stageRoot | Out-Null

$complete = Join-Path $stageRoot 'ajmer-skilllink-complete'
New-Item -ItemType Directory -Force -Path $complete | Out-Null

$topFiles = @('.env.example', '.gitignore', 'README.md', 'LICENSE-AND-DATA-NOTICE.md', 'docker-compose.yml')
foreach ($f in $topFiles) {
    $src = Join-Path $repo $f
    if (Test-Path -LiteralPath $src) { Copy-Item -LiteralPath $src -Destination (Join-Path $complete $f) -Force }
}

$topDirs = @('frontend', 'backend', 'database', 'data-export', 'public-assets', 'docs', 'scripts')
foreach ($d in $topDirs) {
    $src = Join-Path $repo $d
    $dst = Join-Path $complete $d
    if (Test-Path -LiteralPath $src) {
        robocopy $src $dst /E /NFL /NDL /NJH /NJS /NP /XD node_modules dist target .vite .git submission-artifacts /XF *.bak *.zip *.mp4 *.log .env .env.local .env.*.local | Out-Null
        if ($LASTEXITCODE -ge 8) { throw "robocopy failed for $d (code $LASTEXITCODE)" }
    }
}

$completeZip = Join-Path $out 'ajmer-skilllink-complete.zip'
if (Test-Path -LiteralPath $completeZip) { Remove-Item -LiteralPath $completeZip -Force }
Compress-Archive -Path $complete -DestinationPath $completeZip -CompressionLevel Optimal

# Data-only ZIP
$dataOnly = Join-Path $stageRoot 'ajmer-skilllink-data-only'
New-Item -ItemType Directory -Force -Path $dataOnly | Out-Null
$dataDirs = @('database', 'data-export', 'public-assets')
foreach ($d in $dataDirs) {
    $src = Join-Path $repo $d
    $dst = Join-Path $dataOnly $d
    if (Test-Path -LiteralPath $src) {
        robocopy $src $dst /E /NFL /NDL /NJH /NJS /NP /XD node_modules target /XF *.bak *.zip *.mp4 *.log .env .env.local .env.*.local | Out-Null
        if ($LASTEXITCODE -ge 8) { throw "robocopy failed for $d (code $LASTEXITCODE)" }
    }
}
$dataDocs = @('README.md', 'LICENSE-AND-DATA-NOTICE.md', '.env.example')
foreach ($f in $dataDocs) {
    $src = Join-Path $repo $f
    if (Test-Path -LiteralPath $src) { Copy-Item -LiteralPath $src -Destination (Join-Path $dataOnly $f) -Force }
}
$docsDst = Join-Path $dataOnly 'docs'
New-Item -ItemType Directory -Force -Path $docsDst | Out-Null
foreach ($f in @('DATABASE.md', 'API.md', 'BRANDING.md')) {
    $src = Join-Path $repo ('docs/' + $f)
    if (Test-Path -LiteralPath $src) { Copy-Item -LiteralPath $src -Destination (Join-Path $docsDst $f) -Force }
}
$dataZip = Join-Path $out 'ajmer-skilllink-data-only.zip'
if (Test-Path -LiteralPath $dataZip) { Remove-Item -LiteralPath $dataZip -Force }
Compress-Archive -Path $dataOnly -DestinationPath $dataZip -CompressionLevel Optimal

Remove-Item -LiteralPath $stageRoot -Recurse -Force
Write-Host '=== Submission ZIP files created ==='
Get-ChildItem -LiteralPath $out -File | Select-Object Name, @{Name='MB';Expression={[math]::Round($_.Length/1MB,2)}}, FullName | Format-Table -AutoSize
