CREATE TABLE IF NOT EXISTS games (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created_at DATETIME NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created_at DATETIME NOT NULL,
    review_text TEXT NOT NULL,
    author TEXT NOT NULL,
    score INT NOT NULL,
    game_id INT NOT NULL,
    FOREIGN KEY(game_id) REFERENCES games(id)
);