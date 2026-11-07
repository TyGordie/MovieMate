const express = require("express");
const cors = require("cors");
const { initializeApp, applicationDefault } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");

// Set GOOGLE_APPLICATION_CREDENTIALS to the path of your private Firebase service-account JSON.
// Keep that JSON outside Git and never commit it to the repository.
initializeApp({ credential: applicationDefault() });
const db = getFirestore();
const app = express();

app.use(cors());
app.use(express.json({ limit: "1mb" }));
app.use((req, _res, next) => {
    console.log(`${new Date().toISOString()} ${req.method} ${req.path}`);
    next();
});

app.get("/", (_req, res) => res.send("MovieMate API is running"));
app.get("/health", (_req, res) => res.json({ status: "ok", service: "MovieMate API" }));

app.get("/movies", async (req, res) => {
    try {
        const snapshot = await db.collection("movies").get();
        let movies = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        const query = String(req.query.query || "").trim().toLowerCase();
        if (query) {
            movies = movies.filter(movie =>
                String(movie.title || "").toLowerCase().includes(query)
            );
        }
        res.json(movies);
    } catch (error) {
        console.error("GET /movies failed:", error);
        res.status(500).json({ error: "Failed to get movies" });
    }
});

app.get("/movies/:id", async (req, res) => {
    try {
        const doc = await db.collection("movies").doc(req.params.id).get();
        if (!doc.exists) return res.status(404).json({ error: "Movie not found" });
        res.json({ id: doc.id, ...doc.data() });
    } catch (error) {
        console.error("GET /movies/:id failed:", error);
        res.status(500).json({ error: "Failed to get movie" });
    }
});

function readUserId(req, res) {
    const userId = String(req.query.userId || "").trim();
    if (!userId) {
        res.status(400).json({ error: "userId is required" });
        return null;
    }
    return userId;
}

app.get("/api/watchlist", async (req, res) => {
    const userId = readUserId(req, res);
    if (!userId) return;
    try {
        const snapshot = await db.collection("watchlists").doc(userId).collection("movies").get();
        res.json(snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() })));
    } catch (error) {
        console.error("GET /api/watchlist failed:", error);
        res.status(500).json({ error: "Failed to get watchlist" });
    }
});

app.post("/api/watchlist", async (req, res) => {
    const userId = readUserId(req, res);
    if (!userId) return;
    const movie = req.body;
    if (!movie || !movie.id || !movie.title) {
        return res.status(400).json({ error: "A movie id and title are required" });
    }
    try {
        await db.collection("watchlists").doc(userId).collection("movies").doc(String(movie.id)).set(movie);
        res.json({ message: "Movie added to watchlist" });
    } catch (error) {
        console.error("POST /api/watchlist failed:", error);
        res.status(500).json({ error: "Failed to add movie to watchlist" });
    }
});

app.delete("/api/watchlist/:movieId", async (req, res) => {
    const userId = readUserId(req, res);
    if (!userId) return;
    try {
        await db.collection("watchlists").doc(userId).collection("movies").doc(req.params.movieId).delete();
        res.json({ message: "Movie removed from watchlist" });
    } catch (error) {
        console.error("DELETE /api/watchlist/:movieId failed:", error);
        res.status(500).json({ error: "Failed to remove movie from watchlist" });
    }
});

app.post("/api/ratings", async (req, res) => {
    const rating = req.body || {};
    const value = Number(rating.rating);
    if (!rating.userId || !rating.movieId || !Number.isFinite(value) || value < 1 || value > 5) {
        return res.status(400).json({ error: "A user, movie and rating from 1 to 5 are required" });
    }
    try {
        await db.collection("ratings").add({
            userId: String(rating.userId),
            movieId: String(rating.movieId),
            rating: value,
            createdAt: new Date().toISOString()
        });
        res.json({ message: "Rating saved" });
    } catch (error) {
        console.error("POST /api/ratings failed:", error);
        res.status(500).json({ error: "Failed to save rating" });
    }
});

const port = Number(process.env.PORT || 3000);
app.listen(port, "0.0.0.0", () => {
    console.log(`MovieMate API running on port ${port}`);
});
