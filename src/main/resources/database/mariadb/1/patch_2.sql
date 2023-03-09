ALTER TABLE attempt
ADD COLUMN successful BOOLEAN NOT NULL;

ALTER TABLE attempt
CHANGE time duration INT;

ALTER TABLE attempt
ADD COLUMN date TIMESTAMP;

ALTER TABLE attempt
ADD COLUMN failedPlayerID INT;
ALTER TABLE attempt
ADD FOREIGN KEY (failedPlayerID) REFERENCES player(ID);

CREATE TABLE playerJoinLeave
(
    ID INT AUTO_INCREMENT PRIMARY KEY,
    playerID INT NOT NULL,
    time TIMESTAMP NOT NULL,
    online BOOLEAN NOT NULL,

    FOREIGN KEY (playerID) REFERENCES player(ID)
);

CREATE VIEW IF NOT EXISTS playerOnline AS
(
    SELECT a.playerID AS playerID, online AS online FROM
        (SELECT playerID, max(time) as time
        FROM playerJoinLeave
        GROUP BY playerID) a
    LEFT JOIN playerJoinLeave b ON a.playerID  = b.playerID AND a.time = b.time
);