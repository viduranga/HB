package model.imageProcessing;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;


public class Segmentation {

    public static int horizonralCharacterSize = 3;
    public static int verticalCharacterSize = 5;
    public static int spaceBetween = 1;

    public static ArrayList<int[]> getCharcterLines(BufferedImage image){

        ArrayList<int[]> lines = new ArrayList<int[]>();


        //start and end lines of a character line
        int start=0;
        int end=0;
        boolean startAvailable=false;
        boolean endAvailable=true;
        double previousLineDensity= Double.MAX_VALUE;

        long[] histogram = new long[image.getHeight()];
        double avg=0;
        for(int i=0; i<histogram.length; i++){
            histogram[i]=0;
        }
        for(int i=0; i<image.getHeight(); i++){
            for(int j=0; j<image.getWidth(); j++){
                histogram[i]+=image.getRGB(j,i);
            }
        }

        long[] sortedHistogram = Arrays.copyOf(histogram, histogram.length);
        Arrays.sort(sortedHistogram);
        avg= sortedHistogram[(int)(sortedHistogram.length*0.8f)    ];

        for(int i=0; i<histogram.length; i++){
            double lineDensity = (double)histogram[i];
            if(lineDensity < avg){//if the posible start line
                if(endAvailable){
                    if(i-end>spaceBetween){
                        start=i;
                        startAvailable=true;
                        endAvailable=false;
                    }

                }
            }
            else if(lineDensity >= avg && previousLineDensity < avg){
                if(startAvailable){
                    if(i-start>verticalCharacterSize){
                        end=i;
                        endAvailable=true;
                        startAvailable=false;
                        lines.add(new int[]{start, end});
                    }
                    else{//this gets rid of selecting two dots located distance longer than 15
                        startAvailable = false;
                        endAvailable = true;
                    }
                }
            }
            previousLineDensity = lineDensity;
        }

        return lines;
    }

    public static ArrayList<int[]> getCharacters(BufferedImage image, int upper, int lower){


        ArrayList<int[]> lines = new ArrayList<int[]>();


        //start and end lines of a character line
        int start=0;
        int end=0;
        boolean startAvailable=false;
        boolean endAvailable=true;
        double previousLineDensity= Double.MAX_VALUE;

        long[] histogram = new long[image.getWidth()];
        double avg=0;
        for(int i=0; i<histogram.length; i++){
            histogram[i]=0;
        }
        for(int i=0; i<image.getWidth(); i++){
            for(int j=upper; j<lower; j++){
                histogram[i]+=image.getRGB(i,j);
            }
        }

        long[] sortedHistogram = Arrays.copyOf(histogram, histogram.length);
        Arrays.sort(sortedHistogram);
        avg= sortedHistogram[(int)(sortedHistogram.length*0.8f)];//sortedHistogram.length/3];

        for(int i=0; i<histogram.length; i++){
            double lineDensity = (double)histogram[i];
            if(lineDensity < avg){//if the posible start line
                if(endAvailable){
                    if(i-end>spaceBetween){
                        start=i;
                        startAvailable=true;
                        endAvailable=false;
                    }

                }
            }
            else if(lineDensity >= avg && previousLineDensity < avg){
                if(startAvailable){
                    if(i-start>horizonralCharacterSize){
                        end=i;
                        endAvailable=true;
                        startAvailable=false;
                        lines.add(new int[]{start, end});
                    }
                    else{//this gets rid of selecting two dots located distance longer than 15
                        startAvailable = false;
                        endAvailable = true;
                    }
                }
            }
            previousLineDensity = lineDensity;
        }

        return lines;
    }

    int findLocalMinima(int[] arr, int start, int end)
    {
        int mid = (start + end) / 2;

        if (mid - 2 < 0 && mid + 1 >= arr.length)
            return -1;
        if (arr[mid - 2] > arr[mid - 1] && arr[mid - 1] < arr[mid])
            return arr[mid - 1];
        if (arr[mid - 1] > arr[mid - 2])
            return findLocalMinima(arr, start, mid);
        else
            return findLocalMinima(arr, mid, end);
    }

}
