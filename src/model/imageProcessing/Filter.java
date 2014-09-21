package model.imageProcessing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

public class Filter {

    public static float[] matrix = {//this is the model.matrix used to blur the image
            1/16f, 1/8f, 1/16f,
            1/8f, 1/4f, 1/8f,
            1/16f, 1/8f, 1/16f,
    };


    public static BufferedImage resizeImage(BufferedImage originalImage, int new_width, int new_height){
        BufferedImage resizedImage = new BufferedImage(new_width, new_height, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, new_width, new_height, null);
        g.dispose();

        return resizedImage;
    }


    public static BufferedImage getBlurredImage(BufferedImage image, int iterations){
        BufferedImageOp op = new ConvolveOp( new Kernel(3, 3, matrix) );
        BufferedImage blurredImage = image;
        for(int i=0; i<iterations; i++)
            blurredImage= op.filter(blurredImage, null);

        return blurredImage;
    }



    public static BufferedImage medianFilter(BufferedImage image, int adjustment){
        if(adjustment>=9 || adjustment<0){
            throw new ArrayIndexOutOfBoundsException();
        }
        Color[] pixel=new Color[9];
        int[] R=new int[9];
        int[] B=new int[9];
        int[] G=new int[9];

        BufferedImage filtered = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for(int i=1;i<image.getWidth()-1;i++) {
            for (int j = 1; j < image.getHeight() - 1; j++) {
                pixel[0] = new Color(image.getRGB(i - 1, j - 1));
                pixel[1] = new Color(image.getRGB(i - 1, j));
                pixel[2] = new Color(image.getRGB(i - 1, j + 1));
                pixel[3] = new Color(image.getRGB(i, j + 1));
                pixel[4] = new Color(image.getRGB(i + 1, j + 1));
                pixel[5] = new Color(image.getRGB(i + 1, j));
                pixel[6] = new Color(image.getRGB(i + 1, j - 1));
                pixel[7] = new Color(image.getRGB(i, j - 1));
                pixel[8] = new Color(image.getRGB(i, j));
                for (int k = 0; k < 9; k++) {
                    R[k] = pixel[k].getRed();
                    B[k] = pixel[k].getBlue();
                    G[k] = pixel[k].getGreen();
                }
                Arrays.sort(R);
                Arrays.sort(G);
                Arrays.sort(B);
                filtered.setRGB(i, j, new Color(R[4], B[4], G[4]).getRGB());
            }
        }
        return filtered;
    }
}
