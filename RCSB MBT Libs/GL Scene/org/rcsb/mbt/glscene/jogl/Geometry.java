package org.rcsb.mbt.glscene.jogl;



/**
 *  This class provides the base functionality for geometry factory objects.
 *  <P>
 *  @author	John L. Moreland
 *  @copyright	UCSD
 *  @see
 */
public abstract class Geometry
{
	/**
	 * Specifies a rendering style of points.
	 */
	public static final int FORM_POINTS = 0;

	/**
	 * Specifies a rendering style of wireframe lines.
	 */
	public static final int FORM_LINES = 1;

	/**
	 * Specifies a rendering style of simple flat polygons.
	 */
	public static final int FORM_FLAT = 2;

	/**
	 * Specifies a rendering style of thick shaded polygons.
	 */
	public static final int FORM_THICK = 3;

	/**
	 * Specifies the rendering style for shapes.
	 */
	private int form = Geometry.FORM_THICK;
	private static final int FORM_MIN = Geometry.FORM_POINTS;
	private static final int FORM_MAX = Geometry.FORM_THICK;

	/**
	 * Specifies a rendering quality for shapes.
	 */
	private float quality = 1.0f;


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public boolean equals( final Geometry geometry )
	{
		if ( geometry == null ) {
			return false;
		}
		if ( this.form != geometry.form ) {
			return false;
		}
		if ( this.quality != geometry.quality ) {
			return false;
		}
		return true;
	}

	/**
	 * Sets the rendering quality level (0.0 meaning lowest quality but faster
	 * rendering, and 1.0 meaning highest quality but slower rendering).
	 * Values specified outside this range will be clamped between 0.0 and 1.0.
	 * <P>
	 * @param	quality	The rendering quality level.
	 */
	public void setQuality( final float quality_ )
	{
		float quality = quality_;
		if ( quality < 0.0f ) {
			quality = 0.0f;
		}
		if ( quality > 1.0f ) {
			quality = 1.0f;
		}

		this.quality = quality;
	}


	/**
	 * Gets the rendering quality level (0.0 meaning lowest quality but faster
	 * rendering, and 1.0 meaning highest quality but slower rendering).
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public final float getQuality( )
	{
		return this.quality;
	}


	/**
	 * Sets the basic geometric representation used to render the primitive.
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public void setForm( final int form )
	{
		if ( (form < Geometry.FORM_MIN) || (form > Geometry.FORM_MAX) ) {
			throw new IllegalArgumentException( "Geometry.setForm: form must be >= " + Geometry.FORM_MIN + " and <= " + Geometry.FORM_MAX + "."  );
		}

		this.form = form;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public final int getForm( )
	{
		return this.form;
	}
}

