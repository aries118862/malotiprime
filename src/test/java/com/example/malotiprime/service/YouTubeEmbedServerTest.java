package com.example.malotiprime.service;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YouTubeEmbedServerTest {
    @Test
    void servesAnEmbeddablePlayerWithCustomPlaybackControlSupport() throws Exception {
        try (YouTubeEmbedServer server = new YouTubeEmbedServer()) {
            URI page = server.pageUri("aqz-KE-bpKQ", false);
            String html;
            try (var stream = page.toURL().openStream()) {
                html = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }

            assertTrue(html.contains("https://www.youtube-nocookie.com/embed/aqz-KE-bpKQ"));
            assertTrue(html.contains("enablejsapi=1"));
            assertTrue(html.contains("controls=1"));
            assertTrue(html.contains("fs=0"));
            assertTrue(html.contains("allowfullscreen"));
            assertTrue(html.contains("sendPlayerCommand"));
            assertTrue(html.contains("onPlayerFrameLoad()"));
        }
    }

    @Test
    void rejectsMalformedYouTubeIds() throws Exception {
        try (YouTubeEmbedServer server = new YouTubeEmbedServer()) {
            assertThrows(IllegalArgumentException.class, () -> server.pageUri("duplicate", false));
        }
    }
}
