package fr.baptouk.pokerixe.backend.game.turn;

import fr.baptouk.pokerixe.backend.game.turn.action.Action;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public final class Turn {

    private List<Action> actions = new ArrayList<>();

    public void add(Action action) {
        this.actions.add(action);
    }

}
