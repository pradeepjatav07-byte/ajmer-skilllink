# Dot-source this file from the project root: . .\scripts\dev-env.ps1
# It never prints secrets. It only sets process-level environment variables.

$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$envFile = Join-Path $repo '.env'

if (Test-Path -LiteralPath $envFile) {
    Get-Content -LiteralPath $envFile | ForEach-Object {
        if ($_ -match '^\s*([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)\s*$') {
            $name = $Matches[1]
            $value = $Matches[2].Trim().Trim('"')
            if (-not (Get-Item ("Env:" + $name) -ErrorAction SilentlyContinue)) {
                Set-Item ("Env:" + $name) $value
            }
        }
    }
}

if (-not $env:DB_URL) { $env:DB_URL = 'jdbc:mysql://localhost:3306/ajmer_skilllink?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' }
if (-not $env:DB_USER) { $env:DB_USER = 'skilllink' }
if (-not $env:DB_PASSWORD) { $env:DB_PASSWORD = 'change-me-local-only' }
if (-not $env:SERVER_PORT) { $env:SERVER_PORT = '8080' }
if (-not $env:CORS_ALLOWED_ORIGINS) { $env:CORS_ALLOWED_ORIGINS = 'http://localhost:5173,http://127.0.0.1:5173' }

if (-not $env:SKILLLINK_JWT_SECRET_BASE64 -or $env:SKILLLINK_JWT_SECRET_BASE64 -eq 'replace-with-random-32-byte-base64') {
    $bytes = New-Object byte[] 32
    try {
        [Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
    } catch {
        $rng = New-Object Security.Cryptography.RNGCryptoServiceProvider
        $rng.GetBytes($bytes)
        $rng.Dispose()
    }
    $env:SKILLLINK_JWT_SECRET_BASE64 = [Convert]::ToBase64String($bytes)
}

Write-Host 'Development environment loaded. A random JWT secret was generated for this session only.'
Write-Host 'If your MySQL password differs, set $env:DB_PASSWORD before dot-sourcing this file.'
