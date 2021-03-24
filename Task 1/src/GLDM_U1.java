package GDM_U1;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)

public class GLDM_U1_S0571085 implements PlugIn {
	
	final static String[] choices = {
		"Schwarzes Bild",
		"Gelbes Bild",
		"belgischen Fahne",
		"Fahne der USA (ohne Sterne)",
		"horizontalen Schwarz/Rot Verlaufs bei gleichzeitigem vertikalen Schwarz/Blau Verlauf",
		"tschechichen Fahne",
		"bangladeschischen Fahne"
	};
	
	private String choice;
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		GLDM_U1_S0571085 imageGeneration = new GLDM_U1_S0571085();
		imageGeneration.run("");

	}
	
	public void run(String arg) {
		
		int width  = 566;  // Breite
		int height = 400;  // Hoehe
		
		// RGB-Bild erzeugen
		ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1", width, height, 1, NewImage.FILL_BLACK);
		ImageProcessor ip = imagePlus.getProcessor();
		
		// Arrays fuer den Zugriff auf die Pixelwerte
		int[] pixels = (int[])ip.getPixels();
		
		dialog();
		
		////////////////////////////////////////////////////////////////
		// Hier bitte Ihre Aenderungen / Erweiterungen
		
		if ( choice.equals("Schwarzes Bild") ) {
			generateBlackImage(width, height, pixels);
		}
		if ( choice.equals("Gelbes Bild") ) {
			generateYellowImage(width, height, pixels);
		}
		if ( choice.equals("belgischen Fahne") ) {
			generateBelgischeFahne(width, height, pixels);
		}
		if ( choice.equals("Fahne der USA (ohne Sterne)") ) {
			generateUSA(width, height, pixels);
		}
		if ( choice.equals("horizontalen Schwarz/Rot Verlaufs bei gleichzeitigem vertikalen Schwarz/Blau Verlauf") ) {
			generateBlackRed_Blackblue(width, height, pixels);
		}
		if ( choice.equals("tschechichen Fahne") ) {
			generateTschechichen(width, height, pixels);
		}
		if ( choice.equals("bangladeschischen Fahne") ) {
			generateBangladesch(width, height, pixels);
		}
		
		////////////////////////////////////////////////////////////////////
		
		// neues Bild anzeigen
		imagePlus.show();
		imagePlus.updateAndDraw();
	}

	private void generateBlackImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				int r = 0;
				int g = 0;
				int b = 0;
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void generateYellowImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				int r = 255;
				int g = 255;
				int b = 0;
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void generateBelgischeFahne(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				int r = 0;
				int g = 0;
				int b = 0;

				if(x > width * 2/3) {
					r = 255;
					g = 0;
					b = 0;
				}
				if(x <= width * 2/3 && x > width/3) {
					r = 255;
					g = 255;
					b = 0;
				}
				
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void generateUSA(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte

		int LBlau = (int) Math.round(0.4*width) + 1;
		int Streifen = (int)Math.round((double)height /13);		
		int StreifenNr = 1;
		boolean weiß = false;

		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
						
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
					
				int r = 255;						
				int g = 0;
				int b = 0;
				
				if(y==Streifen) {
					
					StreifenNr++;
					Streifen = (int) Math.round(StreifenNr*(double)height/13);				
					weiß = !weiß;
				}
				if(weiß) {
					r = 255;
					g = 255;
					b = 255;
				}
							
				if(StreifenNr <= 7 && x<=LBlau) {
					r = 0;
					g = 0;
					b = 255;
				}
				
				// Werte zurueckschreiben						
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void generateBlackRed_Blackblue(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte

		double Verti_Faktor = (double)height/255;
		double Hori_Faktor = (double) width/255;
		
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				
				int r = 0;
				int g = 0;
				int b = 0;
				
				r = (int) Math.round((double)x/Hori_Faktor);
				b = (int) Math.round((double)y/Verti_Faktor);
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void generateTschechichen(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				int r = 255;
				int g = 255;
				int b = 255;
				

				if(x<y && y<=height/2) {
					b=255;
					r = 0;
					g = 0;
				}
				else if(x<height-y && y>=height/2) {
						b=255;
						r = 0;
						g = 0;
						
					}
				else if(y>200){
						r=255;
						b=0;
						g=0;
					}						

				
				
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void generateBangladesch(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		
		int radius = (int) Math.round((double) width * 1/5);
		int distance;
		
		int p1x = (int) Math.round((double)width *2/5);
		int p1y = height/2;

		
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				int r = 0;
				int g = 106;
				int b = 78;
				
				distance = (int) Math.round(Math.sqrt(Math.pow((double)y - (double)p1y, 2)+Math.pow((double)x-(double)p1x, 2)));

				if(radius>distance) {
					r=244;
					g=42;
					b=65;
				}
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	
	
	private void dialog() {
		// Dialog fuer Auswahl der Bilderzeugung
		GenericDialog gd = new GenericDialog("Bildart");
		
		gd.addChoice("Bildtyp", choices, choices[0]);
		
		
		gd.showDialog();	// generiere Eingabefenster
		
		choice = gd.getNextChoice(); // Auswahl uebernehmen
		
		if (gd.wasCanceled())
			System.exit(0);
	}
}

