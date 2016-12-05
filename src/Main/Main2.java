/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package Main;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opencl.CL10.*;
import static Main.InfoUtil.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class Main2 {

	private Main2() {
	}

	public static void main(String[] args) {
		try ( MemoryStack stack = stackPush() ) {
			demo(stack);
		}
	}

	private static void demo(MemoryStack stack) {
	    IntBuffer counts = stack.mallocInt(1);
	    checkCLError(clGetPlatformIDs(null, counts));
	    int platformCount = counts.get(0);
	    if (platformCount == 0)
	        throw new RuntimeException("No OpenCL platforms found.");
	    PointerBuffer platforms = stack.mallocPointer(platformCount);
	    checkCLError(clGetPlatformIDs(platforms, (IntBuffer) null));
	    PointerBuffer ctxProps = stack.mallocPointer(3);
	    ctxProps.put(0, CL_CONTEXT_PLATFORM).put(2, 0);
	    IntBuffer errcode_ret = stack.callocInt(1);
	    long platform = platforms.get(0);
	    ctxProps.put(1, platform);
	    checkCLError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, null, counts));
	    int deviceCount = counts.get(0);
	    if (deviceCount == 0)
	      throw new RuntimeException("No OpenCL devices found.");
	    PointerBuffer devices = stack.mallocPointer(deviceCount);
	    checkCLError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, devices, (IntBuffer) null));
	    long device = devices.get(0);
	    long context = clCreateContext(ctxProps, device, null, NULL, errcode_ret);
	    checkCLError(errcode_ret);
	    long que = clCreateCommandQueue(context, device, NULL, errcode_ret);
	    checkCLError(errcode_ret);
	    CharSequence add =
	    "kernel void sum(global const float* a, global float* result, int const size) {\n"+ // <- 'kernel' and 'global' !
	    "   const int itemId = get_global_id(0); \n"+
	    "   if(itemId < size) {\n"+
	    "       result[itemId] = a[itemId*2]*3 + a[itemId*2+1]*2 +8 +13 +7;\n"+
	    "   }\n"+
	    "}";
	    long sumProgram = CL10.clCreateProgramWithSource(context, add, null);
	    checkCLError(CL10.clBuildProgram(sumProgram, devices.get(0), "", null,0));
	    checkCLError(errcode_ret);
	    {
		    long time=System.currentTimeMillis();
		    long sumKernel = CL10.clCreateKernel(sumProgram, "sum", errcode_ret);
		    checkCLError(errcode_ret);
		    float[] in  = new float[200];
		    float[] out = new float[100];
		    for (int i = 0; i < 100; i++) {
		        in[i*2] = i+1;
		        in[i*2+1] =i+1;
		    }
		    FloatBuffer aBuff = stack.mallocFloat(200);
		    aBuff.put(in).rewind();
		    long _in = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, aBuff, errcode_ret); // <- READ_ONLY !
		    checkCLError(errcode_ret);
		    long _out = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE, 400, errcode_ret); // <- READ_WRITE !
		    checkCLError(errcode_ret);
		    checkCLError(CL10.clSetKernelArg1p(sumKernel, 0, _in));
		    checkCLError(CL10.clSetKernelArg1p(sumKernel, 1, _out));
		    checkCLError(CL10.clSetKernelArg1i(sumKernel, 2, 100)); // <- clSetKernelArg1i !
		    PointerBuffer globalWorkSize = stack.mallocPointer(1);
		    globalWorkSize.put(0, 100);
		    PointerBuffer kernelEvent = stack.mallocPointer(1);
		    checkCLError(clEnqueueNDRangeKernel(que, sumKernel, 1, null, globalWorkSize, null, null, kernelEvent));
		    PointerBuffer readEvent = stack.mallocPointer(1);
		    checkCLError(clEnqueueReadBuffer(que, _out, 1, 0, out, kernelEvent, readEvent)); // <- read back results !
		    checkCLError(clWaitForEvents(readEvent));
		    for (int i = 0; i < 100; i++) {
		        System.out.println(out[i]);
		    }
		    System.out.println(System.currentTimeMillis()-time);
		    CL10.clReleaseKernel(sumKernel);
		    CL10.clReleaseMemObject(_in);
		    CL10.clReleaseMemObject(_out);
		}
	}

}