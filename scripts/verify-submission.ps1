# Run from the project root: . .\scripts\verify-submission.ps1
$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
Set-Location $repo

. (Join-Path $PSScriptRoot 'dev-env.ps1')

Write-Host '=== 1/3 Frontend production build ==='
Push-Location (Join-Path $repo 'frontend')
npm run build
Pop-Location

Write-Host '=== 2/3 Backend JUnit tests ==='
Push-Location (Join-Path $repo 'backend')
.\mvnw.cmd test
Pop-Location

Write-Host '=== 3/3 Required submission files ==='
$required = @(
    'README.md',
    'docs/API.md',
    'docs/ARCHITECTURE.md',
    'docs/ARCHITECTURE-DIAGRAM.svg',
    'docs/USER-TESTS.md',
    'docs/AI-USE-DECLARATION.md',
    'docs/VIDEO-SCRIPT.md',
    'docs/COMMIT-HISTORY-NOTE.md',
    'docs/SUBMISSION-CHECKLIST.md',
    'scripts/make-submission-zips.ps1'
)
$missing = @()
foreach ($f in $required) { if (-not (Test-Path -LiteralPath (Join-Path $repo $f))) { $missing += $f } }
if ($missing.Count) { throw ('Missing required files: ' + ($missing -join ', ')) }

$tests = (Get-ChildItem (Join-Path $repo 'backend/src/test/java') -Recurse -File -Filter '*.java' | Select-String -Pattern '@Test').Count
Write-Host ("JUnit @Test count: " + $tests)
if ($tests -lt 5) { throw 'At least 5 JUnit tests are required.' }

$tracked = git ls-files
$badTracked = $tracked | Where-Object { $_ -match '(^|/)\.env(\.local)?$|\.bak$|node_modules/|/target/|/dist/' }
if ($badTracked) {
    Write-Warning ('Tracked risky files detected: ' + ($badTracked -join ', '))
} else {
    Write-Host 'No tracked .env, backup, node_modules, target or dist files detected.'
}

Write-Host 'SUBMISSION VERIFICATION COMPLETE.'
