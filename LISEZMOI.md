# 🎯 COMPILATION RAPIDE - Cobblemon Bounties

## ⚡ Méthode ULTRA SIMPLE (Recommandée)

1. **Vérifiez que vous avez Java 21+**
   ```
   java -version
   ```
   Si non, téléchargez depuis : https://adoptium.net/

2. **Ouvrez PowerShell dans ce dossier**
   (Clic-droit dans le dossier → "Ouvrir dans Windows Terminal" ou "PowerShell")

3. **Lancez le script automatique :**
   ```powershell
   .\compile.ps1
   ```

4. **C'est tout !** Le script fait tout automatiquement :
   - ✓ Télécharge Gradle si nécessaire
   - ✓ Configure le projet
   - ✓ Compile le mod
   - ✓ Vous indique où est le fichier .jar

---

## 📦 Le fichier JAR sera ici :
```
build\libs\cobblebounties-1.0.0.jar
```

Copiez-le dans le dossier `mods` de votre serveur !

---

## ❌ Si le script ne s'exécute pas

Windows bloque parfois l'exécution de scripts. Si vous avez une erreur, tapez :
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

Puis relancez `.\compile.ps1`

---

## 🐛 Problèmes ?

### Erreur "java n'est pas reconnu"
→ Java n'est pas installé ou pas dans le PATH
→ Téléchargez Java 21 : https://adoptium.net/

### Erreur pendant la compilation
→ Vérifiez votre connexion internet (téléchargement des dépendances)
→ Consultez COMPILATION.md pour plus de détails

---

## 📚 Plus d'infos

- **Guide complet :** COMPILATION.md
- **Guide du mod :** GUIDE_FR.md
- **Guide simple :** GUIDE_SIMPLE.md
