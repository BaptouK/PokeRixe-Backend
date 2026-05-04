package fr.baptouk.pokerixe.backend.game.provider;

import fr.baptouk.pokerixe.backend.game.Game;
import fr.baptouk.pokerixe.backend.game.play.GameCreationResponse;
import fr.baptouk.pokerixe.backend.game.play.GamePlay;
import fr.baptouk.pokerixe.backend.game.play.GameStatus;
import fr.baptouk.pokerixe.backend.game.mapper.GameMapper;
import fr.baptouk.pokerixe.backend.game.provider.exceptions.GameNotFoundException;
import fr.baptouk.pokerixe.backend.game.provider.exceptions.UserAlreadyInGameException;
import fr.baptouk.pokerixe.backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public final class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private WebClient pokeApiClient;

    /**
     * Liste des parties en cours, qu'elles soient vides ou non.
     */
    private final List<GamePlay> games = new ArrayList<>();

    public Iterable<Game> getGames() {
        return gameRepository.findAll();
    }

    public Game getGame(final UUID gameId) throws GameNotFoundException {
        return gameRepository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);
    }

    /**
     * Créer une partie, la partie est créée avec le joueur qui la crée en local et n'apparaît pas encore dans la base de données
     * Le joueur peut alors le voir dans la liste des parties à rejoindre.
     *
     * @param user {@link User} qui crée la partie
     * @return Un token websocket qui appartient à la partie
     */
    public GameCreationResponse createGame(final User user, final String description, final Integer pokemonTeamSlot) throws UserAlreadyInGameException {
        if (this.games.stream()
                .anyMatch(gamePlay -> gamePlay.getPlayers().stream()
                        .anyMatch(player -> player.getId().equals(user.getId())))) {
            throw new UserAlreadyInGameException();
        }

        final int pokemonCount = user.getTeam().getPokemons().size();

        final GamePlay game = new GamePlay(description);
        game.setGameService(this);
        game.setPokeApiClient(pokeApiClient);

        game.addPlayer(gameMapper.toGamePlayer(user, pokemonTeamSlot));

        this.games.add(game);
        return new GameCreationResponse(game.getId(), game.getUserToken(user.getId()));
    }

    public String joinGame(final User user, final UUID gameId, final int selectSlotPokemon) throws GameNotFoundException {
        final GamePlay gamePlay = this.games.stream()

                .filter(games -> games.getId().equals(gameId))
                .filter(games -> games.getStatus() == GameStatus.WAITING)
                .filter(games -> games.getPlayers().size() < 2)

                .findFirst()
                .orElseThrow(GameNotFoundException::new);

        gamePlay.addPlayer(gameMapper.toGamePlayer(user, selectSlotPokemon));


        /*
         * Le lancement de la partie se fait uniquement quand les deux joueurs sont connectés au websocket.
         */
        return gamePlay.getUserToken(user.getId());
    }

    public List<GamePlay> getAvailableGames(){
        return this.games.stream()
                .filter(gamePlay -> gamePlay.getStatus() == GameStatus.WAITING
                        && gamePlay.getPlayers().size() < 2)
                .toList();
    }

    public List<GamePlay> getGamePlays(){
        return this.games;
    }

    public Optional<GamePlay> getGameByToken(final String token){
        return this.games.stream()
                .filter(gamePlay -> gamePlay.getUserByToken(token) != null)
                .findFirst();
    }

    public List<Game> getHistory(UUID userId) {
        return this.gameRepository.findAll()
                .stream()
                .filter(game -> game.getPlayers().stream()
                        .anyMatch(player -> player.getId().equals(userId)))
                .toList();
    }

    public void saveGame(Game game){
        gameRepository.save(game);
    }

    public void removeGame(UUID gameId) {
        this.games.removeIf(g -> g.getId().equals(gameId));
    }
}
