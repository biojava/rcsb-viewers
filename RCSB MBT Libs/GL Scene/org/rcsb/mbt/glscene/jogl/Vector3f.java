package org.rcsb.mbt.glscene.jogl;

import org.rcsb.mbt.model.util.Algebra;

public class Vector3f {
	public float[] coordinates = {0,0,0};
	
	public Vector3f(final float d1, final float d2, final float d3) {
		this.set(d1,d2,d3);
	}
	
	public Vector3f(final float[] coordinates) {
		this.set(coordinates[0], coordinates[1], coordinates[2]);
	}
	
	public Vector3f(final Vector3f copy) {
		this.set(copy);
	}

	public Vector3f() {
		
	}
	
	public void set(final Vector3f copy) {
		this.set(copy.coordinates[0],copy.coordinates[1],copy.coordinates[2]);
	}
	
	public void set(final float[] coordinates) {
		this.set(coordinates[0], coordinates[1], coordinates[2]);
	}
	
	public void set(final float d1, final float d2, final float d3) {
		this.coordinates[0] = d1;
		this.coordinates[1] = d2;
		this.coordinates[2] = d3;
	}
	
	public void cross(final Vector3f v1, final Vector3f v2) {
		Algebra.crossProduct(v1.coordinates, v2.coordinates, this.coordinates);
	}
	
	public float dot(final Vector3f v) {
		return Algebra.dotProduct(v.coordinates, this.coordinates);
	}
	
	public void normalize() {
		Algebra.normalizeVector(this.coordinates);
	}
	
	public void sub(final Vector3f v) {
		for(int i = 0; i < this.coordinates.length; i++) {
			this.coordinates[i] -= v.coordinates[i];
		}
	}
	
	public void sub(final Vector3f v1, final Vector3f v2) {
		for(int i = 0; i < this.coordinates.length; i++) {
			this.coordinates[i] = v1.coordinates[i] - v2.coordinates[i];
		}
	}
	
	public void add(final Vector3f v) {
		for(int i = 0; i < this.coordinates.length; i++) {
			this.coordinates[i] += v.coordinates[i];
		}
	}
	
	public void add(final Vector3f v1, final Vector3f v2) {
		for(int i = 0; i < this.coordinates.length; i++) {
			this.coordinates[i] = v1.coordinates[i] + v2.coordinates[i];
		}
	}
	
	public void scale(final float value) {
		for(int i = 0; i < this.coordinates.length; i++) {
			this.coordinates[i] *= value;
		}
	}
	
	public void scaleAdd(final float value, final Vector3f v1, final Vector3f v2) {
		for(int i = 0; i < this.coordinates.length; i++) {
			this.coordinates[i] = v1.coordinates[i] * value + v2.coordinates[i];
		}
	}
	
	public void interpolate(final Vector3f v1, final Vector3f v2, final float alpha) {
		for(int i = 0; i < this.coordinates.length; i++) {
			this.coordinates[i] = (1 - alpha) * v1.coordinates[i] + alpha * v2.coordinates[i];
		}
	}
	
	public float length() {
		return Algebra.vectorLength(this.coordinates);
	}
	
	public void negate() {
		for(int i = 0; i < this.coordinates.length; i++) {
			this.coordinates[i] = -this.coordinates[i];
		}
	}
	
	   /**
     * Returns true if the L-infinite distance between this tuple
     * and tuple t1 is less than or equal to the epsilon parameter, 
     * otherwise returns false.  The L-infinite
     * distance is equal to MAX[abs(x1-x2), abs(y1-y2), abs(z1-z2)].
     * @param t1  the tuple to be compared to this tuple
     * @param epsilon  the threshold value  
     * @return  true or false
     */
    public boolean epsilonEquals(final Vector3f t1, final float epsilon)
    {
       float diff;

       diff = this.coordinates[0] - t1.coordinates[0];
       if((diff<0?-diff:diff) > epsilon) {
		return false;
	}

       diff = this.coordinates[1] - t1.coordinates[1];
       if((diff<0?-diff:diff) > epsilon) {
		return false;
	}

       diff = this.coordinates[2] - t1.coordinates[2];
       if((diff<0?-diff:diff) > epsilon) {
		return false;
	}

       return true;

    }
    
    /**
    *
    * <code>mult</code> multiplies this vector by a scalar. The resultant
    * vector is supplied as the second parameter and returned.
    *
    * @param scalar the scalar to multiply this vector by.
    * @param product the product to store the result in.
    * @return product
    */
   public Vector3f mult(final float scalar, final Vector3f product_) {
	   Vector3f product = product_;
       if (null == product) {
           product = new Vector3f();
       }

       product.coordinates[0] = this.coordinates[0] * scalar;
       product.coordinates[1] = this.coordinates[1] * scalar;
       product.coordinates[2] = this.coordinates[2] * scalar;
       return product;
   }
   

   /**
    * <code>subtractLocal</code> subtracts a provided vector to this vector
    * internally, and returns a handle to this vector for easy chaining of
    * calls. If the provided vector is null, null is returned.
    *
    * @param vec
    *            the vector to subtract
    * @return this
    */
   public Vector3f subtractLocal(final Vector3f vec) {
       this.coordinates[0] -= vec.coordinates[0];
       this.coordinates[1] -= vec.coordinates[1];
       this.coordinates[2] -= vec.coordinates[2];
       return this;
   }
   
   /**
    * <code>normalizeLocal</code> makes this vector into a unit vector of
    * itself.
    *
    * @return this.
    */
   public Vector3f normalizeLocal() {
       final float length = this.length();
       if (length != 0) {
           return this.divideLocal(length);
       }
       
       return this;
   }
   
   /**
    * <code>divideLocal</code> divides this vector by a scalar internally,
    * and returns a handle to this vector for easy chaining of calls. Dividing
    * by zero will result in an exception.
    *
    * @param scalar
    *            the value to divides this vector by.
    * @return this
    */
   public Vector3f divideLocal(final float scalar_) {
	   float scalar = scalar_;
       scalar = 1f/scalar;
       this.coordinates[0] *= scalar;
       this.coordinates[1] *= scalar;
       this.coordinates[2] *= scalar;
       return this;
   }
   
   /**
    * <code>multLocal</code> multiplies this vector by a scalar internally,
    * and returns a handle to this vector for easy chaining of calls.
    *
    * @param scalar
    *            the value to multiply this vector by.
    * @return this
    */
   public Vector3f multLocal(final float scalar) {
       this.coordinates[0] *= scalar;
       this.coordinates[1] *= scalar;
       this.coordinates[2] *= scalar;
       return this;
   }
   
   /**
    * <code>addLocal</code> adds a provided vector to this vector internally,
    * and returns a handle to this vector for easy chaining of calls. If the
    * provided vector is null, null is returned.
    *
    * @param vec
    *            the vector to add to this vector.
    * @return this
    */
   public Vector3f addLocal(final Vector3f vec) {
       this.coordinates[0] += vec.coordinates[0];
       this.coordinates[1] += vec.coordinates[1];
       this.coordinates[2] += vec.coordinates[2];
       return this;
   }
   
}
