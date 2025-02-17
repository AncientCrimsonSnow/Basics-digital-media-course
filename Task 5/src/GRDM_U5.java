package GDM_U5_MY;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
 */
public class GRDM_U5 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {"Original", "Filter 1", "Filter 2", "Filter 3"};


	public static void main(String args[]) {

		IJ.open("D:/sail.jpg");
		//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		GRDM_U5 pw = new GRDM_U5();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	public void run(String arg) {
		if (imp==null) 
			imp = WindowManager.getCurrentImage();
		if (imp==null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}


	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}

	} // CustomCanvas inner class


	class CustomWindow extends ImageWindow implements ItemListener {

		private String method;
		
		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			//JPanel panel = new JPanel();
			Panel panel = new Panel();

			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);

			add(panel);
			pack();
		}

		public void itemStateChanged(ItemEvent evt) {

			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			} 

		}


		private void changePixelValues(ImageProcessor ip) {

			
			double[] matrix = new double[9];

			// Array zum Zurückschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			if (method.equals("Original")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						
						pixels[pos] = origPixels[pos];
					}
				}
			}
			if (method.equals("Filter 1")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						
						for(int i = 0; i<matrix.length;i++) {
							matrix[i] = 1.0/9.0;
						}
						int pos = y*width + x;
						
						int[]mPos = {
								(y-1)*width + (x-1),
								(y-1)*width + x,
								(y-1)*width + (x-1),
								y*width + (x-1),
								pos,
								y*width + x+1,
								(y+1)*width + x-1,
								(y+1)*width + x,
								(y+1)*width + x+1
						};
						for(int i = 0;i<mPos.length;i++) {
							if(mPos[i]<0)
								mPos[i] = 0;
							if(mPos[i]>origPixels.length-1)
								mPos[i] = 0;
						}
						
						int rn = 0;
						int gn = 0;
						int bn = 0;
						
						int argb = 0;
						for(int i = 0; i<mPos.length;i++) {
							argb = origPixels[mPos[i]];
							
							int r = (argb >> 16) & 0xff;
							int g = (argb >>  8) & 0xff;
							int b =  argb        & 0xff;
							
							rn += r*matrix[i];
							gn += g*matrix[i];
							bn += b*matrix[i];
						}
						
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
					}
				}
			}
			if (method.equals("Filter 2")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						
						for(int i = 0; i<matrix.length;i++) {
							matrix[i] = -1.0/9.0;
						}
						matrix[4] = 8.0/9.0;
						
						int pos = y*width + x;
						
						int[]mPos = {
								(y-1)*width + (x-1),
								(y-1)*width + x,
								(y-1)*width + (x+1),
								y*width + (x-1),
								pos,
								y*width + x+1,
								(y+1)*width + x-1,
								(y+1)*width + x,
								(y+1)*width + x+1
						};
						for(int i = 0;i<mPos.length;i++) {
							if(mPos[i]<0)
								mPos[i] = 0;
							if(mPos[i]>origPixels.length-1)
								mPos[i] = 0;
						}
						
						int rn = 0;
						int gn = 0;
						int bn = 0;
						
						int argb = 0;
						for(int i = 0; i<mPos.length;i++) {
							argb = origPixels[mPos[i]];
							
							int r = (argb >> 16) & 0xff;
							int g = (argb >>  8) & 0xff;
							int b =  argb        & 0xff;
							
							rn += r*matrix[i];
							gn += g*matrix[i];
							bn += b*matrix[i];
						}
						
						RGB rgb= new RGB(rn+128,gn+128,bn+128);
						
						pixels[pos] = (0xFF<<24) | (rgb.getR()<<16) | (rgb.getG() << 8) | rgb.getB();
					}
				}
			}
			if (method.equals("Filter 3")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						
						for(int i = 0; i<matrix.length;i++) {
							matrix[i] = -1.0/9.0;
						}
						matrix[4] = 17.0/9.0;
						int pos = y*width + x;
						
						int[]mPos = {
								(y-1)*width + (x-1),
								(y-1)*width + x,
								(y-1)*width + (x-1),
								y*width + (x-1),
								pos,
								y*width + x+1,
								(y+1)*width + x-1,
								(y+1)*width + x,
								(y+1)*width + x+1
						};
						for(int i = 0;i<mPos.length;i++) {
							if(mPos[i]<0)
								mPos[i] = 0;
							if(mPos[i]>origPixels.length-1)
								mPos[i] = 0;
						}
						
						int rn = 0;
						int gn = 0;
						int bn = 0;
						
						int argb = 0;
						for(int i = 0; i<mPos.length;i++) {
							argb = origPixels[mPos[i]];
							
							int r = (argb >> 16) & 0xff;
							int g = (argb >>  8) & 0xff;
							int b =  argb        & 0xff;
							
							rn += r*matrix[i];
							gn += g*matrix[i];
							bn += b*matrix[i];
						}
						
						RGB rgb= new RGB(rn,gn,bn);
						
						pixels[pos] = (0xFF<<24) | (rgb.getR()<<16) | (rgb.getG() << 8) | rgb.getB();
					}
				}
			}	
		}


	} // CustomWindow inner class
} 