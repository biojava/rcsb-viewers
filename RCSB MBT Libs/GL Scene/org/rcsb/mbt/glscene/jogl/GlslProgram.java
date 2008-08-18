package org.rcsb.mbt.glscene.jogl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.util.Stack;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;

public class GlslProgram {
	public static boolean supportsShaderPrograms = false;
	
	private static final Stack stack = new Stack();
	
	public int vertexShader = 0;

	public int fragmentShader = 0;

	public int shaderProgram = 0;
	
	public boolean programError = false;
	
	public boolean isDestroyed = true;
	
	public String vertexFilename;
	public String fragmentFilename;
	
	public GlslProgram(String vertexFile, String fragmentFile) {
		this.vertexFilename = vertexFile;
		this.fragmentFilename = fragmentFile;
	}
	
	public static void destroyAllStackedPrograms(GL gl) {
		while(stack.size() > 0) {
			GlslProgram prog = (GlslProgram)stack.pop();
			prog.destroyProgram(gl);
		}
	}
	
	public void loadProgram(GL gl) {
		if(!supportsShaderPrograms) {
			return;
		}
		
		this.isDestroyed = false;
		
		boolean successful = true;
		if (this.vertexShader == 0) {
			successful = this.createVertexShader(gl, vertexFilename);
		}

		if (this.fragmentShader == 0 && successful) {
			successful = this.createFragmentShader(gl, fragmentFilename);
		}

		if (this.shaderProgram == 0 && successful) {
			successful = this.createShaderProgram(gl);
		}

		// if one or more steps for creating the shader failed,
		// disable shaders.
		if (!successful) {
			this.programError = true;
			this.destroyProgram(gl);
		}
	}
	
	public void destroyProgram(GL gl) {
		if(!supportsShaderPrograms) {
			return;
		}
		
		this.isDestroyed = true;
		this.programError = false;
		
		if(this.fragmentShader != 0) {
			gl.glDeleteShader(this.fragmentShader);
			this.fragmentShader = 0;
		}
		if(this.vertexShader != 0) {
			gl.glDeleteShader(this.vertexShader);
			this.vertexShader = 0;
		}
		if(this.shaderProgram != 0) {
			gl.glDeleteProgram(this.shaderProgram);
			this.shaderProgram = 0;
		}
	}
	
	/**
	 * Causes this program to be loaded and pushed to the top of a static Stack
	 */
	public void pushProgram(GL gl) {
		if(!supportsShaderPrograms || (!stack.empty() && stack.peek() == this)) {
			return;
		}
		
		if(!this.programError) {
			gl.glUseProgram(this.shaderProgram);
		}
		stack.push(this);
	}
	
	public void popProgram(GL gl) {
		if(stack.empty() || stack.peek() != this) {
			return;
		}
		
		stack.pop();
			
		if(!supportsShaderPrograms) {
			return;
		}
		
		if(stack.size() > 0) {
			GlslProgram prog = (GlslProgram)stack.lastElement();
			if(!prog.programError) {
				gl.glUseProgram(prog.shaderProgram);
			}
		} else {
			gl.glUseProgram(0);
		}
	}
	
	private boolean createVertexShader(final GL gl, String fileName) {
		final String[] lines = this.getReaderLines(fileName);
		// final String[] lines = this.getReaderLines("test_v.glsl");
		final int[] lengths = this.createLengthArray(lines);

		this.vertexShader = gl.glCreateShader(GL.GL_VERTEX_SHADER);
		gl.glShaderSource(this.vertexShader, lines.length, lines, lengths, 0);
		gl.glCompileShader(this.vertexShader);

		gl.glFlush();
		final IntBuffer buf = BufferUtil.newIntBuffer(1);
		gl.glGetShaderiv(this.vertexShader, GL.GL_INFO_LOG_LENGTH, buf);
		int logLength = buf.get(0);
		buf.rewind();
		gl.glGetShaderiv(this.vertexShader, GL.GL_COMPILE_STATUS, buf);
		final int status = buf.get(0);
		System.err.println("Vertex shader creation...");
		System.err.println("\tstatus: " + (status == GL.GL_TRUE));
		System.err.println("\tlog length: " + logLength + "\n");
		System.err.println("Log:");

		logLength += 10;

		final int[] length = new int[1];
		final byte[] bufArray = new byte[logLength];
		gl.glGetShaderInfoLog(this.vertexShader, logLength, length, 0, bufArray, 0);

		final StringBuffer s = new StringBuffer();
		for (int i = 0; i < length[0]; i++) {
			s.append((char) bufArray[i]);
		}
		final String log = s.toString();
		System.err.println(log + "\n-------------------\n");

		// if the shader was not created, or if it would be run in software,
		// bail.
		if (status == GL.GL_FALSE) {
			return false;
		}
		if (log.indexOf("software") >= 0) {
			if (this.vertexShader > 0) {
				gl.glDeleteShader(this.vertexShader);
			}
			return false;
		}

		return true;
	}

