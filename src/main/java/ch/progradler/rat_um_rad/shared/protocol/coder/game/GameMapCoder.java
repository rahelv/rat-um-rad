package ch.progradler.rat_um_rad.shared.protocol.coder.game;

import ch.progradler.rat_um_rad.shared.models.game.City;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper;

import java.util.List;

public class GameMapCoder implements Coder<GameMap> {
    private Coder<City> cityCoder;
    private Coder<Road> roadCoder;

     public GameMapCoder(Coder<City> cityCoder, Coder<Road> roadCoder) {
         this.cityCoder = cityCoder;
         this.roadCoder = roadCoder;
     }
    @Override
    public String encode(GameMap map, int level) {
        if(map == null) {
            return "null";
        }
        List<String> citiesList = map.getCities().stream()
                .map((city) -> cityCoder.encode(city, level + 2))
                .toList();
        String citiesListEncoded = CoderHelper.encodeStringList(level + 1, citiesList);

        List<String> roadsList = map.getRoads().stream()
                .map((road) -> roadCoder.encode(road, level + 2))
                .toList();
        String roadsListEncoded = CoderHelper.encodeStringList(level + 1, roadsList);

        return CoderHelper.encodeFields(level,
                citiesListEncoded,
                roadsListEncoded);
    }

    @Override
    public GameMap decode(String encoded, int level) {
        if(encoded.equals("") || encoded.equals("null")) {
            return null;
        }
        List<String> fields = CoderHelper.decodeFields(level, encoded);

        List<String> citiesListString = CoderHelper.decodeStringList(level + 1, fields.get(0));
        List<City> citiesList = citiesListString.stream()
                .map((s) -> cityCoder.decode(s, level + 2)).toList();

        List<String> roadsListString = CoderHelper.decodeStringList(level + 1, fields.get(1));
        List<Road> roadsList = roadsListString.stream()
                .map((s) -> roadCoder.decode(s, level + 2)).toList();
        return new GameMap(citiesList, roadsList);
    }
}