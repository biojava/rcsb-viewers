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
 * The code for surface generation was contributed by Dong Xu
 * and Yang Zhang at the University of Michigan, Ann Arbor. This
 * class represents the Java version translated from the original C++
 * code (http://zhanglab.ccmb.med.umich.edu/EDTSurf).
 * 
 * Please reference D. Xu, Y. Zhang (2009) 
 * Generating Triangulated Macromolecular Surfaces by Euclidean 
 * Distance Transform. PLoS ONE 4(12): e8140.
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
 * Created on 2011/11/08
 *
 */ 

package org.rcsb.mbt.surface.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point3f;

import org.rcsb.mbt.surface.datastructure.FaceInfo;
import org.rcsb.mbt.surface.datastructure.Sphere;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;
import org.rcsb.mbt.surface.datastructure.VertInfo;
import org.rcsb.mbt.surface.datastructure.VolumePixel;


/**
 * @author Dong Xu (original C++ version)
 * @author Peter Rose (converted and refactored in Java)
 */
public class EdtSurfaceCalculator {
    private float probeRadius = 0.0f;
    private static float fmargin = 2.5f;
    private int boxlength=128;
    private float fixsf=4;
    private float threshbox=300;

    private int plength = 0;
    private int pwidth = 0;
    private int pheight = 0;
    private float cutradis = 0;
    private int[] widxz = null;
    private int[][] depty = null;
    private float scalefactor=1;
    private VolumePixel[][][] vp = null;
    private Point3f ptran = null;

    private List<FaceInfo> faceList = new ArrayList<FaceInfo>(0);
    private List<VertInfo> vertices = new ArrayList<VertInfo>(0);
    private TriangulatedSurface surface = new TriangulatedSurface();

    private List<Sphere> spheres = null;
    private List<Float> radiiSet = new ArrayList<Float>();
    private int[] radiusIndex = null;

    // nb[26][3]
    private static int nb[][]={{1,0,0}, {-1,0,0}, {0,1,0}, {0,-1,0}, {0,0,1}, {0,0,-1},
    {1,1,0}, {1,-1,0}, {-1,1,0}, {-1,-1,0}, {1,0,1}, {1,0,-1}, {-1,0,1}, {-1,0,-1}, {0,1,1}, {0,1,-1}, {0,-1,1}, {0,-1,-1},
    {1,1,1}, {1,1,-1}, {1,-1,1}, {-1,1,1}, {1,-1,-1}, {-1,-1,1}, {-1,1,-1}, {-1,-1,-1}};

// public void initpara(int seqinit,int seqterm,atom* proseq,bool atomtype,bool btype) {
    // btype: false for VDW solid, true for SA solid
    public EdtSurfaceCalculator(List<Sphere> spheres, float probeRadius, float resolution) {
        this.spheres = spheres;
        this.probeRadius = probeRadius;
        this.fixsf = resolution * this.fixsf;
    }

    public TriangulatedSurface getSurface() {
        return surface;
    }

    private void createRadiusIndex() {
        radiusIndex = new int[spheres.size()];
        for (int i = 0; i < spheres.size(); i++) {
            float radius = spheres.get(i).getRadius();
            int index = radiiSet.indexOf(radius);
            if (index < 0) {
                radiiSet.add(radius);
                radiusIndex[i] = radiiSet.size() - 1;
            } else {
                radiusIndex[i] = index;
            }
        }
    }

    public void initparam() {
        Point3f pMin = new Point3f();
        Point3f pMax = new Point3f();
        boundbox(pMin, pMax);
 //       System.out.println(pMin + " " + pMax);
        Point3f offset = new Point3f(probeRadius + fmargin, probeRadius + fmargin, probeRadius + fmargin);
//        pMin.x -= probeRadius + fmargin;
//        pMin.y -= probeRadius + fmargin;
//        pMin.z -= probeRadius + fmargin;
//        pMax.x += probeRadius + fmargin;
//        pMax.y += probeRadius + fmargin;
//        pMax.z += probeRadius + fmargin;
      pMin.sub(offset);
      pMax.add(offset);

        ptran = new Point3f();
        ptran.negate(pMin);
//        System.out.println(ptran.x + " " + ptran.y + " " + ptran.z);
//        scalefactor = pMax.x - pMin.x;
//        if ((pMax.y - pMin.y) > scalefactor) {
//            scalefactor = pMax.y - pMin.y;
//        }
//        if ((pMax.z - pMin.z) > scalefactor) {
//            scalefactor = pMax.z - pMin.z;
//        }
        scalefactor = (boxlength - 1.0f) / pMin.distanceLinf(pMax);
//	ptran.x=-pmin.x;
//	ptran.y=-pmin.y;
//	ptran.z=-pmin.z;
//	scalefactor=pmax.x-pmin.x;
//	if((pmax.y-pmin.y)>scalefactor)
//		scalefactor=pmax.y-pmin.y;
//	if((pmax.z-pmin.z)>scalefactor)
//		scalefactor=pmax.z-pmin.z;
//	scalefactor=(boxlength-1.0)/double(scalefactor);
        ///////////////////////////add this automatically first fix sf then fix boxlength
//	/*
        boxlength = (int) (boxlength * fixsf / scalefactor);
        scalefactor = fixsf;

        if (boxlength > threshbox) {
            float sfthresh = threshbox / boxlength;
            boxlength = (int) threshbox;
            scalefactor = scalefactor * sfthresh;
        }
//	boxlength=int(boxlength*fixsf/scalefactor);
//	scalefactor=fixsf;
//	double threshbox=300;
//	if(boxlength>threshbox)
//	{
//		double sfthresh=threshbox/double(boxlength);
//		boxlength=int(threshbox);
//		scalefactor=scalefactor*sfthresh;
//	}
//	*/

        plength = (int) (Math.ceil(scalefactor * (pMax.x - pMin.x)) + 1);
        pwidth = (int) (Math.ceil(scalefactor * (pMax.y - pMin.y)) + 1);
        pheight = (int) (Math.ceil(scalefactor * (pMax.z - pMin.z)) + 1);

        if (plength > boxlength) {
            plength = boxlength;
        }
        if (pwidth > boxlength) {
            pwidth = boxlength;
        }
        if (pheight > boxlength) {
            pheight = boxlength;
        }
//	boundingatom(btype); moved to constructor
        cutradis = probeRadius * scalefactor;
//	plength=int(ceil(scalefactor*(pmax.x-pmin.x))+1);
//	pwidth=int(ceil(scalefactor*(pmax.y-pmin.y))+1);
//	pheight=int(ceil(scalefactor*(pmax.z-pmin.z))+1);
//	if(plength>boxlength)
//		plength=boxlength;
//	if(pwidth>boxlength)
//		pwidth=boxlength;
//	if(pheight>boxlength)
//		pheight=boxlength;
//	boundingatom(btype);
//	cutradis=proberadius*scalefactor;
        
//        System.out.printf("box[%3d *%3d *%3d], scale factor: %6.3f\n",
//                plength, pwidth, pheight, scalefactor);
    }

 
//private void boundbox(int seqinit,int seqterm,atom* proseq,bool atomtype,
//							  point3d *minp,point3d *maxp)
public void boundbox(Point3f minP, Point3f maxP)
{
    minP.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    maxP.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

    for (Sphere s: spheres) {
        if (s.getLocation().x < minP.x)
            minP.x = s.getLocation().x;
        if (s.getLocation().y < minP.y)
            minP.y = s.getLocation().y;
        if (s.getLocation().z < minP.z)
            minP.z = s.getLocation().z;
        if (s.getLocation().x > maxP.x)
            maxP.x = s.getLocation().x;
        if (s.getLocation().y > maxP.y)
            maxP.y = s.getLocation().y;
        if (s.getLocation().z > maxP.z)
            maxP.z = s.getLocation().z;
    }
//	int i;
//	minp->x=100000;minp->y=100000;minp->z=100000;
//	maxp->x=-100000;maxp->y=-100000;maxp->z=-100000;
    // loop over all atoms
//	for(i=seqinit;i<=seqterm;i++)
	{
        // simpletype = 1 = ATOM
        // ins = insertion code
//		if(proseq[i].simpletype==1 && proseq[i].ins==' ')
//		{
//            // if MS surface, exclude atomtype 5=H, 12=HX
//			if(atomtype && (proseq[i].detail==5 ||proseq[i].detail==12))
//				continue;
//			if(proseq[i].x<minp->x)
//				minp->x=proseq[i].x;
//			if(proseq[i].y<minp->y)
//				minp->y=proseq[i].y;
//			if(proseq[i].z<minp->z)
//				minp->z=proseq[i].z;
//			if(proseq[i].x>maxp->x)
//				maxp->x=proseq[i].x;
//			if(proseq[i].y>maxp->y)
//				maxp->y=proseq[i].y;
//			if(proseq[i].z>maxp->z)
//				maxp->z=proseq[i].z;
		}
	}

    public void boundingatom() {
        createRadiusIndex();

        int i, j, k;
        float[] tradius = new float[radiiSet.size()]; // -pr 13 atom types/radii
//	double txz,tdept,sradius;
        float txz, tdept, sradius;
        int indx;

        depty = null;
        depty = new int[radiiSet.size()][]; // how large should second index be??
        widxz = new int[radiiSet.size()];
//	for(i=0;i<13;i++)
//	{
//		if(depty[i]!=NULL)
//			 free(depty[i]);
//	}
//	flagradius=btype;
        for (i = 0; i < radiiSet.size(); i++) // -pr 13 atom types/radii
        {
//		if(btype==false) // -pr false for VDW solid, true for SA solid
//			tradius[i]=radiiSet.get(i)*scalefactor+0.5f;
//		else
            tradius[i] = (radiiSet.get(i) + probeRadius) * scalefactor + 0.5f;

            sradius = tradius[i] * tradius[i];
            widxz[i] = (int) tradius[i] + 1;  // -pr used later, index by atom type
//        widxz[i]=int(tradius[i])+1;  // -pr used later, index by atom type
            depty[i] = new int[widxz[i] * widxz[i]]; // -pr used later, index by atom type
            indx = 0;
            for (j = 0; j < widxz[i]; j++) {
                for (k = 0; k < widxz[i]; k++) {
                    txz = j * j + k * k;
                    if (txz > sradius) {
                        depty[i][indx] = -1;
                    } else {
                        tdept = (float) Math.sqrt(sradius - txz);
                        depty[i][indx] = (int) (tdept + 0.0f);
                    }
                    //               System.out.println("depty: " + i + " " +  depty[i][indx]); // check ok
                    indx++;
                }
            }
        }
    }

// void ProteinSurface::fillvoxels(int seqinit,int seqterm,bool atomtype,atom* proseq,bool bcolor) // used
public void fillvoxels(boolean bcolor) // used
{

	int i,j,k;
    vp = new VolumePixel[plength][pwidth][pheight];
//	if(vp==NULL)
//	{
//		vp=new volumepixel**[plength];
//		for(i=0;i<plength;i++)
//			vp[i]=new volumepixel*[pwidth];
//		for(i=0;i<plength;i++)
//		{
//			for(j=0;j<pwidth;j++)
//			{
//				vp[i][j]=new volumepixel[pheight];
//			}
//		}
//	}

	for(i=0;i<plength;i++)
	{
		for(j=0;j<pwidth;j++)
		{
			for(k=0;k<pheight;k++)
			{
                vp[i][j][k] = new VolumePixel();
				vp[i][j][k].inout=false;
				vp[i][j][k].isdone=false;
				vp[i][j][k].isbound=false;
				vp[i][j][k].distance=-1;
				vp[i][j][k].atomid=-1;
				vp[i][j][k].reference=null;
			}
		}
	}
//	int totnum=0;
    for (int ii = 0; ii < spheres.size(); ii++) {
//        fillatom(ii, bcolor);
        fillatom(ii, bcolor);
    }
//	for(i=seqinit;i<=seqterm;i++)
//	{
//		if(proseq[i].simpletype==1 && proseq[i].ins==' ' /*&& (proseq[i].alt==' ' || proseq[i].alt=='A')*/ )
//		{
//			if(atomtype && (proseq[i].detail==5 || proseq[i].detail==12))
//			{
//				continue;
//			}
//			fillatom(i,proseq,bcolor);
//			totnum++;
//		}
//	}
//	printf("%d\n",totnum);

	for(i=0;i<plength;i++)
	{
		for(j=0;j<pwidth;j++)
		{
			for(k=0;k<pheight;k++)
			{
				if(vp[i][j][k].inout)
				{
					vp[i][j][k].isdone=true;
				}
			}
		}
	}
}

private void fillatom(int indx, boolean bcolor) {
	int cx,cy,cz;
	int ox,oy,oz;
        Point3f cp = new Point3f();
//	point3d cp;
        Sphere s = spheres.get(indx);
        Object r = s.getReference();
        Point3f c = s.getLocation();
        cp.x=c.x+ptran.x;
	cp.y=c.y+ptran.y;
	cp.z=c.z+ptran.z;
//	cp.x=proseq[indx].x+ptran.x;
//	cp.y=proseq[indx].y+ptran.y;
//	cp.z=proseq[indx].z+ptran.z;
	cp.x*=scalefactor;
	cp.y*=scalefactor;
	cp.z*=scalefactor;
        cx= (int) (cp.x+0.5f);
	cy= (int) (cp.y+0.5f);
	cz= (int) (cp.z+0.5f);
//    int at = spheres.get(indx).getReferenceId();
    int at = radiusIndex[indx];
//	cx=int(cp.x+0.5);
//	cy=int(cp.y+0.5);
//	cz=int(cp.z+0.5);
//	int at=proseq[indx].detail; // -pr atom type
	int i,j,k;
	int ii,jj,kk;
	int mi,mj,mk;
	int si,sj,sk;
	int tind;
	int nind=0;
	for(i=0;i<widxz[at];i++) // -pr at: atom type
	{
		for(j=0;j<widxz[at];j++)
		{
			if(depty[at][nind]!=-1)
			{

				for( ii=-1;ii<2;ii++)
				{
					for( jj=-1;jj<2;jj++)
					{
						for( kk=-1;kk<2;kk++)
						{
							if(ii!=0 && jj!=0 && kk!=0)
							{
								mi=ii*i;
								mk=kk*j;
								for(k=0;k<=depty[at][nind];k++)
								{
									mj=k*jj;
									si=cx+mi;
									sj=cy+mj;
									sk=cz+mk;
									if(si<0 || sj<0 || sk<0 || si>=plength || sj>=pwidth || sk>=pheight)
									{
										continue;
									}
								if(!bcolor)
									{
										vp[si][sj][sk].inout=true;
										continue;
									}
								else{
									if(vp[si][sj][sk].inout==false)
									{
										vp[si][sj][sk].inout=true;
										vp[si][sj][sk].atomid=indx;
										vp[si][sj][sk].reference=r;
									}
									//no atomic info to each voxel change above line
								//*
								    else if(vp[si][sj][sk].inout)
									{
										tind=vp[si][sj][sk].atomid;
                                        Sphere t = spheres.get(tind);
                                        Point3f tc = t.getLocation();
                                        cp.x=tc.x+ptran.x;
										cp.y=tc.y+ptran.y;
										cp.z=tc.z+ptran.z;
//										cp.x=proseq[tind].x+ptran.x;
//										cp.y=proseq[tind].y+ptran.y;
//										cp.z=proseq[tind].z+ptran.z;
										cp.x*=scalefactor;
										cp.y*=scalefactor;
										cp.z*=scalefactor;
                                        ox= (int) (cp.x+0.5f)-si;
										oy= (int) (cp.y+0.5f)-sj;
										oz= (int) (cp.z+0.5f)-sk;
//										ox=int(cp.x+0.5)-si;
//										oy=int(cp.y+0.5)-sj;
//										oz=int(cp.z+0.5)-sk;
										if(mi*mi+mj*mj+mk*mk<ox*ox+oy*oy+oz*oz)
											vp[si][sj][sk].atomid=indx;
										    vp[si][sj][sk].reference=r;
									}
								//	*/
								}//k
									}//else
							}//if
						}//kk
					}//jj
				}//ii


			}//if
			nind++;
		}//j
	}//i
}

public void fastdistancemap() {
     DistanceMap dm = new DistanceMap();
     dm.fastdistancemap(vp, cutradis, scalefactor);
}

// void ProteinSurface::buildboundary() // used
//{
public void buildbounary() {
	int i,j,k;
	int ii;
	boolean flagbound;
 //   System.out.println(plength + "," + pheight + "," + pwidth);
	for(i=0;i<plength;i++)
	{
		for(j=0;j<pheight;j++)
		{
			for(k=0;k<pwidth;k++)
			{
				if(vp[i][k][j].inout)
				{
					//6 neighbors
//					if(( k-1>-1 && !vp[i][k-1][j].inout) || ( k+1<pwidth &&!vp[i][k+1][j].inout)
//					|| ( j-1>-1 && !vp[i][k][j-1].inout) || ( j+1<pheight &&!vp[i][k][j+1].inout)
//					|| ( i-1>-1 && !vp[i-1][k][j].inout) || ( i+1<plength &&!vp[i+1][k][j].inout))
//						vp[i][k][j].isbound=true;
			//	/*
					//26 neighbors
					flagbound=false;
					ii=0;
					while(!flagbound && ii<26)
					{
						if(i+nb[ii][0]>-1 && i+nb[ii][0]<plength
							&& k+nb[ii][1]>-1 && k+nb[ii][1]<pwidth
							&& j+nb[ii][2]>-1 && j+nb[ii][2]<pheight
							&& !vp[i+nb[ii][0]][k+nb[ii][1]][j+nb[ii][2]].inout)
						{
							vp[i][k][j].isbound=true;
							flagbound=true;
						}
						else ii++;
					}
			//		*/
				}
			}

		}
	}
 
}

//void ProteinSurface::marchingcube(int stype) // used
//{
public void marchingcube(int stype) {
	int i,j,k;
	marchingcubeinit(stype);
    int[][][] vertseq = new int[plength][pwidth][pheight];
//	int ***vertseq;
//	vertseq=new int**[plength];
//	for(i=0;i<plength;i++)
//	{
//		vertseq[i]=new int*[pwidth];
//	}
//	for(i=0;i<plength;i++)
//	{
//		for(j=0;j<pwidth;j++)
//		{
//			vertseq[i][j]=new int[pheight];
//		}
//	}
	for(i=0;i<plength;i++)
	{
		for(j=0;j<pwidth;j++)
		{
			for(k=0;k<pheight;k++)
            {
                vertseq[i][j][k]=-1;
			}
		}
	}

//	if(faces!=NULL)
//	{
//		free(faces);
//	}
//	if(verts!=NULL)
//	{
//		free(verts);
//	}
//	int allocface=20;
//	int allocvert=12;

//	int allocvert=4*(pheight*plength+pwidth*plength+pheight*pwidth);
//	int allocface=2*allocvert;
//     System.out.println("alloc: " + allocvert + " " + allocface);
//	facenumber=0;
//	vertnumber=0;
//	verts=new VertInfo[allocvert];
//    for (int ii = 0; ii < allocvert; ii++) {
//        verts[ii] = new VertInfo();
//    }
//	faces=new FaceInfo[allocface];
//    for (int ii = 0; ii < allocface; ii++) {
//        faces[ii] = new FaceInfo();
//    }

        vertices = new ArrayList<VertInfo>();
        faceList = new ArrayList<FaceInfo>();

	int sumtype;
	int ii,jj,kk;
	int[][] tp = new int[6][3];
//    int tp[6][3];
	/////////////////////////////////////////new added  normal is outer
	//face1
	for(i=0;i<1;i++)
	{
		for(j=0;j<pwidth-1;j++)
		{
			for(k=0;k<pheight-1;k++)
			{
				if(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone
					&& vp[i][j][k+1].isdone)
				{
					tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
					tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
					tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
				    tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
					for(ii=0;ii<4;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);

//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//					faces[facenumber++].b=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
					face.b=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        faceList.add(face);
				}
				else if((vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone)
					||( vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i][j][k+1].isdone)
					||( vp[i][j+1][k+1].isdone && vp[i][j][k+1].isdone && vp[i][j][k].isdone)
					||(vp[i][j][k+1].isdone && vp[i][j][k].isdone && vp[i][j+1][k].isdone))
				{
					if(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
					}
				    else if( vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i][j][k+1].isdone)
					{
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
					}
					else if( vp[i][j+1][k+1].isdone && vp[i][j][k+1].isdone && vp[i][j][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
					}
					else if(vp[i][j][k+1].isdone && vp[i][j][k].isdone && vp[i][j+1][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
					}
					for(ii=0;ii<3;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);
				}

			}
		}
	}
	//face3
	for(i=0;i<plength-1;i++)
	{
		for(j=0;j<1;j++)
		{
			for(k=0;k<pheight-1;k++)
			{
				if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone
					&& vp[i][j][k+1].isdone)
				{
					tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
					tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
					tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
					tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
					for(ii=0;ii<4;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);


//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//					faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
					face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        faceList.add(face);
				}
				else if((vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone)
					||( vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone)
					||( vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone && vp[i][j][k].isdone)
					||(vp[i][j][k+1].isdone && vp[i][j][k].isdone && vp[i+1][j][k].isdone))
				{
					if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
					}
					else if( vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
					}
					else if( vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone && vp[i][j][k].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
					}
					else if(vp[i][j][k+1].isdone && vp[i][j][k].isdone && vp[i+1][j][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
					}
					for(ii=0;ii<3;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);
				}

			}
		}
	}
	//face5
	for(i=0;i<plength-1;i++)
	{
		for(j=0;j<pwidth-1;j++)
		{
			for(k=0;k<1;k++)
			{
				if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone
					&& vp[i][j+1][k].isdone)
				{
					tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
					tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
					tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
					tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
					for(ii=0;ii<4;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);

//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//					faces[facenumber++].b=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
					face.b=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        faceList.add(face);
				}
				else if((vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone)
					||( vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone)
					||( vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i][j][k].isdone)
					||(vp[i][j+1][k].isdone && vp[i][j][k].isdone && vp[i+1][j][k].isdone))
				{
					if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
					}
					else if( vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
					}
					else if( vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i][j][k].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
					}
					else if(vp[i][j+1][k].isdone && vp[i][j][k].isdone && vp[i+1][j][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
					}
					for(ii=0;ii<3;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);
				}

			}
		}
	}
	//face2
	for(i=plength-1;i<plength;i++)
	{
		for(j=0;j<pwidth-1;j++)
		{
			for(k=0;k<pheight-1;k++)
			{
				if(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone
					&& vp[i][j][k+1].isdone)
				{
					tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
					tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
					tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
					tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
					for(ii=0;ii<4;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);

//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//					faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
					face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        faceList.add(face);
				}
				else if((vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone)
					||( vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i][j][k+1].isdone)
					||( vp[i][j+1][k+1].isdone && vp[i][j][k+1].isdone && vp[i][j][k].isdone)
					||(vp[i][j][k+1].isdone && vp[i][j][k].isdone && vp[i][j+1][k].isdone))
				{
					if(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
					}
					else if( vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i][j][k+1].isdone)
					{
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
					}
					else if( vp[i][j+1][k+1].isdone && vp[i][j][k+1].isdone && vp[i][j][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
					}
					else if(vp[i][j][k+1].isdone && vp[i][j][k].isdone && vp[i][j+1][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
					}
					for(ii=0;ii<3;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);
				}

			}
		}
	}
	//face4
	for(i=0;i<plength-1;i++)
	{
		for(j=pwidth-1;j<pwidth;j++)
		{
			for(k=0;k<pheight-1;k++)
			{
				if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone
					&& vp[i][j][k+1].isdone)
				{
					tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
					tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
					tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
					tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
					for(ii=0;ii<4;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);

//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//					faces[facenumber++].b=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
					face.b=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        faceList.add(face);
				}
				else if((vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone)
					||( vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone)
					||( vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone && vp[i][j][k].isdone)
					||(vp[i][j][k+1].isdone && vp[i][j][k].isdone && vp[i+1][j][k].isdone))
				{
					if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
					}
					else if( vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
					}
					else if( vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone && vp[i][j][k].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
					}
					else if(vp[i][j][k+1].isdone && vp[i][j][k].isdone && vp[i+1][j][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
					}
					for(ii=0;ii<3;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);
				}

			}
		}
	}
	//face6
	for(i=0;i<plength-1;i++)
	{
		for(j=0;j<pwidth-1;j++)
		{
			for(k=pheight-1;k<pheight;k++)
			{
				if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone
					&& vp[i][j+1][k].isdone)
				{
					tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
					tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
					tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
					tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
					for(ii=0;ii<4;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);

//                  			faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//					faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
					face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        faceList.add(face);

				}
				else if((vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone)
					||( vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone)
					||( vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i][j][k].isdone)
					||(vp[i][j+1][k].isdone && vp[i][j][k].isdone && vp[i+1][j][k].isdone))
				{
					if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
					}
					else if( vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
					}
					else if( vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i][j][k].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
					}
					else if(vp[i][j+1][k].isdone && vp[i][j][k].isdone && vp[i+1][j][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
					}
					for(ii=0;ii<3;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);
				}

			}
		}
	}

	///////////////////////////////////////////
	for(i=0;i<plength-1;i++)
	{
		for(j=0;j<pwidth-1;j++)
		{
			for(k=0;k<pheight-1;k++)
			{
				sumtype=0;
				for( ii=0;ii<2;ii++)
				{
					for( jj=0;jj<2;jj++)
					{
						for( kk=0;kk<2;kk++)
						{
							if(vp[i+ii][j+jj][k+kk].isdone)
								sumtype++;
						}
					}
				}//ii
//				if(vertnumber+6>allocvert)
//				{
//					allocvert*=2;
//					verts=(vertinfo *)realloc(verts,allocvert*sizeof(vertinfo));
//                    int currentSize = verts.length;
//                    System.arraycopy(verts, 0, verts, 0, allocvert);
//                    for (int cs = currentSize; cs < verts.length; cs++) {
//                        verts[cs] = new VertInfo();
//                    }
                    // do we need to allocate VertInfo objects for the new positions?? Does arraycopy do that?
//				}
//				if(facenumber+3>allocface)
//				{
//					allocface*=2;
//					faces=(faceinfo *)realloc(faces,allocface*sizeof(faceinfo));
//                    int currentSize = faces.length;
//                    System.arraycopy(faces, 0, faces, 0, allocface);
//                    for (int cs = currentSize; cs < faces.length; cs++) {
//                        faces[cs] = new FaceInfo();
//                    }
//				}
				if(sumtype==0)
				{
					//nothing
				}//total0
				else if(sumtype==1)
				{
					//nothing
				}//total1
				else if(sumtype==2)
				{
					//nothing
				}//total2
				else if(sumtype==8)
				{
					//nothing
				}//total8

				else if(sumtype==3)
				{
					if((vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone)
					   ||(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i+1][j+1][k].isdone)
					   ||(vp[i][j+1][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone)
					   ||(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i+1][j][k].isdone)
					   ||(vp[i][j][k+1].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone)
					   ||(vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone)
					   ||(vp[i][j+1][k+1].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone)
					   ||(vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j][k+1].isdone)
					   ||(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone)
					   ||(vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone)
					   ||(vp[i][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone)
					   ||(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i][j][k+1].isdone)
					   ||(vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k+1].isdone)
					   ||(vp[i+1][j+1][k].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone)
					   ||(vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone)
					   ||(vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone)
					   ||(vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone)
					   ||(vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone)
					   ||(vp[i+1][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone)
					   ||(vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i+1][j+1][k+1].isdone)
					   ||(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i][j][k+1].isdone)
					   ||(vp[i][j][k].isdone && vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone)
					   ||(vp[i][j][k+1].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone)
					   ||(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone))
					{
						if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
						}//11
						else if(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
						}//12
						else if(vp[i][j+1][k].isdone && vp[i+1][j][k].isdone&& vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
						}//13
						else if(vp[i][j][k].isdone && vp[i][j+1][k].isdone&& vp[i+1][j][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
						}//14
						else if(vp[i][j][k+1].isdone && vp[i+1][j][k+1].isdone&& vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
						}//21
						else if(vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone&& vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
						}//22
						else if(vp[i][j+1][k+1].isdone && vp[i+1][j][k+1].isdone&& vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
						}//23
						else if(vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone&& vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
						}//24
						else if(vp[i][j][k].isdone && vp[i+1][j][k].isdone&& vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
						}//31
						else if(vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
						}//32
						else if(vp[i][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
						}//33
						else if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
						}//34
						else if(vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
						}//41
						else if(vp[i+1][j+1][k].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
						}//42
						else if(vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
						}//43
						else if(vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone	&& vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
						}//44
						else if(vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone )
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
						}//51
						else if( vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
						}//52
						else if(vp[i+1][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
						}//53
						else if(vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
						}//54
						else if(vp[i][j][k].isdone && vp[i][j+1][k].isdone	&& vp[i][j][k+1].isdone )
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
						}//61
						else if(vp[i][j][k].isdone 	&& vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
						}//62
						else if(vp[i][j][k+1].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
						}//63
						else if(vp[i][j][k].isdone && vp[i][j+1][k].isdone	&& vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
						}//64
						for(ii=0;ii<3;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);
					}//no5 24
				}//total3
				else if(sumtype==4)
				{
					if((vp[i][j][k].isdone && vp[i+1][j][k].isdone
						&& vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone)
						|| (vp[i][j][k+1].isdone && vp[i+1][j][k+1].isdone
						&& vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone)
						|| (vp[i][j][k].isdone && vp[i+1][j][k].isdone
						&& vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone)
						|| (vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone
						&& vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone)
						|| (vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone
						&& vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone)
						|| (vp[i][j][k].isdone && vp[i][j+1][k].isdone
						&& vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone))
					{
						if(vp[i][j][k].isdone && vp[i+1][j][k].isdone
							&& vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;

						}
						else if (vp[i][j][k+1].isdone && vp[i+1][j][k+1].isdone
							&& vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
						}
						else if(vp[i][j][k].isdone && vp[i+1][j][k].isdone
							&& vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
						}
						else if(vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone
							&& vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
						}
						else if(vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone
							&& vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
						}
						else if(vp[i][j][k].isdone && vp[i][j+1][k].isdone
							&& vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
						}
						for(ii=0;ii<4;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);

//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//						faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
						face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                faceList.add(face);
					}//no.8 6

				  else if((vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone  && vp[i][j+1][k+1].isdone)//11
					   ||(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i+1][j+1][k].isdone && vp[i+1][j][k+1].isdone)//12
					   ||(vp[i][j+1][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone && vp[i][j][k+1].isdone)//13
					   ||(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k+1].isdone)//14
					   ||(vp[i][j][k+1].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j+1][k].isdone)//21
					   ||(vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone && vp[i+1][j][k].isdone)//22
					   ||(vp[i][j+1][k+1].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j][k].isdone)//23
					   ||(vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k].isdone)//24
					   ||(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j+1][k+1].isdone)//31
					   ||(vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone && vp[i][j+1][k].isdone)//32
					   ||(vp[i][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone && vp[i+1][j+1][k].isdone)//33
					   ||(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i][j][k+1].isdone && vp[i+1][j+1][k+1].isdone)//34
					   ||(vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j][k+1].isdone)//41
					   ||(vp[i+1][j+1][k].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j][k].isdone)//42
					   ||(vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j+1][k].isdone)//43
					   ||(vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone  && vp[i][j+1][k+1].isdone)//44
					   ||(vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone  && vp[i+1][j][k+1].isdone)//51
					   ||( vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone && vp[i+1][j][k].isdone)//52
					   ||(vp[i+1][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j][k].isdone)//53
					   ||(vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j][k+1].isdone)//54
					   ||(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i][j][k+1].isdone  && vp[i+1][j+1][k+1].isdone)//61
					   ||(vp[i][j][k].isdone && vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k].isdone)//62
					   ||(vp[i][j][k+1].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j][k].isdone)//63
					   ||(vp[i][j][k].isdone && vp[i][j+1][k].isdone&& vp[i][j+1][k+1].isdone && vp[i+1][j][k+1].isdone))
				   {
						if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k].isdone  && vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
						}//11
						else if(vp[i][j][k].isdone && vp[i][j+1][k].isdone && vp[i+1][j+1][k].isdone && vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
						}//12
						else if(vp[i][j+1][k].isdone && vp[i+1][j][k].isdone&& vp[i+1][j+1][k].isdone && vp[i][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
						}//13
						else if(vp[i][j][k].isdone && vp[i][j+1][k].isdone&& vp[i+1][j][k].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
						}//14
						else if(vp[i][j][k+1].isdone && vp[i+1][j][k+1].isdone&& vp[i+1][j+1][k+1].isdone && vp[i][j+1][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
						}//21
						else if(vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone&& vp[i+1][j+1][k+1].isdone && vp[i+1][j][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
						}//22
						else if(vp[i][j+1][k+1].isdone && vp[i+1][j][k+1].isdone&& vp[i+1][j+1][k+1].isdone && vp[i][j][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
						}//23
						else if(vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone&& vp[i+1][j][k+1].isdone && vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
						}//24
						else if(vp[i][j][k].isdone && vp[i+1][j][k].isdone&& vp[i+1][j][k+1].isdone && vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
						}//31
						else if(vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone && vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
						}//32
						else if(vp[i][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone && vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
						}//33
						else if(vp[i][j][k].isdone && vp[i+1][j][k].isdone && vp[i][j][k+1].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
						}//34
						else if(vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
						}//41
						else if(vp[i+1][j+1][k].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
						}//42
						else if(vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j+1][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
						}//43
						else if(vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone	&& vp[i+1][j][k+1].isdone && vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
						}//44
						else if(vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j][k+1].isdone )
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
						}//51
						else if( vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone && vp[i+1][j][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
						}//52
						else if(vp[i+1][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
						}//53
						else if(vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone && vp[i+1][j+1][k+1].isdone && vp[i][j][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
						}//54
						else if(vp[i][j][k].isdone && vp[i][j+1][k].isdone	&& vp[i][j][k+1].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
						}//61
						else if(vp[i][j][k].isdone 	&& vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
						}//62
						else if(vp[i][j][k+1].isdone && vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone && vp[i+1][j][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
						}//63
						else if(vp[i][j][k].isdone && vp[i][j+1][k].isdone	&& vp[i][j+1][k+1].isdone && vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
						}//64
						for(ii=0;ii<3;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);
				   }//no12 24
					else if((vp[i][j][k].isdone && vp[i][j+1][k+1].isdone
						&& vp[i+1][j+1][k].isdone && vp[i][j+1][k].isdone)
						|| (vp[i][j][k].isdone && vp[i+1][j][k].isdone
						&& vp[i+1][j+1][k].isdone && vp[i+1][j][k+1].isdone)
						|| (vp[i][j][k].isdone && vp[i][j][k+1].isdone
						&& vp[i+1][j][k].isdone && vp[i][j+1][k].isdone)
						|| (vp[i][j+1][k].isdone && vp[i+1][j][k].isdone
						&& vp[i+1][j+1][k].isdone && vp[i+1][j+1][k+1].isdone)
						|| (vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone
						&& vp[i+1][j+1][k+1].isdone && vp[i][j+1][k].isdone)
						|| (vp[i][j][k+1].isdone && vp[i+1][j][k].isdone
						&& vp[i+1][j+1][k+1].isdone && vp[i+1][j][k+1].isdone)
						|| (vp[i][j][k].isdone && vp[i][j][k+1].isdone
						&& vp[i+1][j][k+1].isdone && vp[i][j+1][k+1].isdone)
						|| (vp[i][j+1][k+1].isdone && vp[i+1][j][k+1].isdone
						&& vp[i+1][j+1][k].isdone && vp[i+1][j+1][k+1].isdone))
					{
						if(vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone
							&& vp[i][j][k].isdone && vp[i+1][j+1][k].isdone )
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
						}//1
						else if(vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone
							&& vp[i+1][j+1][k].isdone && vp[i][j][k].isdone )
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
						}//2
						else if(vp[i][j][k].isdone && vp[i][j][k+1].isdone
							&& vp[i+1][j][k].isdone && vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
						}//3
						else if(vp[i+1][j+1][k].isdone && vp[i+1][j+1][k+1].isdone
							&& vp[i][j+1][k].isdone && vp[i+1][j][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
						}//4
						else if(vp[i][j+1][k].isdone && vp[i][j+1][k+1].isdone
							&& vp[i+1][j+1][k+1].isdone && vp[i][j][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
						}//5
						else if(vp[i+1][j][k].isdone && vp[i+1][j][k+1].isdone
							&& vp[i+1][j+1][k+1].isdone && vp[i][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
						}//6
						else if(vp[i][j][k].isdone && vp[i][j][k+1].isdone
							&& vp[i+1][j][k+1].isdone && vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
						}//7
						else if(vp[i][j+1][k+1].isdone && vp[i+1][j][k+1].isdone
							&& vp[i+1][j+1][k].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
						}//8
						for(ii=0;ii<3;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);
					}// no.9 8
					else if((vp[i][j][k].isdone && vp[i+1][j][k].isdone
						&& vp[i+1][j+1][k].isdone && vp[i][j][k+1].isdone)
						||(vp[i][j+1][k].isdone && vp[i+1][j][k].isdone
						&& vp[i+1][j+1][k].isdone && vp[i+1][j][k+1].isdone)
						||(vp[i][j+1][k].isdone && vp[i][j][k].isdone
						&& vp[i+1][j+1][k].isdone && vp[i+1][j+1][k+1].isdone)
						||(vp[i][j+1][k].isdone && vp[i][j][k].isdone
						&& vp[i+1][j][k].isdone && vp[i][j+1][k+1].isdone)
						||(vp[i][j+1][k+1].isdone && vp[i][j][k+1].isdone
						&& vp[i+1][j][k+1].isdone && vp[i+1][j][k].isdone)
						||(vp[i+1][j+1][k+1].isdone && vp[i][j][k+1].isdone
						&& vp[i+1][j][k+1].isdone && vp[i+1][j+1][k].isdone)
						||(vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone
						&& vp[i+1][j][k+1].isdone && vp[i][j+1][k].isdone)
						||(vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone
						&& vp[i][j][k+1].isdone && vp[i][j][k].isdone)
						||(vp[i+1][j+1][k].isdone && vp[i][j+1][k+1].isdone
						&& vp[i][j][k+1].isdone && vp[i][j+1][k].isdone)
						||(vp[i+1][j][k+1].isdone && vp[i][j][k].isdone
						&& vp[i][j][k+1].isdone && vp[i][j+1][k].isdone)
						||(vp[i+1][j][k+1].isdone && vp[i][j][k].isdone
						&& vp[i+1][j+1][k+1].isdone && vp[i+1][j][k].isdone)
						||(vp[i][j+1][k+1].isdone && vp[i+1][j+1][k].isdone
						&& vp[i+1][j+1][k+1].isdone && vp[i+1][j][k].isdone))
					{
						if(vp[i][j][k].isdone && vp[i+1][j][k].isdone
							&& vp[i+1][j+1][k].isdone && vp[i][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
						}//1
						else if(vp[i][j+1][k].isdone && vp[i+1][j][k].isdone
							&& vp[i+1][j+1][k].isdone && vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
						}//2
						else if(vp[i][j+1][k].isdone && vp[i][j][k].isdone
							&& vp[i+1][j+1][k].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
						}//3
						else if(vp[i][j+1][k].isdone && vp[i][j][k].isdone
							&& vp[i+1][j][k].isdone && vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
						}//4
						else if(vp[i][j+1][k+1].isdone && vp[i][j][k+1].isdone
							&& vp[i+1][j][k+1].isdone && vp[i+1][j][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
						}//5
						else if(vp[i+1][j+1][k+1].isdone && vp[i][j][k+1].isdone
							&& vp[i+1][j][k+1].isdone && vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
						}//6
						else if(vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone
							&& vp[i+1][j][k+1].isdone && vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
						}//7
						else if(vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone
							&& vp[i][j][k+1].isdone && vp[i][j][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
						}//8
						else if(vp[i+1][j+1][k].isdone && vp[i][j+1][k+1].isdone
							&& vp[i][j][k+1].isdone && vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
						}//9
						else if(vp[i+1][j][k+1].isdone && vp[i][j][k].isdone
							&& vp[i][j][k+1].isdone && vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
						}//10
						else if(vp[i+1][j][k+1].isdone && vp[i][j][k].isdone
							&& vp[i+1][j+1][k+1].isdone && vp[i+1][j][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
						}//11
						else if(vp[i][j+1][k+1].isdone && vp[i+1][j+1][k].isdone
							&& vp[i+1][j+1][k+1].isdone && vp[i+1][j][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
						}//12
						for(ii=0;ii<4;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);

//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//						faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
						face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                faceList.add(face);
					}//no.11 12
					else if((vp[i][j][k].isdone && vp[i+1][j][k].isdone
						&& vp[i][j+1][k].isdone && vp[i+1][j][k+1].isdone)
						||(vp[i][j][k].isdone && vp[i+1][j][k].isdone
						&& vp[i+1][j+1][k].isdone && vp[i+1][j+1][k+1].isdone)
						||(vp[i][j+1][k].isdone && vp[i+1][j][k].isdone
						&& vp[i+1][j+1][k].isdone && vp[i][j+1][k+1].isdone)
						||(vp[i][j+1][k].isdone && vp[i][j][k].isdone
						&& vp[i+1][j+1][k].isdone && vp[i][j][k+1].isdone)
						||(vp[i+1][j+1][k+1].isdone && vp[i][j][k+1].isdone
						&& vp[i+1][j][k+1].isdone && vp[i][j][k].isdone)
						||(vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone
						&& vp[i+1][j][k+1].isdone && vp[i+1][j][k].isdone)
						||(vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone
						&& vp[i][j][k+1].isdone && vp[i+1][j+1][k].isdone)
						||(vp[i+1][j][k+1].isdone && vp[i][j+1][k+1].isdone
						&& vp[i][j][k+1].isdone && vp[i][j+1][k].isdone)
						||(vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone
						&& vp[i][j][k].isdone && vp[i][j+1][k].isdone)
						||(vp[i+1][j][k].isdone && vp[i][j][k].isdone
						&& vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone)
						||(vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone
						&& vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone)
						||(vp[i][j+1][k].isdone && vp[i+1][j+1][k].isdone
						&& vp[i+1][j+1][k+1].isdone && vp[i+1][j][k+1].isdone))
					{
						if(vp[i][j][k].isdone && vp[i+1][j][k].isdone
							&& vp[i][j+1][k].isdone && vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
						}//1
						else if(vp[i][j][k].isdone && vp[i+1][j][k].isdone
							&& vp[i+1][j+1][k].isdone && vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
						}//2
						else if(vp[i][j+1][k].isdone && vp[i+1][j][k].isdone
							&& vp[i+1][j+1][k].isdone && vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
						}//3
						else if(vp[i][j+1][k].isdone && vp[i][j][k].isdone
							&& vp[i+1][j+1][k].isdone && vp[i][j][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
						}//4
						else if(vp[i+1][j+1][k+1].isdone && vp[i][j][k+1].isdone
							&& vp[i+1][j][k+1].isdone && vp[i][j][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
						}//5
						else if(vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone
							&& vp[i+1][j][k+1].isdone && vp[i+1][j][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
						}//6
						else if(vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone
							&& vp[i][j][k+1].isdone && vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
						}//7
						else if(vp[i+1][j][k+1].isdone && vp[i][j+1][k+1].isdone
							&& vp[i][j][k+1].isdone && vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
						}//8
						else if(vp[i+1][j+1][k+1].isdone && vp[i][j+1][k+1].isdone
							&& vp[i][j][k].isdone && vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k;
						}//9
						else if(vp[i+1][j][k].isdone && vp[i][j][k].isdone
							&& vp[i][j][k+1].isdone && vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k;
						}//10
						else if(vp[i+1][j][k+1].isdone && vp[i][j][k+1].isdone
							&& vp[i+1][j+1][k].isdone && vp[i+1][j][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
						}//11
						else if(vp[i][j+1][k].isdone && vp[i+1][j+1][k].isdone
							&& vp[i+1][j+1][k+1].isdone && vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
						}//12
						for(ii=0;ii<4;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);

//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//						faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
						face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                faceList.add(face);
					}//no.14 12
				}//total4
				else if(sumtype==5)
				{
					if((!vp[i+1][j][k].isdone && !vp[i][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
						|| (!vp[i][j+1][k].isdone && !vp[i][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
						|| (!vp[i+1][j+1][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j+1][k+1].isdone)
						|| (!vp[i][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j+1][k+1].isdone)
						|| (!vp[i+1][j][k+1].isdone && !vp[i][j][k].isdone && !vp[i+1][j+1][k].isdone)
						|| (!vp[i][j+1][k+1].isdone && !vp[i][j][k].isdone && !vp[i+1][j+1][k].isdone)
						|| (!vp[i+1][j+1][k+1].isdone && !vp[i+1][j][k].isdone && !vp[i][j+1][k].isdone)
						|| (!vp[i][j][k+1].isdone && !vp[i+1][j][k].isdone && !vp[i][j+1][k].isdone))
					{
						if(!vp[i+1][j][k].isdone && !vp[i][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
						}//1
						else if(!vp[i][j+1][k].isdone && !vp[i][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
						}//2
						else if(!vp[i+1][j+1][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
						}//3
						else if(!vp[i][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
						}//4
						else if(!vp[i+1][j][k+1].isdone && !vp[i][j][k].isdone && !vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
						}//5
						else if(!vp[i][j+1][k+1].isdone && !vp[i][j][k].isdone && !vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
						}//6
						else if(!vp[i+1][j+1][k+1].isdone && !vp[i+1][j][k].isdone && !vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
						}//7
						else if(!vp[i][j][k+1].isdone && !vp[i+1][j][k].isdone && !vp[i][j+1][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
						}//8
						for(ii=0;ii<3;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);

					}//no.7 8
					else if((!vp[i][j][k].isdone && !vp[i+1][j][k].isdone && !vp[i+1][j+1][k].isdone)
				   ||(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone && !vp[i+1][j+1][k].isdone)
				   ||(!vp[i][j+1][k].isdone && !vp[i+1][j][k].isdone && !vp[i+1][j+1][k].isdone)
				   ||(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone && !vp[i+1][j][k].isdone)
				   ||(!vp[i][j][k+1].isdone && !vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
				   ||(!vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
				   ||(!vp[i][j+1][k+1].isdone && !vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
				   ||(!vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j][k+1].isdone)
				   ||(!vp[i][j][k].isdone && !vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone)
				   ||(!vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j][k+1].isdone)
				   ||(!vp[i][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j][k+1].isdone)
				   ||(!vp[i][j][k].isdone && !vp[i+1][j][k].isdone && !vp[i][j][k+1].isdone)
				   ||(!vp[i+1][j+1][k].isdone && !vp[i+1][j][k].isdone && !vp[i+1][j+1][k+1].isdone)
				   ||(!vp[i+1][j+1][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
				   ||(!vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
				   ||(!vp[i+1][j+1][k].isdone && !vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone )
				   ||(!vp[i+1][j+1][k].isdone && !vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone )
				   ||(!vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
				   ||(!vp[i+1][j+1][k].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
				   ||(!vp[i+1][j+1][k].isdone && !vp[i][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone)
				   ||(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone && !vp[i][j][k+1].isdone )
				   ||(!vp[i][j][k].isdone && !vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone)
				   ||(!vp[i][j][k+1].isdone && !vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone)
				   ||(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone))
				{
					if(!vp[i][j][k].isdone && !vp[i+1][j][k].isdone && !vp[i+1][j+1][k].isdone)
					{
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
						tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
						tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
					}//11
					else if(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone && !vp[i+1][j+1][k].isdone)
					{
						tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
						tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k;
					}//12
					else if(!vp[i][j+1][k].isdone && !vp[i+1][j][k].isdone&& !vp[i+1][j+1][k].isdone)
					{
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
						tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
						tp[3][0]=i;tp[3][1]=j;tp[3][2]=k;
					}//13
					else if(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone&& !vp[i+1][j][k].isdone)
					{
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
						tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
					}//14
					else if(!vp[i][j][k+1].isdone && !vp[i+1][j][k+1].isdone&& !vp[i+1][j+1][k+1].isdone)
					{
						tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
						tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
					}//21
					else if(!vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone&& !vp[i+1][j+1][k+1].isdone)
					{
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
						tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
						tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
					}//22
					else if(!vp[i][j+1][k+1].isdone && !vp[i+1][j][k+1].isdone&& !vp[i+1][j+1][k+1].isdone)
					{
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
						tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
						tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
					}//23
					else if(!vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone&& !vp[i+1][j][k+1].isdone)
					{
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
						tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
					}//24
					else if(!vp[i][j][k].isdone && !vp[i+1][j][k].isdone&& !vp[i+1][j][k+1].isdone)
					{
						tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
						tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
						tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
					}//31
					else if(!vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j][k+1].isdone)
					{
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
						tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
						tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
						tp[3][0]=i;tp[3][1]=j;tp[3][2]=k;
					}//32
					else if(!vp[i][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j][k+1].isdone)
					{
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
						tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
						tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k;
					}//33
					else if(!vp[i][j][k].isdone && !vp[i+1][j][k].isdone && !vp[i][j][k+1].isdone)
					{
						tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
						tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
					}//34
					else if(!vp[i+1][j+1][k].isdone && !vp[i+1][j][k].isdone && !vp[i+1][j+1][k+1].isdone)
					{
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
						tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
					}//41
					else if(!vp[i+1][j+1][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
					{
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
						tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k;
					}//42
					else if(!vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
					{
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
						tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
					}//43
					else if(!vp[i+1][j+1][k].isdone && !vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone)
					{
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
						tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
					}//44
					else if(!vp[i+1][j+1][k].isdone && !vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone )
					{
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
						tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
					}//51
					else if( !vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
					{
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
						tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
					}//52
					else if(!vp[i+1][j+1][k].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
					{
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
						tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
					}//53
					else if(!vp[i+1][j+1][k].isdone && !vp[i][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone)
					{
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
						tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
					}//54
					else if(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone && !vp[i][j][k+1].isdone )
					{
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
						tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
						tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
					}//61
					else if(!vp[i][j][k].isdone && !vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone)
					{
						tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
						tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
					}//62
					else if(!vp[i][j][k+1].isdone && !vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone)
					{
						tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
						tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
						tp[3][0]=i;tp[3][1]=j;tp[3][2]=k;
					}//63
					else if(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone)
					{
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
						tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
						tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
						tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
					}//64
					for(ii=0;ii<4;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);

//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//					faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
					face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                        faceList.add(face);
				}//no5 24
					else if((!vp[i][j][k].isdone && !vp[i+1][j][k].isdone && !vp[i+1][j+1][k+1].isdone)//1
						||(!vp[i][j+1][k].isdone && !vp[i+1][j+1][k].isdone && !vp[i][j][k+1].isdone)//2
						||(!vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i+1][j][k].isdone)//3
						||(!vp[i][j][k+1].isdone && !vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k].isdone)//4
						||(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone)//5
						||(!vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i][j+1][k].isdone)//6
						||(!vp[i+1][j][k].isdone && !vp[i+1][j+1][k].isdone && !vp[i][j+1][k+1].isdone)//7
						||(!vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k].isdone)//8
						||(!vp[i][j][k].isdone && !vp[i][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)//9
						||(!vp[i+1][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i][j][k].isdone)//10
						||(!vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j+1][k+1].isdone)//11
						||(!vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j][k+1].isdone))
					{
						if(!vp[i][j][k].isdone && !vp[i+1][j][k].isdone && !vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
							tp[4][0]=i;tp[4][1]=j+1;tp[4][2]=k+1;
						}//1
						else if(!vp[i][j+1][k].isdone && !vp[i+1][j+1][k].isdone && !vp[i][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i+1;tp[4][1]=j;tp[4][2]=k+1;
						}//2
						else if(!vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i+1][j][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
							tp[4][0]=i;tp[4][1]=j;tp[4][2]=k;
						}//3
						else if(!vp[i][j][k+1].isdone && !vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i;tp[4][1]=j+1;tp[4][2]=k;
						}//4
						else if(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone)
						{
						    //tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							//tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							//tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							//tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[4][0]=i+1;tp[4][1]=j;tp[4][2]=k+1;
						}//5
						else if(!vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i;tp[4][1]=j;tp[4][2]=k;
						}//6
						else if(!vp[i+1][j][k].isdone && !vp[i+1][j+1][k].isdone && !vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i;tp[4][1]=j;tp[4][2]=k+1;
						}//7
						else if(!vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
							tp[4][0]=i+1;tp[4][1]=j;tp[4][2]=k;
						}//8
						else if(!vp[i][j][k].isdone && !vp[i][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i+1;tp[4][1]=j+1;tp[4][2]=k;
						}//9
						else if(!vp[i+1][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i][j][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
							tp[4][0]=i;tp[4][1]=j;tp[4][2]=k+1;
						}//10
						else if(!vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
							tp[4][0]=i;tp[4][1]=j+1;tp[4][2]=k;
						}//11
						else if(!vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i+1;tp[4][1]=j;tp[4][2]=k;
						}//12
						for(ii=0;ii<5;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);

//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//						faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
						face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                faceList.add(face);

//						faces[facenumber].a=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//						faces[facenumber].b=vertseq[tp[4][0]][tp[4][1]][tp[4][2]];
//						faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                face = new FaceInfo();
                                                face.a=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
						face.b=vertseq[tp[4][0]][tp[4][1]][tp[4][2]];
						face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                faceList.add(face);

					}//no.6 12-1
					else if((!vp[i][j][k].isdone && !vp[i+1][j][k].isdone && !vp[i][j+1][k+1].isdone)//1
						||(!vp[i][j+1][k].isdone && !vp[i+1][j+1][k].isdone && !vp[i+1][j][k+1].isdone)//2
						||(!vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i][j][k].isdone)//3
						||(!vp[i][j][k+1].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j+1][k].isdone)//4
						||(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone && !vp[i+1][j][k+1].isdone)//5
						||(!vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i][j][k].isdone)//6
						||(!vp[i+1][j][k].isdone && !vp[i+1][j+1][k].isdone && !vp[i][j][k+1].isdone)//7
						||(!vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j][k].isdone)//8
						||(!vp[i][j][k].isdone && !vp[i][j][k+1].isdone && !vp[i+1][j+1][k].isdone)//9
						||(!vp[i+1][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i][j][k+1].isdone)//10
						||(!vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j+1][k].isdone)//11
						||(!vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j][k].isdone))
					{
						if(!vp[i][j][k].isdone && !vp[i+1][j][k].isdone && !vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
							tp[4][0]=i+1;tp[4][1]=j+1;tp[4][2]=k+1;
						}//1
						else if(!vp[i][j+1][k].isdone && !vp[i+1][j+1][k].isdone && !vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i;tp[4][1]=j;tp[4][2]=k+1;
						}//2
						else if(!vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i][j][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
							tp[4][0]=i+1;tp[4][1]=j;tp[4][2]=k;
						}//3
						else if(!vp[i][j][k+1].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i+1;tp[4][1]=j+1;tp[4][2]=k;
						}//4
						else if(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone && !vp[i+1][j][k+1].isdone)
						{
							//tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							//tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							//tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							//tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[4][0]=i+1;tp[4][1]=j+1;tp[4][2]=k+1;
						}//5
						else if(!vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i][j][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i;tp[4][1]=j+1;tp[4][2]=k;
						}//6
						else if(!vp[i+1][j][k].isdone && !vp[i+1][j+1][k].isdone && !vp[i][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i;tp[4][1]=j+1;tp[4][2]=k+1;
						}//7
						else if(!vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
							tp[4][0]=i+1;tp[4][1]=j+1;tp[4][2]=k;
						}//8
						else if(!vp[i][j][k].isdone && !vp[i][j][k+1].isdone && !vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i+1;tp[4][1]=j+1;tp[4][2]=k+1;
						}//9
						else if(!vp[i+1][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone && !vp[i][j][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
							tp[4][0]=i;tp[4][1]=j;tp[4][2]=k;
						}//10
						else if(!vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone && !vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
							tp[4][0]=i;tp[4][1]=j+1;tp[4][2]=k+1;
						}//11
						else if(!vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone && !vp[i+1][j][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i+1;tp[4][1]=j;tp[4][2]=k+1;
						}//12
						for(ii=0;ii<5;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);

//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//						faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
						face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                faceList.add(face);

//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[4][0]][tp[4][1]][tp[4][2]];
//						faces[facenumber++].c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
                                                face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[4][0]][tp[4][1]][tp[4][2]];
						face.c=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
                                                faceList.add(face);

					}//no.6 12-2

				}//total5

				else if(sumtype==6)
				{
					if((!vp[i][j][k].isdone && !vp[i+1][j][k].isdone)
						||(!vp[i][j+1][k].isdone && !vp[i+1][j+1][k].isdone)
						||(!vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
						||(!vp[i][j][k+1].isdone && !vp[i+1][j][k+1].isdone)
						||(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone)
						||(!vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
						||(!vp[i+1][j][k].isdone && !vp[i+1][j+1][k].isdone)
						||(!vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone)
						||(!vp[i][j][k].isdone && !vp[i][j][k+1].isdone)
						||(!vp[i+1][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone)
						||(!vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone)
						||(!vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone))
					{
						if(!vp[i][j][k].isdone && !vp[i+1][j][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
						}//1
						else if(!vp[i][j+1][k].isdone && !vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
						}//2
						else if(!vp[i][j+1][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
						}//3
						else if(!vp[i][j][k+1].isdone && !vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
						}//4
						else if(!vp[i][j][k].isdone && !vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k;
						}//5
						else if(!vp[i+1][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
						}//6
						else if(!vp[i+1][j][k].isdone && !vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
						}//7
						else if(!vp[i][j][k+1].isdone && !vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
						}//8
						else if(!vp[i][j][k].isdone && !vp[i][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k;
						}//9
						else if(!vp[i+1][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
						}//10
						else if(!vp[i+1][j][k].isdone && !vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
						}//11
						else if(!vp[i][j+1][k].isdone && !vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
						}//12
						for(ii=0;ii<4;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);

//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//						faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
						face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                faceList.add(face);

					}//no.2 12

					else if((!vp[i][j][k].isdone && !vp[i+1][j+1][k+1].isdone)
						||(!vp[i+1][j][k].isdone && !vp[i][j+1][k+1].isdone)
						||(!vp[i][j+1][k].isdone && !vp[i+1][j][k+1].isdone)
						||(!vp[i+1][j+1][k].isdone && !vp[i][j][k+1].isdone))
					{
						if(!vp[i][j][k].isdone && !vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
							tp[4][0]=i;tp[4][1]=j+1;tp[4][2]=k;
							tp[5][0]=i+1;tp[5][1]=j;tp[5][2]=k;
						}//1
						else if(!vp[i+1][j][k].isdone && !vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
							tp[4][0]=i;tp[4][1]=j;tp[4][2]=k;
							tp[5][0]=i+1;tp[5][1]=j+1;tp[5][2]=k;
						}//2
						else if(!vp[i][j+1][k].isdone && !vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i+1;tp[4][1]=j+1;tp[4][2]=k;
							tp[5][0]=i;tp[5][1]=j;tp[5][2]=k;
						}//3
						else if(!vp[i+1][j+1][k].isdone && !vp[i][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
							tp[4][0]=i+1;tp[4][1]=j;tp[4][2]=k;
							tp[5][0]=i;tp[5][1]=j+1;tp[5][2]=k;
						}//4
						for(ii=0;ii<6;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);

//						faces[facenumber].a=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
//						faces[facenumber].b=vertseq[tp[4][0]][tp[4][1]][tp[4][2]];
//						faces[facenumber++].c=vertseq[tp[5][0]][tp[5][1]][tp[5][2]];
                                                face = new FaceInfo();
                                                face.a=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
						face.b=vertseq[tp[4][0]][tp[4][1]][tp[4][2]];
						face.c=vertseq[tp[5][0]][tp[5][1]][tp[5][2]];
                                                faceList.add(face);
					}//no.4 4

					else if((!vp[i][j][k].isdone && !vp[i+1][j][k+1].isdone)
						||(!vp[i+1][j][k].isdone && !vp[i][j][k+1].isdone)
						||(!vp[i+1][j][k].isdone && !vp[i+1][j+1][k+1].isdone)
						||(!vp[i+1][j+1][k].isdone && !vp[i+1][j][k+1].isdone)
						||(!vp[i+1][j+1][k].isdone && !vp[i][j+1][k+1].isdone)
						||(!vp[i][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone)
						||(!vp[i][j+1][k].isdone && !vp[i][j][k+1].isdone)
						||(!vp[i][j][k].isdone && !vp[i][j+1][k+1].isdone)
						||(!vp[i][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
						||(!vp[i+1][j][k+1].isdone && !vp[i][j+1][k+1].isdone)
						||(!vp[i][j][k].isdone && !vp[i+1][j+1][k].isdone)
						||(!vp[i+1][j][k].isdone && !vp[i][j+1][k].isdone))
					{
						if(!vp[i][j][k].isdone && !vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k+1;
						}//1
						else if(!vp[i+1][j][k].isdone && !vp[i][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
						}//2
						else if(!vp[i+1][j][k].isdone && !vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k+1;
						}//3
						else if(!vp[i+1][j+1][k].isdone && !vp[i+1][j][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
						}//4
						else if(!vp[i+1][j+1][k].isdone && !vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
						}//5
						else if(!vp[i][j+1][k].isdone && !vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k;
						}//6
						else if(!vp[i][j+1][k].isdone && !vp[i][j][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
						}//7
						else if(!vp[i][j][k].isdone && !vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k;
						}//8
						else if(!vp[i][j][k+1].isdone && !vp[i+1][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
							tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
							tp[3][0]=i+1;tp[3][1]=j+1;tp[3][2]=k;
						}//9
						else if(!vp[i+1][j][k+1].isdone && !vp[i][j+1][k+1].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
							tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
							tp[3][0]=i;tp[3][1]=j+1;tp[3][2]=k;
						}//10
						else if(!vp[i][j][k].isdone && !vp[i+1][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
							tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
							tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[3][0]=i;tp[3][1]=j;tp[3][2]=k+1;
						}//11
						else if(!vp[i+1][j][k].isdone && !vp[i][j+1][k].isdone)
						{
							tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
							tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
							tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
							tp[3][0]=i+1;tp[3][1]=j;tp[3][2]=k+1;
						}//12
						for(ii=0;ii<4;ii++)
						{
							if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
							{
								vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//								verts[vertnumber].p.x=tp[ii][0];
//								verts[vertnumber].p.y=tp[ii][1];
//								verts[vertnumber].p.z=tp[ii][2];
                                                                VertInfo vertex = new VertInfo();
                                                                vertex.p.x=tp[ii][0];
							        vertex.p.y=tp[ii][1];
							        vertex.p.z=tp[ii][2];
                                                                vertices.add(vertex);
//								vertnumber++;
							}
						}
//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//						faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                FaceInfo face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
						face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                                faceList.add(face);

//						faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//						faces[facenumber].b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
//						faces[facenumber++].c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                face = new FaceInfo();
                                                face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
						face.b=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
						face.c=vertseq[tp[3][0]][tp[3][1]][tp[3][2]];
                                                faceList.add(face);
					}//no.3 12

				}//total6

				else if(sumtype==7)
				{
					if(!vp[i][j][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k+1;
					}//1
					else if(!vp[i+1][j][k].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k;
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k+1;
					}//2
					else if(!vp[i+1][j+1][k].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k;
						tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k+1;
					}//3
					else if(!vp[i][j+1][k].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k;
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k+1;
					}//4
					else if(!vp[i][j][k+1].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j;tp[0][2]=k+1;
						tp[1][0]=i;tp[1][1]=j+1;tp[1][2]=k+1;
						tp[2][0]=i;tp[2][1]=j;tp[2][2]=k;
					}//5
					else if(!vp[i+1][j][k+1].isdone)
					{
						tp[0][0]=i+1;tp[0][1]=j+1;tp[0][2]=k+1;
						tp[1][0]=i;tp[1][1]=j;tp[1][2]=k+1;
						tp[2][0]=i+1;tp[2][1]=j;tp[2][2]=k;
					}//6
					else if(!vp[i+1][j+1][k+1].isdone)
					{
						tp[0][0]=i;tp[0][1]=j+1;tp[0][2]=k+1;
						tp[1][0]=i+1;tp[1][1]=j;tp[1][2]=k+1;
						tp[2][0]=i+1;tp[2][1]=j+1;tp[2][2]=k;
					}//7
					else if(!vp[i][j+1][k+1].isdone)
					{
						tp[0][0]=i;tp[0][1]=j;tp[0][2]=k+1;
						tp[1][0]=i+1;tp[1][1]=j+1;tp[1][2]=k+1;
						tp[2][0]=i;tp[2][1]=j+1;tp[2][2]=k;
					}//8
					for(ii=0;ii<3;ii++)
					{
						if(vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]==-1)
						{
							vertseq[tp[ii][0]][tp[ii][1]][tp[ii][2]]=vertices.size();
//							verts[vertnumber].p.x=tp[ii][0];
//							verts[vertnumber].p.y=tp[ii][1];
//							verts[vertnumber].p.z=tp[ii][2];
                                                        VertInfo vertex = new VertInfo();
                                                        vertex.p.x=tp[ii][0];
							vertex.p.y=tp[ii][1];
							vertex.p.z=tp[ii][2];
                                                        vertices.add(vertex);
//							vertnumber++;
						}
					}
//					faces[facenumber].a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
//					faces[facenumber].b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
//					faces[facenumber++].c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        FaceInfo face = new FaceInfo();
                                        face.a=vertseq[tp[0][0]][tp[0][1]][tp[0][2]];
					face.b=vertseq[tp[1][0]][tp[1][1]][tp[1][2]];
					face.c=vertseq[tp[2][0]][tp[2][1]][tp[2][2]];
                                        faceList.add(face);
				}//total7

			}//every ijk
		}//j
	}//i
////	verts=(vertinfo *)realloc(verts,vertnumber*sizeof(vertinfo));
////    int currentSize = verts.length;
////    System.arraycopy(verts, 0, verts, 0, vertnumber);
////    for (int cs = currentSize; cs < verts.length; cs++) {
////        verts[cs] = new VertInfo();
////    }
////	faces=(faceinfo *)realloc(faces,facenumber*sizeof(faceinfo));

//    int currentSize = faces.length;
//    System.arraycopy(faces, 0, faces, 0, facenumber);
//    for (int cs = currentSize; cs < faces.length; cs++) {
//        faces[cs] = new FaceInfo();
//    }

	for(i=0;i<vertices.size();i++)
	{
//		verts[i].atomid=vp[int(verts[i].x)][int(verts[i].y)][int(verts[i].z)].atomid;
//        verts[i].atomid=vp[(int)(verts[i].p.x)][(int)(verts[i].p.y)][(int)(verts[i].p.z)].atomid;
        VertInfo vertex = vertices.get(i);
        vertex.atomid = vp[(int)(vertex.p.x)][(int)(vertex.p.y)][(int)(vertex.p.z)].atomid;
        vertex.reference = vp[(int)(vertex.p.x)][(int)(vertex.p.y)][(int)(vertex.p.z)].reference;


//		verts[i].iscont=false;
                vertex.iscont = false;
//        if(vp[int(verts[i].x)][int(verts[i].y)][int(verts[i].z)].isbound)
//		if(vp[(int)(verts[i].p.x)][(int)(verts[i].p.y)][(int)(verts[i].p.z)].isbound)
//			verts[i].iscont=true;
                if(vp[(int)(vertex.p.x)][(int)(vertex.p.y)][(int)(vertex.p.z)].isbound)
			vertex.iscont=true;
	}
    vertseq = null;
    complete();
    
    // resize arrays
//    truncateArrays();
//	for(i=0;i<plength;i++)
//	{
//		for(j=0;j<pwidth;j++)
//		{
//			delete[]vertseq[i][j];
//		}
//	}

//	for(i=0;i<plength;i++)
//	{
//		delete[]vertseq[i];
//	}
//	delete[]vertseq;
}
//*/

// void ProteinSurface::marchingcubeinit(int stype) // used internally
// {
private void marchingcubeinit(int stype) {
	int i,j,k;
	//vdw
	if(stype==1)
	{
		for(i=0;i<plength;i++)
		{
			for(j=0;j<pwidth;j++)
			{
				for(k=0;k<pheight;k++)
				{
					vp[i][j][k].isbound=false;
				}
			}
		}

	}
	//ses
	else if(stype==4)
	{
		///////////////without vdw
		for(i=0;i<plength;i++)
		{
			for(j=0;j<pwidth;j++)
			{
				for(k=0;k<pheight;k++)
				{
					vp[i][j][k].isdone=false;
					if(vp[i][j][k].isbound)
					{
						vp[i][j][k].isdone=true;
					}
					//new add
					vp[i][j][k].isbound=false;
				}
			}
		}

	}
	else if(stype==2)
	{
		///////////////////////after vdw
		for(i=0;i<plength;i++)
		{
			for(j=0;j<pwidth;j++)
			{
				for(k=0;k<pheight;k++)
				{
				//	if(vp[i][j][k].inout && vp[i][j][k].distance>=cutradis)
					if(vp[i][j][k].isbound && vp[i][j][k].isdone)
					{
						vp[i][j][k].isbound=false;
					}
					else if(vp[i][j][k].isbound && !vp[i][j][k].isdone)
					{
						vp[i][j][k].isdone=true;
					}
				}
			}
		}

	}
	//sas
	else if(stype==3)
	{
		for(i=0;i<plength;i++)
		{
			for(j=0;j<pwidth;j++)
			{
				for(k=0;k<pheight;k++)
				{
					vp[i][j][k].isbound=false;
				}
			}
		}
	}

}

private void complete() {
    surface.setVertices(vertices);
    surface.setFaces(faceList);
    //
    surface.computenorm();
    // In EDT surface area calculation the surface area of the raw surface is
    // reported!
//    surface.laplaciansmooth(1);
//    System.out.println("scalefactor, ptran" + scalefactor + " " + ptran);
    //
    float sinv = 1.0f/scalefactor;
    for (VertInfo v: vertices) {
       v.p.scale(sinv);
       v.p.sub(ptran);
    }
    for (FaceInfo f: faceList) {
       if(f.inout) {
           int temp = f.b;
           f.b = f.c;
           f.c = temp;
       }
    }
 //   System.out.println("complete");
//    outputply("c://1STP.ply");
}

//void calcareavolume() // used, optional?
//{
//	int i,j,k;
//	double totvol=0.10*scalefactor*vertices.size();
//	for(i=0;i<plength;i++)
//	{
//		for(j=0;j<pwidth;j++)
//		{
//			for(k=0;k<pheight;k++)
//			{
//				if(vp[i][j][k].isdone)
//					totvol+=1;
//			}
//		}
//	}
//	double totarea=0;
//	for(i=0;i<facenumber;i++)
//	{
//		totarea+=faces[i].area;
//	}
//
//	sarea=totarea/scalefactor/scalefactor;
//	svolume=(totvol)/scalefactor/scalefactor/scalefactor;
//
//}
// void ProteinSurface::outputply(char *filename,atom* proseq,int ncolor) // used, optional
public void outputply(String fileName) // used, optional
{
	int i;
//	unsigned char chaincolor[256];
//	int tchain,indcolor2=1;

//	for(i=0;i<256;i++)
//	{
//		chaincolor[i]=0;
//	}
//	FILE *file;
//	file=fopen(filename,"wt");
//	if(file==NULL)
//	{
//		printf("wrong to output ply file %s\n",filename);
//		return;
//	}
    PrintWriter writer = null;
    try {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
    } catch (IOException ex) {
        Logger.getLogger(EdtSurfaceCalculator.class.getName()).log(Level.SEVERE, null, ex);
    }
	writer.println("ply");
	writer.println("format ascii 1.0");
	writer.println("comment ball mesh");
	writer.println("element vertex " + vertices.size());
	writer.println("property float x");
	writer.println("property float y");
	writer.println("property float z");
        writer.println("property float nx");
	writer.println("property float ny");
	writer.println("property float nz");
	writer.println("element face " + faceList.size());
	writer.println("property list uchar int vertex_indices" );
	writer.println("end_header" );

    for(i=0;i<vertices.size();i++)
	{
        VertInfo v = vertices.get(i);
//		writer.printf("%.3f %.3f %.3f %.3f %.3f %.3f\n", v.p.x/scalefactor-ptran.x,v.p.y/scalefactor-ptran.y,
//			v.p.z/scalefactor-ptran.z, v.normal.x, v.normal.y, v.normal.z);
                writer.printf("%.3f %.3f %.3f %.3f %.3f %.3f\n", v.p.x,v.p.y, v.p.z, v.normal.x, v.normal.y, v.normal.z);
//                System.out.println("atomid: " + verts[i].atomid + ", inout: " + verts[i].inout + ", iscont" + verts[i].iscont);
	}
	for(i=0;i<faceList.size();i++)
	{
            FaceInfo face = faceList.get(i);
		if(!face.inout)//outer
			writer.printf("3 %d %d %d\n", face.a,face.b,face.c);
		else
			writer.printf("3 %d %d %d\n", face.a,face.c,face.b);
	}
	writer.flush();
    writer.close();
}
}
