package com.rickenbazolo.mcp;

import java.util.UUID;

public record Article(UUID id, String title, String content, String type) {
}
