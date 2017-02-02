package pl.setblack.pongi.games;

import javaslang.collection.List;
import javaslang.control.Either;
import javaslang.control.Option;
import javaslang.control.Try;
import pl.setblack.pongi.games.repo.GamesRepository;
import pl.setblack.pongi.games.repo.GamesRepositoryNonBlocking;
import pl.setblack.pongi.users.api.Session;
import pl.setblack.pongi.users.repo.SessionsRepo;
import ratpack.exec.Promise;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;
import ratpack.jackson.Jackson;
import ratpack.jackson.JsonRender;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by jarek on 2/1/17.
 */
public class GamesService {

    private final GamesRepositoryNonBlocking gamesRepo;

    private final SessionsRepo sessionsRepo;


    public GamesService(final GamesRepository gamesRepo, SessionsRepo sessionsRepo) {
        this.gamesRepo = new GamesRepositoryNonBlocking(gamesRepo);

        this.sessionsRepo = sessionsRepo;
    }

    public Action<Chain> define() {
        return chain -> chain
                .prefix("games", listGames());
    }

    private Action<? super Chain> createGame() {
        return chain -> chain.post("create", ctx -> {
            ctx.parse(String.class).then(gameName -> {
                final UUID uuid = UUID.randomUUID();
                renderAsync(
                        ctx,
                        sess -> gamesRepo.createGame(uuid.toString(),gameName, sess.userId),
                        ()-> Try.failure(new IllegalArgumentException("no user session")));
            });

        });
    }

    private Action<? super Chain> listGames() {
        return chain -> chain.get(
                ctx -> renderAsync(ctx, (any) -> gamesRepo.listGames(),
                ()-> List.empty()));
    }

    private <T> void renderAsync(Context ctx,
                                 Function<Session, CompletionStage<T>> async,
                                 Supplier<T> alternative) {
        String bearer = ctx.getRequest().getHeaders().get("Authorization");
        final String sessionId = bearer.replace("bearer ","");
        final Option<Session> session = sessionsRepo.getSession(sessionId);

        final Promise<JsonRender> result = Promise.async(d ->
            d.accept(session.map(
                    sess -> {
                        final CompletionStage<JsonRender> future = async.apply(sess).thenApply(Jackson::json);
                        return future;
                    })
                    .getOrElse(
                            CompletableFuture.completedFuture(alternative.get())
                                    .thenApply(Jackson::json)))
        );
        ctx.render(result);

    }
}