	private boolean createFragmentShader(final GL gl, String fileName) {
		final String[] lines = this.getReaderLines(fileName);
		// final String[] lines = this.getReaderLines("test_f.glsl");
		final int[] lengths = this.createLengthArray(lines);

		this.fragmentShader = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
		gl.glShaderSource(this.fragmentShader, lines.length, lines, lengths, 0);
		gl.glCompileShader(this.fragmentShader);

		gl.glFlush();
		final IntBuffer buf = BufferUtil.newIntBuffer(1);
		gl.glGetShaderiv(this.fragmentShader, GL.GL_INFO_LOG_LENGTH, buf);
		int logLength = buf.get(0);
		buf.rewind();
		gl.glGetShaderiv(this.fragmentShader, GL.GL_COMPILE_STATUS, buf);
		final int status = buf.get(0);
		System.err.println("Fragment shader creation...");
		System.err.println("\tstatus: " + (status == GL.GL_TRUE));
		System.err.println("\tlog length: " + logLength + "\n");
		System.err.println("Log:");

		logLength += 10;

		final int[] length = new int[1];
		final byte[] bufArray = new byte[logLength];
		gl
				.glGetShaderInfoLog(this.fragmentShader, logLength, length, 0,
						bufArray, 0);

		final StringBuffer s = new StringBuffer();
		for (int i = 0; i < length[0]; i++) {
			s.append((char) bufArray[i]);
		}
		final String log = s.toString();
		System.err.println(log + "\n-------------------\n");

		// if the shader was not created, or if it would be run in software,
		// bail.
		if (status == GL.GL_FALSE) {
			return false;
		}
		if (log.indexOf("software") >= 0) {
			if (this.fragmentShader > 0) {
				gl.glDeleteShader(this.fragmentShader);
			}
			return false;
		}

		return true;
	}

	private boolean createShaderProgram(final GL gl) {
		this.shaderProgram = gl.glCreateProgram();
		gl.glAttachShader(this.shaderProgram, this.vertexShader);
		gl.glAttachShader(this.shaderProgram, this.fragmentShader);
		gl.glLinkProgram(this.shaderProgram);
//		gl.glUseProgram(this.shaderProgram);

		gl.glFlush();
		final IntBuffer buf = BufferUtil.newIntBuffer(1);
		gl.glGetProgramiv(this.shaderProgram, GL.GL_INFO_LOG_LENGTH, buf);
		int logLength = buf.get(0);
		buf.rewind();
		gl.glGetProgramiv(this.shaderProgram, GL.GL_LINK_STATUS, buf);
		final int status = buf.get(0);
		System.err.println("Shader program creation...");
		System.err.println("\tstatus: " + (status == GL.GL_TRUE));
		System.err.println("\tlog length: " + logLength + "\n");
		System.err.println("Log:");

		logLength += 10;

		final int[] length = new int[1];
		final byte[] bufArray = new byte[logLength];
		gl
				.glGetProgramInfoLog(this.shaderProgram, logLength, length, 0,
						bufArray, 0);

		final StringBuffer s = new StringBuffer();
		for (int i = 0; i < length[0]; i++) {
			s.append((char) bufArray[i]);
		}
		final String log = s.toString();
		System.err.println(log + "\n-------------------\n");

		// if the shader was not created, or if it would be run in software,
		// bail.
		if (status == GL.GL_FALSE) {
			return false;
		}
		if (log.indexOf("software") >= 0) {
			return false;
		}

		return true;
	}

	private String[] getReaderLines(final String filename) {
		final String[] lines = { "" };

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(super.getClass()
					.getResource(filename).openStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines[0] += line + "\n";
			}
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException useless) {
				}
			}
		}

		return lines;
	}

	private int[] createLengthArray(final String[] lines) {
		final int[] lengths = new int[lines.length];

		for (int i = 0; i < lines.length; i++) {
			lengths[i] = lines[i].length();
		}

		return lengths;
	}
}
