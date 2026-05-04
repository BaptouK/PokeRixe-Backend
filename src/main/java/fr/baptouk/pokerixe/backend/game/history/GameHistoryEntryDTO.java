package fr.baptouk.pokerixe.backend.game.history;

import fr.baptouk.pokerixe.backend.game.analysis.GameAnalysis;
import fr.baptouk.pokerixe.backend.game.turn.Turn;

import java.util.List;

public record GameHistoryEntryDTO(
        String id,
        String date,
        String opponentName,
        String result,
        int turnCount,
        GameAnalysis analysis,
        List<HistoryPokemonDTO> playerTeam,
        List<HistoryPokemonDTO> opponentTeam,
        List<Turn> log
) {}
