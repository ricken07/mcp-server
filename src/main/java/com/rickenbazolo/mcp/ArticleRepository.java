package com.rickenbazolo.mcp;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ArticleRepository {

    private static final Map<UUID, Article> articles = new ConcurrentHashMap<>();

    static {
        // Initialiser avec des données d'exemple
        Article article1 = new Article(
            UUID.randomUUID(),
            "Introduction au serveur Java MCP",
            "Cet article explique comment construire un serveur MCP en utilisant le SDK Java...",
            "TECHNICAL"
        );
        Article article2 = new Article(
            UUID.randomUUID(),
            "Comprendre les algorithmes d'apprentissage automatique",
            "Une plongée approfondie dans les algorithmes d'apprentissage automatique courants et leurs applications...",
            "SCIENTIFIC"
        );
        Article article3 = new Article(
            UUID.randomUUID(),
            "Créer des API RESTful avec Spring Boot",
            "Apprenez à créer des API REST prêtes pour la production en utilisant le framework Spring Boot...",
            "TECHNICAL"
        );

        articles.put(article1.id(), article1);
        articles.put(article2.id(), article2);
        articles.put(article3.id(), article3);
    }

    public static List<Article> search(String query, String type) {
        return articles.values().stream()
            .filter(article -> {
                boolean matchesType = type == null || type.isEmpty() || article.type().equals(type);
                boolean matchesQuery = query == null || query.isEmpty() ||
                    article.title().toLowerCase().contains(query.toLowerCase()) ||
                    article.content().toLowerCase().contains(query.toLowerCase());
                return matchesType && matchesQuery;
            })
            .collect(Collectors.toList());
    }

    public static Article create(String title, String content, String type) {
        UUID id = UUID.randomUUID();
        Article article = new Article(id, title, content, type);
        articles.put(id, article);
        return article;
    }

    public static Optional<Article> findById(UUID id) {
        return Optional.ofNullable(articles.get(id));
    }

    public static List<Article> findAll() {
        return new ArrayList<>(articles.values());
    }
}
