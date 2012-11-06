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
package org.rcsb.vf.glscene.jogl.tiles;


import java.awt.Dimension;
import java.nio.Buffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.model.util.DebugState;

/**
 * A fairly direct port of Brian Paul's tile rendering library, found
 * at http://www.mesa3d.org/brianp/TR.html. I've java-fied it, but the
 * functionality is the same.
 * 
 * @author ryanm
 */
public class TileRenderer
{
	private static final int DEFAULT_TILE_WIDTH = 256;

	private static final int DEFAULT_TILE_HEIGHT = 256;

	private static final int DEFAULT_TILE_BORDER = 0;

	/**
	 * Enumeration flags for accessing variables
	 * 
	 * @author ryanm
	 */
//	public static enum TREnum
//	{
//		/**
//		 * The width of a tile
//		 */
//		TR_TILE_WIDTH,
//		/**
//		 * The height of a tile
//		 */
//		TR_TILE_HEIGHT,
//		/**
//		 * The width of the border around the tiles
//		 */
//		TR_TILE_BORDER,
//		/**
//		 * The width of the final image
//		 */
//		TR_IMAGE_WIDTH,
//		/**
//		 * The height of the final image
//		 */
//		TR_IMAGE_HEIGHT,
//		/**
//		 * The number of rows of tiles
//		 */
//		TR_ROWS,
//		/**
//		 * The number of columns of tiles
//		 */
//		TR_COLUMNS,
//		/**
//		 * The current row number
//		 */
//		TR_CURRENT_ROW,
//		/**
//		 * The current column number
//		 */
//		TR_CURRENT_COLUMN,
//		/**
//		 * The width of the current tile
//		 */
//		TR_CURRENT_TILE_WIDTH,
//		/**
//		 * The height of the current tile
//		 */
//		TR_CURRENT_TILE_HEIGHT,
//		/**
//		 * The order that the rows are traversed
//		 */
//		TR_ROW_ORDER
//	}

//	 enums
	private static final int TR_TILE_WIDTH = 100;
	private static final int TR_TILE_HEIGHT = 1;
	private static final int TR_TILE_BORDER = 2;
	private static final int TR_IMAGE_WIDTH = 3;
	private static final int TR_IMAGE_HEIGHT = 4;
	private static final int TR_ROWS = 5;
	private static final int TR_COLUMNS = 6;
	private static final int TR_CURRENT_ROW = 7;
	private static final int TR_CURRENT_COLUMN = 8;
	private static final int TR_CURRENT_TILE_WIDTH = 9;
	private static final int TR_CURRENT_TILE_HEIGHT = 10;
	private static final int TR_ROW_ORDER = 11;
	private static final int TR_TOP_TO_BOTTOM = 12;
	private static final int TR_BOTTOM_TO_TOP = 13;
	
//	/**
//	 * Indicates we are traversing rows from the top to the bottom
//	 */
//	public static final int TR_TOP_TO_BOTTOM = 1;
//
//	/**
//	 * Indicates we are traversing rows from the bottom to the top
//	 */
//	public static final int TR_BOTTOM_TO_TOP = 2;

	/* Final image parameters */
	private final Dimension imageSize = new Dimension();

	private int imageFormat, imageType;

	private Buffer imageBuffer;

	/* Tile parameters */
	private final Dimension tileSize = new Dimension();

	private final Dimension tileSizeNB = new Dimension();

	private int tileBorder;

	private int tileFormat, tileType;

	private Buffer tileBuffer;

	/* Projection parameters */
	private boolean perspective;

	private double left;

	private double right;

	private double bottom;

	private double top;

	private double near;

	private double far;

	/* Misc */
	private int rowOrder;

	private int rows, columns;

	private int currentTile;

	private int currentTileWidth, currentTileHeight;

	private int currentRow, currentColumn;

	private final int[] viewportSave = new int[ 4 ];

