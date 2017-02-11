package pl.setblack.pongi.scores;

import javax.annotation.concurrent.Immutable;

/**
 * Created by jarek on 2/5/17.
 */
@Immutable
public class ScoreRecord {
    public final String userId;
    public final GameResult result;
    public final int playerScored;
    public final int opponentScore;
    public final String gameId;

    public ScoreRecord(
            String userId,
            GameResult result,
            int playerScore,
            int opponentScore,
            String gameId) {
        this.userId = userId;
        this.result = result;
        this.playerScored = playerScore;
        this.opponentScore = opponentScore;
        this.gameId = gameId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScoreRecord{");
        sb.append("userId='")
                .append(userId)
                .append('\'');
        sb.append(", result=")
                .append(result);
        sb.append(", playerScored=")
                .append(playerScored);
        sb.append(", opponentScore=")
                .append(opponentScore);
        sb.append(", gameId='")
                .append(gameId)
                .append('\'');
        sb.append('}');
        return sb.toString();
    }
}
