# Guide de Compilation - Cobblemon Bounties
## Pour Windows (PowerShell)

### Étape 1 : Vérifier Java
Ouvrez PowerShell et tapez :
```powershell
java -version
```

**Vous devez avoir Java 21 ou supérieur.**
Si ce n'est pas le cas, téléchargez-le depuis : https://adoptium.net/

### Étape 2 : Initialiser Gradle Wrapper
Dans le dossier du projet, exécutez :
```powershell
# Télécharger Gradle temporairement
$gradle_version = "8.8"
Invoke-WebRequest -Uri "https://services.gradle.org/distributions/gradle-$gradle_version-bin.zip" -OutFile "gradle.zip"
Expand-Archive -Path "gradle.zip" -DestinationPath "."
.\gradle-$gradle_version\bin\gradle.bat wrapper --gradle-version $gradle_version
Remove-Item -Recurse -Force "gradle-$gradle_version"
Remove-Item "gradle.zip"
```

### Étape 3 : Compiler le mod
Une fois le wrapper créé, compilez avec :
```powershell
.\gradlew.bat build
```

### Étape 4 : Récupérer le JAR
Le fichier .jar compilé sera dans :
```
build\libs\cobblebounties-1.0.0.jar
```

---

## Alternative Simple : Utiliser Gradle directement

Si vous avez déjà Gradle installé sur votre système :
```powershell
gradle build
```

---

## En cas d'erreur

### Erreur : "JAVA_HOME n'est pas défini"
```powershell
# Trouver où Java est installé
Get-Command java | Select-Object -ExpandProperty Definition

# Définir JAVA_HOME (remplacez le chemin par le vôtre)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot"
```

### Erreur : "gradle-wrapper.jar manquant"
C'est exactement ce qu'on résout à l'Étape 2 ci-dessus.

### Erreur de compilation
Si la compilation échoue, vérifiez que :
- Vous avez bien Java 21+
- Votre connexion internet fonctionne (pour télécharger les dépendances)
- Le dossier `gradle/wrapper/` contient bien le fichier `gradle-wrapper.jar`

---

## Support
Si vous rencontrez des problèmes, partagez le message d'erreur complet.
