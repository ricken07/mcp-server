# Blog MCP Server

Serveur MCP (Model Context Protocol) pour gérer des articles de blog avec recherche et création.

## 📖 Conception du serveur MCP

Ce projet est un exemple d'implémentation d'un serveur MCP utilisant le **SDK Java** (sans framework Spring ou autre).

### Architecture MCP

Le Model Context Protocol (MCP) définit une **architecture en couches** avec une séparation claire des responsabilités :

#### 1. **Couche Client/Server**
- `McpServer` : Gère les opérations du protocole
- `McpSession` : Gère les interactions synchrones et asynchrones
- Support des modèles de programmation **synchrone** (`McpSyncServer`) et **asynchrone** (`McpAsyncServer`)

#### 2. **Couche Transport**
- Abstraction de la sérialisation des messages JSON-RPC
- **Transport Stdio** : Communication via stdin/stdout (utilisé dans ce projet)
- Alternatives : HTTP SSE, Streamable-HTTP pour architectures distribuées

#### 3. **Couche Protocole**
Le SDK supporte les capacités MCP suivantes :
- **Tools** : Découverte et exécution d'outils (utilisé ici)
- **Resources** : Gestion de ressources avec URIs et subscriptions
- **Prompts** : Templates de prompts pour interactions IA
- **Completion** : Suggestions de complétion
- **Logging** : Système de logs
- **Progress** : Suivi de progression des opérations

### Principes de conception

Ce serveur illustre les **bonnes pratiques** du SDK Java MCP :

1. **Modularité sans dépendances de framework**
   - Utilisation du module `mcp-core` uniquement
   - Pas de dépendance à Spring ou autre framework web
   - Transport stdio inclus par défaut

2. **Initialisation du serveur**
   ```java
   // 1. Créer le transport
   StdioServerTransportProvider transport =
       new StdioServerTransportProvider(McpJsonMapper.getDefault());

   // 2. Créer le serveur avec capabilities
   McpSyncServer server = McpServer.sync(transport)
       .serverInfo("blog-mcp-server", "1.0.0")
       .capabilities(ServerCapabilities.builder()
           .tools(true)
           .build())
       .build();
   ```

3. **Enregistrement des outils**
   - Définition des outils avec schéma JSON pour les paramètres
   - Handlers pour la logique métier
   - Gestion des erreurs avec `CallToolResult`

4. **Négociation des capacités**
   - Vérification de compatibilité de version du protocole
   - Échange de fonctionnalités lors de l'initialisation
   - Validation et gestion d'erreurs type-safe

### Pourquoi le SDK Java ?

- **Simplicité** : Pas de configuration complexe de framework
- **Légèreté** : Dépendances minimales
- **Portabilité** : JAR exécutable standalone
- **Compréhension** : Code clair pour apprendre le protocole MCP
- **Performance** : Communication directe via stdio pour processus locaux

### Référence

📚 Documentation officielle : [MCP Java SDK Overview](https://modelcontextprotocol.io/sdk/java/mcp-overview)

## 🚀 Build

```bash
mvn clean package
```

Le JAR exécutable sera généré dans `target/mcp-server-1.0-SNAPSHOT-executable.jar`

## ⚙️ Configuration pour Claude Desktop

### Option 1: Configuration via le fichier de configuration Claude

Ajoutez cette configuration à votre fichier de configuration Claude Desktop :

**Sur macOS** : `~/Library/Application Support/Claude/claude_desktop_config.json`

**Sur Windows** : `%APPDATA%\Claude\claude_desktop_config.json`

```json
{
  "mcpServers": {
    "blog-mcp-server": {
      "command": "java",
      "args": [
        "-jar",
        "/<absolute_path>/mcp-server/target/mcp-server-1.0-SNAPSHOT-executable.jar"
      ]
    }
  }
}
```

**⚠️ Important** : Remplacez le chemin absolu par le chemin réel vers votre JAR.

### Option 2 : Configuration avec script

Créez un script `run-server.sh` :

```bash
#!/bin/bash
java -jar /<absolute_path>/mcp-server/target/mcp-server-1.0-SNAPSHOT-executable.jar
```

Puis dans la configuration Claude :

```json
{
  "mcpServers": {
    "blog-mcp-server": {
      "command": "/chemin/vers/run-server.sh"
    }
  }
}
```

## 🛠️ Outils disponibles

### 1. `search_articles`
Rechercher des articles de blog par mot-clé et/ou type.

**Paramètres** :
- `query` (string, optionnel) : Mot-clé à rechercher
- `type` (string, optionnel) : Type d'article (`TECHNICAL` ou `SCIENTIFIC`)

**Exemple** :
```json
{
  "query": "Java",
  "type": "TECHNICAL"
}
```

### 2. `create_article`
Créer un nouvel article de blog.

**Paramètres** :
- `title` (string, requis) : Titre de l'article
- `content` (string, requis) : Contenu de l'article
- `type` (string, requis) : Type d'article (`TECHNICAL` ou `SCIENTIFIC`)

**Exemple** :
```json
{
  "title": "Introduction à Spring Boot",
  "content": "Spring Boot est un framework...",
  "type": "TECHNICAL"
}
```

## 🧪 Test manuel

Pour tester le serveur manuellement :

```bash
java -jar target/mcp-server-1.0-SNAPSHOT-executable.jar
```

Le serveur communique via stdin/stdout selon le protocole MCP.

## 📝 Données d'exemple

Le serveur démarre avec 3 articles pré-chargés :
1. "Introduction au serveur Java MCP" (TECHNICAL)
2. "Comprendre les algorithmes d'apprentissage automatique" (SCIENTIFIC)
3. "Créer des API RESTful avec Spring Boot" (TECHNICAL)

## 🔧 Dépannage

### Erreur "Could not attach to MCP server"

1. **Vérifiez que le JAR est bien généré** :
   ```bash
   ls -l target/mcp-server-1.0-SNAPSHOT-executable.jar
   ```

2. **Vérifiez que le JAR peut s'exécuter** :
   ```bash
   java -jar target/mcp-server-1.0-SNAPSHOT-executable.jar
   ```
   Le serveur devrait afficher : `Serveur MCP Blog démarré et en attente de connexions`

3. **Vérifiez le chemin dans la configuration Claude** :
   - Le chemin doit être **absolu**
   - Vérifiez les permissions d'exécution
   - Vérifiez que Java est dans le PATH

4. **Vérifiez les logs de Claude Desktop** :
   - Sur macOS : `~/Library/Logs/Claude/`
   - Cherchez les erreurs liées au serveur MCP

5. **Redémarrez Claude Desktop** après avoir modifié la configuration

## 📄 Versions

- Java : 21
- MCP SDK : 0.16.0
- Maven : 3.x

## 🏗️ Architecture

```
src/main/java/com/rickenbazolo/mcp/
├── BlogMcpServer.java      # Serveur MCP principal
├── Article.java            # Modèle Article (record)
└── ArticleRepository.java  # Repository en mémoire
```
