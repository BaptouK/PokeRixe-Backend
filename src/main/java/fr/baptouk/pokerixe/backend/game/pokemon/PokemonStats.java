package fr.baptouk.pokerixe.backend.game.pokemon;

import lombok.Data;

@Data
public class PokemonStats {

    private int  hp,attack,defense,specialAttack,specialDefense,speed;

    public PokemonStats(int hp, int attack, int defense, int specialAttack, int specialDefense, int speed) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
    }
}
