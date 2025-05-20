# Documentation Technique - Éditeur de Scénario Interactif

## 1. Vue d'ensemble
L'éditeur de scénario est une application Java Swing permettant de créer et modifier des scénarios pour un jeu d'aventure textuel. Il offre une interface graphique intuitive pour gérer les chapitres, les choix, les combats et les modificateurs de statistiques.

## 2. Architecture

### 2.1 Structure des packages
```
fr.aryvoxx.projava/
├── model/
│   ├── Chapter.java
│   ├── Choice.java
│   ├── Creature.java
│   ├── Personnage.java
│   ├── Scenario.java
│   └── StoryManager.java
└── view/
    ├── GameFrame.java
    └── ScenarioEditor.java
```

### 2.2 Classes principales
- **ScenarioEditor** : Interface principale de l'éditeur
- **StoryManager** : Gestionnaire de l'histoire et des transitions
- **Chapter** : Représente un chapitre du scénario
- **Scenario** : Conteneur de chapitres et gestionnaire de sauvegarde/chargement

## 3. Fonctionnalités implémentées

### 3.1 Édition de scénario
- Création et suppression de chapitres
- Édition du texte des chapitres
- Gestion des choix et des transitions
- Configuration des combats
- Modification des statistiques

### 3.2 Interface utilisateur
- Thème sombre personnalisé
- Panneaux redimensionnables
- Recherche de chapitres
- Barre d'outils avec raccourcis
- Aide contextuelle

### 3.3 Gestion des fichiers
- Sauvegarde au format texte
- Chargement de scénarios
- Format de fichier structuré

## 4. Problèmes rencontrés et solutions

### 4.1 Interface utilisateur
1. **Problème** : Interface trop large nécessitant un défilement horizontal
   - **Solution** : Ajustement des dimensions des panneaux et de la fenêtre
   - Panneau gauche : 200px
   - Panneau central : flexible
   - Panneau droit : 300px

2. **Problème** : Composants Swing non stylisés
   - **Solution** : Implémentation d'un thème sombre personnalisé
   - Couleurs définies pour tous les composants
   - Effets de survol sur les boutons
   - Bordures et marges cohérentes

### 4.2 Gestion des combats
1. **Problème** : Mort du personnage lors de l'épuisement de l'endurance
   - **Solution** : Modification du système de combat
   - Dégâts appliqués à l'endurance en priorité
   - Dégâts réduits de moitié sur la santé quand l'endurance est à 0
   - Mort uniquement quand la santé atteint 0

### 4.3 Compilation
1. **Problème** : Classes manquantes pour les événements de document
   - **Solution** : Ajout des imports nécessaires
   ```java
   import javax.swing.event.DocumentListener;
   import javax.swing.event.DocumentEvent;
   ```

## 5. Améliorations futures

### 5.1 Fonctionnalités à ajouter
- Prévisualisation du scénario
- Validation des liens entre chapitres
- Export en différents formats
- Système de versions
- Historique des modifications

### 5.2 Optimisations possibles
- Amélioration des performances de chargement
- Meilleure gestion de la mémoire
- Interface plus réactive
- Support multilingue

## 6. Format de fichier

### 6.1 Structure
```
#ID
Texte du chapitre
Choix 1|ID_cible
Choix 2|ID_cible
[MOD_STATS:STAT:VALEUR]
[COMBAT:ENNEMI:HABILETÉ:ENDURANCE]
```

### 6.2 Exemple
```
#1
Vous vous éveillez dans une forêt...
Se lever|2
Rester allongé|3
[MOD_STATS:ENDURANCE:-2]
[COMBAT:DRAGON:12:20]
```

## 7. Raccourcis clavier
- Ctrl+S : Sauvegarder
- Ctrl+O : Charger
- Ctrl+N : Nouveau scénario

## 8. Dépendances
- Java 8 ou supérieur
- Swing (inclus dans JDK)

## 9. Installation et exécution
1. Compiler le projet avec Maven ou l'IDE
2. Exécuter la classe Main
3. L'éditeur se lance avec une interface graphique

## 10. Maintenance
- Vérifier régulièrement les logs d'erreur
- Sauvegarder fréquemment le travail
- Faire des copies de sauvegarde des scénarios importants 