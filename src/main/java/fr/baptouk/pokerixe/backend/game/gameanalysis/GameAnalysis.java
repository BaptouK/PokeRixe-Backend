package fr.baptouk.pokerixe.backend.game.gameanalysis;

import org.springframework.data.annotation.Id;

import java.util.Map;
import java.util.UUID;

public class GameAnalysis {

    private UUID id = UUID.randomUUID();

    private int score;

    private Map<Integer,Integer> score_tour;

    private String conseil;
}