	/**
	 * Creates a new TileRender object
	 */
	public TileRenderer()
	{
		this.tileSize.width = TileRenderer.DEFAULT_TILE_WIDTH;
		this.tileSize.height = TileRenderer.DEFAULT_TILE_HEIGHT;
		this.tileBorder = TileRenderer.DEFAULT_TILE_BORDER;
		this.rowOrder = TileRenderer.TR_BOTTOM_TO_TOP;
		this.currentTile = -1;
	}

	/**
	 * Sets up the number of rows and columns needed
	 */
	private void setup()
	{
		this.columns = ( this.imageSize.width + this.tileSizeNB.width - 1 ) / this.tileSizeNB.width;
		this.rows = ( this.imageSize.height + this.tileSizeNB.height - 1 ) / this.tileSizeNB.height;
		this.currentTile = 0;

//		assert columns >= 0;
//		assert rows >= 0;
	}

	/**
	 * Sets the size of the tiles to use in rendering. The actual
	 * effective size of the tile depends on the border size, ie (
	 * width - 2*border ) * ( height - 2 * border )
	 * 
	 * @param width
	 *           The width of the tiles. Must not be larger than the GL
	 *           context
	 * @param height
	 *           The height of the tiles. Must not be larger than the
	 *           GL context
	 * @param border
	 *           The width of the borders on each tile. This is needed
	 *           to avoid artifacts when rendering lines or points with
	 *           thickness > 1.
	 */
	public void setTileSize( final int width, final int height, final int border )
	{
//		assert ( border >= 0 );
//		assert ( width >= 1 );
//		assert ( height >= 1 );
//		assert ( width >= 2 * border );
//		assert ( height >= 2 * border );

		this.tileBorder = border;
		this.tileSize.width = width;
		this.tileSize.height = height;
		this.tileSizeNB.width = width - 2 * border;
		this.tileSizeNB.height = height - 2 * border;
		this.setup();
	}

	/**
	 * Specify a buffer the tiles to be copied to. This is not
	 * necessary for the creation of the final image, but useful if you
	 * want to inspect each tile in turn.
	 * 
	 * @param format
	 *           Interpreted as in glReadPixels
	 * @param type
	 *           Interpreted as in glReadPixels
	 * @param image
	 *           The buffer itself. Must be large enough to contain a
	 *           tile, minus any borders
	 */
	public void setTileBuffer( final int format, final int type, final Buffer image )
	{
		this.tileFormat = format;
		this.tileType = type;
		this.tileBuffer = image;
	}

	/**
	 * Sets the desired size of the final image
	 * 
	 * @param width
	 *           The width of the final image
	 * @param height
	 *           The height of the final image
	 */
	public void setImageSize( final int width, final int height )
	{
		this.imageSize.width = width;
		this.imageSize.height = height;
		this.setup();
	}

	/**
	 * Sets the buffer in which to store the final image
	 * 
	 * @param format
	 *           Interpreted as in glReadPixels
	 * @param type
	 *           Interpreted as in glReadPixels
	 * @param image
	 *           the buffer itself, must be large enough to hold the
	 *           final image
	 */
	public void setImageBuffer( final int format, final int type, final Buffer image )
	{
		this.imageFormat = format;
		this.imageType = type;
		this.imageBuffer = image;
	}

	/**
	 * Gets the parameters of this TileRender object
	 * 
	 * @param param
	 *           The parameter that is to be retrieved
	 * @return the value of the parameter
	 */
	public int getParam( final int param )
	{
		switch( param )
		{
			case TR_TILE_WIDTH:
				return this.tileSize.width;
			case TR_TILE_HEIGHT:
				return this.tileSize.height;
			case TR_TILE_BORDER:
				return this.tileBorder;
			case TR_IMAGE_WIDTH:
				return this.imageSize.width;
			case TR_IMAGE_HEIGHT:
				return this.imageSize.height;
			case TR_ROWS:
				return this.rows;
			case TR_COLUMNS:
				return this.columns;
			case TR_CURRENT_ROW:
				if( this.currentTile < 0 ) {
					return -1;
				} else {
					return this.currentRow;
				}
			case TR_CURRENT_COLUMN:
				if( this.currentTile < 0 ) {
					return -1;
				} else {
					return this.currentColumn;
				}
			case TR_CURRENT_TILE_WIDTH:
				return this.currentTileWidth;
			case TR_CURRENT_TILE_HEIGHT:
				return this.currentTileHeight;
			case TR_ROW_ORDER:
				return this.rowOrder;
			default:
				return 0;
		}
	}

