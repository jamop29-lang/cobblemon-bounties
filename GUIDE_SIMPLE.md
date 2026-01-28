# 🎯 GUIDE ULTRA SIMPLE - EN 3 ÉTAPES

## ✨ CE FICHIER CONTIENT LE MOD COMPLET ET PRÊT À COMPILER

Tout est déjà fait ! Tu n'as RIEN à modifier manuellement ! 🎉

---

## 📦 ÉTAPE 1 : Extrais le ZIP

Extrais `cobblemon-bounties-final.zip` n'importe où sur ton PC.

Tu vas avoir un dossier `cobblemon-bounties-final/` avec tout dedans.

---

## 🔨 ÉTAPE 2 : Compile le mod

### Sur Windows :
1. Ouvre un terminal (PowerShell ou CMD) dans le dossier
2. Tape : `gradlew.bat build`

### Sur Mac/Linux :
1. Ouvre un terminal dans le dossier  
2. Tape : `./gradlew build`

**Attends quelques minutes** (la première fois c'est plus long, Gradle télécharge des trucs)

Quand c'est fini, tu vois : `BUILD SUCCESSFUL`

---

## 🎮 ÉTAPE 3 : Installe sur ton serveur

### 3.1 - Récupère le fichier compilé
Va dans : `build/libs/`

Tu trouveras : `cobblebounties-1.0.0.jar`

### 3.2 - Installe Impactor (obligatoire)
**Télécharge Impactor** : https://modrinth.com/mod/impactor/version/5.3.0+1.21.1-fabric

Tu auras : `impactor-fabric-5.3.0+1.21.1.jar`

### 3.3 - Place les deux fichiers
Mets les DEUX fichiers dans `/mods` de ton serveur :
- `cobblebounties-1.0.0.jar` ✅
- `impactor-fabric-5.3.0+1.21.1.jar` ✅

### 3.4 - Démarre le serveur
Lance ton serveur normalement !

---

## ✅ C'EST TOUT !

Le mod est maintenant installé et **100% fonctionnel** avec :
- ✅ Primes automatiques toutes les 24h
- ✅ Commande `/bounties` 
- ✅ Argent donné VRAIMENT (via Impactor)
- ✅ Items donnés VRAIMENT (dans l'inventaire)

---

## 🎮 UTILISATION EN JEU

### Pour les joueurs :
```
/bounties  → Voir les primes actives
```

Quand tu captures le bon Pokémon :
- Message de progression
- Récompense automatique à la complétion
- Argent + Items dans ton inventaire !

---

## ⚙️ CONFIGURATION (optionnel)

Après le premier lancement, un fichier apparaît :
`config/cobblebounties.json`

Tu peux modifier :
- Nombre de primes (par défaut 3)
- Durée des primes (par défaut 24h)  
- Pokémon par rareté
- Récompenses par rareté
- Montants d'argent
- etc.

Arrête le serveur, modifie le JSON, redémarre !

---

## ❓ PROBLÈMES ?

### "BUILD FAILED" quand je compile
➡️ Assure-toi d'avoir **Java 21** installé
➡️ Tape : `java -version` pour vérifier

### Le serveur ne démarre pas
➡️ Vérifie que tu as bien **Impactor** installé
➡️ Regarde les logs pour l'erreur exacte

### Les récompenses ne sont pas données
➡️ Vérifie dans les logs du serveur :
```
[INFO] Economy integration initialized with currency: Dollar
```
Si tu vois ce message → tout est OK !

Si tu vois :
```
[WARN] Economy not available - logging reward only
```
➡️ Impactor n'est pas chargé correctement

---

## 🎉 PROFITE !

Ton système de primes Cobblemon est maintenant opérationnel ! 🚀

Besoin d'aide ? Envoie-moi les logs ! 😊
