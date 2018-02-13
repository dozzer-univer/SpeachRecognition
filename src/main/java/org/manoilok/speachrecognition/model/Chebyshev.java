package main.java.org.manoilok.speachrecognition.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Modest on 23.11.2016.
 */
public class Chebyshev {
    private  int N=256;
    private  int cSize=256;
    private double PI= Math.PI;


    private double cm (List<Double> s, int k,int step){
        double c=0;
        for (int i=step*N;i<(step+1)*N;i++){
            c+=s.get(i)*Math.cos((PI*k*(i+0.5f))/N);
        }
        return Math.sqrt(2.f/N)*c;
    }
    private double s_m (List<Double> s, int k,int step){
        double sm=0;
        for (int i=0;i<k;i++){
            sm+=g(k)*cm(s,k,step)*Math.cos(i*Math.acos(Math.cos(PI*(i+0.5)/N)));
        }
        return sm;
    }
    private  double g(int m){
        return m==0?Math.sqrt(0.5f):1;
    }

    private double a (List<Double> s, int k,int step){
        double a=0;
        //int j=1;
        //int N=s.size();
        double min = Collections.min(s);
        double max = Collections.max(s);
        for (int i=step*N;i<(step+1)*N;i++){
            double ki =((2*s.get(i))-(max+min))/(max-min);
            double t = Math.cos(k*Math.acos(ki));
            a+=s.get(i)*Math.cos((2*PI*k*i)/N);
        }
        return 2*a/N;
    }
    private double b (List<Double> s, int k,int step){
        double b=0;
        //int j=1;
        double min = Collections.min(s);
        double max = Collections.max(s);
        for (int i=step*N;i<(step+1)*N;i++){
            double ki =((2*s.get(i))-(max+min))/(max-min);
            double t = Math.cos(k*Math.acos(ki));
            b+=s.get(i)*Math.sin((2*PI*k*i)/N);
        }
        return 2*b/N;
    }
    private double t (List<Double> s, int k,int step){

        double c = cm(s , k , step);
        return Math.sqrt(c*c);
    }
    public List<List<Double>> chebyshev(List<Double> s){
        int spectrCount = s.size()/N;
        List<List<Double>> ck =new ArrayList<>();
        for (  int i=0; i< spectrCount; i++){
            ck.add(new ArrayList<>());
            for (int j=0; j<cSize; j++)
                ck.get(i).add(t(s,j,i));
        }
        return ck;
    }
}
