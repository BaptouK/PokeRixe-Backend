package fr.baptouk.pokerixe.backend.game.analysis;

import java.util.Map;
import java.util.UUID;

public final class GameAnalysis {

    private UUID id = UUID.randomUUID();

    private int score;

    private Map<Integer,Integer> scoreByTurn;

    private String advice;
}
