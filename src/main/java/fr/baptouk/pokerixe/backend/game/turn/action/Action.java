package fr.baptouk.pokerixe.backend.game.turn.action;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class Action {

    private final String actionType;
    private final String actionDescription;

}
