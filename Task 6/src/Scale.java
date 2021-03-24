package GDM_U6;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class Scale implements PlugInFilter {
	
	ImagePlus imp;
	String method;
	int[] pix;
	int[] pix_n;
	int width;
	int height;
	int width_n;
	int height_n;
	
	public static void main(String[] args) {
		IJ.open("f:/Alles Mögliche/Uni/Projekte/GDM_Ubung/src/GDM_U6/component.jpg");
		Scale pw = new Scale();
		pw.imp = IJ.getImage();
		ImageProcessor processor = pw.imp.getProcessor();
		pw.run(processor);
	}
	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about"))
		{showAbout(); return DONE;}
		return DOES_RGB+NO_CHANGES;
		// kann RGB-Bilder und veraendert das Original nicht
	}
	public void run(ImageProcessor ip) {

		String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear"};

		GenericDialog gd = new GenericDialog("scale");
		gd.addChoice("Methode",dropdownmenue,dropdownmenue[0]);
		gd.addNumericField("Hoehe:",500,0);
		gd.addNumericField("Breite:",400,0);

		gd.showDialog();
		
		height_n = (int)gd.getNextNumber(); // _n fuer das neue skalierte Bild
		width_n =  (int)gd.getNextNumber();
		
		width  = ip.getWidth();  // Breite bestimmen
		height = ip.getHeight(); // Hoehe bestimmen

		//height_n = height;
		//width_n  = width;
		
		ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild",
		                   width_n, height_n, 1, NewImage.FILL_BLACK);
		
		ImageProcessor ip_n = neu.getProcessor();
		
		String choice = gd.getNextChoice();
		
		pix = (int[])ip.getPixels();
		pix_n = (int[])ip_n.getPixels();
		
		// Schleife ueber das neue Bild
		for (int y_n=0; y_n<height_n; y_n++) {
			for (int x_n=0; x_n<width_n; x_n++) {
				switch(choice) {
				
				case "Kopie":
					Kopie(x_n, y_n);
					break;
				case "Pixelwiederholung":
					Pixelwiederhholung(x_n, y_n);
					break;
				case "Bilinear":
					Bilinear(x_n, y_n);
					break;
				}
			}
		}


		// neues Bild anzeigen
		neu.show();
		neu.updateAndDraw();
	}
	void showAbout() {
		IJ.showMessage("");
	}
	private void Kopie(int x_n, int y_n) {
		int y = y_n;
		int x = x_n;
		
		if (y < height && x < width) {
			int pos_n = y_n*width_n + x_n;
			int pos  =  y  *width   + x;
		
			pix_n[pos_n] = pix[pos];
		}
	}
	private void Pixelwiederhholung(int x_n, int y_n) {
		double factorX = (double)(width-1)/(width_n-1);
		double factorY = (double)(height-1)/(height_n-1);
		
		int pos_n = pos(x_n, y_n, width_n);
		
		//Ursprünliche Werte
		int x = (int) Math.round(factorX*x_n);
		int y = (int) Math.round(factorY*y_n);
		
		int pos = pos(x, y, width);
		
		pix_n[pos_n] = pix[pos];
		
	}
	private void Bilinear(int x_n, int y_n) {
		
		double factorX = (double)(width-1)/(width_n-1);
		double factorY = (double)(height-1)/(height_n-1);
			
		//A:
		int ax = (int) Math.round(factorX * x_n);
		int ay = (int) Math.round(factorY * y_n);

		//all 9 points around A (x,y)
		int[][]posi = {	{ax-1, ax, ax+1, ax-1, ax, ax+1, ax-1, ax, ax+1},
						{ay-1, ay-1, ay-1, ay, ay, ay, ay+1, ay+1, ay+1}};
		
		//real position
		double x = factorX*x_n;
		double y = factorY+y_n;
		
		//A-D-rest
		//positon/distance
		Map<Integer, Double> A_D_Rest = new HashMap<Integer, Double>();
		
		//save distance for each of nine
		double distance;
		for(int i = 0; i<posi[0].length; i++) {
			int tempX = posi[0][i];
			int tempY = posi[1][i];
			
			//calc distance between real position and current pos
			distance = Math.sqrt(Math.pow(x-tempX, 2)+Math.pow(y-tempY, 2));
			
			int pos = pos(tempX, tempY, width);
			//fügt die Position hinzu wenn es größer 0 ist und nicht über das max geht
			if(pos>0 && pos < pix.length)
				A_D_Rest.put(pos(tempX, tempY, width), distance);
		}
		//A-D pos/distance
		Map<Integer, Double> A_D = new HashMap<Integer, Double>();
		//add all values from A-D together
		Double wholeLength = 0.0;
		for(int i = 0; i<4; i++) {
			Double min = Collections.min(A_D_Rest.values());
			int pos = 0;
			//find pos to min Value
			for(Entry<Integer, Double> pos_distance : A_D_Rest.entrySet()) {
				if(pos_distance.getValue() == min) {
					pos = pos_distance.getKey();
					break;
				}
			}
			A_D.put(pos, min);
			A_D_Rest.remove(pos);
			wholeLength += min;
		}
		
		int r = 0;
		int g = 0;
		int b = 0;
		for(Entry<Integer,Double> a_d : A_D.entrySet()) {
			//Wie lang dieser Punkt im Verhältnis entfernt ist
			Double percent = a_d.getValue()/wholeLength;
			percent = Math.round(percent*100)/100.0;;
			int rgb = pix[a_d.getKey()];
			RGB temp = new RGB(	(rgb >> 16) & 0xff,
								(rgb >> 8) & 0xff,
								 rgb & 0xff);
			r += (int) (percent*temp.getR());
			g += (int) (percent*temp.getG());
			b += (int) (percent*temp.getB());
		}	
		RGB P = new RGB(r, g, b);
		
		pix_n[pos(x_n, y_n, width_n)] = (0xff<<24) | (P.getR()<<16) | (P.getG()<<8) | (P.getB());
		
	}
	private int pos(int x, int y, int width) {
		return y*width+x;
	}
}

