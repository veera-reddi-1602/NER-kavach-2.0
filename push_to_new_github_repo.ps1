param (
    [string]$RepoUrl
)

Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "  NER KAVACH 3.0 — AI Landslide Early Warning System" -ForegroundColor Green
Write-Host "  GitHub Repository Upload Utility" -ForegroundColor Green
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host ""

if (-not $RepoUrl) {
    $RepoUrl = Read-Host "Enter your new GitHub Repository URL (e.g. https://github.com/username/new-repo.git)"
}

if (-not $RepoUrl) {
    Write-Error "No Repository URL provided. Aborting."
    exit 1
}

Set-Location -Path $PSScriptRoot

Write-Host ""
Write-Host "[1/4] Initializing git and staging all required files..." -ForegroundColor Yellow
git init
git add .

Write-Host ""
Write-Host "[2/4] Committing project files..." -ForegroundColor Yellow
git commit -m "Initial Release: NER KAVACH 3.0 with Production APK, AI Engine, IoT, and Offline Mesh"

Write-Host ""
Write-Host "[3/4] Configuring branch and remote ($RepoUrl)..." -ForegroundColor Yellow
git branch -M main
git remote remove origin 2>$null
git remote add origin $RepoUrl

Write-Host ""
Write-Host "[4/4] Pushing to GitHub..." -ForegroundColor Yellow
git push -u origin main --force

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "==============================================================================" -ForegroundColor Green
    Write-Host "  SUCCESS! Codebase pushed to $RepoUrl" -ForegroundColor Green
    Write-Host "==============================================================================" -ForegroundColor Green
} else {
    Write-Warning "Git push failed. Please check repository permissions or network connection."
}
