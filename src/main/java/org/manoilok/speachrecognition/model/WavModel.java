package main.java.org.manoilok.speachrecognition.model;


import java.util.List;

/**
 * Created by Modest on 09.09.2016.
 */
public class WavModel {

    private String fileName;
    private List<Double> wavBytes;
    private List<List<Double>> spectr;
    private List<List<Double>> band;
    private List<List<Double>> distance;

    public WavModel(List<Double> wavBytes, String fileName) {
        this.wavBytes = wavBytes;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<List<Double>> getBand() {
        return band;
    }

    public void setBand(List<List<Double>> band) {
        this.band = band;
    }

    public List<Double> getWavBytes() {
        return wavBytes;
    }

    public List<List<Double>> getSpectr() {
        return spectr;
    }

    public void setSpectr(List<List<Double>> spectr) {
        this.spectr = spectr;
    }

    public void setWavBytes(List<Double> wavBytes) {

        this.wavBytes = wavBytes;
    }

    public WavModel() {
        this.wavBytes = null;
    }

    public WavModel(List<Double> wavBytes) {
        this.wavBytes = wavBytes;
    }

    public List<List<Double>> getDistance() {
        return distance;
    }

    public void setDistance(List<List<Double>> distance) {
        this.distance = distance;
    }
}
