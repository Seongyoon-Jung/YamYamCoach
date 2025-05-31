@echo off
setlocal enabledelayedexpansion

:: BASE_DIR 설정
set "BASE_DIR=%~dp0"
set "BASE_DIR=!BASE_DIR:~0,-1!"

:: 로그 폴더 생성
if not exist "!BASE_DIR!\logs" (
    mkdir "!BASE_DIR!\logs"
)

:: ▶ Backend 실행
echo ▶ Starting Backend...
pushd "!BASE_DIR!\backend"
echo [%TIME%] Backend start >> "!BASE_DIR!\logs\backend.log"
start "Backend" cmd /c "gradlew.bat bootRun >> \"!BASE_DIR!\logs\backend.log\" 2>&1"
popd

:: ▶ Recommend 실행
echo ▶ Starting Recommend Service...
pushd "!BASE_DIR!\recommend"
echo [%TIME%] Recommend start >> "!BASE_DIR!\logs\recommend.log"
start "Recommend" cmd /c "\"!BASE_DIR!\recommend\venv\Scripts\python.exe\" recommend_server.py >> \"!BASE_DIR!\logs\recommend.log\" 2>&1"
popd

:: ▶ Frontend 실행
echo ▶ Starting Frontend...
pushd "!BASE_DIR!\frontend"
echo [%TIME%] Frontend start >> "!BASE_DIR!\logs\frontend.log"
start "Frontend" cmd /c "npm run dev >> \"!BASE_DIR!\logs\frontend.log\" 2>&1"
popd

endlocal
