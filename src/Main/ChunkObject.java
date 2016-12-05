package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import J3DBool.BooleanModeller;
import J3DBool.Solid;

public class ChunkObject {
	private Solid s;
	private Shape3D obj;
	private BranchGroup mebr;
	private TransformGroup metr;
	private TransformGroup tr;
	private String location;
	private Chunk c;
	private Appearance ap;
	public ChunkObject(String str,TransformGroup tr,Chunk c,TransformGroup metr,BranchGroup mebr){
		this.metr=metr;
		this.mebr=mebr;
		location=str;
		s=new Solid(new File(str+"/obj.solid"),new Color3f(0,1,0));
		obj=new Shape3D(s.getGeometry());
		this.tr=tr;
		Material m=new Material();
		ap=new Appearance();
		//TextureLoader loader = new TextureLoader("./textures/texture.jpg", new Container());
	    //Texture texture = loader.getTexture();
	    //texture.setBoundaryModeS(Texture.WRAP);
	    //texture.setBoundaryModeT(Texture.CLAMP);
	    //texture.setBoundaryColor( new Color4f( 0.0f, 1.0f, 1.0f, 1.0f ) );
	    //TextureAttributes tatr=new TextureAttributes();
	    //tatr.setTextureMode(TextureAttributes.DECAL);
	    //ap.setTextureAttributes(tatr);
		//ap.setTexture(texture);
		//ap.setTexCoordGeneration(new TexCoordGeneration(TexCoordGeneration.OBJECT_LINEAR, TexCoordGeneration.TEXTURE_COORDINATE_3));
		m.setShininess(100000);
		m.setAmbientColor(0,1,0);
		m.setDiffuseColor(0,1,0);
		m.setLightingEnable(true);
		ap.setMaterial(m);
		obj.setAppearance(ap);
		if(mebr.isLive())mebr.detach();
		metr.addChild(obj);
		tr.addChild(mebr);
		this.c=c;
		c.add(s);
		
	}
	public void remove(Solid r){
		/*if(!s.isEmpty()){
		BooleanModeller m=new BooleanModeller(s,r);
		s=m.getDifference();}*/
		tr.removeChild(mebr);
		metr.removeChild(obj);
		obj=new Shape3D(s.getGeometry());
		obj.setAppearance(ap);
		metr.addChild(obj);
		tr.addChild(mebr);
		c.remove(r);
	}
	public void add(Solid r){
		/*if(!s.isEmpty()){
		BooleanModeller m=new BooleanModeller(s,r);
		s=m.getUnion();}*/
		tr.removeChild(mebr);
		metr.removeChild(obj);
		obj=new Shape3D(s.getGeometry());
		obj.setAppearance(ap);
		metr.addChild(obj);
		tr.addChild(mebr);
		c.add(s);
	}
	public void save(){
		PrintWriter writer = null;
		if(s.isEmpty()){new File(location+"/obj.solid").delete();new File(location).delete();}
		try {
			writer = new PrintWriter(location+"/obj.solid", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		NumberFormat f = new DecimalFormat("0.00000000000000E0000");
		writer.println(""+s.getVertices().length);
		for(int i=0;i<s.getVertices().length;i++){
			writer.println(""+i+" "+f.format(s.getVertices()[i].x)+" "+f.format(s.getVertices()[i].y)+" "+f.format(s.getVertices()[i].z));
		}
		writer.println("");
		writer.println(""+s.getIndices().length/3);
		for(int i=0;i<s.getIndices().length;i+=3){
			writer.println(""+i/3+" "+s.getIndices()[i]+" "+s.getIndices()[i+1]+" "+s.getIndices()[i+2]);
		}
		writer.close();
	}
	public boolean intersects(Solid s){
		return (!s.isEmpty())&&(!this.s.isEmpty())&&(!( new BooleanModeller(s,this.s).getIntersection().isEmpty()));
	}
	public Solid dig(Solid s){
		long t=System.currentTimeMillis();
		BooleanModeller b=new BooleanModeller(this.s,s);
		this.s=b.getDifference();
		System.err.println(System.currentTimeMillis()-t);
		remove(s);
		return b.getIntersection();
	}
	public Solid place(Solid s){
		BooleanModeller b=new BooleanModeller(this.s,s);
		this.s=b.getUnion();
		add(s);
		return b.getIntersection();
	}
}
