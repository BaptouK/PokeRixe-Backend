package fr.baptouk.pokerixe.backend.game.play.lifecycle;

import fr.baptouk.pokerixe.backend.game.attack.GameAttack;
import fr.baptouk.pokerixe.backend.game.play.GamePlay;
import fr.baptouk.pokerixe.backend.game.play.GameStatus;
import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import fr.baptouk.pokerixe.backend.game.player.PlayerStatus;
import fr.baptouk.pokerixe.backend.game.pokemon.GamePokemon;
import fr.baptouk.pokerixe.backend.game.pokemon.PokemonTypeEffectiveness;
import fr.baptouk.pokerixe.backend.game.turn.Turn;
import fr.baptouk.pokerixe.backend.game.turn.action.Action;
import fr.baptouk.pokerixe.backend.game.turn.action.Attack;
import fr.baptouk.pokerixe.backend.game.turn.action.LogEntry;
import fr.baptouk.pokerixe.backend.game.turn.action.Switch;
import fr.baptouk.pokerixe.backend.game.websocket.packets.PacketFactory;
import fr.baptouk.pokerixe.backend.game.websocket.packets.game.lifecycle.MustSwichPacket;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
public final class GameLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(GameLifecycle.class);

    private final GamePlay gamePlay;
    private final Map<UUID, Action> playerActions = new HashMap<>();

    public void addPlayerActions(UUID user, Action action) {
        playerActions.put(user, action);
        gamePlay.getPlayersWhoActed().add(user);
    }

    public Mono<Void> fetchPokemons(WebClient pokeApiClient) {
        List<Mono<Void>> initTasks = gamePlay.getPlayers().stream()
                .map(player -> player.init(pokeApiClient))
                .toList();

        return Mono.when(initTasks)
                .doOnSuccess(v -> gamePlay.start())
                .doOnError(e -> logger.error("Erreur pendant le fetch", e));
    }

    public void computeTurn() {
        if (playerActions.size() < 2 || gamePlay.getPlayers().size() < 2) {
            return;
        }

        logger.info("Les 2 joueurs ont joué");

        List<GamePlayer> players = gamePlay.getPlayers();
        Map<UUID, Action> actions = new HashMap<>(this.playerActions);

        Turn turn = new Turn();
        turn.add(new LogEntry("turn_start", "Tour " + (gamePlay.getTurns().size() + 1)));

        resolveSwitchActions(players, actions);

        actions.forEach((uuid, action) -> {
            if (action instanceof Switch) {
                findPlayerById(players, uuid).ifPresent(player -> {
                    GamePokemon newPoke = getActivePokemon(player);
                    if (newPoke != null) {
                        turn.add(new LogEntry("switch", player.getPseudo() + " envoie " + newPoke.getName() + " !"));
                    }
                });
            }
        });

        GamePlayer p1 = players.get(0);
        GamePlayer p2 = players.get(1);
        GamePokemon poke1 = getActivePokemon(p1);
        GamePokemon poke2 = getActivePokemon(p2);

        if (poke1 == null || poke2 == null) {
            logger.warn("Impossible de résoudre le tour: un des Pokémon actifs est introuvable");
            gamePlay.getTurns().add(turn);
            turnFinish();
            return;
        }

        Attack p1Attack = getAttackAction(actions.get(p1.getId()));
        Attack p2Attack = getAttackAction(actions.get(p2.getId()));

        if (poke1.getStats().getSpeed() >= poke2.getStats().getSpeed()) {
            logAndResolveAttack(turn, p1, p2, poke1, poke2, p1Attack);
            if (poke2.getHp() > 0) {
                logAndResolveAttack(turn, p2, p1, poke2, poke1, p2Attack);
            }
        } else {
            logAndResolveAttack(turn, p2, p1, poke2, poke1, p2Attack);
            if (poke1.getHp() > 0) {
                logAndResolveAttack(turn, p1, p2, poke1, poke2, p1Attack);
            }
        }

        gamePlay.getTurns().add(turn);
        turnFinish();
    }

    public void damageCalcul(GamePokemon poke1, GamePokemon poke2, Attack attack) {

        double niveau = (50 * 0.4) + 2;

        double coeff = calculateTypeCoefficient(poke1, poke2, attack);

        logger.info("Coeff d'attaque : {}",coeff);

        double stab = 1;
        if (poke1.getType().contains(poke1.getAttacks().get(attack.getIndexAttack()).getType())) {
            stab *= 1.5;
        }

        int damage = (int) ((((poke1.getAttacks().get(attack.getIndexAttack()).getPower()
                * ((double) poke1.getStats().getAttack() / poke2.getStats().getDefense()) * niveau) / 50) + 2) * coeff * stab);

        poke2.setHp(poke2.getHp() - damage);
        if (poke2.getHp() <= 0) {
            poke2.setHp(0);
        }
    }

    private double calculateTypeCoefficient(GamePokemon attacker, GamePokemon defender, Attack attack) {
        GameAttack gameAttack = attacker.getAttacks().get(attack.getIndexAttack());
        return PokemonTypeEffectiveness.calculateTypeCoefficient(gameAttack.getType(), defender.getType());
    }

    private void resolveSwitchActions(List<GamePlayer> players, Map<UUID, Action> actions) {
        actions.forEach((uuid, action) -> {
            if (action instanceof Switch switchAction) {
                findPlayerById(players, uuid)
                        .ifPresent(player -> player.setIndexSelectedPokemon(switchAction.getIndexPokemon()));
            }
        });
    }

    private void logAndResolveAttack(Turn turn,
                                     GamePlayer attackerPlayer,
                                     GamePlayer defenderPlayer,
                                     GamePokemon attackerPokemon,
                                     GamePokemon defenderPokemon,
                                     Attack attack) {
        if (attack == null || defenderPokemon.getHp() <= 0 || attackerPokemon.getHp() <= 0) {
            return;
        }

        String attackName = attackerPokemon.getAttacks().get(attack.getIndexAttack()).getName();
        logger.info("{} attaque {} avec {}", attackerPlayer.getPseudo(), defenderPlayer.getPseudo(), attackName);

        turn.add(new LogEntry("attack", attackerPokemon.getName() + " utilise " + attackName + " !"));
        damageCalcul(attackerPokemon, defenderPokemon, attack);

        if (defenderPokemon.getHp() == 0) {
            turn.add(new LogEntry("faint", defenderPokemon.getName() + " de " + defenderPlayer.getPseudo() + " est K.O. !"));
        }
    }

    private Attack getAttackAction(Action action) {
        if (action instanceof Attack attack) {
            return attack;
        }
        return null;
    }

    private Optional<GamePlayer> findPlayerById(List<GamePlayer> players, UUID playerId) {
        return players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst();
    }

    private GamePokemon getActivePokemon(GamePlayer player) {
        Integer indexSelectedPokemon = player.getIndexSelectedPokemon();
        if (indexSelectedPokemon == null) {
            return null;
        }
        return player.getTeam().getPokemons().get(indexSelectedPokemon);
    }

    public void applyForcedSwitch(UUID playerId, int switchIndex) {
        findPlayerById(gamePlay.getPlayers(), playerId)
                .ifPresent(p -> p.setIndexSelectedPokemon(switchIndex));
        playerActions.remove(playerId);

        if (!gamePlay.getTurns().isEmpty()) {
            Turn lastTurn = gamePlay.getTurns().getLast();
            findPlayerById(gamePlay.getPlayers(), playerId).ifPresent(p -> {
                GamePokemon newPoke = getActivePokemon(p);
                if (newPoke != null) {
                    lastTurn.add(new LogEntry("switch", p.getPseudo() + " envoie " + newPoke.getName() + " !"));
                }
            });
        }

        gamePlay.getPlayers().forEach(p -> {
            GamePokemon active = getActivePokemon(p);
            if (active != null && active.getHp() == 0) {
                p.setStatus(PlayerStatus.MUST_SWITCH);
            } else {
                p.setStatus(PlayerStatus.WAITING_ACTION);
            }
        });

        if (gameIsFinished()) {
            turnFinish();
        } else {
            boolean anyMust = gamePlay.getPlayers().stream()
                    .anyMatch(p -> {
                        GamePokemon a = getActivePokemon(p);
                        return a != null && a.getHp() == 0;
                    });
            gamePlay.sendFullState(anyMust ? "waiting_switch" : "waiting_actions", "");
        }
    }

    public boolean isSwitchPending() {
        return gamePlay.getPlayers().stream().anyMatch(p -> {
            GamePokemon active = getActivePokemon(p);
            return active != null && active.getHp() == 0;
        });
    }

    private boolean gameIsFinished() {
        return gamePlay.getPlayers().stream()
                .anyMatch(player -> player.getTeam().getPokemons().stream()
                        .allMatch(pokemon -> pokemon.getHp() <= 0));
    }

    private Optional<GamePlayer> getWinner() {
        return gamePlay.getPlayers().stream()
                .filter(player -> player.getTeam().getPokemons().stream()
                        .anyMatch(pokemon -> pokemon.getHp() > 0))
                .findFirst();
    }

    public void turnFinish() {
        playerActions.clear();
        gamePlay.getPlayersWhoActed().clear();

        List<GamePlayer> players = gamePlay.getPlayers();
        GamePlayer p1 = players.get(0);
        GamePlayer p2 = players.get(1);

        if (gameIsFinished()) {
            String winnerPseudo = getWinner().map(GamePlayer::getPseudo).orElse("");
            logger.info("Partie terminée, gagnant : {}", winnerPseudo);

            if (!gamePlay.getTurns().isEmpty()) {
                gamePlay.getTurns().getLast()
                        .add(new LogEntry("fight_end", winnerPseudo + " remporte le combat !"));
            }

            getWinner().ifPresent(winner -> gamePlay.setWinnerId(winner.getId()));
            gamePlay.setDate(LocalDateTime.now());

            gamePlay.setStatus(GameStatus.FINISHED);
            gamePlay.sendFullState("finished", winnerPseudo);
            gamePlay.finishGame();
            closeSessions();
            return;
        }

        GamePokemon activeP1 = getActivePokemon(p1);
        GamePokemon activeP2 = getActivePokemon(p2);

        boolean p1MustSwitch = activeP1 != null && activeP1.getHp() == 0;
        boolean p2MustSwitch = activeP2 != null && activeP2.getHp() == 0;

        if (p1MustSwitch) {
            p1.setStatus(PlayerStatus.MUST_SWITCH);
        } else {
            p1.setStatus(PlayerStatus.WAITING_ACTION);
        }

        if (p2MustSwitch) {
            p2.setStatus(PlayerStatus.MUST_SWITCH);
        } else {
            p2.setStatus(PlayerStatus.WAITING_ACTION);
        }

        if (p1MustSwitch) {
            sendMustSwitch(p1);
        }
        if (p2MustSwitch) {
            sendMustSwitch(p2);
        }

        gamePlay.setStatus(GameStatus.PLAYING);
        gamePlay.sendFullState((p1MustSwitch || p2MustSwitch) ? "waiting_switch" : "waiting_actions", "");
    }

    private void closeSessions() {
        gamePlay.getPlayerSessions().values().forEach(sessionId -> {
            WebSocketSession session = PacketFactory.getSession(sessionId);
            if (session != null) {
                try {
                    session.close(CloseStatus.NORMAL);
                } catch (IOException e) {
                    logger.warn("Erreur lors de la fermeture de la session {}", sessionId, e);
                }
            }
        });
    }

    private void sendMustSwitch(GamePlayer player) {
        String sessionId = gamePlay.getPlayerSessions().get(player.getId());
        WebSocketSession session = PacketFactory.getSession(sessionId);

        if (session == null) {
            logger.warn("Impossible d'envoyer MustSwitch à {}: session introuvable", player.getPseudo());
            return;
        }

        new MustSwichPacket().send(session);
    }
}
