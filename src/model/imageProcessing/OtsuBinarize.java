package model.imageProcessing;


import java.awt.*;
import java.awt.image.*;

public class OtsuBinarize {

    //static Point neighborhood = new Point(10, 10);
    static int neighborhood_x= 12;
    static int neighborhood_y= 12;
    public static int c = 7;


    static int[][] redArr= null;//red values of the image that wants to be binarized
    static int[][] alphaArr= null;//alpha values of the image that wants to be binarized

    // Return histogram of grayscale image
    public static int[] partialImageHistogram(BufferedImage input, int start_x, int end_x, int start_y, int end_y) {

        int[] histogram = new int[256];

        for(int i=0; i<histogram.length; i++) histogram[i] = 0;

        for(int i=start_x; i<end_x; i++) {
            for(int j=start_y; j<end_y; j++) {
                //int red = new Color(input.getRGB (i, j)).getRed();
                histogram[redArr[i][j]]++;
            }
        }

        return histogram;
    }

    // The luminance method
    public static BufferedImage toGray(BufferedImage original) {

        int alpha, red, green, blue;
        int newPixel;

        BufferedImage lum = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        for(int i=0; i<original.getWidth(); i++) {
            for(int j=0; j<original.getHeight(); j++) {

                // Get pixels by R, G, B
                alpha = new Color(original.getRGB(i, j)).getAlpha();
                red = new Color(original.getRGB(i, j)).getRed();
                green = new Color(original.getRGB(i, j)).getGreen();
                blue = new Color(original.getRGB(i, j)).getBlue();

                red = (int) (0.21 * red + 0.71 * green + 0.07 * blue);
                // Return back to original format
                newPixel = colorToRGB(alpha, red, red, red);

                // Write pixels into image
                lum.setRGB(i, j, newPixel);

            }
        }

        return lum;

    }

    // Get binary treshold using Otsu's method
    private static int partialOtsuTreshold(BufferedImage original, int start_x, int end_x, int start_y, int end_y) {



        int[] histogram = partialImageHistogram(original, start_x, end_x, start_y, end_y);
        int total = (end_x-start_x) * (end_y-start_y);

        float sum = 0;
        for(int i=0; i<256; i++) sum += i * histogram[i];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for(int i=0 ; i<256 ; i++) {
            wB += histogram[i];
            if(wB == 0) continue;
            wF = total - wB;

            if(wF == 0) break;

            sumB += (float) (i * histogram[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if(varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;

    }


    public static BufferedImage partialBinarize(BufferedImage original) {

        int red;
        int newPixel;
        int local[][];
        BufferedImage binarized = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        for(int i=0; i<original.getWidth(); i++) {
            for(int j=0; j<original.getHeight(); j++) {

                local= getLocal(original, i, j);
                int segmantThreshold = partialOtsuTreshold(original, local[0][0], local[0][1], local[1][0], local[1][1]) - c;

                // Get pixels
                red = redArr[i][j];//new Color(original.getRGB(i, j)).getRed();
                int alpha = alphaArr[i][j];//new Color(original.getRGB(i, j)).getAlpha();
                if(red > segmantThreshold) {
                    newPixel = 255;
                }
                else {
                    newPixel = 0;
                }
                newPixel = colorToRGB(alpha, newPixel, newPixel, newPixel);
                binarized.setRGB(i, j, newPixel);

            }
        }

        return binarized;

    }

    // Convert R, G, B, Alpha to standard 8 bit
    private static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }

    /**
     * this method is to return the local square of a given point
     * @param image
     * @param x
     * @param y
     * @return
     */
    public static int[][] getLocal(BufferedImage image, int x, int y){
        int[][] local= new int[2][2];
        int tmp=0;

        tmp= x-neighborhood_x;
        local[0][0]= (tmp<0)? 0:tmp;
        tmp= x+neighborhood_x;
        local[0][1]= (tmp>=image.getWidth())? image.getWidth():tmp;

        tmp= y-neighborhood_y;
        local[1][0]= (tmp<0)? 0:tmp;
        tmp= y+neighborhood_y;
        local[1][1]= (tmp>=image.getHeight())? image.getHeight():tmp;

        return local;
    }

   public static void setRedAndAlphaValues(BufferedImage image){
       redArr = new int[image.getWidth()][image.getHeight()];
       alphaArr = new int[image.getWidth()][image.getHeight()];
       for(int i=0; i<image.getWidth(); i++) {
           for(int j=0; j<image.getHeight(); j++) {
               redArr[i][j] = new Color(image.getRGB (i, j)).getRed();
               alphaArr[i][j] = new Color(image.getRGB (i, j)).getAlpha();
           }
       }
   }

}