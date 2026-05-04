package fr.baptouk.pokerixe.backend.game.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class GameAnalysis {

    private int score;

    private Map<String, Integer> scoreByTurn;

    private String advice;
}