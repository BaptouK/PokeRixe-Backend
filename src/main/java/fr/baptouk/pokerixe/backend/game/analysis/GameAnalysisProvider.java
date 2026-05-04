package fr.baptouk.pokerixe.backend.game.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.baptouk.pokerixe.backend.game.Game;
import fr.baptouk.pokerixe.backend.game.turn.Turn;
import fr.baptouk.pokerixe.backend.game.turn.action.Action;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public final class GameAnalysisProvider {

    private static final String POKEAPI_URL = "https://pokeapi.co/api/v2";

    @Autowired
    private OllamaApi ollamaApi;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ollama.model}")
    private String modelName;

    @Value("${ollama.prompt.file.path}")
    private String promptFilePath;

    public CompletableFuture<GameAnalysis> startAnalysis(final Game game) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final String prompt = this.generatePrompt(game);

                log.info("Generated prompt for analysis: {}", prompt);

                OllamaApi.ChatResponse response = ollamaApi.chat(
                        OllamaApi.ChatRequest.builder(this.modelName)
                                .messages(Collections.singletonList(
                                        OllamaApi.Message.builder(OllamaApi.Message.Role.USER)
                                                .content(prompt)
                                                .build()
                                ))
                                .build()
                );

                String content = response.message().content();

                content = content
                        .replace("```json", "")
                        .replace("```", "")
                        .trim();

                log.info("Received analysis response: {}", content);

                return objectMapper.readValue(content, GameAnalysis.class);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    private String generatePrompt(final Game game) throws IOException {
        final String template = Files.readString(
                new File(this.promptFilePath).toPath(),
                StandardCharsets.UTF_8
        );

        final List<Turn> turns = game.getTurns();

        final Map<String, Integer> scoresTours = new LinkedHashMap<>();
        final StringBuilder logsBruts = new StringBuilder();

        final List<String> playerPokemons = game.getPlayers().getFirst().getTeam().getPokemons()
                .stream()
                .map(p -> p.getName().toLowerCase())
                .toList();

        final List<String> opponentPokemons = game.getPlayers().getLast().getTeam().getPokemons()
                .stream()
                .map(p -> p.getName().toLowerCase())
                .toList();

        for (int i = 0; i < turns.size(); i++) {
            final Turn turn = turns.get(i);

            final StringBuilder turnDesc = new StringBuilder();

            for (Action action : turn.getActions()) {
                turnDesc.append(action.getActionDescription()).append(" ");
            }

            logsBruts
                    .append("T")
                    .append(i + 1)
                    .append(": ")
                    .append(turnDesc)
                    .append(" | ");

            final TurnFacts facts = this.buildTurnFacts(
                    turn,
                    playerPokemons,
                    opponentPokemons
            );

            int score;

            if (!facts.isAttack() && facts.getMoveType() != null) {
                score = 10;
            } else {
                final double mult = facts.getMultiplier();

                if (mult == 0) {
                    score = 0;
                } else if (mult <= 0.5) {
                    score = 5;
                } else if (mult >= 2) {
                    score = 20;
                } else {
                    score = 10;
                }
            }

            scoresTours.put("Tour " + (i + 1), score);
        }

        return template
                .replace("{logs_bruts}", logsBruts.toString())
                .replace("{scores_tours}", objectMapper.writeValueAsString(scoresTours))
                .replace("{conseils_json}", objectMapper.writeValueAsString(AdvicePool.CONSEILS_POOL));
    }

    private TurnFacts buildTurnFacts(
            final Turn turn,
            final List<String> playerPokemons,
            final List<String> opponentPokemons
    ) {
        final TurnFacts facts = new TurnFacts();

        final Pattern pattern = Pattern.compile(
                "(\\w+) (?:utilise|used) ([\\w\\s-]+)!",
                Pattern.CASE_INSENSITIVE
        );

        for (Action action : turn.getActions()) {
            final String desc = action.getActionDescription();

            final Matcher matcher = pattern.matcher(desc);

            if (!matcher.find()) {
                continue;
            }

            final String pokemonName = matcher.group(1).toLowerCase();
            final String moveName = matcher.group(2).trim();

            if (!playerPokemons.contains(pokemonName)) {
                continue;
            }

            final MoveInfo moveInfo = this.getMoveInfo(moveName);

            final List<String> targetTypes = this.getPokemonTypes(
                    opponentPokemons.get(0)
            );

            facts.setMoveType(moveInfo.type());
            facts.setAttack(!moveInfo.damageClass().equalsIgnoreCase("status"));

            final double multiplier = this.getTypeEfficacy(
                    moveInfo.type(),
                    targetTypes
            );

            facts.setMultiplier(multiplier);

            break;
        }

        return facts;
    }

    private List<String> getPokemonTypes(final String pokemonName) {
        try {
            final String url = POKEAPI_URL + "/pokemon/" + pokemonName.toLowerCase();

            final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            final JsonNode root = objectMapper.readTree(response.getBody());

            final List<String> result = new ArrayList<>();

            for (JsonNode typeNode : root.get("types")) {
                result.add(
                        typeNode
                                .get("type")
                                .get("name")
                                .asText()
                                .toUpperCase()
                );
            }

            return result;
        } catch (Exception e) {
            return List.of("NORMAL");
        }
    }

    private MoveInfo getMoveInfo(final String moveName) {
        try {
            final String slug = moveName
                    .toLowerCase()
                    .trim()
                    .replace(" ", "-");

            final String url = POKEAPI_URL + "/move/" + slug;

            final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            final JsonNode root = objectMapper.readTree(response.getBody());

            final String type = root
                    .get("type")
                    .get("name")
                    .asText()
                    .toUpperCase();

            final String damageClass = root
                    .get("damage_class")
                    .get("name")
                    .asText();

            return new MoveInfo(type, damageClass);

        } catch (Exception e) {
            return new MoveInfo("NORMAL", "physical");
        }
    }

    private double getTypeEfficacy(
            final String moveType,
            final List<String> defenderTypes
    ) {
        try {
            final String url = POKEAPI_URL + "/type/" + moveType.toLowerCase();

            final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            final JsonNode root = objectMapper.readTree(response.getBody());
            final JsonNode damageRelations = root.get("damage_relations");

            final Set<String> noDamage = extractTypeSet(
                    damageRelations.get("no_damage_to")
            );

            final Set<String> halfDamage = extractTypeSet(
                    damageRelations.get("half_damage_to")
            );

            final Set<String> doubleDamage = extractTypeSet(
                    damageRelations.get("double_damage_to")
            );

            double multiplier = 1.0;

            for (String defenderType : defenderTypes) {
                if (noDamage.contains(defenderType)) {
                    return 0.0;
                }

                if (doubleDamage.contains(defenderType)) {
                    multiplier *= 2.0;
                }

                if (halfDamage.contains(defenderType)) {
                    multiplier *= 0.5;
                }
            }

            return multiplier;

        } catch (Exception e) {
            return 1.0;
        }
    }

    private Set<String> extractTypeSet(final JsonNode array) {
        final Set<String> result = new HashSet<>();

        for (JsonNode node : array) {
            result.add(node.get("name").asText().toUpperCase());
        }

        return result;
    }
}