package org.rcsb.mbt.glscene.geometry;

public class Quat4f {
	public float[] coordinates = {0,0,0,0};
	
	public Quat4f() {

	}
	
	public Quat4f(final float x, final float y, final float z, final float w) {
		this.set(x,y,z,w);
	}
	
	public Quat4f(final Quat4f copy) {
		this.set(copy.coordinates[0], copy.coordinates[1], copy.coordinates[2], copy.coordinates[3]);
	}

	public void set(final float x, final float y, final float z, final float w) {
		this.coordinates[0] = x;
		this.coordinates[1] = y;
		this.coordinates[2] = z;
		this.coordinates[3] = w;
	}
	
	public void set(final Quat4f copy) {
		this.set(copy.coordinates[0], copy.coordinates[1], copy.coordinates[2], copy.coordinates[3]);
	}
	
	  /**
	   * Sets the value of this quaternion to the quaternion product of
	   * quaternions q1 and q2 (this = q1 * q2).  
	   * Note that this is safe for aliasing (e.g. this can be q1 or q2).
	   * @param q1 the first quaternion
	   * @param q2 the second quaternion
	   */
	  public final void mul(final Quat4f q1, final Quat4f q2)
	  {
	    if (this != q1 && this != q2) {
	      this.coordinates[3] = q1.coordinates[3]*q2.coordinates[3] - q1.coordinates[0]*q2.coordinates[0] - q1.coordinates[1]*q2.coordinates[1] - q1.coordinates[2]*q2.coordinates[2];
	      this.coordinates[0] = q1.coordinates[3]*q2.coordinates[0] + q2.coordinates[3]*q1.coordinates[0] + q1.coordinates[1]*q2.coordinates[2] - q1.coordinates[2]*q2.coordinates[1];
	      this.coordinates[1] = q1.coordinates[3]*q2.coordinates[1] + q2.coordinates[3]*q1.coordinates[1] - q1.coordinates[0]*q2.coordinates[2] + q1.coordinates[2]*q2.coordinates[0];
	      this.coordinates[2] = q1.coordinates[3]*q2.coordinates[2] + q2.coordinates[3]*q1.coordinates[2] + q1.coordinates[0]*q2.coordinates[1] - q1.coordinates[1]*q2.coordinates[0];
	    } else {
	      float	x, y, w;

	      w = q1.coordinates[3]*q2.coordinates[3] - q1.coordinates[0]*q2.coordinates[0] - q1.coordinates[1]*q2.coordinates[1] - q1.coordinates[2]*q2.coordinates[2];
	      x = q1.coordinates[3]*q2.coordinates[0] + q2.coordinates[3]*q1.coordinates[0] + q1.coordinates[1]*q2.coordinates[2] - q1.coordinates[2]*q2.coordinates[1];
	      y = q1.coordinates[3]*q2.coordinates[1] + q2.coordinates[3]*q1.coordinates[1] - q1.coordinates[0]*q2.coordinates[2] + q1.coordinates[2]*q2.coordinates[0];
	      this.coordinates[2] = q1.coordinates[3]*q2.coordinates[2] + q2.coordinates[3]*q1.coordinates[2] + q1.coordinates[0]*q2.coordinates[1] - q1.coordinates[1]*q2.coordinates[0];
	      this.coordinates[3] = w;
	      this.coordinates[0] = x;
	      this.coordinates[1] = y;
	    }
	  }


	 /**
	   * Sets the value of this quaternion to the quaternion product of
	   * itself and q1 (this = this * q1).  
	   * @param q1 the other quaternion
	   */
	  public final void mul(final Quat4f q1)
	  {
	      float     x, y, w; 

	       w = this.coordinates[3]*q1.coordinates[3] - this.coordinates[0]*q1.coordinates[0] - this.coordinates[1]*q1.coordinates[1] - this.coordinates[2]*q1.coordinates[2];
	       x = this.coordinates[3]*q1.coordinates[0] + q1.coordinates[3]*this.coordinates[0] + this.coordinates[1]*q1.coordinates[2] - this.coordinates[2]*q1.coordinates[1];
	       y = this.coordinates[3]*q1.coordinates[1] + q1.coordinates[3]*this.coordinates[1] - this.coordinates[0]*q1.coordinates[2] + this.coordinates[2]*q1.coordinates[0];
	       this.coordinates[2] = this.coordinates[3]*q1.coordinates[2] + q1.coordinates[3]*this.coordinates[2] + this.coordinates[0]*q1.coordinates[1] - this.coordinates[1]*q1.coordinates[0];
	       this.coordinates[3] = w;
	       this.coordinates[0] = x;
	       this.coordinates[1] = y;
	  } 


