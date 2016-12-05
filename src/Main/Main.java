package Main;

//import org.lwjgl.opencl.InfoUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.*;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.FunctionProviderLocal;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL11.*;
import static org.lwjgl.opencl.KHRICD.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.Pointer.*;

public class Main{
	@SuppressWarnings("unused")
	public static void main(String[] args){
		Game g=new Game("sample");
	}
}