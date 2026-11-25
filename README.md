# Maloti Prime

Maloti Prime is a standalone JavaFX desktop streaming demo using an
MVC-style layout:

- `model/` contains the demo catalog, credentials, and video model.
- `view/` contains the FXML application shell and JavaFX styles.
- `controller/` handles navigation, demo sign-in, and video playback.

The bundled catalog contains 24 unique YouTube trailers across three categories
(eight videos per category). Each shelf shows five cards at a time and scrolls
horizontally to the remaining trailers. Video playback uses YouTube's embedded
player with custom play/pause controls. The catalog, demo login, and My List are
handled locally for the current app session without a cloud backend.

Demo credentials:

```text
Email:    user1@gmail.com
Password: 123admin
```

## Run

```powershell
.\mvnw.cmd javafx:run
```

Navigation and action icons are bundled in `src/main/resources`; the hero and
YouTube trailers and thumbnails require an internet connection. Playback is
subject to YouTube availability and each video's embedding permissions.
