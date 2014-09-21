package controller;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import model.imageProcessing.Segmentation;
import model.ocr.OCR;
import view.MainWindow;

public class Main {

    //static OCR model.ocr = new OCR(0);
    static OCR[] ocr = new OCR[4];

    public String in = null; //input file path

    public ArrayList<ArrayList<int[][]>> characterPositions;
    public ArrayList<ArrayList<Character>> characters = new ArrayList<ArrayList<Character>>();

    static char[] letters =
            {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

    //size of the resized image
    double max_width= 1366;
    double max_height= 1366;

    private BufferedImage original, grayscale, blured, resized, binarized, lined, recognized;

    public static void main(String[] args) throws IOException {
        for(int i=0; i<ocr.length; i++){//initialize model.ocr networks
            ocr[i] = new OCR(i);
        }
        MainWindow.start();
    }

    public void processImage() throws IOException {

        File original_f = new File(in + ".jpg");
        String gray_f = in +"_gray";
        String blurry_f = in +"_blur";
        String output_f = in +"_bin";
        String lined_f = in +"_lined";

        original = ImageIO.read(original_f);

        double height_ratio= max_height/original.getHeight();
        double width_ratio= max_width/original.getWidth();
        double ratio= (height_ratio<width_ratio)? height_ratio:width_ratio;


        grayscale = model.imageProcessing.OtsuBinarize.toGray(original);
        blured = model.imageProcessing.Filter.getBlurredImage(grayscale, 2);
        //blured = model.imageProcessing.Filter.medianFilter(blured, 2);
        resized = model.imageProcessing.Filter.resizeImage(blured, (int) (blured.getWidth() * ratio), (int) (blured.getHeight() * ratio));
        model.imageProcessing.OtsuBinarize.setRedAndAlphaValues(resized);
        binarized = model.imageProcessing.OtsuBinarize.partialBinarize(resized);
        binarized = model.imageProcessing.Filter.medianFilter(binarized,4);
        //binarized = model.imageProcessing.Filter.medianFilter(binarized,2);

        characterPositions = getCharacterPositions(binarized);
        lined = getLinedImage(binarized, characterPositions);

        writeImage(grayscale, gray_f);
        writeImage(blured, blurry_f);
        writeImage(binarized, output_f);
        writeImage(lined, lined_f);
    }


    public void train(int networkNo){

        ocr[networkNo].initBeforeTrain(letters.length);
        for (int i = 0; i < characterPositions.size(); i++) {
            //System.out.println(characterPositions.get(i).size());
            if(characterPositions.get(i).size()==letters.length ){//&& noOfOCRsTrained<4) {
                for (int j = 0; j < characterPositions.get(i).size(); j++) {
                    int[][] position = characterPositions.get(i).get(j);
                    if (position[0][0]<position[0][1] && position[1][0]<position[1][1]) {
                        //model.ocr[noOfOCRsTrained].clear();
                        BufferedImage characterImage = getcharacterImage(binarized, position);
                        //model.ocr[noOfOCRsTrained].add(characterImage, letters[j]);
                        ocr[networkNo].add(characterImage, letters[j]);
                    }
                }
                ocr[networkNo].train();
                ocr[networkNo].save();
                ocr[networkNo].clearList();
                //model.ocr[noOfOCRsTrained].train();
                //model.ocr[noOfOCRsTrained].save();
            }
        }
        ocr[networkNo] = new OCR(networkNo);

    }

    public boolean recognize() throws IOException {
        for (OCR anOcr : ocr) {
            //if(ocr[i].net==null) {
            anOcr.initBeforeTrain(letters.length);
            boolean succuess = anOcr.load();
            if (!succuess) {
                return false;
            }
            anOcr.train();
            //}
        }
        for (int i = 0; i < characterPositions.size(); i++) {
            ArrayList<Character> characterLine = new ArrayList<Character>();
            for (int j = 0; j < characterPositions.get(i).size(); j++) {
                int position[][] = characterPositions.get(i).get(j);
                if (position[0][0]<position[0][1] && position[1][0]<position[1][1]) {
                    BufferedImage characterImage = getcharacterImage(binarized, position);

                        char[] recognizedChar = new char[ocr.length];
                        for(int k=0; k<ocr.length; k++){
                            recognizedChar[k] = ocr[k].recognize(characterImage);
                        }
                        characterLine.add(getMode(recognizedChar));
                }else{
                    characterLine.add('\0');
                }
            }
            characters.add(characterLine);
        }
        recognized = getRecognizedImage(binarized, characterPositions, characters);

        String recognized_f = in +"_recognized";
        writeImage(recognized, recognized_f);

        for(int i=0; i<ocr.length; i++){//initialize model.ocr networks
            ocr[i] = new OCR(i);
        }
        return true;
    }

    public static BufferedImage getcharacterImage(BufferedImage image, int[][] position){
        BufferedImage tmp;
        try {
             tmp = image.getSubimage(
                    position[0][0], position[1][0], position[0][1] - position[0][0], position[1][1] - position[1][0]);
        }catch (RasterFormatException e){
            System.out.println(position[0][0]+ " "+ position[0][1] + " " +image.getWidth());
            throw e;
        }
        return tmp;
    }

    private static ArrayList<ArrayList<int[][]>> getCharacterPositions(BufferedImage image){
        //this stores all the character positions first the line array then the character array
        ArrayList<ArrayList<int[][]>> characterPositions = new ArrayList<ArrayList<int[][]>>();
        ArrayList<int[]> characterLines = Segmentation.getCharcterLines(image);

        int[] minimaLines = new int[characterLines.size()+1];

        minimaLines[0]=characterLines.get(0)[0]/2;
        minimaLines[minimaLines.length-1]=(characterLines.get(characterLines.size()-1)[1]+image.getHeight())/2;

        for(int i=1; i<minimaLines.length-1; i++){
            minimaLines[i] = (characterLines.get(i)[0] + characterLines.get(i-1)[1])/2;
        }


        for(int i=0; i< characterLines.size(); i++){

            ArrayList<int[][]> charactersInTheLine = new ArrayList<int[][]>();

            int[] boundY = {minimaLines[i], minimaLines[i+1]};//characterLines.get(i);
            ArrayList<int[]> character = Segmentation.getCharacters(image, boundY[0], boundY[1]);
            for(int j=0; j< character.size(); j++){
                int[] boundX = character.get(j);
                //System.out.println(boundX[0]+" "+boundY[0]+" "+ (boundX[1]-boundX[0])+" "+ (boundY[1]-boundY[0]));
                int[][] tmp = {{boundX[0], boundX[1]}, {boundY[0], boundY[1]}};
                charactersInTheLine.add(tmp);
            }
            characterPositions.add(charactersInTheLine);
        }

        return characterPositions;
    }

    private static BufferedImage getLinedImage(BufferedImage image, ArrayList<ArrayList<int[][]>> characterPositions){
        BufferedImage lined = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = lined.createGraphics();
        g2d.setColor(Color.green);
        BasicStroke bs = new BasicStroke(1);
        g2d.setStroke(bs);

        //System.out.println(characterPositions.size()+" "+characterPositions.get(0).size());
        g2d.drawImage(image, 0, 0, null);
        for(int i=0; i<characterPositions.size(); i++){
            for (int j = 0; j<characterPositions.get(i).size(); j++) {
                int[][] tmp = characterPositions.get(i).get(j);
//                if(i==1 && j==0)
//                    g2d.fillRect(tmp[0][0], tmp[1][0], tmp[0][1] - tmp[0][0], tmp[1][1] - tmp[1][0]);
                g2d.drawRect(tmp[0][0], tmp[1][0], tmp[0][1] - tmp[0][0], tmp[1][1] - tmp[1][0]);
            }
        }
        g2d.dispose();

        return lined;
    }

    private static BufferedImage getRecognizedImage(
            BufferedImage image, ArrayList<ArrayList<int[][]>> characterPositions, ArrayList<ArrayList<Character>> characters){
        BufferedImage characterWritten = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = characterWritten.createGraphics();
        g2d.setColor(Color.red);
        Font f = new Font(Font.SERIF, Font.BOLD, 18);
        g2d.setFont(f);
        BasicStroke bs = new BasicStroke(3);
        g2d.setStroke(bs);

        g2d.drawImage(image, 0, 0, null);

        for(int i=0; i<characterPositions.size(); i++) {
            for (int j = 0; j < characterPositions.get(i).size(); j++) {
                int[][] tmp = characterPositions.get(i).get(j);

                int x = (tmp[0][0]+tmp[0][1])/2;
                int y = (tmp[1][0]+tmp[1][1])/2;
                g2d.drawString(characters.get(i).get(j).toString(), x, y);
            }
        }

        g2d.dispose();

        return characterWritten;
    }

    private static void writeImage(BufferedImage image, String output) throws IOException {
        File file = new File(output+".jpg");
        ImageIO.write(image, "jpg", file);
    }

    public void saveFile(File file){
        int wordSpace = 15;
        FileWriter fw = null;
        String data = "";
        try {
            fw = new FileWriter(file);
            for(int i=0; i<characters.size(); i++){
                for(int j=0; j<characters.get(i).size()-1; j++){
                    data = data.concat(characters.get(i).get(j).toString());
                    if((characterPositions.get(i).get(j+1)[0][0]-characterPositions.get(i).get(j)[0][1])
                            >wordSpace){
                        data = data.concat(" ");
                    }
                }
                data = data.concat(characters.get(i).get(characters.get(i).size()-1).toString());//tha last character
                                                                                                //isn't captured with the loop
                data = data.concat("\n");
            }
            try {
                fw.write(data);
                fw.flush();
                fw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                String cmd = "rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath();
                Runtime.getRuntime().exec(cmd);
            }
            else {
                Desktop.getDesktop().edit(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static char getMode(char[] n){//get the most frequent value of the array
        char t = 0;
        for(int i=0; i<n.length; i++){
            for(int j=1; j<n.length-i; j++){
                if(n[j-1] > n[j]){
                    t = n[j-1];
                    n[j-1] = n[j];
                    n[j] = t;
                }
            }
        }

        char mode = n[0];
        int temp = 1;
        int temp2 = 1;
        for(int i=1;i<n.length;i++){
            if(n[i-1] == n[i]){
                temp++;
            }
            else {
                temp = 1;
            }
            if(temp >= temp2){
                mode = n[i];
                temp2 = temp;
            }
        }
        return mode;
    }
}
