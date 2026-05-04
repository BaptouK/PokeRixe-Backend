package fr.baptouk.pokerixe.backend.game.analysis;

import lombok.Data;

@Data
public class TurnFacts {

    private String moveType;
    private double multiplier = 1.0;
    private boolean attack = false;


}