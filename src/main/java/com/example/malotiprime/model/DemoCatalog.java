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
            video("M3YVTgTl-F0", "Big Fish", "The official fantasy-drama trailer.", "Drama"),

            video("tcdUhdOlz9M", "The Hangover", "A comedy about a bachelor party that gets out of hand.", "Comedy"),
            video("qGqiHJTsRkQ", "Knives Out", "A witty murder mystery with an eccentric detective.", "Comedy"),
            video("1Fg5iWmQjwk", "The Grand Budapest Hotel", "A comic adventure at a legendary European hotel.", "Comedy"),
            video("GncQtURdcE4", "Groundhog Day", "A weatherman relives the same day in a small town.", "Comedy"),
            video("4eaZ_48ZYog", "Superbad", "Two friends try to make the most of their final school days.", "Comedy"),
            video("3PsUJFEBC74", "School of Rock", "A failed musician inspires a classroom through rock music.", "Comedy"),
            video("pBk4NYhWNMM", "Barbie", "Barbie discovers a new world beyond Barbieland.", "Comedy"),
            video("6hDkhw5Wkas", "Ghostbusters", "A team of scientists starts a supernatural investigation business.", "Comedy"),

            video("k10ETZ41q5o", "The Conjuring", "Paranormal investigators help a family in a haunted farmhouse.", "Horror"),
            video("WR7cc5t7tv8", "A Quiet Place", "A family must stay silent to survive a sound-hunting threat.", "Horror"),
            video("FnCdOQsX5kc", "It", "A group of friends confronts a terrifying presence in their town.", "Horror"),
            video("DzfpyUB60YY", "Get Out", "A weekend visit reveals a disturbing secret.", "Horror"),
            video("hNCmb-4oXJA", "Us", "A family faces frightening doubles of themselves.", "Horror"),
            video("V6wWKNij_1M", "Hereditary", "A family uncovers unsettling secrets after a loss.", "Horror"),
            video("zwAM5UnGd2s", "The Nun", "A priest and novice investigate a sinister abbey.", "Horror"),
            video("dCVh4lBfW-c", "A Nightmare on Elm Street", "A group of teenagers is haunted by a killer in their dreams.", "Horror"),

            video("0pdqf4P9MB8", "La La Land", "A musician and an actor pursue their dreams in Los Angeles.", "Romance"),
            video("FC6biTjEyZw", "The Notebook", "A lifelong love story unfolds across changing times.", "Romance"),
            video("1dYv5u6v55Y", "Pride & Prejudice", "Elizabeth Bennet and Mr. Darcy navigate love and first impressions.", "Romance"),
            video("AWmjzCZr0Jw", "10 Things I Hate About You", "A high-school romance begins with an unusual arrangement.", "Romance"),
            video("ZQ-YX-5bAs0", "Crazy Rich Asians", "A woman discovers the world of her boyfriend's wealthy family.", "Romance"),
            video("9ItBvH5J6ss", "The Fault in Our Stars", "Two teenagers find love while facing serious illness.", "Romance"),
            video("Eh993__rOxA", "Me Before You", "An unexpected friendship changes two people's lives.", "Romance"),
            video("T7A810duHvw", "About Time", "A young man discovers a surprising ability that shapes his love life.", "Romance"),
            video("nSbzyEJ8X9E", "A Star Is Born", "A musician helps a rising singer find her voice and audience.", "Romance"),

            video("zSWdZVtXT7E", "Interstellar", "Explorers search for a future for humanity beyond Earth.", "Science Fiction"),
            video("n9xhJrPXop4", "Dune", "A young heir travels to a dangerous desert planet.", "Science Fiction"),
            video("vKQi3bBA1y8", "The Matrix", "A programmer discovers that reality is not what it seems.", "Science Fiction"),
            video("YoHD9XEInc0", "Inception", "A specialist enters dreams to carry out an impossible idea.", "Science Fiction"),
            video("gCcx85zbxz4", "Blade Runner 2049", "A new blade runner uncovers a secret that could change society.", "Science Fiction"),
            video("tFMo3UJ4B4g", "Arrival", "A linguist works to communicate with visitors from another world.", "Science Fiction"),
            video("wxN1T1uxQ2g", "Everything Everywhere All at Once", "A woman discovers the many lives she could have lived.", "Science Fiction"),
            video("ej3ioOneTy8", "The Martian", "An astronaut stranded on Mars fights to find a way home.", "Science Fiction"),
            video("sGbxmsDFVnE", "Star Wars: The Force Awakens", "A new generation joins the fight across a galaxy far away.", "Science Fiction"));

    private static final List<String> GENRES = List.of(
            "Animation", "Action", "Drama", "Comedy", "Horror", "Romance", "Science Fiction");

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
