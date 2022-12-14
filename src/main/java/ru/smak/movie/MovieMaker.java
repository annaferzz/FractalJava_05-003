package ru.smak.movie;

import ru.smak.graphics.Colorizer;
import ru.smak.graphics.FractalPainter;
import ru.smak.graphics.Plane;
import ru.smak.math.fractals.Fractal;


import java.util.ArrayList;

public class MovieMaker {
    /**
     * Длительность видео в секундах.
     */
    int time;
    /**
     * Количество сменяемых кадров за одну секунду.
     */
    int fps;
    int N,width,height;//общее количество добавляемых кадров, ширина и высота
    double K;//суммарное изменение фрактала(его площади)
    /**
     * Массив кадров, передаваемых пользователем.
     */
    private Colorizer color;
    private Fractal fractal;
    /**
     * Массив кадров, передаваемых пользователем.
     */
    private ArrayList<FractalPainter> keyFrames;//массив ключевых кадров
    private ArrayList<Double> coefficients;//массив коэффициентов
    private ArrayList<Integer> countOfFrames;//массив количества добавляемых кадров между соседними ключевыми кадрами

    private ArrayList<FractalPainter> frames;//массив всех кадров

    public MovieMaker(ArrayList<FractalPainter> keyFrames, int time, int fps){
        this.frames = new ArrayList<>();
        this.keyFrames = keyFrames;
        this.time = time;
        this.fps = fps;
        this.color = keyFrames.get(0).getColorFunc();
        this.fractal = keyFrames.get(0).getFractal();
        this.width = keyFrames.get(0).getWidth();
        this.height = keyFrames.get(0).getHeight();
        this.coefficients = getCoefficients();
        this.countOfFrames = (ArrayList<Integer>)(getCountOfFrames().clone());
        this.N = numberOfFrames();
        this.K = sumCoeff();
        System.out.println("В КОНСТРУКТОРЕ");
        System.out.println("getCountOfFrames[0] " + getCountOfFrames().get(0));
        System.out.println("countOfFrames[0] " + countOfFrames.get(0));
        /*System.out.println("Количество ключевых кадров:");
        System.out.println(keyFrames.size());
        System.out.println("Количество K:");
        System.out.println(coefficients.size());
        System.out.println("Массив коэффициентов К:");
        for (int i = 0; i < coefficients.size(); i++){
            System.out.println(coefficients.get(i));
        }
        System.out.println("Количество N:");
        System.out.println(countOfFrames.size());
        System.out.println("Массив номеров N:");
        for (int i = 0; i < coefficients.size(); i++){
            System.out.println(coefficients.get(i));
        }
        System.out.println("Общий массив кадров(количество):");
        System.out.println(frames.size());*/
    }

    public void create(){
        for (int i = 0; i < keyFrames.size()-1; i++){
            frames.add(keyFrames.get(i));
            int k = 1;
            double deltaXMin = keyFrames.get(i+1).getPlane().getXMin() - keyFrames.get(i).getPlane().getXMin();
            double deltaXMax = keyFrames.get(i).getPlane().getXMax() - keyFrames.get(i+1).getPlane().getXMax();
            double deltaYMin = keyFrames.get(i+1).getPlane().getYMin() - keyFrames.get(i).getPlane().getYMin();
            double deltaYMax = keyFrames.get(i).getPlane().getYMax() - keyFrames.get(i+1).getPlane().getYMax();
            for (int j = 0; j < countOfFrames.get(i); j++){
                //добавляем кадры
                double xMin = keyFrames.get(i).getPlane().getXMin() + k*deltaXMin/countOfFrames.get(i);
                double xMax = keyFrames.get(i).getPlane().getXMax() - k*deltaXMax/countOfFrames.get(i);
                double yMin = keyFrames.get(i).getPlane().getYMin() + k*deltaYMin/countOfFrames.get(i);
                double yMax = keyFrames.get(i).getPlane().getYMax() - k*deltaYMax/countOfFrames.get(i);
                k++;
                fractal = keyFrames.get(i).getFractal();
                color = keyFrames.get(i).getColorFunc();
                Plane p = new Plane(xMin, xMax, yMin, yMax, width, height);
                frames.add(new FractalPainter(p,fractal, color));
            }
        }
        //
        System.out.println("Количество ключевых кадров:");
        System.out.println(keyFrames.size());
        System.out.println("Количество K:");
        System.out.println(coefficients.size());
        System.out.println("Массив коэффициентов К:");
        for (int i = 0; i < coefficients.size(); i++){
            System.out.println(coefficients.get(i));
        }
        System.out.println("Количество кадров, которые нужно добавить N:");
        System.out.println(countOfFrames.size());
        System.out.println("Массив N:");
        for (int i = 0; i < countOfFrames.size(); i++){
            System.out.println(countOfFrames.get(i));
        }
        System.out.println("Общий массив кадров(количество):");
        System.out.println(frames.size());

        System.out.println("coefficients[0] " + coefficients.get(0));
        System.out.println("N " + N);
        System.out.println("K " + K);
        System.out.println("getCountOfFrames[0] " + getCountOfFrames().get(0));
        System.out.println("countOfFrames[0] " + countOfFrames.get(0));
    }

    //метод, который возвращает коэффициент - во сколько раз изменилась плоскость
    public double getCoeff(FractalPainter p1, FractalPainter p2){
        return Math.abs((p1.getPlane().getXMax()-p1.getPlane().getXMin())*(p1.getPlane().getYMax()-p1.getPlane().getYMin())
                /(p2.getPlane().getXMax()-p2.getPlane().getXMin())/(p2.getPlane().getYMax()-p2.getPlane().getYMin()));
    }
    //массив коэффициентов
    public ArrayList<Double> getCoefficients(){
        ArrayList<Double> c = new ArrayList<>();
        for (int i = 0; i < keyFrames.size()-1; i++){
            c.add(getCoeff(keyFrames.get(i), keyFrames.get(i+1)));
        }
        return c;
    }
    //сумма коэффициентов К
    public double sumCoeff(){
        double res = 0;
        for (int i = 0; i < this.coefficients.size(); i++){
            res += this.coefficients.get(i);
        }
        return res;
    }

    //массив количества кадров
    public ArrayList<Integer> getCountOfFrames(){
        ArrayList<Integer> n = new ArrayList<Integer>();
        for(int i = 0; i < keyFrames.size()-1; i++){
            n.add((int)(coefficients.get(i)*(double)N/K));
        }
        return n;
    }
    //общее число добавляемых кадров
    public int numberOfFrames(){
        return fps*time-keyFrames.size();
    }
    public int getFps() {
        return fps;
    }
    public void setFps(int fps){
        this.fps = fps;
    }

    public int getTime() {
        return time;
    }
    public void setTime(int time){
        this.time = time;
    }

    public void show(){

    }
}