	/**
	 * Sets the order of row traversal
	 * 
	 * @param order
	 *           The row traversal order, must be
	 *           eitherTR_TOP_TO_BOTTOM or TR_BOTTOM_TO_TOP
	 */
	public void setRowOrder( final int order )
	{
		if( order == TileRenderer.TR_TOP_TO_BOTTOM || order == TileRenderer.TR_BOTTOM_TO_TOP ) {
			this.rowOrder = order;
		}
	}

	/**
	 * Sets the context to use an orthographic projection. Must be
	 * called before rendering the first tile
	 * 
	 * @param left
	 *           As in glOrtho
	 * @param right
	 *           As in glOrtho
	 * @param bottom
	 *           As in glOrtho
	 * @param top
	 *           As in glOrtho
	 * @param zNear
	 *           As in glOrtho
	 * @param zFar
	 *           As in glOrtho
	 */
	public void trOrtho( final double left, final double right, final double bottom, final double top, final double zNear,
			final double zFar )
	{
		this.perspective = false;
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;
		this.near = zNear;
		this.far = zFar;
	}

	/**
	 * Sets the perspective projection frustrum. Must be called before
	 * rendering the first tile
	 * 
	 * @param left
	 *           As in glFrustrum
	 * @param right
	 *           As in glFrustrum
	 * @param bottom
	 *           As in glFrustrum
	 * @param top
	 *           As in glFrustrum
	 * @param zNear
	 *           As in glFrustrum
	 * @param zFar
	 *           As in glFrustrum
	 */
	public void trFrustum( final double left, final double right, final double bottom, final double top, final double zNear,
			final double zFar )
	{
		this.perspective = true;
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;
		this.near = zNear;
		this.far = zFar;
	}

	/**
	 * Convenient way to specify a perspective projection
	 * 
	 * @param fovy
	 *           As in gluPerspective
	 * @param aspect
	 *           As in gluPerspective
	 * @param zNear
	 *           As in gluPerspective
	 * @param zFar
	 *           As in gluPerspective
	 */
	public void trPerspective( final double fovy, final double aspect, final double zNear, final double zFar )
	{
		double xmin, xmax, ymin, ymax;
		ymax = zNear * Math.tan( fovy * 3.14159265 / 360.0 );
		ymin = -ymax;
		xmin = ymin * aspect;
		xmax = ymax * aspect;
		this.trFrustum( xmin, xmax, ymin, ymax, zNear, zFar );
	}

