package com.example.malotiprime.model;

import java.util.List;

public final class DemoCatalog {
    private static final List<Video> VIDEOS = List.of(
            video("aqz-KE-bpKQ", "Big Buck Bunny", "A playful open short film from Blender Foundation.", "Animation"),
            video("eRsGyueVLvQ", "Sintel", "An open fantasy short film from Blender Foundation.", "Animation"),
            video("Y-rmzh0PI3c", "Cosmos Laundromat", "A surreal animated short from Blender Foundation.", "Animation"),
            video("mN0zPOpADL4", "Agent 327: Operation Barbershop", "An animated spy adventure from Blender Studio.", "Animation"),
            video("WhWc3b3KhnY", "Spring", "A fantasy short film from Blender Open Movies.", "Animation"),
            video("TLkA0RELQ1g", "Elephants Dream", "A surreal animated short from Blender Foundation.", "Animation"),
            video("c3sBBRxDAqk", "Ratatouille", "An animated family-film trailer.", "Animation"),
            video("sUkZFetWYY0", "Despicable Me", "An animated comedy-film trailer.", "Animation"),

            video("6ZfuNTqbHE8", "Avengers: Infinity War", "The official Marvel Studios action trailer.", "Action"),
            video("TcMBFSGVi1c", "Avengers: Endgame", "The official Marvel Studios action trailer.", "Action"),
            video("RFinNxS5KN4", "Jurassic World", "The official dinosaur-adventure trailer.", "Action"),
            video("mqqft2x_Aa4", "The Batman", "The official DC action and crime trailer.", "Action"),
            video("V75dMMIW2B4", "The Lord of the Rings: The Fellowship of the Ring", "The official fantasy-adventure trailer.", "Action"),
            video("qSqVVswa420", "Top Gun: Maverick", "The official aviation-action trailer.", "Action"),
            video("wb49-oV0F78", "Mission: Impossible - Fallout", "The official action and spy-thriller trailer.", "Action"),
            video("8ugaeA-nMTc", "Iron Man", "The official Marvel Studios action trailer.", "Action"),

            video("uYPbbksJxIg", "Oppenheimer", "The official historical-drama trailer.", "Drama"),
            video("2e-eXJ6HgkQ", "Titanic", "The official romantic-drama trailer.", "Drama"),
            video("TQfATDZY5Y4", "The Dark Knight", "The official crime-drama trailer.", "Drama"),
            video("7d_jQycdQGo", "Whiplash", "The official music-drama trailer.", "Drama"),
            video("6hB3S9bIaco", "The Shawshank Redemption", "The official prison-drama trailer.", "Drama"),
            video("KQ0gFidlro8", "Minari", "The official family-drama trailer.", "Drama"),
            video("OiTiKOy59o4", "Gravity", "The official science-fiction drama trailer.", "Drama"),
            video("M3YVTgTl-F0", "Big Fish", "The official fantasy-drama trailer.", "Drama"));

    private static final List<String> GENRES = List.of("Animation", "Action", "Drama");

    private DemoCatalog() {
    }

    public static List<Video> videos() {
        return VIDEOS;
    }

    public static List<String> genres() {
        return GENRES;
    }

    private static Video video(String id, String title, String description, String genre) {
        return new Video(
                id,
                title,
                description,
                genre,
                "https://www.youtube.com/watch?v=" + id,
                "https://img.youtube.com/vi/" + id + "/hqdefault.jpg",
                0);
    }
}
