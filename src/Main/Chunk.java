package Main;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;

import J3DBool.BooleanModeller;
import J3DBool.Solid;

public class Chunk {
	private TransformGroup helptr;
	private BranchGroup helpbr;
	ArrayList<ChunkObject> objs;
	private long x;
	private long y;
	private long z;
	private String location;
	private TransformGroup tr;
	private Solid s;
	public Chunk(long x,long y,long z,String location,TransformGroup tr,BranchGroup helpbr,TransformGroup helptr){
		this.helpbr=helpbr;
		this.helptr=helptr;
		this.x=x;
		this.y=y;
		this.z=z;
		objs=new ArrayList<ChunkObject>();
		this.location=location;
		this.tr=tr;
		s=new Solid();
		addObjects();
	}
	public Chunk(long x,long y,long z){
	//Fake Chunk Object used in the Chunks object's hasChunk
		this.x=x;
		this.y=y;
		this.z=z;
	}
	@Override
	public boolean equals(Object o){
	  if(o instanceof Chunk){
		Chunk c = (Chunk) o;
	    return ((this.x==c.x)&&(this.y==c.y)&&(this.z==c.z));
	  }
	  return false;
	}
	@Override
	public int hashCode(){
		return 1;
	}
	public void addObjects(){
		File dir = new File(location+"/"+x+"/"+y+"/"+z+"/");
		String[] objects = dir.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
		for(int i=0;i<objects.length;i++){
			objs.add(new ChunkObject(location+"/"+x+"/"+y+"/"+z+"/"+objects[i],tr,this,helptr,helpbr));
		}
	}
	public static boolean CheckChunk(long x,long y,long z,String saveName){
		return new File("./saves/"+saveName+"/"+x+"/"+y+"/"+z+"/").exists();
	}
	public static void CreateChunk(long x,long y,long z,String saveName){
		new File("./saves/"+saveName+"/"+x+"/"+y+"/"+z+"/").mkdirs();
	}
	public void save(){
		for(int i=0;i<objs.size();i++){
			objs.get(i).save();
		}
	}
	public boolean intersects(Solid s){
		return (!s.isEmpty())&&(!this.s.isEmpty())&&(! new BooleanModeller(s,this.s).getIntersection().isEmpty());
	}
	public void remove(Solid s){
		if(!s.isEmpty()&&!this.s.isEmpty()){
			this.s=new BooleanModeller(this.s,s).getDifference();
		}
	}
	public void add(Solid s){
		if(this.s.isEmpty()){
			this.s=new Solid(s.getVertices(),s.getIndices(),s.getColors());
		}else if(!s.isEmpty()){
			this.s=new BooleanModeller(this.s,s).getUnion();
		}
	}
	public void dig(Solid s) {
		this.remove(s);
		for(int i=0;i<objs.size();i++){
			if(objs.get(i).intersects(s)) objs.get(i).dig(s);
		}
	}
	public void place(Solid s) {
		this.remove(s);
		for(int i=0;i<objs.size();i++){
			if(objs.get(i).intersects(s)) objs.get(i).place(s);
		}
	}
	public Solid getChunk(){
		return s;
	}
}
