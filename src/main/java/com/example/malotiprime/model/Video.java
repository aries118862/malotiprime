package com.example.malotiprime.model;

public record Video(
        String id,
        String title,
        String description,
        String genre,
        String youtubeUrl,
        String thumbnailUrl,
        long views) {
}
