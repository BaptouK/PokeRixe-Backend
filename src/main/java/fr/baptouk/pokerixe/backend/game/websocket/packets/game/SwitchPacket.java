package fr.baptouk.pokerixe.backend.game.websocket.packets.game;

import fr.baptouk.pokerixe.backend.game.play.GamePlay;
import fr.baptouk.pokerixe.backend.game.player.PlayerStatus;
import fr.baptouk.pokerixe.backend.game.turn.action.Switch;
import fr.baptouk.pokerixe.backend.game.websocket.packets.ReceivablePacket;
import fr.baptouk.pokerixe.backend.game.websocket.packets.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public record SwitchPacket(int switchToSlotIndex) implements ReceivablePacket, SendablePacket {

    private static final Logger logger = LoggerFactory.getLogger(SwitchPacket.class);

    @Override
    public void handleRecieve(WebSocketSession session, UUID user, GamePlay game) {

        var player = game.getPlayers().stream()
                .filter(p -> p.getId().equals(user))
                .findFirst();

        if (player.isEmpty()) {
            logger.warn("Joueur {} introuvable dans la partie", user);
            return;
        }

        // Accepter le switch si en MUST_SWITCH (obligatoire) ou WAITING_ACTION (volontaire)
        if (game.lifecycle.isSwitchPending() && player.get().getStatus() != PlayerStatus.MUST_SWITCH) {
            logger.warn("Un switch obligatoire est en attente. Le joueur {} ne peut pas switcher maintenant.", user);
            return;
        }

        if (player.get().getStatus() != PlayerStatus.WAITING_ACTION && player.get().getStatus() != PlayerStatus.MUST_SWITCH) {
            logger.warn("Joueur {} ne peut pas switcher en ce moment (status: {})", user, player.get().getStatus());
            return;
        }

        logger.info("Un joueur a Switch");

        // Vérifier si c'est un switch obligatoire (Pokémon mort) ou volontaire
        var currentPokemon = game.getPlayers().stream()
                .filter(p -> p.getId().equals(user))
                .findFirst()
                .map(p -> p.getTeam().getPokemons().get(p.getIndexSelectedPokemon()))
                .orElse(null);

        boolean requiredSwitch = currentPokemon != null && currentPokemon.getHp() <= 0;

        if (requiredSwitch) {
            // Switch obligatoire : on applique directement et on termine le tour sans attendre l'adversaire
            logger.info("Joueur {} a switché son Pokémon mort, tour terminé", user);
            game.lifecycle.applyForcedSwitch(user, switchToSlotIndex);
        } else {
            // Switch volontaire : c'est une action du tour normale
            game.lifecycle.addPlayerActions(user, new Switch(switchToSlotIndex));
            game.lifecycle.computeTurn();
        }

    }
}
