package pl.setblack.pongi.scores.repo;

import javaslang.collection.List;
import javaslang.control.Option;
import pl.setblack.airomem.core.Persistent;
import pl.setblack.pongi.scores.ScoreRecord;
import pl.setblack.pongi.scores.UserScore;

import java.nio.file.Paths;

/**
 * @author Tomasz Smiechowicz
 */
public class ScoreRespositoryES implements ScoresRepository{

    private final Persistent<ScoresRepositoryInMem> persistentController;

    public ScoreRespositoryES() {
        this.persistentController = Persistent.loadOptional(
                Paths.get("airomem/score"), () -> new ScoresRepositoryInMem()
        );
    }

    @Override
    public void registerScore(List<ScoreRecord> rec) {
        this.persistentController.execute(scoreRepo -> scoreRepo.registerScore(rec));
    }

    @Override
    public Option<UserScore> getUserScore(String userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UserScore> getTopScores(int limit) {
        return this.persistentController.query(scoreRepo -> scoreRepo.getTopScores(limit));
    }
}
