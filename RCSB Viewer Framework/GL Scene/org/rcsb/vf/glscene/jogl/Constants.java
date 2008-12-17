package org.rcsb.vf.glscene.jogl;

public class Constants {

	// atom/bond colors
	public static final float black[] = { 0.0f, 0.0f, 0.0f, 1.0f };
	public static final float white[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	public static final float transparentWhite[] = { 1.0f, 1.0f, 1.0f, 0.5f };
	public static final float yellow[] = { 1.0f, 1.0f, 0.0f, 1.0f };
	public static final float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	public static final float[] atomHighShininess = { 100.0f };

	// chain colors
	public static final Color3f chainSpecularColor = new Color3f(0.7f, 0.7f, 0.7f);
//	public static final Color3f ambientColor = new Color3f(0.9f, 0.9f, 0.9f);
	public static final Color3f chainEmissiveColor = new Color3f(0.05f, 0.05f, 0.05f);
	public static final float[] chainHighShininess = { 128f };
	public static final float[] chainNoShininess = { 0 };
	
	public static float[] colorTemp = {0,0,0,0};
}
