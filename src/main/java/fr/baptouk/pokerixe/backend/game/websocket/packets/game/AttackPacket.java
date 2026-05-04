package fr.baptouk.pokerixe.backend.game.websocket.packets.game;


import fr.baptouk.pokerixe.backend.game.play.GamePlay;
import fr.baptouk.pokerixe.backend.game.player.PlayerStatus;
import fr.baptouk.pokerixe.backend.game.turn.action.Attack;
import fr.baptouk.pokerixe.backend.game.websocket.packets.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public record AttackPacket(int moveSlot) implements ReceivablePacket {

    private static final Logger logger = LoggerFactory.getLogger(AttackPacket.class);

    @Override
    public void handleRecieve(WebSocketSession session, UUID user, GamePlay game) {

        var player = game.getPlayers().stream()
                .filter(p -> p.getId().equals(user))
                .findFirst();

        if (player.isEmpty()) {
            logger.warn("Joueur {} introuvable dans la partie", user);
            return;
        }

        if (player.get().getStatus() != PlayerStatus.WAITING_ACTION) {
            logger.warn("Joueur {} ne peut pas attaquer en ce moment (status: {})", user, player.get().getStatus());
            return;
        }

        // Vérifier que le Pokémon actif n'est pas mort
        var activePokemon = player.get().getTeam().getPokemons().get(player.get().getIndexSelectedPokemon());
        if (activePokemon.getHp() <= 0) {
            logger.warn("Joueur {} doit d'abord switcher son Pokémon mort", user);
            return;
        }

        if (game.lifecycle.isSwitchPending() && player.get().getStatus() != PlayerStatus.MUST_SWITCH) {
            logger.warn("Un switch obligatoire est en attente. Le joueur {} ne peut pas attaquer maintenant.", user);
            return;
        }

        if (moveSlot < 0) {
            logger.warn("MoveSlot invalide reçu de {}: {}", user, moveSlot);
            return;
        }

        logger.info("Un joueur a attaqué");

        game.lifecycle.addPlayerActions(user, new Attack(moveSlot));

        game.lifecycle.computeTurn();


    }
}
