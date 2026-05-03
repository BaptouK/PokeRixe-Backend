package fr.baptouk.pokerixe.backend.game.websocket.dto;

import fr.baptouk.pokerixe.backend.game.play.GamePlay;
import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import fr.baptouk.pokerixe.backend.game.pokemon.GamePokemon;
import fr.baptouk.pokerixe.backend.game.turn.Turn;

public record FullStateGameDto(
        String gameId,
        int turnNumber,
        String FightPhase, // 'waiting_actions' | 'waiting_switch' | 'finished'
        CurrentPlayerStateDto player,
        OpponentPlayerStateDto opponent,
        Boolean playerHasActed,
        Boolean mustSwitch,
        java.util.List<Turn> turns,
        String winner
) {
    public static FullStateGameDto from(GamePlay game, GamePlayer currentPlayer, GamePlayer opponentPlayer, CurrentPlayerStateDto p1, OpponentPlayerStateDto p2, String fightPhase, String winner) {
        Integer activeIndex = currentPlayer.getIndexSelectedPokemon();
        GamePokemon activePokemon = activeIndex != null
                ? currentPlayer.getTeam().getPokemons().get(activeIndex)
                : null;
        boolean mustSwitch = activePokemon != null && activePokemon.getHp() == 0;

        boolean playerHasActed = game.getPlayersWhoActed().contains(currentPlayer.getId());

        return new FullStateGameDto(
                game.getId().toString(),
                game.getTurns().size(),
                fightPhase,
                p1,
                p2,
                playerHasActed,
                mustSwitch,
                game.getTurns(),
                winner
        );
    }
}
