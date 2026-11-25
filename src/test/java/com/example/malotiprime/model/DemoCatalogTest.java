package com.example.malotiprime.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoCatalogTest {
    @Test
    void catalogHasThreeGenresWithEightUniqueVideosEach() {
        assertTrue(DemoCatalog.genres().size() >= 3);
        assertTrue(DemoCatalog.videos().size() >= 24);
        assertEquals(DemoCatalog.videos().size(),
                new HashSet<>(DemoCatalog.videos().stream().map(Video::id).toList()).size());
        assertEquals(DemoCatalog.videos().size(),
                new HashSet<>(DemoCatalog.videos().stream().map(Video::title).toList()).size());
        assertEquals(DemoCatalog.videos().size(),
                new HashSet<>(DemoCatalog.videos().stream().map(Video::youtubeUrl).toList()).size());

        for (String genre : DemoCatalog.genres()) {
            long count = DemoCatalog.videos().stream()
                    .filter(video -> video.genre().equals(genre))
                    .count();
            assertTrue(count >= 8, genre + " has fewer than eight videos");
        }
    }

    @Test
    void everyVideoHasAYouTubeVideoAndThumbnailUrl() {
        assertFalse(DemoCatalog.videos().isEmpty());
        for (Video video : DemoCatalog.videos()) {
            assertTrue(video.youtubeUrl().matches("https://www\\.youtube\\.com/watch\\?v=[\\w-]{11}"));
            assertTrue(video.thumbnailUrl().startsWith("https://img.youtube.com/vi/"));
        }
    }
}