	/**
	 * Begins rendering a tile. The projection matrix stack should be
	 * left alone after calling this
	 * 
	 * @param gl
	 *           The gl context
	 */
	public void beginTile( final GL gl )
	{
		GL2 gl2 = gl.getGL2();
		
		if( this.currentTile <= 0 )
		{
			this.setup();
			/*
			 * Save user's viewport, will be restored after last tile
			 * rendered
			 */
			gl.glGetIntegerv( GL.GL_VIEWPORT, this.viewportSave, 0 );
		}

		/* which tile (by row and column) we're about to render */
		if( this.rowOrder == TileRenderer.TR_BOTTOM_TO_TOP )
		{
			this.currentRow = this.currentTile / this.columns;
			this.currentColumn = this.currentTile % this.columns;
		}
		else if( this.rowOrder == TileRenderer.TR_TOP_TO_BOTTOM )
		{
			this.currentRow = this.rows - ( this.currentTile / this.columns ) - 1;
			this.currentColumn = this.currentTile % this.columns;
		}
		else
		{
			/* This should never happen */
//			assert false;
		}
//		assert ( currentRow < rows );
//		assert ( currentColumn < columns );

		final int border = this.tileBorder;

		int th, tw;

		/* Compute actual size of this tile with border */
		if( this.currentRow < this.rows - 1 )
		{
			th = this.tileSize.height;
		}
		else
		{
			th = this.imageSize.height - ( this.rows - 1 ) * ( this.tileSizeNB.height  ) + 2 * border;
		}

		if( this.currentColumn < this.columns - 1 )
		{
			tw = this.tileSize.width;
		}
		else
		{
			tw = this.imageSize.width - ( this.columns - 1 ) * ( this.tileSizeNB.width  ) + 2 * border;
		}

		/* Save tile size, with border */
		this.currentTileWidth = tw;
		this.currentTileHeight = th;

		gl.glViewport( 0, 0, tw, th );

		/* save current matrix mode */
		final int[] matrixMode = new int[ 1 ];
		gl.glGetIntegerv( GL2.GL_MATRIX_MODE, matrixMode, 0 );
		gl2.glMatrixMode( GL2.GL_PROJECTION );
		gl2.glLoadIdentity();

		/* compute projection parameters */
		final double l =
				this.left + ( this.right - this.left ) * ( this.currentColumn * this.tileSizeNB.width - border )
						/ this.imageSize.width;
		final double r = l + ( this.right - this.left ) * tw / this.imageSize.width;
		final double b =
				this.bottom + ( this.top - this.bottom ) * ( this.currentRow * this.tileSizeNB.height - border )
						/ this.imageSize.height;
		final double t = b + ( this.top - this.bottom ) * th / this.imageSize.height;

		if( this.perspective )
		{
			gl2.glFrustum( l, r, b, t, this.near, this.far );
		}
		else
		{
			gl2.glOrtho( l, r, b, t, this.near, this.far );
		}

		/* restore user's matrix mode */
		gl2.glMatrixMode( matrixMode[ 0 ] );
	}

