package Main;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import J3DBool.BooleanModeller;
import J3DBool.Solid;
import maths.Math3d;

public class Chunks {
	Hashtable<String, Chunk> chunks;
	private String gameName;
	private TransformGroup tr;
	private TransformGroup helptr;
	private BranchGroup helpbr;
	Chunks(String s,TransformGroup tr){
		gameName = s;
		this.tr=tr;
		chunks=new Hashtable<String, Chunk>();
		helpbr=new BranchGroup();
		helptr=new TransformGroup();
		helpbr.setCapability(BranchGroup.ALLOW_DETACH);
		helpbr.addChild(helptr);
	}
	public Chunk getChunk(long x, long y, long z){
		return chunks.get("("+x+"."+y+"."+z+")");
	}
	public void importChunk(long x,long y,long z){
		chunks.put("("+x+"."+y+"."+z+")", new Chunk(x, y, z, "./saves/"+gameName, tr,helpbr,helptr));
	}
	public boolean hasChunk(long x,long y,long z){
		return chunks.containsKey("("+x+"."+y+"."+z+")");
	}
	public void add(long x,long y,long z,String saveName,long d){
		for(long i=-d;i<d+1;i++){
			for(long j=-d;j<d+1;j++){
				for(long k=-d;k<d+1;k++){
					if(Chunk.CheckChunk(x+i,y+j,z+k,saveName)){
						if(!this.hasChunk(x+i,y+j,z+k)){
							this.importChunk(x+i,y+j,z+k);
						}
					}else{
						Chunk.CreateChunk(x+i,y+j,z+k, saveName);
						this.importChunk(x+i,y+j,z+k);
					}
				}
			}
		}
	}
	public void save(){
		Enumeration<String> e=chunks.keys();
		while(e.hasMoreElements()){
			chunks.get(e.nextElement()).save();
		}
	}
	public Point3d RayTrace(Point3d p,double angleX,double angleY,double distance){
		Vector3d look =new Vector3d(0,0,1);
		Vector3d up=Math3d.rotateY(Math3d.rotateX(look,angleY-90),angleX);
		Vector3d right=Math3d.rotateY(look,angleX+90);
		look=Math3d.rotateY(Math3d.rotateX(look,angleY),angleX);
		look.normalize();
		up.normalize();
		right.normalize();
		Solid s=new Solid();
		Point3d[] Vertices={
				new Point3d(p.x-(right.x/200)-(up.x/200),p.y-(right.y/200)-(up.y/200),p.z-(right.z/200)-(up.z/200)),
				new Point3d(p.x+(right.x/200)-(up.x/200),p.y+(right.y/200)-(up.y/200),p.z+(right.z/200)-(up.z/200)),
				new Point3d(p.x+(up.x/200),p.y+(up.y/200),p.z+(up.z/200)),
				new Point3d(p.x-(right.x/200)-(up.x/200)+(look.x*distance),p.y-(right.y/200)-(up.y/200)+(look.y*distance),p.z-(right.z/200)-(up.z/200)+(look.z*distance)),
				new Point3d(p.x+(right.x/200)-(up.x/200)+(look.x*distance),p.y+(right.y/200)-(up.y/200)+(look.y*distance),p.z+(right.z/200)-(up.z/200)+(look.z*distance)),
				new Point3d(p.x+(up.x/200)+(look.x*distance),p.y+(up.y/200)+(look.y*distance),p.z+(up.z/200)+(look.z*distance))
		};
		int[] indices={
				2,1,0,
				3,4,5,
				0,4,3,
				0,1,4,
				2,5,4,
				2,4,1,
				5,2,0,
				5,0,3
		};
		s.setData(Vertices, indices, new Color3f(0,1,0));
		Enumeration<String> e=chunks.keys();
		Point3d closest=new Point3d(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);
		double cd=Double.MAX_VALUE;
		while(e.hasMoreElements()){
			Solid ch=chunks.get(e.nextElement()).getChunk();
			if(!ch.isEmpty()){
				BooleanModeller m=new BooleanModeller(ch, s);
				Point3d c=Math3d.getClosest(m.getIntersection(),p);
				if(Math3d.distance(c, p)<cd){
					cd=Math3d.distance(c, p);
					closest=c;
				}
			}
		}
		closest.x=((int)(closest.x*50))/50.0;
		return closest;
	}
	public boolean intersects(Solid s){
		Enumeration<String> e=chunks.keys();
		while(e.hasMoreElements()){
			if(chunks.get(e.nextElement()).intersects(s))return true;
		}
		return false;
	}
	public void dig(Solid s) {
		Enumeration<String> e=chunks.keys();
		while(e.hasMoreElements()){
			chunks.get(e.nextElement()).dig(s);
		}
	}
	public void place(Solid s) {
		Enumeration<String> e=chunks.keys();
		while(e.hasMoreElements()){
			chunks.get(e.nextElement()).place(s);
		}
	}
}
