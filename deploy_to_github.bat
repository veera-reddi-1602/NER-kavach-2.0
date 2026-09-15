@echo off
echo ====================================================
echo  DEPLOYING NER KAVACH 3.0 TO GITHUB PAGES
echo ====================================================
cd /d "%~dp0frontend"
git add .
git commit -m "Update NER KAVACH 3.0 Frontend"
git branch -M main
git remote remove origin >nul 2>&1
git remote add origin https://github.com/veera-reddi-1602/nerkavach.git
git push -u origin main --force
git push origin main:gh-pages --force
echo.
echo ====================================================
echo  SUCCESS! Pushed to GitHub!
echo  Your GitHub Pages URL will be:
echo  https://veera-reddi-1602.github.io/nerkavach/
echo ====================================================
pause
