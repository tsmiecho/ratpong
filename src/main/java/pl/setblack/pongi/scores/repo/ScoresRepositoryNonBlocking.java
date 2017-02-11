package pl.setblack.pongi.scores.repo;

import javaslang.collection.List;
import javaslang.control.Option;
import pl.setblack.pongi.scores.ScoreRecord;
import pl.setblack.pongi.scores.UserScore;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jarek on 2/5/17.
 */
public class ScoresRepositoryNonBlocking {

    private final ScoresRepository scoresRepository;

    private final Executor writesExecutor = Executors.newSingleThreadExecutor();

    public ScoresRepositoryNonBlocking(ScoresRepository repository) {
        this.scoresRepository = repository;
    }

    public void registerScore(List<ScoreRecord> rec){
        writesExecutor.execute(() -> scoresRepository.registerScore(rec));
    }

    public CompletionStage<Option<UserScore>> getUserScore(String userId) {
        final CompletableFuture<Option<UserScore>> result = new CompletableFuture<>();
        writesExecutor.execute( ()-> {
            result.complete(this.scoresRepository.getUserScore(userId));
        });
        return result;
    }

    public CompletionStage<List<UserScore>> getTopScores(final int limit) {
        final CompletableFuture<List<UserScore>> result = new CompletableFuture<>();
        writesExecutor.execute( ()-> {
            result.complete(this.scoresRepository.getTopScores(limit));
        });
        return result;
    }
}
