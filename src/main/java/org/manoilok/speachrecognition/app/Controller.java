package main.java.org.manoilok.speachrecognition.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.org.manoilok.speachrecognition.model.*;
import main.java.org.manoilok.speachrecognition.service.WavModelService;
import main.java.org.manoilok.speachrecognition.service.WavModelServiceImpl;

import java.util.*;

import static javax.swing.JOptionPane.showMessageDialog;

public class Controller {
    private Main main;
    private WavModel wavModel;
    private WavModelService wavModelService;
    private List<WavModel> etalons;
    private Map<String,Double> distanceMap;
    private  boolean fourer;
    private  boolean chebyshev;

    @FXML
    private LineChart currSignalChart;
    @FXML
    private NumberAxis xAxisCurrSignal;
    @FXML
    private NumberAxis yAxisCurrSignal;

    @FXML
    private LineChart withOutLPChart;
    @FXML
    private NumberAxis xAxisWithOutLP;
    @FXML
    private NumberAxis yAxisWithOutLP;

    @FXML
    private LineChart normalizeChart;
    @FXML
    private NumberAxis xAxisNorm;
    @FXML
    private NumberAxis yAxisNorm;

    @FXML
    private LineChart spectrChart;
    @FXML
    private NumberAxis xAxisSpectr;
    @FXML
    private NumberAxis yAxisSpectr;

    @FXML
    private ScrollBar scrollBar;

    @FXML
    private LineChart bandChart;
    @FXML
    private NumberAxis xAxisBannd;
    @FXML
    private NumberAxis yAxisBannd;

    @FXML
    private ScrollBar bandScrollBar;

    @FXML
    private TableView<RecgnitionResult> tableRecognition;

    @FXML
    private TableColumn<RecgnitionResult, String> fileNameColumn;

    @FXML
    private TableColumn<RecgnitionResult, Double> distanceColumn;
    @FXML
    TextField thresholdField;
    @FXML
    TextField nField;
    @FXML
    TextField fcpField;

    public void setMain(Main main) {
        this.main = main;
    }

    public void loadFile(){
        //showMessageDialog(null, "This language just gets better and better!");
        wavModelService=new WavModelServiceImpl();
        wavModel= new WavModel(wavModelService.load());
        currSignalChart.getData().clear();
        wavModelService.showOnChart(xAxisCurrSignal,yAxisCurrSignal,currSignalChart,wavModel.getWavBytes(),"");

        //ActivityDetector activityDetector = new ActivityDetector(Collections.max( wavModel.getWavBytes())/2,256,256);
        //activityDetector.process(wavModelService.envelopExtracting(wavModel.getWavBytes()));
        //wavModelService.showOnChart(xAxisCurrSignal,yAxisCurrSignal,currSignalChart,wavModelService.envelopExtracting(wavModel.getWavBytes()),"0");
        withOutLPChart.getData().clear();
        wavModel.setWavBytes(wavModelService.delLatentPeriod(wavModel));
        wavModelService.showOnChart(xAxisWithOutLP,yAxisWithOutLP,withOutLPChart,wavModel.getWavBytes(),"");

        normalizeChart.getData().clear();
        wavModel.setWavBytes(wavModelService.normalize(wavModel));
        wavModelService.showOnChart(xAxisNorm,yAxisNorm,normalizeChart,wavModel.getWavBytes(),"");
        wavModelService.showOnChart(xAxisNorm,yAxisNorm,normalizeChart,wavModelService.envelopExtracting(wavModel.getWavBytes()),"0");
        spectrChart.getData().clear();


    }
    public void loadEtalons() {
        List<Double> dist = new ArrayList<>();
        ObservableList<RecgnitionResult> result =  FXCollections.observableArrayList();
       // List<List<Double>>
        distanceMap = new HashMap<>();
        etalons = new ArrayList<>();
        etalons=wavModelService.loadEtalons();
//        for (List<Double> ld :wavModelService.loadEtalons()){
//            etalons.add(new WavModel(ld));
//        }
        for (WavModel wm :etalons ){
            wm.setWavBytes(wavModelService.delLatentPeriod(wm));
            wm.setWavBytes(wavModelService.normalize(wm));
            if (fourer){
                FFT fft = new FFT();
                wm.setSpectr( fft.fff(wm.getWavBytes()));
            }
            else {
                Chebyshev chebyshev = new Chebyshev();
                wm.setSpectr( chebyshev.chebyshev(wm.getWavBytes()));
            }

            wm.setBand(wavModelService.spectLineView(wm));
           // wavModelService.setEuclidianDistanceMatrix(wavModel,wm);
            DTW dtw = new DTW(wavModel.getBand(),wm.getBand());
            //dtw.compute();
            //dist.add(wavModelService.findMinDistanse(wm));
            //showMessageDialog(null, dist);
            result.add(new RecgnitionResult(wm.getFileName(),dtw.getDistance()));
        }
        tableRecognition.setItems(result);
    }

