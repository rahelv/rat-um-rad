package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.util.GameConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.*;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.ROAD_BUILT;

/**
 * Handles build road action
 */
public class RoadActionHandler extends ActionHandler<String> {
    /**
     * Is init in {@link RoadActionHandler#validate}.
     * Is saved as variable to save time when retrieving it from
     * game twice (validation and game state update).
     */
    private Road road;

    public RoadActionHandler(IGameRepository gameRepository, IUserRepository userRepository, OutputPacketGateway outputPacketGateway, GameEndUtil gameEndUtil) {
        super(gameRepository, userRepository, outputPacketGateway, gameEndUtil);
    }

    @Override
    protected String validate(Game game, String ipAddress, String roadId) {
        Map<String, String> roadsBuilt = game.getRoadsBuilt();
        if (roadsBuilt.containsKey(roadId)) return ROAD_ALREADY_BUILT_ON;

        List<Road> roads = game.getMap().getRoads();
        Optional<Road> roadOpt = roads.stream().filter((r) -> r.getId().equals(roadId)).findFirst();

        if (roadOpt.isEmpty()) return ROAD_DOES_NOT_EXIST;

        road = roadOpt.get();
        Player player = game.getPlayers().get(ipAddress);

        if (player.getWheelsRemaining() < road.getRequiredWheels()) {
            return NOT_ENOUGH_WHEELS_TO_BUILD_ROAD;
        }

        if (!hasRequiredCardsToBuild(player, road)) return NOT_ENOUGH_CARDS_OF_REQUIRED_COLOR_TO_BUILD_ROAD;
        return null;
    }

    private Road getRoad(Game game, String roadId) {
        if (road != null) return road;
        List<Road> roads = game.getMap().getRoads();
        Optional<Road> roadOpt = roads.stream()
                .filter((r) -> r.getId().equals(roadId)).findFirst();

        return roadOpt.get();
    }

    private boolean hasRequiredCardsToBuild(Player player, Road road) {
        List<WheelCard> playersCardsOfColor = player.getWheelCards().stream()
                .filter((c) -> c.getColor() == road.getColor())
                .toList();
        return playersCardsOfColor.size() >= road.getRequiredWheels();
    }

    @Override
    protected void updateGameState(Game game, String ipAddress, String roadId) {
        Player player = game.getPlayers().get(ipAddress);
        road = getRoad(game, roadId);

        List<WheelCard> playersCardsOfColor = new ArrayList<>(player.getWheelCards().stream()
                .filter((c) -> c.getColor() == road.getColor())
                .toList());

        for (int i = 0; i < road.getRequiredWheels(); i++) {
            player.getWheelCards().remove(playersCardsOfColor.get(i));
        }

        player.setWheelsRemaining(player.getWheelsRemaining() - road.getRequiredWheels());
        player.setScore(player.getScore() + GameConfig.scoreForRoadBuild(road.getRequiredWheels()));

        // TODO: check if has very few wheels left -> send info that game will finish soon

        game.getRoadsBuilt().put(road.getId(), ipAddress);
    }

    @Override
    protected boolean canEndGame() {
        return true;
    }

    @Override
    protected ServerCommand getActivityCommand() {
        return ROAD_BUILT;
    }
}
