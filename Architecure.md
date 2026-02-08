# Architecture de la plateforme Omnichannel

## Introduction
Cette plateforme utilise une **architecture microservices** pour gérer les services clients et les interactions via différents canaux. Chaque microservice est autonome et peut être déployé de manière indépendante. Les services interagissent principalement via des API REST, et certaines interactions asynchrones sont gérées par des queues de messages.

## Diagramme d'architecture

Voici une vue d'ensemble de l'architecture de la plateforme Omnichannel :

+------------------+ +-----------------+ +--------------------+
| API Gateway | <---> | Eureka Server | <---> | Config Server |
+------------------+ +-----------------+ +--------------------+
| |
v v
+----------------+ +-------------------+
| User Service | | Notification Svc |
+----------------+ +-------------------+
|
v
+------------------+ +----------------------+
| Customer Svc | | Interaction Service |
+------------------+ +----------------------+


## Description des services principaux

### 1. **API Gateway**
L'API Gateway est le point d'entrée unique pour toutes les requêtes externes. Il redirige les requêtes vers les services appropriés. Il est responsable de :
- L'authentification et l'autorisation des requêtes.
- Le routage des requêtes vers les services backend.
- La gestion des erreurs et des réponses.

### 2. **Eureka Server**
Eureka Server est un service de **découverte de services**. Chaque microservice s'enregistre auprès de Eureka, et l'API Gateway ou d'autres services peuvent découvrir ces services dynamiquement.

### 3. **Config Server**
Le Config Server centralise les **paramètres de configuration** pour tous les services. Il permet de gérer des configurations partagées (par exemple, URLs, clés API, etc.) et de les fournir aux services au moment de leur démarrage.

### 4. **User Service**
User Service gère les **utilisateurs** du système. Il est responsable de :
- L'enregistrement des utilisateurs.
- La gestion de leurs informations (nom, email, etc.).
- L'authentification et la gestion des sessions.

### 5. **Customer Service**
Customer Service gère les **données des clients**. Il est responsable de :
- L'enregistrement des informations des clients.
- La gestion des préférences et des historiques des clients.

### 6. **Notification Service**
Le service de notification envoie des messages ou alertes aux utilisateurs ou aux clients. Il peut envoyer des notifications par **email**, **SMS**, ou via d'autres canaux.

## Communication entre les services

### 1. **API REST**
La majorité des services communiquent entre eux via des **API REST**. Par exemple, l'API Gateway redirige les requêtes HTTP vers le `User Service` pour récupérer les informations sur un utilisateur.

Exemple de communication API entre l'API Gateway et User Service :
```bash
GET /users/{id}
2. Messagerie asynchrone
Pour les tâches qui ne nécessitent pas une réponse immédiate ou pour des processus en arrière-plan, certains services communiquent de manière asynchrone via des queues de messages comme Kafka ou RabbitMQ. Par exemple :

Notification Service peut publier un message sur Kafka pour indiquer qu'un email doit être envoyé à un utilisateur.

Le service d'email consomme ce message et envoie l'email de notification.

3. Services asynchrones et synchrones
Les services synchrones répondent immédiatement aux requêtes API (par exemple, le User Service qui récupère des données utilisateur via un appel HTTP).

Les services asynchrones traitent des tâches de fond sans attendre une réponse immédiate (par exemple, Notification Service qui gère l'envoi de messages).

Gestion des bases de données
Chaque microservice possède sa propre base de données pour garantir son indépendance et son autonomie. Cependant, certains services peuvent partager des données ou des informations via des messages ou des API.

User Service : Utilise PostgreSQL pour gérer les informations des utilisateurs.

Customer Service : Utilise MongoDB pour stocker des données non structurées sur les clients.

Interaction Service : Utilise une base de données relationnelle pour enregistrer les interactions avec les clients.

Notification Service : Stocke les logs des notifications dans une base de données NoSQL pour un accès rapide.

Sécurité
1. Authentification
Le système utilise OAuth 2.0 pour gérer l'authentification des utilisateurs. Lorsqu'un utilisateur se connecte, il reçoit un token JWT qui est utilisé pour accéder aux autres services.

2. Autorisation
Chaque service vérifie que l'utilisateur possède les droits nécessaires avant de lui permettre d'effectuer certaines actions (par exemple, un utilisateur admin peut modifier les données des utilisateurs, tandis qu'un utilisateur standard peut seulement les consulter).

3. Chiffrement
Les données sensibles (par exemple, les mots de passe des utilisateurs) sont chiffrées dans la base de données en utilisant des algorithmes de cryptage modernes comme BCrypt.

Choix technologiques
Framework : Spring Boot et Spring Cloud sont utilisés pour construire des microservices robustes et évolutifs.

Base de données : Chaque service utilise une base de données qui correspond à ses besoins :

PostgreSQL pour les données relationnelles.

MongoDB pour les données non structurées.

Messagerie : Kafka est utilisé pour les communications asynchrones entre les services.

Sécurité : OAuth 2.0 et JWT pour gérer l'authentification et l'autorisation.

Conteneurisation : Docker est utilisé pour isoler et déployer chaque service indépendamment.


---

### **Conclusion :**
Ce fichier **`ARCHITECTURE.md`** fournit une vue détaillée de l'architecture de ton proje