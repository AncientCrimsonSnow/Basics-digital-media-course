package GDM_U4_2;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.*;
import ij.gui.*;
import java.awt.*;

import GDM_U3.RGB;
import ij.plugin.filter.*;


public class GRDM_U4 implements PlugInFilter {

	protected ImagePlus imp;
	final static String[] choices = {"Wischen", "Weiche Blende","Overlay(A,B)","Overlay(B,A)","Schieben", "Chroma Key", "Extra"};

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB+STACK_REQUIRED;
	}
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		IJ.open("D:\\StackB.zip");
		
		GRDM_U4 sd = new GRDM_U4();
		sd.imp = IJ.getImage();
		ImageProcessor B_ip = sd.imp.getProcessor();
		sd.run(B_ip);
	}

	public void run(ImageProcessor B_ip) {
		
		// Film B wird uebergeben
		ImageStack stack_B = imp.getStack();
		
		int length = stack_B.getSize();
		int width  = B_ip.getWidth();
		int height = B_ip.getHeight();
		
		// ermoeglicht das Laden eines Bildes / Films
		Opener o = new Opener();
		OpenDialog od_A = new OpenDialog("Ausw√É¬§hlen des 2. Filmes ...",  "");
				
		// Film A wird dazugeladen
		String dateiA = od_A.getFileName();
		if (dateiA == null) return; // Abbruch
		String pfadA = od_A.getDirectory();
		ImagePlus A = o.openImage(pfadA,dateiA);
		if (A == null) return; // Abbruch

		ImageProcessor A_ip = A.getProcessor();
		ImageStack stack_A  = A.getStack();

		if (A_ip.getWidth() != width || A_ip.getHeight() != height)
		{
			IJ.showMessage("Fehler", "Bildgr√É¬∂√É≈∏en passen nicht zusammen");
			return;
		}
		
		// Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
		length = Math.min(length,stack_A.getSize());

		ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
		ImageStack stack_Erg  = Erg.getStack();

		// Dialog fuer Auswahl des Ueberlagerungsmodus
		GenericDialog gd = new GenericDialog("√É≈ìberlagerung");
		gd.addChoice("Methode",choices,"");
		gd.showDialog();

		int methode = 0;		
		String s = gd.getNextChoice();
		if (s.equals("Wischen")) methode = 1;
		if (s.equals("Weiche Blende")) methode = 2;
		if (s.equals("Overlay(A,B)")) methode = 3;
		if (s.equals("Overlay(B,A)")) methode = 4;
		if (s.equals("Schieben")) methode = 5;
		if (s.equals("Chroma Key")) methode =6;
		if (s.equals("Extra")) methode = 7;

		// Arrays fuer die einzelnen Bilder
		int[] pixels_B;
		int[] pixels_A;
		int[] pixels_Erg;
		boolean test = true;
		boolean testa = true;
		boolean testb = true;
		boolean testc = true;
		// Schleife ueber alle Bilder
		for (int z=1; z<=length; z++)
		{
			boolean test2 = true;
			
			pixels_B   = (int[]) stack_B.getPixels(z);
			pixels_A   = (int[]) stack_A.getPixels(z);
			pixels_Erg = (int[]) stack_Erg.getPixels(z);

			int pos = 0;
			for (int y=0; y<height; y++)
				for (int x=0; x<width; x++, pos++)
				{
					int cA = pixels_A[pos];
					int rA = (cA & 0xff0000) >> 16;
					int gA = (cA & 0x00ff00) >> 8;
					int bA = (cA & 0x0000ff);

					int cB = pixels_B[pos];
					int rB = (cB & 0xff0000) >> 16;
					int gB = (cB & 0x00ff00) >> 8;
					int bB = (cB & 0x0000ff);

					//zX = 0 z = 0%; lengthz = 100%
					int zX = (int) ((z-1)*(double)width/(length-1));
					
					if (methode == 1)
					{
						if (y+1 > zX)
							pixels_Erg[pos] = pixels_B[pos];
						else
							pixels_Erg[pos] = pixels_A[pos];
					}

					if (methode == 2){
						int alpha = (z-1)*255/(length-1);
						//int alpha = (z-1)/(length-1)*255;
						
						int r = (rB*alpha+rA*(255-alpha))/255;
						int g = (gB*alpha+gA*(255-alpha))/255;
						int b = (bB*alpha+bA*(255-alpha))/255;
						
						r = RGB.Limiter(r);
						g = RGB.Limiter(g);
						b = RGB.Limiter(b);
						
						
					pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					if (methode == 3){
				
						
						int r = ((rB<=128)? rA*rB/128 : 255-((255-rA)*(255-rB)/128));
						int g = ((gB<=128)? gA*gB/128 : 255-((255-gA)*(255-gB)/128));
						int b = ((bB<=128)? bA*bB/128 : 255-((255-bA)*(255-bB)/128));

						r = RGB.Limiter(r);
						g = RGB.Limiter(g);
						b = RGB.Limiter(b);
						
					pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					if (methode == 4){
				
						
						int r = ((rA<=128)? rB*rA/128 : 255-((255-rB)*(255-rA)/128));
						int g = ((gA<=128)? gB*gA/128 : 255-((255-gB)*(255-gA)/128));
						int b = ((bA<=128)? bB*bA/128 : 255-((255-bB)*(255-bA)/128));

						r = RGB.Limiter(r);
						g = RGB.Limiter(g);
						b = RGB.Limiter(b);
						
					pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}
					if (methode == 5){
						//Berechnet unabh‰ngig von R‰ndern die neue Position vom Pixel
						int posNew = pos - zX;
						//ist posNew ist zu weit rechts
						if(posNew>pixels_A.length-1)
							//posNew wird rechter Rand
							posNew=pixels_A.length-1;
						else {
						//ist posNew zu weit links setzte auf linken Rand
							if(posNew<0)
								posNew = 0;
						}
						//Wenn Pixel.x ist grˆﬂer als die Zeit(Rechts Bild)
						if (x+1 > zX){	
							pixels_Erg[pos] = pixels_A[posNew];
						}
						//Wenn Pixel.x ist kleiner als die Zeit(Rechts Bild)
						else {
							pixels_Erg[pos] = pixels_B[posNew];
						}
					}
					if(methode == 6){
						//orange
						int refR = 222;
						int refG = 182;
						int refB = 64;
						
						double distanceOrange = Math.sqrt(	Math.pow(refR - rA,2) +
															Math.pow(refR - gA,2) +
															Math.pow(refR - bA,2));
						double distanceWhite = Math.sqrt(	Math.pow(255 - rA,2) +
															Math.pow(255 - gA,2) +
															Math.pow(255 - bA,2));
						double distanceBlack = Math.sqrt(	Math.pow(0 - rA,2) +
															Math.pow(0 - gA,2) +
															Math.pow(0 - bA,2));
						if(distanceOrange > distanceWhite || distanceOrange > distanceBlack)
							pixels_Erg[pos] = pixels_A[pos];
						else
							pixels_Erg[pos] = pixels_B[pos];
								
					}
					if(methode == 7){
						
						//mittleren 40% sind die blende
						int Start = (int)(length - 0.7*length);
						int End = (int)(length - 0.3*length);;


						if(z >= Start && z <= End) {
							
							int cross = End - Start;
							int crossPos = End-z;
							int percent = (int)(100-((double)crossPos/(double)cross*100));
							int grad = (int)(3.6*(double)percent);
							Point Mid = new Point(width/2, height/2);
							int[] angles = {
									UM.getAngle(Mid, new Point(0, height)),
									UM.getAngle(Mid, new Point(0, 0)),
									UM.getAngle(Mid, new Point(width, 0)),
									UM.getAngle(Mid, new Point(width, height)),
							};
								
							int[] xpoints = new int[3];
							int[] ypoints = new int[3];
							int points = 3;	
							Polygon area = null;
							
							if(grad <= angles[1]) {
								points = 3;
								xpoints = new int[points];
								ypoints = new int[points];
								xpoints[0] = Mid.getX();
								ypoints[0] = Mid.getY();
								xpoints[1] = Mid.getX();;
								ypoints[1] = 0;
								xpoints[2] = 0;
								ypoints[2] = 0;
								if(test) {
									System.out.println("Hi");
									for(int i = 0;i<points;i++) {
										System.out.print("X: "+xpoints[i]+ "; ");
										System.out.print("Y: "+ypoints[i]+"; ");
									}
									test=false;
								}
							}					
							else if(grad <= angles[2]) {
								points = 4;
								xpoints = new int[points];
								ypoints = new int[points];
								xpoints[0] = Mid.getX();
								ypoints[0] = Mid.getY();
								xpoints[1] = Mid.getX();;
								ypoints[1] = 0;
								xpoints[2] = 0;
								ypoints[2] = 0;
								xpoints[3] = 0;
								ypoints[3] = height;
								if(testa) {
									System.out.println("Hi");
									for(int i = 0;i<points;i++) {
										System.out.print("X: "+xpoints[i]+ "; ");
										System.out.print("Y: "+ypoints[i]+"; ");
									}
									testa=false;
								}
							}					
							else if(grad <= angles[3]) {
								points = 5;
								xpoints = new int[points];
								ypoints = new int[points];
								xpoints[0] = Mid.getX();
								ypoints[0] = Mid.getY();
								xpoints[1] = Mid.getX();;
								ypoints[1] = 0;
								xpoints[2] = 0;
								ypoints[2] = 0;
								xpoints[3] = 0;
								ypoints[3] = height;
								xpoints[4] = width;
								ypoints[4] = height;
								if(testb) {
									System.out.println("Hi");
									for(int i = 0;i<points;i++) {
										System.out.print("X: "+xpoints[i]+ "; ");
										System.out.print("Y: "+ypoints[i]+"; ");
									}
									testb=false;
								}
							}
							else if(grad <= 360) {
								points = 6;
								xpoints = new int[points];
								ypoints = new int[points];
								xpoints[0] = Mid.getX();
								ypoints[0] = Mid.getY();
								xpoints[1] = Mid.getX();;
								ypoints[1] = 0;
								xpoints[2] = 0;
								ypoints[2] = 0;
								xpoints[3] = 0;
								ypoints[3] = height;
								xpoints[4] = width;
								ypoints[4] = height;
								xpoints[5] = width;
								ypoints[5] = 0;
								if(testc) {
									System.out.println("Hi");
									for(int i = 0;i<points;i++) {
										System.out.print("X: "+xpoints[i]+ "; ");
										System.out.print("Y: "+ypoints[i]+"; ");
									}
									testc=false;
								}
							}
							if(test2) {
								System.out.println("");
								System.out.print("Z:" + z);
								System.out.print(" " +grad+"∞");
								System.out.print(" "+ percent+"%");
								test2=false;
							}
							area = new Polygon(xpoints, ypoints, points);

							if(area.contains(x, y)) 
								pixels_Erg[pos] = pixels_B[pos];
							else 
								pixels_Erg[pos] = pixels_A[pos];
						}
						else
							if(z<Start)
								pixels_Erg[pos] = pixels_A[pos];
							if(z>Start)
								pixels_Erg[pos] = pixels_B[pos];
					}
				}
		}
		// neues Bild anzeigen
		Erg.show();
		Erg.updateAndDraw();

	}

}
