/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package Main;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.CountDownLatch;

import static org.lwjgl.opencl.CL10.*;
import static Main.InfoUtil.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class CLDemo {

	private CLDemo() {
	}

	public static void main(String[] args) {
		try ( MemoryStack stack = stackPush() ) {
			demo(stack);
		}
	}

	private static void demo(MemoryStack stack) {
		IntBuffer pi = stack.mallocInt(1);
		checkCLError(clGetPlatformIDs(null, pi));
		if ( pi.get(0) == 0 )
			throw new RuntimeException("No OpenCL platforms found.");
		PointerBuffer platforms = stack.mallocPointer(pi.get(0));
		checkCLError(clGetPlatformIDs(platforms, (IntBuffer)null));

		PointerBuffer ctxProps = stack.mallocPointer(3);
		ctxProps
			.put(0, CL_CONTEXT_PLATFORM)
			.put(2, 0);

		IntBuffer errcode_ret = stack.callocInt(1);
			long platform = platforms.get(0);
			ctxProps.put(1, platform);

			CLCapabilities platformCaps = CL.createPlatformCapabilities(platform);

			checkCLError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, null, pi));

			PointerBuffer devices = stack.mallocPointer(pi.get(0));
			checkCLError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, devices, (IntBuffer)null));
				long device = devices.get(0);
				CLCapabilities caps = CL.createDeviceCapabilities(device, platformCaps);
				CLContextCallback contextCB;
				long context = clCreateContext(ctxProps, device, contextCB = CLContextCallback.create((errinfo, private_info, cb, user_data) -> {
					System.err.println("[LWJGL] cl_context_callback");
					System.err.println("\tInfo: " + memUTF8(errinfo));
				}), NULL, errcode_ret);
				checkCLError(errcode_ret);
				long que=clCreateCommandQueue(context, device, NULL, errcode_ret);
				CharSequence add=
				"_kernel void sum(_global const float* a, _global float* result, int const size) {\n"+
				"	const int itemId = get_global_id(0); \n"+
				"	if(itemId < size) {\n"+
				"		result[itemId] = a[itemId*2] + a[itemId*2+1];\n"+
				"	}\n"+
				"}";
				long sumProgram=CL10.clCreateProgramWithSource(context, add, null);
				CLProgramCallback buildCallback;
				CountDownLatch latch = new CountDownLatch(1);
				int errcode = clBuildProgram(sumProgram, device, "", buildCallback = CLProgramCallback.create((program, user_data) -> {
					log(String.format(
						"The cl_program [0x%X] was built %s",
						program,
						getProgramBuildInfoInt(program, device, CL_PROGRAM_BUILD_STATUS) == CL_SUCCESS ? "successfully" : "unsuccessfully"
					));
					String log = getProgramBuildInfoStringASCII(program, device, CL_PROGRAM_BUILD_LOG);
					if ( !log.isEmpty() )
						log(String.format("BUILD LOG:\n----\n%s\n-----", log));

					latch.countDown();
				}), NULL);
				checkCLError(errcode);
				long sumKernel=CL10.clCreateKernel(sumProgram, "sum", (int[])null);
				float[] in=new float[200];
				float[] out=new float[100];
				for(int i=0;i<100;i++){
					in[i]=i;
					in[i+1]=i;
				}
				FloatBuffer aBuff = BufferUtils.createFloatBuffer(200);
				aBuff.put(in);
				aBuff.rewind();
				IntBuffer errorBuff = BufferUtils.createIntBuffer(1); // Error buffer

				long _in = CL10.clCreateBuffer(context, CL10.CL_MEM_WRITE_ONLY | CL10.CL_MEM_COPY_HOST_PTR, aBuff, errorBuff);
				checkCLError(errorBuff.get(0));
				long _out = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY, 400, errorBuff);
				checkCLError(errorBuff.get(0));
				CL10.clSetKernelArg1p(sumKernel,0,_in);
				CL10.clSetKernelArg1p(sumKernel,1,_out);
				CL10.clSetKernelArg1p(sumKernel, 2, 100);
				PointerBuffer globalWorkSize = BufferUtils.createPointerBuffer(1);
				globalWorkSize.put(0, 100);
				clEnqueueNDRangeKernel(que, sumKernel, 1, null, globalWorkSize, null, null, null);
				CL10.clFinish(que);
				for(int i=0;i<100;i++){
					System.out.println(out[i]);
				}
				
	}

	private static void log(String s) {
		System.out.println(s);
		
	}


}