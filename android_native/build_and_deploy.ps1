$ErrorActionPreference = "Stop"

$BUILD_TOOLS = "$env:LOCALAPPDATA\Android\Sdk\build-tools\36.0.0"
$PLATFORM_JAR = "$env:LOCALAPPDATA\Android\Sdk\platforms\android-35\android.jar"
$JAVAC = "C:\Program Files\Android\Android Studio\jbr\bin\javac.exe"
$ADB = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"

$ROOT = if ($PSScriptRoot) { $PSScriptRoot } else { "D:\Desktop\JARVIS\NER KAVACH\android_native" }
$BUILD_DIR = "$ROOT\build"
$GEN_DIR = "$BUILD_DIR\gen"
$OBJ_DIR = "$BUILD_DIR\obj"
$COMPILED_RES = "$BUILD_DIR\compiled_res"

# Clean build directory
if (Test-Path $BUILD_DIR) {
    Remove-Item -Recurse -Force $BUILD_DIR
}
New-Item -ItemType Directory -Force -Path $GEN_DIR | Out-Null
New-Item -ItemType Directory -Force -Path $OBJ_DIR | Out-Null
New-Item -ItemType Directory -Force -Path $COMPILED_RES | Out-Null

Write-Host "1. Compiling resources with AAPT2..."
& "$BUILD_TOOLS\aapt2.exe" compile --dir "$ROOT\res" -o "$COMPILED_RES\resources.zip"

Write-Host "2. Linking resources with AAPT2..."
& "$BUILD_TOOLS\aapt2.exe" link -I $PLATFORM_JAR `
    --manifest "$ROOT\AndroidManifest.xml" `
    -A "$ROOT\assets" `
    -o "$BUILD_DIR\base.apk" `
    --java $GEN_DIR `
    --auto-add-overlay `
    "$COMPILED_RES\resources.zip"

Write-Host "3. Compiling Java sources with javac..."
$javaFiles = Get-ChildItem -Path "$ROOT\src", $GEN_DIR -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
& $JAVAC -encoding UTF-8 -source 1.8 -target 1.8 -cp $PLATFORM_JAR -d $OBJ_DIR $javaFiles

Write-Host "4. Converting class files to DEX with d8..."
$classFiles = Get-ChildItem -Path $OBJ_DIR -Recurse -Filter "*.class" | Select-Object -ExpandProperty FullName
& "$BUILD_TOOLS\d8.bat" --output $BUILD_DIR --lib $PLATFORM_JAR $classFiles

Write-Host "5. Packaging DEX into base APK..."
Copy-Item "$BUILD_DIR\base.apk" "$BUILD_DIR\unaligned.apk" -Force
$JAR = "C:\Program Files\Android\Android Studio\jbr\bin\jar.exe"
& $JAR uf "$BUILD_DIR\unaligned.apk" -C "$BUILD_DIR" "classes.dex"

Write-Host "6. Aligning APK with zipalign..."
$ALIGNED_APK = "$ROOT\NER_KAVACH_3.0.apk"
if (Test-Path $ALIGNED_APK) { Remove-Item -Force $ALIGNED_APK }
& "$BUILD_TOOLS\zipalign.exe" -f -v 4 "$BUILD_DIR\unaligned.apk" $ALIGNED_APK

Write-Host "7. Signing APK with permanent release keystore..."
& "$BUILD_TOOLS\apksigner.bat" sign --ks "$ROOT\ner_kavach_release.keystore" `
    --ks-pass "pass:android" `
    --ks-key-alias "androiddebugkey" `
    --key-pass "pass:android" `
    $ALIGNED_APK

Write-Host "8. Verifying APK signature..."
& "$BUILD_TOOLS\apksigner.bat" verify -v $ALIGNED_APK

Write-Host "9. Installing updated APK to connected Android device..."
& $ADB -s 10BG490UHC00DS7 install -r $ALIGNED_APK

Write-Host "10. Granting permissions and enabling Accessibility for WhatsApp Automation..."
& $ADB -s 10BG490UHC00DS7 shell pm grant com.nerkavach.app android.permission.SEND_SMS
& $ADB -s 10BG490UHC00DS7 shell pm grant com.nerkavach.app android.permission.POST_NOTIFICATIONS
& $ADB -s 10BG490UHC00DS7 shell pm grant com.nerkavach.app android.permission.ACCESS_FINE_LOCATION
& $ADB -s 10BG490UHC00DS7 shell settings put secure enabled_accessibility_services com.nerkavach.app/com.nerkavach.app.WhatsAppAutoSendService
& $ADB -s 10BG490UHC00DS7 shell settings put secure accessibility_enabled 1

Write-Host "11. Launching NER Kavach 3.0 on device..."
& $ADB -s 10BG490UHC00DS7 shell am start -S -n com.nerkavach.app/.MainActivity

Write-Host "SUCCESS! NER KAVACH 3.0 APK Built, Configured & Installed Successfully!"
