
package model.ocr;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;


public class Entry {

    BufferedImage entryImage;

	/**
	 * The down sample component used with this component.
	 */
	protected SampleData sample;

	/**
	 * Specifies the left boundary of the cropping rectangle.
	 */
	protected int downSampleLeft;

	/**
	 * Specifies the right boundary of the cropping rectangle.
	 */
	protected int downSampleRight;

	/**
	 * Specifies the top boundary of the cropping rectangle.
	 */
	protected int downSampleTop;

	/**
	 * Specifies the bottom boundary of the cropping rectangle.
	 */
	protected int downSampleBottom;

	/**
	 * The downsample ratio for x.
	 */
	protected double ratioX;

	/**
	 * The downsample ratio for y
	 */
	protected double ratioY;

	/**
	 * The pixel map of what the user has drawn. Used to downsample it.
	 */
	protected int pixelMap[];


    public void setEntryImage(BufferedImage entryImage){
        this.entryImage= entryImage;
    }


	/**
	 * Called to downsample the image and store it in the down sample component.
	 */
	public void downSample() {
		final int w = entryImage.getWidth();
		final int h = entryImage.getHeight();

		final PixelGrabber grabber = new PixelGrabber(entryImage, 0, 0, w,
				h, true);
		try {

			grabber.grabPixels();
			this.pixelMap = (int[]) grabber.getPixels();
			findBounds(w, h);

			// now downsample
			final SampleData data = this.sample;

			this.ratioX = (double) (this.downSampleRight - this.downSampleLeft)
					/ (double) data.getWidth();
			this.ratioY = (double) (this.downSampleBottom - this.downSampleTop)
					/ (double) data.getHeight();

			for (int y = 0; y < data.getHeight(); y++) {
				for (int x = 0; x < data.getWidth(); x++) {
					if (downSampleRegion(x, y)) {
						data.setData(x, y, true);
					} else {
						data.setData(x, y, false);
					}
				}
			}
		} catch (final InterruptedException e) {
		}
	}

	/**
	 * Called to downsample a quadrant of the image.
	 * 
	 * @param x
	 *            The x coordinate of the resulting downsample.
	 * @param y
	 *            The y coordinate of the resulting downsample.
	 * @return Returns true if there were ANY pixels in the specified quadrant.
	 */
	protected boolean downSampleRegion(final int x, final int y) {
		final int w = this.entryImage.getWidth();
		final int startX = (int) (this.downSampleLeft + (x * this.ratioX));
		final int startY = (int) (this.downSampleTop + (y * this.ratioY));
		final int endX = (int) (startX + this.ratioX);
		final int endY = (int) (startY + this.ratioY);

		for (int yy = startY; yy <= endY; yy++) {
			for (int xx = startX; xx <= endX; xx++) {
				final int loc = xx + (yy * w);

				if (this.pixelMap[loc] != -1) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * This method is called to automatically crop the image so that whitespace
	 * is removed.
	 * 
	 * @param w
	 *            The width of the image.
	 * @param h
	 *            The height of the image
	 */
	protected void findBounds(final int w, final int h) {
		// top line
		for (int y = 0; y < h; y++) {
			if (!hLineClear(y)) {
				this.downSampleTop = y;
				break;
			}

		}
		// bottom line
		for (int y = h - 1; y >= 0; y--) {
			if (!hLineClear(y)) {
				this.downSampleBottom = y;
				break;
			}
		}
		// left line
		for (int x = 0; x < w; x++) {
			if (!vLineClear(x)) {
				this.downSampleLeft = x;
				break;
			}
		}

		// right line
		for (int x = w - 1; x >= 0; x--) {
			if (!vLineClear(x)) {
				this.downSampleRight = x;
				break;
			}
		}
	}

	/**
	 * Get the down sample component to be used with this component.
	 * 
	 * @return The down sample component.
	 */
	public SampleData getSample() {
		return this.sample;
	}

	/**
	 * This method is called internally to see if there are any pixels in the
	 * given scan line. This method is used to perform autocropping.
	 * 
	 * @param y
	 *            The horizontal line to scan.
	 * @return True if there were any pixels in this horizontal line.
	 */
	protected boolean hLineClear(final int y) {
		final int w = this.entryImage.getWidth();
		for (int i = 0; i < w; i++) {
			if (this.pixelMap[(y * w) + i] != -1) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Set the sample control to use. The sample control displays a downsampled
	 * version of the character.
	 * 
	 * @param s
	 */
	public void setSampleData(final SampleData s) {
		this.sample = s;
	}

	/**
	 * This method is called to determine ....
	 * 
	 * @param x
	 *            The vertical line to scan.
	 * @return True if there are any pixels in the specified vertical line.
	 */
	protected boolean vLineClear(final int x) {
		final int w = this.entryImage.getWidth();
		final int h = this.entryImage.getHeight();
		for (int i = 0; i < h; i++) {
			if (this.pixelMap[(i * w) + x] != -1) {
				return false;
			}
		}
		return true;
	}
}