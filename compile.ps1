# Script de compilation automatique pour Cobblemon Bounties
# Utilisez : .\compile.ps1

Write-Host "=== Compilation de Cobblemon Bounties ===" -ForegroundColor Cyan
Write-Host ""

# Etape 1 : Verifier Java
Write-Host "[1/4] Verification de Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.Line }
    Write-Host "OK Java trouve : $javaVersion" -ForegroundColor Green
    
    # Extraire le numero de version
    if ($javaVersion -match '"(\d+)\.') {
        $javaMajor = [int]$Matches[1]
    } elseif ($javaVersion -match '"(\d+)"') {
        $javaMajor = [int]$Matches[1]
    }
    
    if ($javaMajor -lt 21) {
        Write-Host "ERREUR : Java 21 ou superieur requis (vous avez Java $javaMajor)" -ForegroundColor Red
        Write-Host "  Telechargez Java 21 depuis : https://adoptium.net/" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "ERREUR : Java n'est pas installe ou n'est pas dans le PATH" -ForegroundColor Red
    Write-Host "  Telechargez Java 21 depuis : https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Etape 2 : Verifier le fichier gradle-wrapper.jar
Write-Host "[2/4] Verification du Gradle Wrapper..." -ForegroundColor Yellow
$wrapperJar = "gradle\wrapper\gradle-wrapper.jar"

if (-not (Test-Path $wrapperJar)) {
    Write-Host "Le fichier gradle-wrapper.jar est manquant. Telechargement..." -ForegroundColor Yellow
    
    # Telecharger Gradle temporairement
    $gradleVersion = "8.8"
    $gradleZip = "gradle-temp.zip"
    $gradleUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-bin.zip"
    
    try {
        Write-Host "  Telechargement de Gradle $gradleVersion..." -ForegroundColor Gray
        Invoke-WebRequest -Uri $gradleUrl -OutFile $gradleZip -UseBasicParsing
        
        Write-Host "  Extraction..." -ForegroundColor Gray
        Expand-Archive -Path $gradleZip -DestinationPath "." -Force
        
        Write-Host "  Initialisation du wrapper..." -ForegroundColor Gray
        & ".\gradle-$gradleVersion\bin\gradle.bat" wrapper --gradle-version $gradleVersion --no-daemon
        
        Write-Host "  Nettoyage..." -ForegroundColor Gray
        Remove-Item -Recurse -Force "gradle-$gradleVersion"
        Remove-Item $gradleZip
        
        Write-Host "OK Gradle Wrapper initialise" -ForegroundColor Green
    } catch {
        Write-Host "ERREUR lors du telechargement de Gradle" -ForegroundColor Red
        Write-Host "  $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "OK Gradle Wrapper deja present" -ForegroundColor Green
}

Write-Host ""

# Etape 3 : Compilation
Write-Host "[3/4] Compilation du mod..." -ForegroundColor Yellow
Write-Host "  Cela peut prendre quelques minutes (telechargement des dependances)..." -ForegroundColor Gray

try {
    & .\gradlew.bat build --no-daemon
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "OK Compilation reussie !" -ForegroundColor Green
    } else {
        Write-Host "ERREUR lors de la compilation" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "ERREUR lors de la compilation" -ForegroundColor Red
    Write-Host "  $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Etape 4 : Localiser le JAR
Write-Host "[4/4] Localisation du fichier JAR..." -ForegroundColor Yellow

$jarFile = Get-ChildItem -Path "build\libs\" -Filter "*.jar" -Exclude "*-sources.jar" | Select-Object -First 1

if ($jarFile) {
    Write-Host "OK Fichier JAR cree : $($jarFile.FullName)" -ForegroundColor Green
    Write-Host ""
    Write-Host "=== COMPILATION TERMINEE ===" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Le mod est pret a etre utilise !" -ForegroundColor Green
    Write-Host "Fichier : $($jarFile.FullName)" -ForegroundColor White
    Write-Host ""
    Write-Host "Placez ce fichier dans le dossier 'mods' de votre serveur Minecraft." -ForegroundColor Yellow
} else {
    Write-Host "ERREUR : Fichier JAR introuvable" -ForegroundColor Red
    exit 1
}
