package Main;

import java.util.Vector;

import J3dPackage.*;
import maths.*;

public class Main {
	public static void main(String[] args){
		Time.timeSetup();
		Vector<Point3d> a=Math3d.LineIntersections(new Point3d(0,0,0),new Vector3d(1,1,1), 5);
		Time.time();
		Time.timeSetup();
		a=Math3d.LineIntersections(new Point3d(0,0,0),new Vector3d(-1,-1,-1), 64);
		Time.time();
		System.out.println(a);
	}
}
