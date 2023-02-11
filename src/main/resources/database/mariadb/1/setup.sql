CREATE TABLE IF NOT EXISTS player
(
    ID      INT AUTO_INCREMENT PRIMARY KEY,
    name    TINYTEXT,
    playing BOOLEAN  NOT NULL,
    uuid    TINYTEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS challenge
(
    ID      INT AUTO_INCREMENT PRIMARY KEY,
    name    TINYTEXT NOT NULL,
    active  BOOLEAN
);

CREATE TABLE IF NOT EXISTS attempt
(
    ID      INT AUTO_INCREMENT PRIMARY KEY,
    time    INT NOT NULL,
    nether  BOOLEAN,
    end     BOOLEAN
);

CREATE TABLE IF NOT EXISTS attemptPlayer
(
    attemptID  INT NOT NULL,
    playerID   INT NOT NULL,

    FOREIGN KEY (attemptID) REFERENCES attempt(ID),
    FOREIGN KEY (playerID) REFERENCES player(ID)
);

CREATE TABLE IF NOT EXISTS attemptChallenge
(
    attemptID    INT NOT NULL,
    challengeID  INT NOT NULL,

    FOREIGN KEY (attemptID) REFERENCES attempt(ID),
    FOREIGN KEY (challengeID) REFERENCES challenge(ID)
);