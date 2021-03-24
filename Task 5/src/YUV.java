package GDM_U5_MY;

public class YUV {
	private double y;
	private double U;
	private double V;

	public YUV() {
		// TODO Auto-generated constructor stub
	}
	
	public YUV(double y,double U, double V){
		this.setY(y);
		this.setU(U);
		this.setV(V);
	}
	
	public YUV changeBrightness(double brightness){
		YUV yuv = new YUV();
		
		yuv.setY(getY() + brightness);
		yuv.setU(getU());
		yuv.setV(getV());
		
		return yuv;
	}
	
	public YUV changeKontrast(double Kontrast){
		YUV yuv = new YUV();
		
		yuv.setY(Kontrast*(getY()-128) + 128);
		yuv.setU(getU());
		yuv.setV(getV());
		
		return yuv;
	}
	
	public YUV changeSaettigung(double Saettigung){
		YUV yuv = new YUV();
		
		yuv.setY(getY());
		yuv.setU(getU()*Saettigung);
		yuv.setV(getV()*Saettigung);
		
		return yuv;
	}
	
	public YUV changeHue(double Hue){
		YUV yuv = new YUV();
		
		yuv.setY(getY());
		yuv.setU(getU()* ( Math.cos(Math.toRadians(Hue)) + Math.sin(Math.toRadians(Hue))));
		yuv.setV(getV()* ( Math.sin(Math.toRadians(Hue)) - Math.cos(Math.toRadians(Hue))));
		
		return yuv;
	}

	
	public RGB transformToRGB(){
		double R = getY() + getV()/0.877;
		double B = getY() + getU()/0.493;
		double G = 1/0.587*getY() - 0.299/0.587*R - 0.114/0.587*B;
		
		return new RGB(R,G,B);
	}
	
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getU() {
		return U;
	}
	public void setU(double U) {
		this.U = U;
	}
	public double getV() {
		return V;
	}
	public void setV(double V) {
		this.V = V;
	}

	
}