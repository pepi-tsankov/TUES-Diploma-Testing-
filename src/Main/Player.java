package Main;

import java.util.Timer;
import java.util.TimerTask;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.universe.SimpleUniverse;

import J3DBool.Solid;
import maths.Math3d;

public class Player{
	private double maxSpeed=3;
	private Object flock=new Object();
	private double ff;
	private Object ulock=new Object();
	private double uf;
	private Object rlock=new Object();
	private double rf;
	private Vector3d up;
	private Vector3d front;
	private Point3d player;
	private double anglex=0;
	private double angley=0;
	private Transform3D view;
	private SimpleUniverse universe=null;
	private Timer force;
	private Chunks chunks;
	private TransformGroup guit;
	private BranchGroup guib;
	private Player(){
		ff=0;
		uf=0;
		rf=0;
		up= new Vector3d(0,1,0);
		front=new Vector3d(0,0,1);
		player=new Point3d(0,0,0);
	}
	public Player(SimpleUniverse u,Chunks c){
		guib=new BranchGroup();
		guit=new TransformGroup();
		guib.setCapability(BranchGroup.ALLOW_DETACH);
		guib.addChild(guit);
		Solid s=Math3d.getSphereAt(new Point3d(0,0,-0.02), 0.0002, new Color3f(1,1,1));
		guit.addChild(s);
		chunks=c;
		ff=0;
		uf=0;
		rf=0;
		force=new Timer();
		Player p=this;
		universe=u;
		up= new Vector3d(0,1,0);
		front=new Vector3d(0,0,1);
		this.player=new Point3d(0,0,0);
		guit.setTransform(getView());
		u.addBranchGraph(guib);
		force.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				synchronized(flock){
					p.fd(maxSpeed*ff/30);
					if(chunks.intersects(Math3d.getPlayerColisonAt(player))){
						boolean moved=false;
						for(int i=0;i<3;i++){
							p.up(0.1*i);
							if(chunks.intersects(Math3d.getPlayerColisonAt(player))){
								p.up(-(0.1*i));
							}else{
								moved=true;
								break;
							}
						}
						if(!moved){
							p.fd(-maxSpeed*ff/30);
							ff=0;
						}
					}
				}
				synchronized(rlock){
					p.notLeft(maxSpeed*rf/30);
					if(chunks.intersects(Math3d.getPlayerColisonAt(player))){
						boolean moved=false;
						p.up(0.1);
						if(chunks.intersects(Math3d.getPlayerColisonAt(player))){
							p.up(-(0.1));
						}else{
							moved=true;
						}
						if(!moved){
							p.notLeft(-(maxSpeed*rf/30));
							rf=0;
						}
					}
				}
				synchronized(p){
					synchronized(ulock){
						uf-=(9.8/60);
						p.up(uf/30);
						if(chunks.intersects(Math3d.getPlayerColisonAt(player))){
							p.up(-uf/30);
							uf=0;
						}
					}
				}
			setView();
			}
			
		}, 0, 1000/30);
	}
	public Transform3D getView(){
		view = new Transform3D();
		Point3d eye=new Point3d();
		Vector3d eyev=Math3d.rotateY(Math3d.rotateX(front,angley),anglex);
		eyev.normalize();
		eye.x=player.x+(eyev.x/100);
		eye.y=player.y+(eyev.y/100);
		eye.z=player.z+(eyev.z/100);
		view.lookAt(player,eye,Math3d.rotateY(Math3d.rotateX(up,angley),anglex));
		view.invert();
		return view;
	}
	public void setView(){
		synchronized(this){
			guib.detach();
			guit.setTransform(this.getView());
			universe.addBranchGraph(guib);
			universe.getViewingPlatform().getViewPlatformTransform().setTransform(this.getView());
		}
	}
	public Vector3d getViewVecor(){
		Vector3d eyev=Math3d.rotateY(Math3d.rotateX(front,angley),anglex);
		return eyev;
	}
	private void fd(double speed){
		Vector3d eyev=Math3d.rotateY(front,anglex);
		eyev.normalize();
		player.x+=eyev.x*speed;
		player.y+=eyev.y*speed;
		player.z+=eyev.z*speed;
	}
	private void notLeft(double speed){
		Vector3d eyev=Math3d.rotateY(front,anglex-90);
		eyev.normalize();
		player.x+=eyev.x*speed;
		player.y+=eyev.y*speed;
		player.z+=eyev.z*speed;
	}
	private void up(double speed){
		player.y+=speed;
	}
	public Point3d getPosition(){
		return player;
	}
	public /*synchronized*/ void rotatex(double angle){
		anglex+=angle;
		while(anglex>360){
			anglex-=360;
		}
		while(anglex<0){
			anglex+=360;
		}
		//setView();
	}
	public /*synchronized*/ void rotatey(double angle){
		angley+=angle;
		if(angley>90){
			angley=90;
		}
		if(angley<-90){
			angley=-90;
		}
		//setView();
	}
	public void move(Point3d pos){
		player=pos;
		setView();
	}
	public double getX(){
		return player.x;
	}
	public double getY(){
		return player.y;
	}
	public double getZ(){
		return player.z;
	}
	public Player copy() {
		Player p=new Player();
		p.up=(Vector3d) up.clone();
		p.front=(Vector3d) front.clone();
		p.player=(Point3d) player.clone();
		p.anglex=anglex;
		p.angley=angley;
		p.view=view;
		p.universe=universe;
		return p;
	}
	public void forward(double force){
		synchronized(flock){
			ff=force;
		}
	}
	public void upwards(double force){
		synchronized(ulock){
			uf=force;
		}
	}
	public void right(double force){
		synchronized(rlock){
			rf=force;
		}
	}
	public double getXa(){
		return anglex;
	}
	public double getYa(){
		return angley;
	}
}
