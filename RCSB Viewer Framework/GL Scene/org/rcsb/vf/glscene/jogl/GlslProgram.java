/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.vf.glscene.jogl;

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
