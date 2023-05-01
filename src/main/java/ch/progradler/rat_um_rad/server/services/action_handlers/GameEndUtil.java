package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.PlayerEndResult;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;

import java.util.*;
import java.util.stream.Collectors;

import static ch.progradler.rat_um_rad.shared.util.GameConfig.MAX_WHEELS_LEFT_TO_END_GAME;

/**
 * Can determine whether a game has ended or not
 */
public class GameEndUtil {
    boolean isGameEnded(Game game) {
        for (Player player : game.getPlayers().values()) {
            if (player.getWheelsRemaining() <= MAX_WHEELS_LEFT_TO_END_GAME) {
                return true;
            }
        }
        return false;
    }

    void updateScoresAndEndResult(Game game) {
        for (String ipAddress : game.getPlayerIpAddresses()) {
            Player player = game.getPlayers().get(ipAddress);

            PlayerEndResult endResult = getPlayerEndResult(ipAddress, player, game);
            for (DestinationCard card : endResult.getAchievedShorts()) {
                player.setScore(player.getScore() + card.getPoints());
            }
            for (DestinationCard card : endResult.getNotAchievedShorts()) {
                player.setScore(player.getScore() - card.getPoints());
            }

            // TODO: re-add: int longDestCardsPoints = player.getLongDestinationCard().getPoints();
            player.setScore(player.getScore() /* TODO:+ (endResult.hasAchievedLong() ? 1 : -1) * longDestCardsPoints*/);

            player.setEndResult(endResult);
        }
    }

    private PlayerEndResult getPlayerEndResult(String ipAddress, Player player, Game game) {
        List<Road> roads = game.getMap().getRoads();
        Map<String, String> allRoadsBuilt = game.getRoadsBuilt();
        List<Road> builtRoadsOfPlayer = roads.stream()
                .filter((road) -> ipAddress.equals(allRoadsBuilt.get(road.getId()))).toList();

        List<DestinationCard> achieved = new ArrayList<>();
        List<DestinationCard> notAchieved = new ArrayList<>();

        List<Connection> connectedCities = getConnectedCities(builtRoadsOfPlayer);

        boolean achievedLong = connectedCities.contains(fromDestinationCard(player.getLongDestinationCard()));


        for (DestinationCard card : player.getShortDestinationCards()) {
            if (connectedCities.contains(fromDestinationCard(card))) {
                achieved.add(card);
            } else {
                notAchieved.add(card);
            }
        }

        return new PlayerEndResult(achieved, notAchieved, achievedLong);
    }

    Connection fromDestinationCard(DestinationCard card) {
        return new Connection(card.getDestination1().getId(), card.getDestination2().getId());
    }

    /**
     * @return List of city id pairs. Where the first item in the pair is alphabetically first.
     */
    private List<Connection> getConnectedCities(List<Road> builtRoads) {
        List<Connection> result = new ArrayList<>();
        for (Road road : builtRoads) {
            String fromCity = road.getFromCityId();
            List<Connection> allConnectedWithFromCity = result.stream()
                    .filter((connection) -> connection.isConnectedTo(fromCity))
                    .collect(Collectors.toList());

            String toCity = road.getToCityId();
            List<Connection> allConnectedWithToCity = result.stream()
                    .filter((connection) -> connection.isConnectedTo(toCity))
                    .collect(Collectors.toList());

            // all connected with from city are also connected with to city
            for (Connection connection : allConnectedWithFromCity) {
                result.add(new Connection(toCity, connection.otherCity(fromCity)));
            }
            // all connected with to city are also connected with from city
            for (Connection connection : allConnectedWithToCity) {
                result.add(new Connection(fromCity, connection.otherCity(toCity)));
            }

            result.add(new Connection(fromCity, road.getToCityId()));
        }
        return result;
    }

    /**
     * Holds 2 city ids alphabetically sorted.
     */
    private static class Connection {
        final List<String> cities;

        public Connection(String cityId1, String cityId2) {
            cities = Arrays.asList(cityId1, cityId2);
            Collections.sort(cities);
        }

        boolean isConnectedTo(String cityId) {
            return cities.contains(cityId);
        }

        String otherCity(String thisCity) {
            if (cities.get(0).equals(thisCity)) {
                return cities.get(1);
            }
            return cities.get(0);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Connection)) return false;
            Connection that = (Connection) o;
            return cities.equals(that.cities);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cities);
        }
    }
}
