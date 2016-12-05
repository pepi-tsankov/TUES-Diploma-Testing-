package maths;

import java.io.File;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import J3DBool.Solid;
public class Math3d {
	public static double SignedVolumeOfTriangle(Point3d ps, Point3d ps2, Point3d ps3) {
	    double v321 = ps3.x*ps2.y*ps.z;
	    double v231 = ps2.x*ps3.y*ps.z;
	    double v312 = ps3.x*ps.y*ps2.z;
	    double v132 = ps.x*ps3.y*ps2.z;
	    double v213 = ps2.x*ps.y*ps3.z;
	    double v123 = ps.x*ps2.y*ps3.z;
	    return (1.0f/6.0f)*(-v321 + v231 + v312 - v132 - v213 + v123);
	}
	public static double VolumeOfMesh(Solid mesh) {
		Point3d[] ps=mesh.getVertices();
		int[] inds=mesh.getIndices();
		double volume=0;
		for(int i=0;i<inds.length;i+=3){
			volume+=SignedVolumeOfTriangle(ps[inds[i]],ps[inds[i+1]],ps[inds[i+2]]);
		}
	    return Math.abs(volume);
	}
	public static Vector3d rotateZ(Vector3d vec,double angle){
		angle=Math.toRadians(angle);
		Vector3d result=new Vector3d(vec.x*Math.cos(angle)-vec.y*Math.sin(angle),vec.x*Math.sin(angle)+vec.y*Math.cos(angle),vec.z);
		result.normalize();
		return result;
	}
	public static Vector3d rotateX(Vector3d vec,double angle){
		angle=Math.toRadians(angle);
		Vector3d result=new Vector3d(vec.x,vec.y*Math.cos(angle)-vec.z*Math.sin(angle),vec.y*Math.sin(angle)+vec.z*Math.cos(angle));
		result.normalize();
		return result;
	}
	public static Vector3d rotateY(Vector3d vec,double angle){
		angle=Math.toRadians(angle);
		Vector3d result=new Vector3d(vec.x*Math.cos(angle)+vec.z*Math.sin(angle),vec.y,vec.z*Math.cos(angle)-vec.x*Math.sin(angle));
		result.normalize();
		return result;
	}
	public static double distance(Point3d a,Point3d b){
		return(Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y)+(a.z-b.z)*(a.z-b.z)));
	}
	public static Solid getSphereAt(Point3d p,double size,Color3f c){
		size/=2;
		Solid s=new Solid(new File("./HelperObjects/Sphere.solid"),c);
		Point3d[] verticies=s.getVertices();
		if(p==null){return new Solid();}
		for(int i=0;i<s.getVertices().length;i++){
			verticies[i].x*=size;
			verticies[i].y*=size;
			verticies[i].z*=size;
			verticies[i].x+=p.x;
			verticies[i].y+=p.y;
			verticies[i].z+=p.z;
		}
		s.setData(verticies, s.getIndices(), s.getColors());
		return s;
	}
	public static Solid getPlayerColisonAt(Point3d p){
		Solid s=new Solid(new File("./HelperObjects/player.solid"),new Color3f(0,0,1));
		Point3d[] verticies=s.getVertices();
		if(p==null){return new Solid();}
		for(int i=0;i<s.getVertices().length;i++){
			verticies[i].x+=p.x;
			verticies[i].y+=p.y;
			verticies[i].z+=p.z;
		}
		s.setData(verticies, s.getIndices(), s.getColors());
		return s;
	}
	public static Point3d getClosest(Solid s,Point3d p){
		Point3d closest=new Point3d(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);
		double cd=Double.MAX_VALUE;
		for(int i=0;i<s.getVertices().length;i++){
			if(distance(s.getVertices()[i],p)<cd){
				cd=distance(s.getVertices()[i],p);
				closest=s.getVertices()[i];
			}
		}
		return closest;
	}
	public static Point3d getLoadCenter(Solid s){
		double x=0;
		double y=0;
		double z=0;
		for(int i=s.getVertices().length-1;i>=0;i--){
			x+=s.getVertices()[i].x;
			y+=s.getVertices()[i].y;
			z+=s.getVertices()[i].z;
		}
		x/=s.getVertices().length;
		y/=s.getVertices().length;
		z/=s.getVertices().length;
		return new Point3d(x,y,z);
	}
}
