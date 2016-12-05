package Main;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

import J3DBool.Solid;
import maths.Math3d;

public class Game extends JFrame implements MouseMotionListener,KeyListener,MouseListener{
	public static double digWidth=0.3;
	private static final long serialVersionUID = -3427361626363632819L;
	private Player mainp;
	private Robot r;
	private SimpleUniverse u;
	private JFrame frame;
	private TransformGroup chunkView;
	private Canvas3D canvas=new Canvas3D(SimpleUniverse.getPreferredConfiguration());
	private BranchGroup universe;
	private static BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	private static Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
	private Chunks chunks;
	private String saveName;
	private void setup(){
		frame=this;
		super.addWindowListener(new WindowAdapter() {
	          public void windowClosing(WindowEvent winEvent) {
	        	  //chunks.save();
	        	  frame.getContentPane().setCursor(Cursor.getDefaultCursor());
	        	  System.exit(0);
	          }	
	    });
		try {
			r=new Robot();
		} catch (AWTException e) {}
		super.addMouseMotionListener(this);
		canvas.addMouseMotionListener(this);
		this.addMouseMotionListener(this);
		super.addKeyListener(this);
		canvas.addKeyListener(this);
		this.addKeyListener(this);
		super.addMouseListener(this);
		canvas.addMouseListener(this);
		this.addMouseListener(this);
		canvas.setSize(854, 480);
		super.getContentPane().add(canvas);
		u=new SimpleUniverse(canvas);
		super.pack();
		super.setVisible(true);
		u.getViewingPlatform().setNominalViewingTransform();
		frame.getContentPane().setCursor(blankCursor);
		universe=new BranchGroup();
		chunkView=new TransformGroup();
		chunkView.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		chunkView.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		chunkView.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		chunkView.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
		chunkView.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		Color3f light1Color = new Color3f(1f, 1f, 1f);
	    BoundingSphere bounds =new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
	    Vector3f light1Direction = new Vector3f(12, -112.0f, 15);
	    DirectionalLight light1= new DirectionalLight(light1Color, light1Direction);
	    light1.setInfluencingBounds(bounds);
	    chunkView.addChild(light1);
		universe.addChild(chunkView);
		universe.setCapability(BranchGroup.ALLOW_DETACH);
		u.addBranchGraph(universe);
		chunks=new Chunks(saveName,chunkView);
		mainp=new Player(u,chunks);
		mainp.move(new Point3d(7,10,0));
		mainp.rotatey(90);
		chunks.add((long)mainp.getX()/1000,(long)mainp.getY()/1000,(long)mainp.getZ()/1000, saveName,1);
		mainp.setView();
		u.getViewer().getView().setFrontClipDistance(0.001);
		frame.pack();
	}
	Game(String s){
		super("Island Domination");
		saveName=s;
		setup();
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		this.mousePressed(e);
		this.mouseMoved(e);
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		frame.getContentPane().setCursor(blankCursor);
		mainp.rotatex(-(e.getXOnScreen() - (this.getLocationOnScreen().x+super.getWidth()/2))*0.05);
		mainp.rotatey((e.getYOnScreen() - (this.getLocationOnScreen().y+super.getHeight()/2))*0.03);
		r.mouseMove(this.getLocationOnScreen().x + super.getWidth()/2, this.getLocationOnScreen().y + super.getHeight()/2);
	}
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyChar()=='d'){
			mainp.right(0.2);
		}
		if(e.getKeyChar()=='a'){
			mainp.right(-0.2);
		}
		if(e.getKeyChar()=='w'){
			mainp.forward(0.2);
		}
		if(e.getKeyChar()=='s'){
			mainp.forward(-0.2);
		}
		if(e.getKeyChar()=='D'){
			mainp.right(0.1);
		}
		if(e.getKeyChar()=='A'){
			mainp.right(-0.1);
		}
		if(e.getKeyChar()=='W'){
			mainp.forward(0.1);
		}
		if(e.getKeyChar()=='S'){
			mainp.forward(-0.1);
		}
		if(e.getKeyChar()=='h'){
			Solid s=Math3d.getSphereAt(Math3d.getLoadCenter(chunks.getChunk(0, 0, 0).getChunk()), 2, new Color3f(1,0,0));
			BranchGroup bg=new BranchGroup();
			TransformGroup tg=new TransformGroup();
			tg.addChild(s);
			bg.addChild(tg);
			u.addBranchGraph(bg);
			universe.detach();
		}
		if(e.getKeyChar()==' '){
			if(chunks.intersects(Math3d.getPlayerColisonAt(new Point3d(mainp.getX(),mainp.getY()-0.05,mainp.getZ())))){
				mainp.upwards(3);
			}
		}
		if(e.getKeyChar()=='z'||e.getKeyChar()=='Z'){
			//mainp.upwards(-0.1);
		}
		/*if(e.getKeyChar()=='h'||e.getKeyChar()=='H'){
			System.out.print(""+mainp.getPosition());
		}
		if(e.getKeyChar()=='m'||e.getKeyChar()=='M'){
			digWidth*=1/0.9;
		}
		if(e.getKeyChar()=='l'||e.getKeyChar()=='L'){
			digWidth*=0.9;
		}*/
		
		chunks.add((long)mainp.getX()/1000,(long)mainp.getY()/1000,(long)mainp.getZ()/1000, saveName,1);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyChar()=='d'||e.getKeyChar()=='D'){
			mainp.right(0);
		}
		if(e.getKeyChar()=='a'||e.getKeyChar()=='A'){
			mainp.right(0);
		}
		if(e.getKeyChar()=='w'||e.getKeyChar()=='W'){
			mainp.forward(0);
		}
		if(e.getKeyChar()=='s'||e.getKeyChar()=='S'){
			mainp.forward(0);
		}
		/*
		if(e.getKeyChar()=='q'||e.getKeyChar()=='Q'){
			mainp.upwards(0);
		}
		if(e.getKeyChar()=='z'||e.getKeyChar()=='Z'){
			mainp.upwards(0);
		}*/
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1){
			ChunkObjectThread c=new ChunkObjectThread("",chunks,mainp,ChunkObjectThread.DIG);
			c.start();
		}
		if(e.getButton()==MouseEvent.BUTTON3){
			ChunkObjectThread c=new ChunkObjectThread("",chunks,mainp,ChunkObjectThread.PLACE);
			c.start();
		}
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		frame.getContentPane().setCursor(blankCursor);
		mainp.rotatex(-(e.getXOnScreen() - (this.getLocationOnScreen().x+super.getWidth()/2))*0.05);
		mainp.rotatey((e.getYOnScreen() - (this.getLocationOnScreen().y+super.getHeight()/2))*0.03);
		r.mouseMove(this.getLocationOnScreen().x + super.getWidth()/2, this.getLocationOnScreen().y + super.getHeight()/2);
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
