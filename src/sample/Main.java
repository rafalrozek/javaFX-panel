package sample;


import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.colors.Bright;
import eu.hansolo.tilesfx.events.TimeEvent;
import eu.hansolo.tilesfx.events.TimeEventListener;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class Main extends Application {
    private static final Random RND = new Random();
    int WIDTH = 150;
    int HEIGHT = 60;
    int status = 0;
    int start = 0;
    int alarm = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller ctrl = loader.getController();


        primaryStage.setTitle("Panel");
        primaryStage.setScene(new Scene(root, 1000, 400));

        primaryStage.setResizable(false);
        primaryStage.show();
        ChartData smoothChartData1 = new ChartData("", 0, Tile.BLUE);
        ChartData smoothChartData2 = new ChartData("", 0, Tile.BLUE);
        ChartData smoothChartData3 = new ChartData("", 0, Tile.BLUE);
        ChartData smoothChartData4 = new ChartData("", 0, Tile.BLUE);

        //wykres zuzycia wody
        Tile tile = TileBuilder.create().skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .prefSize(120, 60)
                .minValue(0)
                .maxValue(40)
                .title("")
                .backgroundColor(Color.TRANSPARENT)
                .unit("ml")
                .text("Test")
                .textSize(Tile.TextSize.BIGGER)
                //.chartType(ChartType.LINE)
                //.dataPointsVisible(true)
                .chartData(smoothChartData1, smoothChartData2, smoothChartData3, smoothChartData4)
                .tooltipText("")
                .animated(true)
                .build();
        //wykres predkosci obrotu
        Tile tile2 = TileBuilder.create().skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .prefSize(120, 60)
                .minValue(0)
                .maxValue(40)
                .title("")
                .backgroundColor(Color.TRANSPARENT)
                .unit("RPM")
                .text("Test")
                .textSize(Tile.TextSize.BIGGER)
                //.chartType(ChartType.LINE)
                //.dataPointsVisible(true)
                .chartData(smoothChartData1, smoothChartData2, smoothChartData3, smoothChartData4)
                .tooltipText("")
                .animated(true)
                .build();


        //timer
        final Tile countdownTile = TileBuilder.create()
                .skinType(Tile.SkinType.COUNTDOWN_TIMER)
                .maxSize(70, 70)
                .prefSize(80,80)
                .backgroundColor(Color.BLACK)
                .barColor(Bright.BLUE_GREEN)
                .timePeriod(Duration.ofSeconds(15))
                .onAlarm(e -> alarm = 1)
                .build();

        //aktualizacja wykresow co sekunde
        countdownTile.setOnTimeEvent(new TimeEventListener() {
            @Override
            public void onTimeEvent(TimeEvent timeEvent) {
                tile.getChartData().add(new ChartData("", RND.nextDouble() * 300 + 50, Instant.now()));
                tile2.getChartData().add(new ChartData("", RND.nextDouble() * 1000 + 50, Instant.now()));

            }
        });

        //zakonczenie prania
        countdownTile.setOnAlarm(e -> {
            tile.getChartData().add(new ChartData("", 0, Instant.now()));
            tile2.getChartData().add(new ChartData("", 0, Instant.now()));
        });

        //temperatura
        Gauge dashboardGauge = createGauge(Gauge.SkinType.DASHBOARD);
        Tile dashboardTile  = TileBuilder.create()
                .prefSize(150, 70)
                .skinType(Tile.SkinType.CUSTOM)
                .title("")
                .minValue(20)
                .maxValue(80)
                .text("")
                .value(10)
                .backgroundColor(Color.TRANSPARENT)
                .graphic(dashboardGauge)
                .build();


        Tile switchTile = TileBuilder.create()
                .prefSize(100, 80)
                .skinType(Tile.SkinType.SWITCH)
                .title("")
                .text("")
                .backgroundColor(Color.TRANSPARENT)
                //.description("Test")
                .build();

        //przycisk start/stop
        switchTile.setOnSwitchPressed(e -> {
            if(start == 0){
                start = 1;
                alarm = 0;
                countdownTile.setRunning(true);
            }
            else {
                start = 0;
                //tile.clearChartData();
                tile.getChartData().add(new ChartData("", 0, Instant.now()));
                tile2.getChartData().add(new ChartData("", 0, Instant.now()));
                countdownTile.setRunning(false);

                System.out.println(countdownTile.getCurrentValue());
                if(alarm == 1) {

                    countdownTile.setTimePeriod(Duration.ofSeconds(15));
                }

            }


            System.out.println("Switch pressed");
        });

        //dodaje wszystkie wykresy do paneli
        ctrl.wykres.getChildren().add(countdownTile);
        ctrl.wykres1.getChildren().add(dashboardTile);
        ctrl.wykres11.getChildren().add(tile);
        ctrl.wykres111.getChildren().add(switchTile);
        ctrl.wykres112.getChildren().add(tile2);

        //pokrętło
        ctrl.s0.setVisible(true);
        ctrl.s1.setVisible(false);
        ctrl.s2.setVisible(false);
        ctrl.s3.setVisible(false);


        dashboardGauge.setValue(20);

        ctrl.circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                status += 1;
                if(status == 4){
                    status = 0;
                }
                if(status == 0){
                    ctrl.s0.setVisible(true);
                    ctrl.s1.setVisible(false);
                    ctrl.s2.setVisible(false);
                    ctrl.s3.setVisible(false);

                    dashboardGauge.setValue(20);
                }
                else if(status == 1){
                    ctrl.s0.setVisible(false);
                    ctrl.s1.setVisible(true);
                    ctrl.s2.setVisible(false);
                    ctrl.s3.setVisible(false);

                    dashboardGauge.setValue(40);
                }
                else if(status == 2){
                    ctrl.s0.setVisible(false);
                    ctrl.s1.setVisible(false);
                    ctrl.s2.setVisible(true);
                    ctrl.s3.setVisible(false);

                    dashboardGauge.setValue(60);
                }
                else if(status == 3){
                    ctrl.s0.setVisible(false);
                    ctrl.s1.setVisible(false);
                    ctrl.s2.setVisible(false);
                    ctrl.s3.setVisible(true);

                    dashboardGauge.setValue(80);
                }
            }
        });

    }
    private Gauge createGauge(final Gauge.SkinType TYPE) {
        return GaugeBuilder.create()
                .skinType(TYPE)
                .prefSize(WIDTH, HEIGHT)
                .animated(true)
                //.title("")
                .unit("\u00B0C")
                .valueColor(Tile.FOREGROUND)
                .titleColor(Tile.FOREGROUND)
                .unitColor(Tile.FOREGROUND)
                .barColor(Tile.BLUE)
                .needleColor(Tile.FOREGROUND)
                .barColor(Tile.BLUE)
                .barBackgroundColor(Tile.BACKGROUND.darker())
                .tickLabelColor(Tile.FOREGROUND)
                .majorTickMarkColor(Tile.FOREGROUND)
                .minorTickMarkColor(Tile.FOREGROUND)
                .mediumTickMarkColor(Tile.FOREGROUND)
                .build();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
