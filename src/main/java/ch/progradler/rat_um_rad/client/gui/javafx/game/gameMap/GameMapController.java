package ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap;

import ch.progradler.rat_um_rad.client.gui.javafx.game.UiUtil;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.shared.models.Point;
import ch.progradler.rat_um_rad.shared.models.game.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shows the current Game Map
 */
public class GameMapController extends GridPane {
    private static final double CITY_GRAPHIC_DIM = 16;

    private GameService gameService;
    private GameMapModel gameMapModel;
    @FXML
    private ComboBox<Road> roadsToBuildListView;
    @FXML
    private Label gameID;
    @FXML
    private Label status;
    @FXML
    private Label createdAt;
    @FXML
    private Label requiredPlayers;
    @FXML
    private Pane mapPane;

    @FXML
    private Group mapObjectsGroup;
    private static final int ROAD_LINE_WIDTH = 8;


    public GameMapController() {
        this.gameService = new GameService();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/game/GameMapView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void initData(GameMapModel gameMapModel) {
        this.gameMapModel = gameMapModel;
        Platform.runLater(() -> {
            drawMap(gameMapModel.getGameMap());
            gameID.setText(this.gameMapModel.getGameID());
            status.setText(this.gameMapModel.getStatus().toString());
            createdAt.setText(this.gameMapModel.getCreatedAt().toString());
            requiredPlayers.setText(String.valueOf(this.gameMapModel.getRequiredPlayers()));
        });
        this.gameMapModel.updateFields();

        setRoadObservableList();
        this.gameMapModel.setBuiltRoadObservableList();
    }

    private void drawMap(GameMap gameMap) {
        drawRoads(gameMap.getRoads(), gameMapModel.getCitiesMap());
        drawCities(gameMap.getCities());
    }


    private void drawCities(List<City> cities) {
        for (City city : cities) {
            double x = city.getPoint().getX();
            double y = city.getPoint().getY();
            Rectangle cityGraphic = new Rectangle(x - CITY_GRAPHIC_DIM / 2, y - CITY_GRAPHIC_DIM / 2,
                    CITY_GRAPHIC_DIM, CITY_GRAPHIC_DIM);
            cityGraphic.setFill(Color.DARKGRAY);

            this.mapObjectsGroup.getChildren().addAll(cityGraphic, cityNameText(city, x, y));
        }
    }

    private Text cityNameText(City city, double x, double y) {
        Text name = new Text(x, y - 10, city.getName());
        name.setStroke(Color.WHITE);
        name.setStrokeWidth(0.8);
        name.setFont(Font.font(null, FontWeight.BOLD, 14));
        name.setFill(Color.BLACK);
        return name;
    }

    private void drawRoads(List<Road> roads, Map<String, City> cities) {
        Map<String, String> roadsBuilt = gameMapModel.getRoadsBuilt();

        for (Road road : roads) {
            drawRoad(cities, roadsBuilt, road);
        }
    }

    private void drawRoad(Map<String, City> cities, Map<String, String> roadsBuilt, Road road) {
        City from = cities.get(road.getFromCityId());
        City to = cities.get(road.getToCityId());
        Point start = from.getPoint();
        Point end = to.getPoint();

        Line roadLine = getRoadLine(road, from, to, start, end);
        mapObjectsGroup.getChildren().add(roadLine);

        String builtBy = roadsBuilt.get(road.getId());
        if (builtBy == null) return;
        PlayerColor playerColor = gameMapModel.getPlayerColor(builtBy);
        mapObjectsGroup.getChildren().add(getWheelLine(road, start, end, playerColor));
    }

    private Line getRoadLine(Road road, City from, City to, Point start, Point end) {
        List<Double> strokes = getRoadStrokes(road.getRequiredWheels(), start, end);

        Line roadLine = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        roadLine.setStroke(Color.valueOf(road.getColor().toString()));
        roadLine.setStrokeWidth(ROAD_LINE_WIDTH);

        roadLine.getStrokeDashArray().addAll(strokes);

        roadLine.setOnMouseClicked(event -> {
            System.out.println("Wants to build from " + from.getName() + " to " + to.getName());
            try {
                this.gameService.buildRoad(road.getId());
            } catch (IOException e) {
                e.printStackTrace(); //TODO: signal failure to user an let him build the road again
            }
        });
        return roadLine;
    }

    private Line getWheelLine(Road road, Point start, Point end, PlayerColor color) {
        Line wheelLine = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        wheelLine.setStroke(UiUtil.playerColor(color)); // TODO: fix!
        wheelLine.setStrokeWidth(ROAD_LINE_WIDTH * 0.7);

        wheelLine.getStrokeDashArray().addAll(getWheelStrokes(road.getRequiredWheels(), start, end));

        return wheelLine;
    }

    private List<Double> getRoadStrokes(int requiredWheels, Point start, Point end) {
        double dx = start.getX() - end.getX();
        double dy = start.getY() - end.getY();
        double d = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

        List<Double> strokes = new ArrayList<>();
        double gapLength = d / (4 * requiredWheels - 1);
        double strokeLength = 3 * gapLength;
        for (int i = 0; i < requiredWheels; i++) {
            strokes.add(strokeLength);
            strokes.add(gapLength);
        }
        return strokes;
    }

    private List<Double> getWheelStrokes(int requiredWheels, Point start, Point end) {
        double dx = start.getX() - end.getX();
        double dy = start.getY() - end.getY();
        double d = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

        List<Double> strokes = new ArrayList<>();
        double roadGapLength = d / (4 * requiredWheels - 1);
        double roadStrokeLength = 3 * roadGapLength;

        double strokeLength = 0.6 * roadStrokeLength;
        double wheelRoadDiff = (roadStrokeLength - strokeLength);
        double wheelRoadDiffDiv2 = wheelRoadDiff / 2;

        double gapLength = roadGapLength + wheelRoadDiff;

        strokes.add(0.1); // line (not visible)
        strokes.add(wheelRoadDiffDiv2); // gap
        for (int i = 0; i < requiredWheels; i++) {
            strokes.add(strokeLength);
            strokes.add(gapLength);
        }
        strokes.add(0.1); // line (not visible)
        strokes.add(wheelRoadDiffDiv2); // gap
        return strokes;
    }

    private void setRoadObservableList() {
        this.gameMapModel.setRoadsToBuildObservableList();
        roadsToBuildListView.setItems(this.gameMapModel.getRoadsToBuildObservableList());
        roadsToBuildListView.setConverter(new StringConverter<>() {
            @Override
            public String toString(Road road) {
                if (road == null) {
                    return "";
                }
                return road.getId();
            }

            @Override
            public Road fromString(String string) {
                return null;
            }
        });
        roadsToBuildListView.setCellFactory(
                listview -> new ListCell<>() {
                    @Override
                    public void updateItem(Road road, boolean empty) {
                        super.updateItem(road, empty);
                        textProperty().unbind();
                        if (road != null)
                            //TODO: textProperty().bind();
                            textProperty().setValue(road.getId());
                        else
                            setText(null);
                    }
                }
        );
    }

    public void updateGameMapModel(ClientGame clientGame) {
        this.gameMapModel.updateClientGame(clientGame);
    }

    public void updateGameMapModelWithMap(ClientGame clientGame) {
        this.gameMapModel.updateClientGameWithMap(clientGame);
        this.gameMapModel.updateFields();
    }

    @FXML
    private void buildRoadAction(ActionEvent event) {
        try {
            this.gameService.buildRoad(this.roadsToBuildListView.getValue().getId());
        } catch (IOException e) {
            e.printStackTrace(); //TODO: signal failure to user an let him build the road again
        }
    }

    @FXML
    private void requestWheelCards(ActionEvent event) {
        try {
            this.gameService.requestWheelCards();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void selectDestinationCards(ActionEvent event) {
        try {
            this.gameService.requestShortDestinationCards();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
