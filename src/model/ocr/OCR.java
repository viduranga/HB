
package model.ocr;

import model.som.NormalizeInput.NormalizationType;
import model.som.SelfOrganizingMap;
import model.som.TrainSelfOrganizingMap;
import model.som.TrainSelfOrganizingMap.LearningMethod;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;


public class OCR {

	/**
	 * Serial id for this class.
	 */
	// private static final long serialVersionUID = -6779380961875907013L;

	/**
	 * The downsample width for the application.
	 */
	static final int DOWNSAMPLE_WIDTH = 10;

	/**
	 * The down sample height for the application.
	 */
	static final int DOWNSAMPLE_HEIGHT = 14;

	static final double MAX_ERROR = 0.01;


	boolean halt;

    int ID;

	/**
	 * The entry component for the user to draw into.
	 */
	Entry entry;

	/**
	 * The down sample component to display the drawing downsampled.
	 */
	SampleData sample;

	/**
	 * The letters that have been defined.
	 */
	ArrayList letterListModel = new ArrayList();
	/**
	 * The neural network.
	 */
	public SelfOrganizingMap net;


	/**
	 * The constructor.
	 */
	public OCR(int ID) {
        this.ID = ID;

		this.entry = new Entry();

		this.sample = new SampleData(' ', DOWNSAMPLE_WIDTH, DOWNSAMPLE_HEIGHT);

		this.entry.setSampleData(this.sample);
	}

	/**
	 * Called to add the current image to the training set
	 *
	 */
	public void add(BufferedImage image, char character) {
		int i;
		this.downSample(image);
		final SampleData sampleData = (SampleData) this.sample.clone();
		sampleData.setLetter(character);

		for (i = 0; i < this.letterListModel.size(); i++) {
			final Comparable str = (Comparable) this.letterListModel.get(i);


			if (str.compareTo(sampleData) > 0) {
				this.letterListModel.add(i, sampleData);
				return;
			}
		}
		this.letterListModel.add(this.letterListModel.size(), sampleData);

	}

	/**
	 * Called to clear the image.
	 *
	 */
	public void clear() {
		this.sample.clear();
	}

    public void clearList() {
        this.letterListModel = new ArrayList();
    }

	/**
	 * Called to downsample the image.
	 *
	 */
	public void downSample(BufferedImage image) {
        this.entry.setEntryImage(image);
		this.entry.downSample();
	}


	/**
	 * Called when the load button is pressed.
	 *
	 */
	public boolean load() {
		try {
			FileReader f;// the actual file stream
			BufferedReader r;// used to read the file line by line

			f = new FileReader(new File("./sample"+ID+".dat"));
			r = new BufferedReader(f);
			String line;
			int i = 0;

			this.letterListModel.clear();

			while ((line = r.readLine()) != null) {
				final SampleData ds = new SampleData(line.charAt(0),
						OCR.DOWNSAMPLE_WIDTH, OCR.DOWNSAMPLE_HEIGHT);
				this.letterListModel.add(i++, ds);
				int idx = 2;
				for (int y = 0; y < ds.getHeight(); y++) {
					for (int x = 0; x < ds.getWidth(); x++) {
						ds.setData(x, y, line.charAt(idx++) == '1');
					}
				}
			}

			r.close();
			f.close();
			clear();
            return true;

		} catch (final Exception e) {
            JOptionPane.showMessageDialog(null, "I need to be trained first");
            return false;
			//e.printStackTrace();
		}

	}

	/**
	 * Used to map neurons to actual letters.
	 * 
	 * @return The current mapping between neurons and letters as an array.
	 */
	public char[] mapNeurons() {
		final char map[] = new char[this.letterListModel.size()];

		for (int i = 0; i < map.length; i++) {
			map[i] = '?';
		}
		for (int i = 0; i < this.letterListModel.size(); i++) {
			final double input[] = new double[DOWNSAMPLE_WIDTH * DOWNSAMPLE_HEIGHT];
			int idx = 0;
			final SampleData ds = (SampleData) this.letterListModel
					.get(i);
			for (int y = 0; y < ds.getHeight(); y++) {
				for (int x = 0; x < ds.getWidth(); x++) {
					input[idx++] = ds.getData(x, y) ? .5 : -.5;
				}
			}

			final int best = this.net.winner(input);
			map[best] = ds.getLetter();
		}
		return map;
	}

	/**
	 * Called when the recognize button is pressed.
	 *
	 */
	public char recognize(BufferedImage image) {
		if (this.net == null) {
            JOptionPane.showMessageDialog(null, "I need to be trained first");
            return '\0';
		}
		this.downSample(image);

		final double input[] = new double[DOWNSAMPLE_WIDTH * DOWNSAMPLE_HEIGHT];
		int idx = 0;
		final SampleData ds = this.sample;
		for (int y = 0; y < ds.getHeight(); y++) {
			for (int x = 0; x < ds.getWidth(); x++) {
				input[idx++] = ds.getData(x, y) ? .5 : -.5;
			}
		}

		final int best = this.net.winner(input);
		final char map[] = mapNeurons();
		clear();
        return map[best];
	}


	/**
	 * Called when the save button is clicked.
	 *
	 */
	public void save() {
		try {
			OutputStream os;// the actual file stream
			PrintStream ps;// used to read the file line by line

			os = new FileOutputStream("./sample"+ID+".dat", false);
			ps = new PrintStream(os);

			for (int i = 0; i < this.letterListModel.size(); i++) {
				final SampleData ds = (SampleData) this.letterListModel
						.get(i);
				ps.print(ds.getLetter() + ":");
				for (int y = 0; y < ds.getHeight(); y++) {
					for (int x = 0; x < ds.getWidth(); x++) {
						ps.print(ds.getData(x, y) ? "1" : "0");
					}
				}
				ps.println("");
			}
            ps.close();
			os.close();
			clear();

		} catch (final Exception e) {
            e.printStackTrace();
		}

	}

    public void initBeforeTrain(int output){
        final int inputNeuron = OCR.DOWNSAMPLE_HEIGHT
                * OCR.DOWNSAMPLE_WIDTH;
        final int outputNeuron = output;//this.letterListModel.size();

        this.clear();
        this.clearList();
        this.net = new SelfOrganizingMap(inputNeuron, outputNeuron,
                NormalizationType.MULTIPLICATIVE);
    }

	/**
	 * Called when the train button is pressed.
	 *
	 */
	public void train() {
        try {

            final int inputNeuron = OCR.DOWNSAMPLE_HEIGHT
                    * OCR.DOWNSAMPLE_WIDTH;

            final double set[][] = new double[this.letterListModel.size()][inputNeuron];

            for (int t = 0; t < this.letterListModel.size(); t++) {
                int idx = 0;
                final SampleData ds = (SampleData) this.letterListModel
                        .get(t);
                for (int y = 0; y < ds.getHeight(); y++) {
                    for (int x = 0; x < ds.getWidth(); x++) {
                        set[t][idx++] = ds.getData(x, y) ? .5 : -.5;
                    }
                }
            }


            final TrainSelfOrganizingMap train = new TrainSelfOrganizingMap(
                    this.net, set,LearningMethod.SUBTRACTIVE,0.5);
            int tries = 1;

            do {
                train.iteration();
            } while ((train.getTotalError() > MAX_ERROR) && !this.halt);

            this.halt = true;
            //update(tries, train.getTotalError(), train.getBestError());

            //this.letterListModel = new ArrayList();
        } catch (final Exception e) {
            e.printStackTrace();
        }
	}

}