	 /** 
	   * Multiplies quaternion q1 by the inverse of quaternion q2 and places
	   * the value into this quaternion.  The value of both argument quaternions 
	   * is preservered (this = q1 * q2^-1).
	   * @param q1 the first quaternion 
	   * @param q2 the second quaternion
	   */ 
	  public final void mulInverse(final Quat4f q1, final Quat4f q2) 
	  {   
	      final Quat4f  tempQuat = new Quat4f(q2);  
	 
	      tempQuat.inverse(); 
	      this.mul(q1, tempQuat); 
	  }
	 


	 /**
	   * Multiplies this quaternion by the inverse of quaternion q1 and places
	   * the value into this quaternion.  The value of the argument quaternion
	   * is preserved (this = this * q^-1).
	   * @param q1 the other quaternion
	   */
	  public final void mulInverse(final Quat4f q1)
	  {  
	      final Quat4f  tempQuat = new Quat4f(q1);

	      tempQuat.inverse();
	      this.mul(tempQuat);
	  }


	  /**
	   * Sets the value of this quaternion to quaternion inverse of quaternion q1.
	   * @param q1 the quaternion to be inverted
	   */
	  public final void inverse(final Quat4f q1)
	  {
	    float norm;

	    norm = 1.0f/(q1.coordinates[3]*q1.coordinates[3] + q1.coordinates[0]*q1.coordinates[0] + q1.coordinates[1]*q1.coordinates[1] + q1.coordinates[2]*q1.coordinates[2]);
	    this.coordinates[3] =  norm*q1.coordinates[3];
	    this.coordinates[0] = -norm*q1.coordinates[0];
	    this.coordinates[1] = -norm*q1.coordinates[1];
	    this.coordinates[2] = -norm*q1.coordinates[2];
	  }


	  /**
	   * Sets the value of this quaternion to the quaternion inverse of itself.
	   */
	  public final void inverse()
	  {
	    float norm;  
	 
	    norm = 1.0f/(this.coordinates[3]*this.coordinates[3] + this.coordinates[0]*this.coordinates[0] + this.coordinates[1]*this.coordinates[1] + this.coordinates[2]*this.coordinates[2]);
	    this.coordinates[3] *=  norm;
	    this.coordinates[0] *= -norm;
	    this.coordinates[1] *= -norm;
	    this.coordinates[2] *= -norm;
	  }


	  /**
	   * Sets the value of this quaternion to the normalized value
	   * of quaternion q1.
	   * @param q1 the quaternion to be normalized.
	   */
	  public final void normalize(final Quat4f q1)
	  {
	    float norm;

	    norm = (q1.coordinates[0]*q1.coordinates[0] + q1.coordinates[1]*q1.coordinates[1] + q1.coordinates[2]*q1.coordinates[2] + q1.coordinates[3]*q1.coordinates[3]);

	    if (norm > 0.0) {
	      norm = (float)(1.0/Math.sqrt(norm));
	      this.coordinates[0] = norm*q1.coordinates[0];
	      this.coordinates[1] = norm*q1.coordinates[1];
	      this.coordinates[2] = norm*q1.coordinates[2];
	      this.coordinates[3] = norm*q1.coordinates[3];
	    } else {
	      this.coordinates[0] =  0.0f;
	      this.coordinates[1] =  0.0f;
	      this.coordinates[2] =  0.0f;
	      this.coordinates[3] =  0.0f;
	    }
	  }


	  /**
	   * Normalizes the value of this quaternion in place.
	   */
	  public final void normalize()
	  {
	    float norm;

	    norm = (this.coordinates[0]*this.coordinates[0] + this.coordinates[1]*this.coordinates[1] + this.coordinates[2]*this.coordinates[2] + this.coordinates[3]*this.coordinates[3]);

	    if (norm > 0.0) {
	      norm = (float)(1.0 / Math.sqrt(norm));
	      this.coordinates[0] *= norm;
	      this.coordinates[1] *= norm;
	      this.coordinates[2] *= norm;
	      this.coordinates[3] *= norm;
	    } else {
	      this.coordinates[0] =  0.0f;
	      this.coordinates[1] =  0.0f;
	      this.coordinates[2] =  0.0f;
	      this.coordinates[3] =  0.0f;
	    }
	  }
	
}
