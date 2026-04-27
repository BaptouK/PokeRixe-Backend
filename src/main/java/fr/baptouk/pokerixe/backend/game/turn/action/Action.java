package fr.baptouk.pokerixe.backend.game.turn.action;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public abstract class Action {

    private final String name;

    public Action(String name) {
        this.name = name;
    }

}
