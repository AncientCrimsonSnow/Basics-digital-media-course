package GDM_U2;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import GDM_U3.RGB;
import GDM_U3.YUV;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

/**
     Opens an image window and adds a panel below the image
     
     y_neu = (y_alt-128 *contrast + 128 + h.
     u_neu = u_alt * sat.
     v_neu = v_alt * sat.
     u_neu = cos(phi)*u_alt - sin(phi) * v_alt.
     v_neu = sin(phi)* v_alt + cos (phi) * v_alt.
     https://imi-gdm.github.io/uebungen/uebung2/uebung2.htm
*/
public class GRDM_U2 implements PlugIn {

    ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	
	
    public static void main(String args[]) {
		//new ImageJ();
    	IJ.open("D:/orchid.jpg");
    	//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");
		
		GRDM_U2 pw = new GRDM_U2();
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
    
    
    class CustomWindow extends ImageWindow implements ChangeListener {
         
        private JSlider jSliderHelligkeit;
		private JSlider jSliderKontrast;
		private JSlider jSliderS‰ttigung;
		private JSlider jSliderHue;
		
		private double brightness = 0;
		private double Kontrast = 10;
		private double Saettigung = 10;
		private double Hue = 90;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }
    
        void addPanel() {
        	//JPanel panel = new JPanel();
        	Panel panel = new Panel();

            panel.setLayout(new GridLayout(4, 1));
            jSliderHelligkeit = makeTitledSilder("Helligkeit", -128, 128, 0);
            jSliderKontrast = makeTitledSilder("Konrast", 0, 100, 10);
            jSliderS‰ttigung = makeTitledSilder("S‰ttigung", 0, 50, 10);
            jSliderHue = makeTitledSilder("Hue", 0, 360, 90);
            panel.add(jSliderHelligkeit);
            panel.add(jSliderKontrast);
            panel.add(jSliderS‰ttigung);
            panel.add(jSliderHue);
            
            add(panel);
            
            pack();
         }
      
        private JSlider makeTitledSilder(String string, int minVal, int maxVal, int val) {
		
        	JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
        	Dimension preferredSize = new Dimension(width, 50);
        	slider.setPreferredSize(preferredSize);
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), 
					string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
			slider.setMajorTickSpacing((maxVal - minVal)/10 );
			slider.setPaintTicks(true);
			slider.addChangeListener(this);
			
			return slider;
		}
        
        private void setSliderTitle(JSlider slider, String str) {
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
				str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
		}

		public void stateChanged( ChangeEvent e ){
			JSlider slider = (JSlider)e.getSource();

			if (slider == jSliderHelligkeit) {
				brightness = slider.getValue();
				String str = "Helligkeit " + brightness; 
				setSliderTitle(jSliderHelligkeit, str); 
			}
			
			if (slider == jSliderKontrast) {
				Kontrast = slider.getValue();
				String str = "Kontrast " + Kontrast/10; 
				setSliderTitle(jSliderKontrast, str); 
			}
			
			if (slider == jSliderS‰ttigung) {
				Saettigung = slider.getValue();
				String str = "Saettigung " + Saettigung/10; 
				setSliderTitle(jSliderS‰ttigung, str); 
			}
			
			if (slider == jSliderHue) {
				Hue = slider.getValue();
				String str = "Hue " + Hue; 
				setSliderTitle(jSliderHue, str); 
			}
			
			changePixelValues(imp.getProcessor());
			
			imp.updateAndDraw();
		}

		
		private void changePixelValues(ImageProcessor ip) {
			
			// Array fuer den Zugriff auf die Pixelwerte
			int[] pixels = (int[])ip.getPixels();
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
					
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					
					// anstelle dieser drei Zeilen sp√§ter hier die Farbtransformation durchf√ºhren,
					// die Y Cb Cr -Werte ver√§ndern und dann wieder zur√ºcktransformieren
					RGB rgb = new RGB(r,g,b);
					YUV yuv = rgb.transformToYUV();
					
					yuv = yuv.changeBrightness(brightness);
					yuv = yuv.changeKontrast(Kontrast/10);
					yuv = yuv.changeSaettigung(Saettigung/10);
					yuv = yuv.changeHue(Hue);
					
					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
					
					RGB rn = yuv.transformToRGB();
					pixels[pos] = (0xFF<<24) | (rn.getR()<<16) | (rn.getG()<<8) | rn.getB();
				}
			}
		}
		
    } // CustomWindow inner class
} 