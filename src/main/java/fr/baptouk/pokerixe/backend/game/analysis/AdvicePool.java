package fr.baptouk.pokerixe.backend.game.analysis;

import java.util.Map;

public final class AdvicePool {

    public static final Map<String, String> CONSEILS_POOL = Map.of(
            "T1", "Tu dois mieux gérer les résistances.",
            "T2", "Essaye de placer tes boosts plus tôt.",
            "T3", "Attention aux attaques inefficaces.",
            "T4", "Ton switch défensif était mauvais.",
            "T5", "Bonne anticipation sur ce tour.",
            "T6", "Tu peux mieux exploiter les faiblesses.",
            "T7", "Analyse correcte mais améliorable."
    );

    private AdvicePool() {
    }
}