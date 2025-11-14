package com.rickenbazolo.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.*;
import java.util.logging.Logger;

public class BlogMcpServer {

    private static final Logger logger = Logger.getLogger(BlogMcpServer.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            // 1. Créer le transport (stdio pour communication locale)
            StdioServerTransportProvider transport = new StdioServerTransportProvider(McpJsonMapper.getDefault());

            // 2. Créer le serveur MCP synchrone
            McpSyncServer server = McpServer.sync(transport)
                    .serverInfo("blog-mcp-server", "1.0.0")
                    .capabilities(McpSchema.ServerCapabilities.builder()
                            .tools(true)
                            .resources(false, false)
                            .prompts(false)
                            .build())
                    .build();

            // 3. Définir l'outil de recherche d'articles
            McpServerFeatures.SyncToolSpecification searchTool =
                    new McpServerFeatures.SyncToolSpecification(
                            McpSchema.Tool.builder()
                                    .name("search_articles")
                                    .description("Rechercher des articles de blog par type ou mot-clé")
                                    .inputSchema(McpJsonMapper.getDefault(), """
                                            {
                                              "type": "object",
                                              "properties": {
                                                "query": {
                                                  "type": "string",
                                                  "description": "Mot-clé à rechercher"
                                                },
                                                "type": {
                                                  "type": "string",
                                                  "enum": ["TECHNICAL", "SCIENTIFIC"],
                                                  "description": "Filtrer par type"
                                                }
                                              }
                                            }
                                            """)
                                    .build(),
                            null, // paramètre call déprécié
                            (exchange, request) -> {
                                Map<String, Object> arguments = request.arguments();
                                String query = (String) arguments.get("query");
                                String type = (String) arguments.get("type");

                                // Logique métier : recherche dans la base de données
                                List<Article> results = ArticleRepository.search(query, type);

                                String jsonResults;
                                try {
                                    jsonResults = objectMapper.writeValueAsString(results);
                                } catch (JsonProcessingException e) {
                                    logger.severe("Erreur lors de la sérialisation des résultats de recherche : " + e.getMessage());
                                    return McpSchema.CallToolResult.builder()
                                            .addTextContent("Erreur : " + e.getMessage())
                                            .isError(true)
                                            .build();
                                }

                                return McpSchema.CallToolResult.builder()
                                        .addTextContent(jsonResults)
                                        .isError(false)
                                        .build();
                            }
                    );

            // 4. Définir l'outil de création d'article
            McpServerFeatures.SyncToolSpecification createTool =
                    new McpServerFeatures.SyncToolSpecification(
                            McpSchema.Tool.builder()
                                    .name("create_article")
                                    .description("Créer un nouvel article de blog")
                                    .inputSchema(McpJsonMapper.getDefault(), """
                                            {
                                              "type": "object",
                                              "properties": {
                                                "title": { "type": "string" },
                                                "content": { "type": "string" },
                                                "type": {
                                                  "type": "string",
                                                  "enum": ["TECHNICAL", "SCIENTIFIC"]
                                                }
                                              },
                                              "required": ["title", "content", "type"]
                                            }
                                            """)
                                    .build(),
                            null, // paramètre call déprécié
                            (exchange, request) -> {
                                Map<String, Object> arguments = request.arguments();
                                String title = (String) arguments.get("title");
                                String content = (String) arguments.get("content");
                                String type = (String) arguments.get("type");

                                // Logique métier : création dans la base de données
                                Article created = ArticleRepository.create(title, content, type);

                                return McpSchema.CallToolResult.builder()
                                        .addTextContent("Article créé avec succès : " + created.id())
                                        .isError(false)
                                        .build();
                            }
                    );

            // 5. Enregistrer les outils
            server.addTool(searchTool);
            server.addTool(createTool);

            // 6. Le serveur est prêt à recevoir des connexions
            System.err.println("Serveur MCP Blog démarré et en attente de connexions");

            // Maintenir le serveur actif - il écoute automatiquement sur stdin/stdout
            Thread.currentThread().join();

        } catch (Exception e) {
            logger.severe("Erreur lors du démarrage du serveur MCP : " + e.getMessage());
            System.exit(1);
        }
    }
}
