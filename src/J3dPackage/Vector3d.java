package J3dPackage;

public class Vector3d implements Cloneable{
	public double x;
	public double y;
	public double z;
	public Vector3d(double x,double y,double z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public Vector3d(){
		this.x=1;
		this.y=0;
		this.z=0;
	}
	public void cross(Vector3d a,Vector3d b){
		x=a.y*b.z-a.z*b.y;
		y=a.z*b.x-a.x*b.z;
		z=a.x*b.y-a.y*b.x;
	}
	public void normalize(){
		double l= Math.sqrt(x*x+y*y+z*z);
		x=x/l;
		y=y/l;
		z=z/l;
	}
	public double angle(Vector3d xz) {
		Vector3d a=xz.clone();
		Vector3d b=this.clone();
		a.normalize();
		b.normalize();
		double c=Math.sqrt(a.x*b.x+a.y*b.y+a.z*b.z);
		double s_x=(2-(c*c))/2;
		double s_y=Math.sqrt(1-(s_x*s_x));
		return Math.atan2(s_x, s_y);
	}
	public double length(){
		return Math.sqrt(x*x+y*y+z*z);
	}
	@Override
	public Vector3d clone(){
		return new Vector3d(x,y,z);
	}
	public double dot(Vector3d a) {
		return a.length()*length()*angle(a);
	}
	@Override
	public String toString(){
		return "("+x+";"+y+";"+z+")";
	}
}