    @FXML
    private void initialize() {


        // устанавливаем тип и значение которое должно хранится в колонке
        distanceColumn.setCellValueFactory(new PropertyValueFactory<RecgnitionResult, Double>("distance"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<RecgnitionResult, String>("filename"));
    }
    public void moveScrolBar(){
        int value = (int) scrollBar.getValue();
        spectrChart.getData().clear();
        wavModelService.showOnChart(xAxisSpectr,yAxisSpectr,spectrChart,wavModel.getSpectr().get(value),Integer.toString(value));
        System.out.print(scrollBar.getValue());

    }
    public void moveBandScrolBar(){
        int value = (int) bandScrollBar.getValue();
        bandChart.getData().clear();
        wavModelService.showOnChart(xAxisBannd,yAxisBannd,bandChart,wavModel.getBand().get(value),Integer.toString(value));
        int N = 0;
        double fcp=0;
        try {
            fcp=Double.parseDouble(fcpField.getText());
            N= Integer.parseInt(nField.getText());
        }
        catch ( Exception e){}

        wavModelService.showOnChart(xAxisBannd,yAxisBannd,bandChart,wavModelService.getLowPassFilterData( wavModel.getBand().get(value),N,fcp),"0");

        System.out.print(bandScrollBar.getValue());

    }
    public void fourerButton(){
        fourer = true;
        chebyshev = false;
        FFT fft = new FFT();
        wavModel.setSpectr( fft.fff(wavModel.getWavBytes()));
        spectrChart.getData().clear();
        wavModelService.showOnChart(xAxisSpectr,yAxisSpectr,spectrChart,wavModel.getSpectr().get(0),"0");

        scrollBar.setMax(wavModel.getSpectr().size()-1);
        bandChart.getData().clear();
        wavModel.setBand(wavModelService.spectLineView(wavModel));
        wavModelService.showOnChart(xAxisBannd,yAxisBannd,bandChart,wavModel.getBand().get(0),"0");
        //wavModelService.showOnChart(xAxisBannd,yAxisBannd,bandChart,wavModelService.getLowPassFilterData( wavModel.getBand().get(0)),"0");
        bandScrollBar.setMax(wavModel.getBand().size()-1);

    }
    public void chebyshevButton(){
        fourer = false;
        chebyshev = true;
        Chebyshev chebyshev = new Chebyshev();
        wavModel.setSpectr( chebyshev.chebyshev(wavModel.getWavBytes()));
        spectrChart.getData().clear();
        wavModelService.showOnChart(xAxisSpectr,yAxisSpectr,spectrChart,wavModel.getSpectr().get(0),"0");

        scrollBar.setMax(wavModel.getSpectr().size()-1);
        bandChart.getData().clear();
        wavModel.setBand(wavModelService.spectLineView(wavModel));
        wavModelService.showOnChart(xAxisBannd,yAxisBannd,bandChart,wavModel.getBand().get(0),"0");
        //wavModelService.showOnChart(xAxisBannd,yAxisBannd,bandChart,wavModelService.getLowPassFilterData( wavModel.getBand().get(0)),"0");
        bandScrollBar.setMax(wavModel.getBand().size()-1);

    }
    public void applySegmentationButton(){
        double threshold=0;
        try {
             threshold=Double.parseDouble(thresholdField.getText());
        }
        catch ( Exception e){}
        ActivityDetector activityDetector = new ActivityDetector(threshold,256,256);
        List<Integer> segmentation = activityDetector.process(wavModelService.envelopExtracting(wavModel.getWavBytes()));

        wavModelService.showSegmentationOnChart(xAxisNorm,yAxisNorm,normalizeChart,segmentation,Collections.max( wavModel.getWavBytes()),"0");
    }
    public void applyLPFilterButton(){
        int N = 0;
        double fcp=0;
        try {
            fcp=Double.parseDouble(fcpField.getText());
            N= Integer.parseInt(nField.getText());
        }
        catch ( Exception e){}

        wavModelService.showOnChart(xAxisBannd,yAxisBannd,bandChart,wavModelService.getLowPassFilterData( wavModel.getBand().get(0),N,fcp),"0");
    }
}
