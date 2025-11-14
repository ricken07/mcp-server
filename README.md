# Blog MCP Server

Serveur MCP (Model Context Protocol) pour gérer des articles de blog avec recherche et création.

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
