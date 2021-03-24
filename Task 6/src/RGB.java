package GDM_U6;

public class RGB {
	private int r;
	private int g;
	private int b;

	public RGB(double r, double g, double b){
		this.setR((int)Math.round(r));
		this.setG((int)Math.round(g));
		this.setB((int)Math.round(b));
	}
	
	public RGB(int r, int g, int b){
		this.setR(r);
		this.setG(g);
		this.setB(b);
	}
	
	public YUV transformToYUV(){
		double Y = 0.299 * getR() + 0.587 * getG() + 0.114 * getB();
		double U = (getB()-Y)*0.493;
		double V = (getR()-Y)*0.877;
		
		return new YUV(Y,U,V);
	}
	public static int Limiter(int x){
		if(x<0){
			x=0;
		}
		if(x>255){
			x=255;
		}
		
		return x;	
	}

	    public boolean equals(RGB rgb){
	        if(getR()==rgb.getR()){
	            if(getG()==rgb.getG()){
	                if(getB()==rgb.getB()){
	                    return true;
	                }else{
	                    System.out.println("B "+getB()+"!="+rgb.getB());
	                }
	            }else{
	                System.out.println("G "+getG()+"!="+rgb.getG());
	            }
	        }else{
	            System.out.println("R "+getR()+"!="+rgb.getR());
	        }
	        return false;
	    }

	public int getR() {
		return r;
	}
	public void setR(int r) {
		this.r = Limiter(r);
	}
	public int getG() {
		return g;
	}
	public void setG(int g) {
		this.g = Limiter(g);
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = Limiter(b);
	}
}
