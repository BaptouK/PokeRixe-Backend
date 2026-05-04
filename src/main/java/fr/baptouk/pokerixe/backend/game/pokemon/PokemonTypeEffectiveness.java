package fr.baptouk.pokerixe.backend.game.pokemon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonTypeEffectiveness {

    private static final Map<PokemonType, TypeMatchup> EFFECTIVENESS_TABLE = new HashMap<>();

    static {

        EFFECTIVENESS_TABLE.put(PokemonType.NORMAL, new TypeMatchup(
                List.of(), // Super efficace contre
                List.of(PokemonType.ROCK, PokemonType.STEEL), // Peu efficace contre
                List.of(PokemonType.GHOST) // Sans effet contre
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.FIRE, new TypeMatchup(
                List.of(PokemonType.GRASS, PokemonType.ICE, PokemonType.BUG, PokemonType.STEEL),
                List.of(PokemonType.FIRE, PokemonType.WATER, PokemonType.ROCK, PokemonType.DRAGON),
                List.of()
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.WATER, new TypeMatchup(
                List.of(PokemonType.FIRE, PokemonType.GROUND, PokemonType.ROCK),
                List.of(PokemonType.WATER, PokemonType.GRASS, PokemonType.DRAGON),
                List.of()
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.ELECTRIC, new TypeMatchup(
                List.of(PokemonType.WATER, PokemonType.FLYING),
                List.of(PokemonType.ELECTRIC, PokemonType.GRASS, PokemonType.DRAGON),
                List.of()
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.GRASS, new TypeMatchup(
                List.of(PokemonType.WATER, PokemonType.GROUND, PokemonType.ROCK),
                List.of(PokemonType.FIRE, PokemonType.GRASS, PokemonType.POISON, PokemonType.FLYING, PokemonType.BUG),
                List.of()
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.ICE, new TypeMatchup(
                List.of(PokemonType.FLYING, PokemonType.GROUND, PokemonType.GRASS, PokemonType.DRAGON),
                List.of(PokemonType.FIRE, PokemonType.WATER, PokemonType.ICE, PokemonType.STEEL),
                List.of()
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.FIGHTING, new TypeMatchup(
                List.of(PokemonType.NORMAL, PokemonType.ICE, PokemonType.ROCK, PokemonType.DARK, PokemonType.STEEL),
                List.of(PokemonType.FLYING, PokemonType.PSYCHIC, PokemonType.FAIRY),
                List.of(PokemonType.GHOST)
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.POISON, new TypeMatchup(
                List.of(PokemonType.GRASS, PokemonType.FAIRY),
                List.of(PokemonType.POISON, PokemonType.GROUND, PokemonType.ROCK),
                List.of(PokemonType.STEEL)
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.GROUND, new TypeMatchup(
                List.of(PokemonType.FIRE, PokemonType.ELECTRIC, PokemonType.POISON, PokemonType.ROCK, PokemonType.STEEL),
                List.of(PokemonType.GRASS, PokemonType.BUG),
                List.of(PokemonType.FLYING)
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.FLYING, new TypeMatchup(
                List.of(PokemonType.FIGHTING, PokemonType.BUG, PokemonType.GRASS),
                List.of(PokemonType.ELECTRIC, PokemonType.ROCK, PokemonType.STEEL),
                List.of()
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.PSYCHIC, new TypeMatchup(
                List.of(PokemonType.FIGHTING, PokemonType.POISON),
                List.of(PokemonType.PSYCHIC, PokemonType.STEEL),
                List.of(PokemonType.DARK)
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.BUG, new TypeMatchup(
                List.of(PokemonType.GRASS, PokemonType.PSYCHIC, PokemonType.DARK),
                List.of(PokemonType.FIRE, PokemonType.FLYING, PokemonType.ROCK),
                List.of()
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.ROCK, new TypeMatchup(
                List.of(PokemonType.FIRE, PokemonType.ICE, PokemonType.FLYING, PokemonType.BUG),
                List.of(PokemonType.FIGHTING, PokemonType.GROUND, PokemonType.STEEL),
                List.of()
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.GHOST, new TypeMatchup(
                List.of(PokemonType.PSYCHIC, PokemonType.GHOST),
                List.of(PokemonType.DARK),
                List.of(PokemonType.NORMAL, PokemonType.FIGHTING)
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.DRAGON, new TypeMatchup(
                List.of(PokemonType.DRAGON),
                List.of(PokemonType.STEEL),
                List.of()
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.DARK, new TypeMatchup(
                List.of(PokemonType.PSYCHIC, PokemonType.GHOST),
                List.of(PokemonType.FIGHTING, PokemonType.DARK, PokemonType.FAIRY),
                List.of()
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.STEEL, new TypeMatchup(
                List.of(PokemonType.ICE, PokemonType.ROCK, PokemonType.FAIRY),
                List.of(PokemonType.FIRE, PokemonType.WATER, PokemonType.ELECTRIC, PokemonType.STEEL),
                List.of(PokemonType.POISON)
        ));

        EFFECTIVENESS_TABLE.put(PokemonType.FAIRY, new TypeMatchup(
                List.of(PokemonType.FIGHTING, PokemonType.DARK, PokemonType.DRAGON),
                List.of(PokemonType.FIRE, PokemonType.POISON, PokemonType.STEEL),
                List.of()
        ));
    }

    public static double calculateTypeCoefficient(PokemonType attackType, List<PokemonType> defenderTypes) {
        TypeMatchup matchup = EFFECTIVENESS_TABLE.get(attackType);

        if (matchup == null || defenderTypes == null || defenderTypes.isEmpty()) {
            return 1.0;
        }

        double coefficient = 1.0;

        for (PokemonType defenderType : defenderTypes) {
            if (matchup.superEffective.contains(defenderType)) {
                coefficient *= 2.0;
            } else if (matchup.notVeryEffective.contains(defenderType)) {
                coefficient *= 0.5;
            } else if (matchup.noDamage.contains(defenderType)) {
                coefficient *= 0.0;
            }
        }

        return coefficient;
    }

    private static class TypeMatchup {
        List<PokemonType> superEffective;
        List<PokemonType> notVeryEffective;
        List<PokemonType> noDamage;

        TypeMatchup(List<PokemonType> superEffective, List<PokemonType> notVeryEffective, List<PokemonType> noDamage) {
            this.superEffective = superEffective;
            this.notVeryEffective = notVeryEffective;
            this.noDamage = noDamage;
        }
    }
}

