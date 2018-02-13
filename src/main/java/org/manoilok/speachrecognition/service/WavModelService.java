package main.java.org.manoilok.speachrecognition.service;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import main.java.org.manoilok.speachrecognition.model.WavModel;

import java.util.List;

/**
 * Created by Modest on 09.09.2016.
 */
public interface WavModelService {

    List<Double> load();
    List<WavModel> loadEtalons();
    List<Double> delLatentPeriod(WavModel wavModel);
    List<Double> normalize(WavModel wavModel);
    void showOnChart(NumberAxis xAxis,NumberAxis yAxis,LineChart currSignalChart,List<Double> bytes,String s);
    List<List<Double>> spectLineView(WavModel wavModel);
    List<Double> getLowPassFilterData(List<Double> band,int N, double fCP);
    List<Double> envelopExtracting(List<Double> wavBayts);
    void showSegmentationOnChart(NumberAxis xAxis,NumberAxis yAxis,LineChart currSignalChart,List<Integer> bytes,double max,String s);
}
