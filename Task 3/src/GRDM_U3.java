package GDM_U3;

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
public class GRDM_U3 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {"Original", "Rot-Kanal", "Graustufen","Negativ","Binärbild","Fehlerdiffusion","Sepia", "SixColor"};


	public static void main(String args[]) {

		IJ.open("D:/Bear.jpg");
		//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		GRDM_U3 pw = new GRDM_U3();
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

			// Array zum ZurÃ¼ckschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			if (method.equals("Original")) 
				Original(pixels);		
			if (method.equals("Rot-Kanal")) 
				Rot_Kanal(pixels);
			if (method.equals("Graustufen")) 
				Graustufen(pixels);
			if (method.equals("Negativ")) 
				Negativ(pixels);
			
				int stufen = 10;
			if (method.equals("Binärbild"))
				Binärbild(pixels,stufen);
			if (method.equals("Fehlerdiffusion")) 
				Fehlerdiffusion(pixels);
			if (method.equals("Sepia")) 
				Sepia(pixels);
			if (method.equals("SixColor")) 
				Six_Color(pixels);			
		}
		private void Original(int[] pixels) {
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					
					pixels[pos] = origPixels[pos];
				}
			}
		}
		private void Rot_Kanal(int[] pixels) {
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					//int g = (argb >>  8) & 0xff;
					//int b =  argb        & 0xff;

					int rn = r;
					int gn = 0;
					int bn = 0;

					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		private void Graustufen(int[] pixels) {
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					RGB rgb = new RGB(r,b,b);
					YUV yuv = rgb.transformToYUV();
					yuv.setU(0);
					yuv.setV(0);
					
					rgb = yuv.transformToRGB();
					int rn = rgb.getR();
					int gn = rgb.getG();
					int bn = rgb.getB();

					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		private void Negativ(int[] pixels) {
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;

					int rn = 255-r;
					int gn = 255-g;
					int bn = 255-b;

					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
			
			
			
		}
		private void Binärbild(int[] pixels, int Stufen) {
			if(Stufen < 1) Stufen = 1;
			if(Stufen > 255) Stufen = 255;
			double delta = 255/Stufen;
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					
					RGB rgb = new RGB(r,b,b);
					YUV yuv = rgb.transformToYUV();
					yuv.setU(0);
					yuv.setV(0);
					
					if(Stufen == 1) yuv.setY(0);
					else {		
						if(yuv.getY()<delta) yuv.setY(0);
						else {
							for(int i = 1; i<= Stufen;i++) {
								if(i*delta>=yuv.getY()) {
									yuv.setY(i*delta);
									break;
								}
							}
						}		
					}					
					rgb = yuv.transformToRGB();
					int rn = rgb.getR();
					int gn = rgb.getG();
					int bn = rgb.getB();

					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		private void Fehlerdiffusion(int[] pixels) {
			int Stufen = 2;
			int error = 0;
			double delta = 255/Stufen;
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					
					RGB rgb = new RGB(r,b,b);
					YUV yuv = rgb.transformToYUV();
					yuv.setU(0);
					yuv.setV(0);
					

					int Ynew = 255;

					
					if(yuv.getY()+error<delta) {
						Ynew = 0;
					}
					else {
						for(int i = 1; i<= Stufen;i++) {
							if(i*delta>=yuv.getY()+error) {							
								Ynew = (int) Math.round(i*delta);
								break;
							}	
						}		
					}
					error = (int) (yuv.getY() + error - Ynew);
					yuv.setY(Ynew);
					
					
					rgb = yuv.transformToRGB();
					int rn = rgb.getR();
					int gn = rgb.getG();
					int bn = rgb.getB();

					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		private void Sepia(int[] pixels) {
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					//https://stackoverflow.com/questions/21899824/java-convert-a-greyscale-and-sepia-version-of-an-image-with-bufferedimage
					int rn = RGB.Limiter((int) (r*0.393 + g*0.769+b*0.189));
					int gn = RGB.Limiter((int) (r*0.349 + g*0.686+b*0.168));
					int bn = RGB.Limiter((int) (r*0.272 + g*0.534+b*0.131));

					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		private void Six_Color(int[] pixels) {
			RGB [] Colors = {
				new RGB(0,0,0),
				new RGB(255,255,255),
				new RGB(112,80,67),
				new RGB(46,100,138),
				new RGB(164,133,102),
				new RGB(70,69,65)};
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 

					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					double distance = 500;
					double newDistance;
					RGB closeColor = Colors[0];
					for(RGB E: Colors) {
						newDistance = Math.sqrt(Math.pow(r-E.getR(),2) + Math.pow(g-E.getG(),2) + Math.pow(b-E.getB(),2));
						if(newDistance<distance) {
							distance = newDistance;
							closeColor = E;
						}						
					}

					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

					pixels[pos] = (0xFF<<24) | (closeColor.getR()<<16) | (closeColor.getG()<<8) | closeColor.getB();
				}
			}
			
		}


	} // CustomWindow inner class
} 