	/**
	 * Must be called after rendering the scene
	 * 
	 * @param gl
	 *           the gl context
	 * @return true if there are more tiles to be rendered, false if
	 *         the final image is complete
	 */
	public boolean endTile( final GL gl )
	{
		final int[] prevRowLength = new int[ 1 ], prevSkipRows = new int[ 1 ], prevSkipPixels = new int[ 1 ], prevAlignment =
				new int[ 1 ];

//		assert ( currentTile >= 0 );

		/* be sure OpenGL rendering is finished */
		gl.glFlush();

		/* save current glPixelStore values */
		gl.glGetIntegerv( GL2.GL_PACK_ROW_LENGTH, prevRowLength, 0 );
		gl.glGetIntegerv( GL2.GL_PACK_SKIP_ROWS, prevSkipRows, 0 );
		gl.glGetIntegerv( GL2.GL_PACK_SKIP_PIXELS, prevSkipPixels, 0 );
		gl.glGetIntegerv( GL2.GL_PACK_ALIGNMENT, prevAlignment, 0 );

		if( this.tileBuffer != null )
		{
			final int srcX = this.tileBorder;
			final int srcY = this.tileBorder;
			final int srcWidth = this.tileSizeNB.width;
			final int srcHeight = this.tileSizeNB.height;
			gl.glReadPixels( srcX, srcY, srcWidth, srcHeight, this.tileFormat, this.tileType, this.tileBuffer );
		}

		if( this.imageBuffer != null )
		{
			final int srcX = this.tileBorder;
			final int srcY = this.tileBorder;
			final int srcWidth = this.currentTileWidth - 2 * this.tileBorder;
			final int srcHeight = this.currentTileHeight - 2 * this.tileBorder;
			final int destX = this.tileSizeNB.width * this.currentColumn;
			final int destY = this.tileSizeNB.height * this.currentRow;

			/* setup pixel store for glReadPixels */
			gl.glPixelStorei( GL2.GL_PACK_ROW_LENGTH, this.imageSize.width );
			gl.glPixelStorei( GL2.GL_PACK_SKIP_ROWS, destY );
			gl.glPixelStorei( GL2.GL_PACK_SKIP_PIXELS, destX );
			gl.glPixelStorei( GL2.GL_PACK_ALIGNMENT, 1 );

			/* read the tile into the final image */
			gl.glReadPixels( srcX, srcY, srcWidth, srcHeight, this.imageFormat, this.imageType, this.imageBuffer );
		}

		/* restore previous glPixelStore values */
		gl.glPixelStorei( GL2.GL_PACK_ROW_LENGTH, prevRowLength[ 0 ] );
		gl.glPixelStorei( GL2.GL_PACK_SKIP_ROWS, prevSkipRows[ 0 ] );
		gl.glPixelStorei( GL2.GL_PACK_SKIP_PIXELS, prevSkipPixels[ 0 ] );
		gl.glPixelStorei( GL2.GL_PACK_ALIGNMENT, prevAlignment[ 0 ] );

		/* increment tile counter, return 1 if more tiles left to render */
		this.currentTile++;
		if( this.currentTile >= this.rows * this.columns )
		{
			/* restore user's viewport */
			gl.glViewport( this.viewportSave[ 0 ], this.viewportSave[ 1 ], this.viewportSave[ 2 ], this.viewportSave[ 3 ] );
			this.currentTile = -1; /* all done */
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Tile rendering causes problems with using glRasterPos3f, so you
	 * should use this replacement instead
	 * 
	 * @param x
	 *           As in glRasterPos3f
	 * @param y
	 *           As in glRasterPos3f
	 * @param z
	 *           As in glRasterPos3f
	 * @param gl
	 *           The gl context
	 * @param glu
	 *           A GLU object
	 */
	public void trRasterPos3f( final float x, final float y, final float z, final GL gl, final GLU glu )
	{
		GL2 gl2 = gl.getGL2();
		
		if( this.currentTile < 0 )
		{
			/* not doing tile rendering right now. Let OpenGL do this. */
			gl2.glRasterPos3f( x, y, z );
		}
		else
		{
			final double[] modelview = new double[ 16 ], proj = new double[ 16 ];
			final int[] viewport = new int[ 4 ];
			final double[] winCoords = new double[ 3 ];

			/* Get modelview, projection and viewport */
			gl2.glGetDoublev( GL2.GL_MODELVIEW_MATRIX, modelview, 0 );
			gl2.glGetDoublev( GL2.GL_PROJECTION_MATRIX, proj, 0 );
			viewport[ 0 ] = 0;
			viewport[ 1 ] = 0;
			viewport[ 2 ] = this.currentTileWidth;
			viewport[ 3 ] = this.currentTileHeight;

			/* Project object coord to window coordinate */
			if( glu.gluProject( x, y, z, modelview, 0, proj, 0, viewport, 0, winCoords, 0 ) )
			{

				try {
					/* set raster pos to window coord (0,0) */
					gl2.glMatrixMode( GL2.GL_MODELVIEW );
					gl2.glPushMatrix();
					gl2.glLoadIdentity();
					gl2.glMatrixMode( GL2.GL_PROJECTION );
					gl2.glPushMatrix();
					gl2.glLoadIdentity();
					gl2.glOrtho( 0.0, this.currentTileWidth, 0.0, this.currentTileHeight, 0.0, 1.0 );
					gl2.glRasterPos3d( 0.0, 0.0, -winCoords[2] );

					/*
					 * Now use empty bitmap to adjust raster position to
					 * (winX,winY)
					 */
					{
						final byte[] bitmap = { 0 };
						gl2.glBitmap( 1, 1, 0.0f, 0.0f, ( float ) winCoords[0], ( float ) winCoords[1], bitmap, 0);
					}
				} catch (Exception e) {
					if (DebugState.isDebug())
						e.printStackTrace();
				}

				/* restore original matrices */
				gl2.glPopMatrix(); /* proj */
				gl2.glMatrixMode( GL2.GL_MODELVIEW );
				gl2.glPopMatrix();
			}
		}
	}
}
