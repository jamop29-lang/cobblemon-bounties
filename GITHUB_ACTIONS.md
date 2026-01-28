# 🚀 Compilation Automatique avec GitHub Actions

Ce guide vous explique comment compiler le mod **automatiquement et gratuitement** avec GitHub.

## 📋 Prérequis

- Un compte GitHub (gratuit) : https://github.com/signup

## 🎯 Étapes à suivre

### 1. Créer un nouveau dépôt GitHub

1. Allez sur https://github.com/new
2. Nom du dépôt : `cobblemon-bounties` (ou ce que vous voulez)
3. **Cochez "Public"** (obligatoire pour utiliser GitHub Actions gratuitement)
4. **NE PAS** cocher "Add a README file"
5. Cliquez sur **"Create repository"**

### 2. Uploader les fichiers du projet

**Option A : Via l'interface web (plus simple)**

1. Sur la page de votre nouveau dépôt, cliquez sur **"uploading an existing file"**
2. **Glissez-déposez TOUS les fichiers** du dossier `cobblemon-bounties-final/` 
   - ⚠️ Important : Ne pas uploader le dossier `libs/` (inutile)
   - ⚠️ Important : Ne pas oublier le dossier `.github/` (avec le point au début)
3. Scrollez en bas et cliquez sur **"Commit changes"**

**Option B : Avec GitHub Desktop ou Git (si vous connaissez)**

```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/VOTRE_USERNAME/cobblemon-bounties.git
git push -u origin main
```

### 3. Lancer la compilation

Une fois les fichiers uploadés :

1. Allez dans l'onglet **"Actions"** en haut du dépôt
2. Vous verrez le workflow **"Build Mod"** qui se lance automatiquement
3. Cliquez dessus pour voir la progression
4. ⏳ Attendez 3-5 minutes que la compilation se termine

### 4. Télécharger le mod compilé

1. Quand la compilation est terminée (✅ coche verte)
2. Scrollez en bas de la page
3. Dans la section **"Artifacts"**, cliquez sur **"cobblebounties-mod"**
4. Un fichier ZIP sera téléchargé
5. Décompressez-le : vous trouverez `cobblebounties-1.0.0.jar` 🎉

### 5. Installer le mod sur votre serveur

1. Copiez le fichier `cobblebounties-1.0.0.jar` 
2. Collez-le dans le dossier `/mods` de votre serveur
3. Redémarrez le serveur
4. Le mod est installé ! 🎊

## 🔄 Recompiler après modifications

Si vous modifiez le code :

1. Uploadez les fichiers modifiés sur GitHub
2. Le workflow se relance automatiquement
3. Téléchargez le nouveau JAR dans "Artifacts"

## ⚠️ Notes importantes

- **Le dossier `libs/` n'est PAS nécessaire** pour GitHub Actions
- La compilation se fait sur les serveurs de GitHub, pas sur votre PC
- C'est **100% gratuit** pour les dépôts publics
- Les artifacts (fichiers compilés) sont conservés 90 jours

## ❓ En cas de problème

Si la compilation échoue :

1. Cliquez sur le workflow en erreur
2. Cliquez sur "build" pour voir les logs
3. Cherchez les messages d'erreur en rouge
4. Partagez-moi les erreurs si besoin d'aide

## 🎓 Avantages de cette méthode

✅ Pas besoin d'installer Java sur votre PC  
✅ Pas besoin de Gradle  
✅ Pas besoin de télécharger Cobblemon  
✅ Compilation automatique à chaque modification  
✅ Fonctionne sur Windows, Mac, Linux  
✅ 100% gratuit  

Bon courage ! 🚀
