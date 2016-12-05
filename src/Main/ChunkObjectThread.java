package Main;

import javax.vecmath.Color3f;

import J3DBool.BooleanModeller;
import maths.Math3d;

class ChunkObjectThread implements Runnable {
   public static final int DIG = 0;
   public static final int PLACE = 1;
   private Thread t;
   private String threadName;
   Chunks chunks;
   Player mainp;
   int function;
   
   ChunkObjectThread( String name, Chunks chunks, Player player, int func){
       threadName = name;
       this.chunks=chunks;
       this.mainp=player.copy();
       function=func;
   }
   private void dig(){
	   chunks.dig(Math3d.getSphereAt(chunks.RayTrace(mainp.getPosition(), mainp.getXa(),mainp.getYa(),5),Game.digWidth,new Color3f(0,0,1)));
   }
   private void place(){
	   if(new BooleanModeller(Math3d.getSphereAt(chunks.RayTrace(mainp.getPosition(), mainp.getXa(),mainp.getYa(),5),Game.digWidth,new Color3f(0,0,1)), Math3d.getPlayerColisonAt(mainp.getPosition())).getIntersection().isEmpty()){
		   chunks.place(Math3d.getSphereAt(chunks.RayTrace(mainp.getPosition(), mainp.getXa(),mainp.getYa(),5),Game.digWidth,new Color3f(0,0,1)));
	   }
   }
   public void run() {
	   if(function==DIG){
		   this.dig();
	   }
	   if(function==PLACE){
		   this.place();
	   }
   }
   
   public void start ()
   {
      if (t == null)
      {
         t = new Thread (this, threadName);
         t.start ();
      }
   }

}