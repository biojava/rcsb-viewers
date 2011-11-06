/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rcsb.mbt.surface.core;

import javax.vecmath.Point3i;

import org.rcsb.mbt.surface.datastructure.VolumePixel;


/**
 *
 * @author Peter Rose
 */
public class DistanceMap {
     // nb[26][3]
    private static int nb[][]={{1,0,0}, {-1,0,0}, {0,1,0}, {0,-1,0}, {0,0,1}, {0,0,-1},
    {1,1,0}, {1,-1,0}, {-1,1,0}, {-1,-1,0}, {1,0,1}, {1,0,-1}, {-1,0,1}, {-1,0,-1}, {0,1,1}, {0,1,-1}, {0,-1,1}, {0,-1,-1},
    {1,1,1}, {1,1,-1}, {1,-1,1}, {-1,1,1}, {1,-1,-1}, {-1,-1,1}, {-1,1,-1}, {-1,-1,-1}};

    int plength;
    int pwidth;
    int pheight;
    VolumePixel[][][] vp;
    int positin;
    int allocout;
    int positout;
    int eliminate;
    int totalinnervox;

    public void fastdistancemap(VolumePixel[][][] vp, float cutradis, float scalefactor) // used for MS and SES surfaces
{
        this.vp = vp;
        this.plength = vp.length;
        this.pwidth = vp[0].length;
        this.pheight = vp[0][0].length;
  //      System.out.println("[" + plength + "*" + pwidth + "*" + pheight +"]");

	int i,j,k;
//	int positin,positout;
//        int eliminate = 0;
        eliminate = 0;

	int certificate;
	int totalsurfacevox=0;
//	int totalinnervox=0;
        totalinnervox=0;
//	voxel2 ***boundpoint;
//	boundpoint=new voxel2 **[plength];
//	for(i=0;i<plength;i++)
//	{
//		boundpoint[i]=new voxel2*[pwidth];
//	}
//	for(i=0;i<plength;i++)
//	{
//		for(j=0;j<pwidth;j++)
//		{
//			boundpoint[i][j]=new voxel2[pheight];
//		}
//	}
    Point3i[][][] boundpoint = new Point3i[plength][pwidth][pheight];
    for (i = 0; i < plength; i++) {
        for (j = 0; j < pwidth; j++) {
            for (k = 0; k < pheight; k++) {
                boundpoint[i][j][k] = new Point3i();
            }
        }
    }


	for(i=0;i<plength;i++)
	{
		for(j=0;j<pwidth;j++)
		{
			for(k=0;k<pheight;k++)
			{
				vp[i][j][k].isdone=false;
				if(vp[i][j][k].inout)
				{
					if(vp[i][j][k].isbound)
					{
						totalsurfacevox++;
						boundpoint[i][j][k].x=i;
						boundpoint[i][j][k].y=j;
						boundpoint[i][j][k].z=k;
						vp[i][j][k].distance=0;
						vp[i][j][k].isdone=true;
					}
				    else
					{
						totalinnervox++;
					}
				}
			}
		}
	}
	int allocin= (int)(1.2*totalsurfacevox);
//	int allocout= (int)(1.2*totalsurfacevox);
        allocout= (int)(1.2*totalsurfacevox);
	if(allocin>totalinnervox)
		allocin=totalinnervox;
	if(allocin<totalsurfacevox)
		allocin=totalsurfacevox;
	if(allocout>totalinnervox)
		allocout=totalinnervox;
	 Point3i[] inarray=new Point3i[allocin];
         for (int ii = 0; ii < inarray.length; ii++) {
             inarray[ii] = new Point3i();
         }
	 Point3i[] outarray=new Point3i[allocout];
         for (int ii = 0; ii < outarray.length; ii++) {
             outarray[ii] = new Point3i();
         }
	 positin=0;positout=0;

	 for(i=0;i<plength;i++)
	 {
		 for(j=0;j<pwidth;j++)
		 {
			 for(k=0;k<pheight;k++)
			 {
				 if(vp[i][j][k].isbound)
				 {
					 inarray[positin].x=i;
					 inarray[positin].y=j;
					 inarray[positin].z=k;
					 positin++;
					 vp[i][j][k].isbound=false;//as flag of outarray
				 }
			 }
		 }
	 }
	certificate=totalinnervox;
///////////////////////////////////////////////////

	do {
            fastoneshell(boundpoint, inarray, outarray);
	//	printf("%d %d %d %d %d\n",positin,allocout,positout,totalsurfacevox,totalinnervox);
		certificate-=eliminate;
	/*
		for(i=0;i<positout;i++)
			{
			  inarray[i].ix=outarray[i].ix;
			  inarray[i].iy=outarray[i].iy;
			  inarray[i].iz=outarray[i].iz;
			}
			positin=positout;*/
		//new code only less dist
		positin=0;
		for(i=0;i<positout;i++)
		{
			vp[outarray[i].x][outarray[i].y][outarray[i].z].isbound=false;
			if(vp[outarray[i].x][outarray[i].y][outarray[i].z].distance<=1.02*cutradis)
			{
				inarray[positin].x=outarray[i].x;
				inarray[positin].y=outarray[i].y;
				inarray[positin].z=outarray[i].z;
				positin++;
			}
			if(positin>=allocin)
			{
				int inLength = allocin;
                                allocin*=2;
				if(allocin>totalinnervox) allocin=totalinnervox;
//				inarray=(voxel2 *)realloc(inarray,allocin*sizeof(voxel2));
                                System.arraycopy(inarray, 0, inarray, positout, allocin);
                                for (int ii = inLength; ii < allocin; ii++) {
                                    inarray[ii] = new Point3i();
                                }
			}
		}
	}
	while(positin!=0);
	//while(positout!=0);
	if(certificate!=0)
	{
	//	printf("wrong number\n");
	}

//	 free(inarray);
//	 free(outarray);

	 double cutsf=scalefactor-0.5;
	 if(cutsf<0) cutsf=0;
//	 cutsf=100000000;
	 for(i=0;i<plength;i++)
	 {
		 for(j=0;j<pwidth;j++)
		 {
			 for(k=0;k<pheight;k++)
			 {
				 vp[i][j][k].isbound=false;
				 //ses solid
				 if(vp[i][j][k].inout)
				 {
					 if(!vp[i][j][k].isdone
						 || (vp[i][j][k].isdone && vp[i][j][k].distance>=cutradis-0.50/(0.1+cutsf))//0.33  0.75/scalefactor
						 )
					 {
						 vp[i][j][k].isbound=true;
						 //new add
						 if(vp[i][j][k].isdone)
							vp[i][j][k].atomid=vp[boundpoint[i][j][k].x][boundpoint[i][j][k].y][boundpoint[i][j][k].z].atomid;
					 }
				 }
			 }
		 }
	 }


//	 for(i=0;i<plength;i++)
//	 {
//		 for(j=0;j<pwidth;j++)
//		 {
//			 delete[]boundpoint[i][j];
//		 }
//	 }
//
//	 for(i=0;i<plength;i++)
//	 {
//		 delete[]boundpoint[i];
//	 }
//	 delete[] boundpoint;
}

private void fastoneshell(Point3i[][][] boundpoint, Point3i[] inarray, Point3i[] outarray) // used for MS and SES surfaces
{
//	int i, number,positout;
        int i, number;
	int tx,ty,tz;
	int dx,dy,dz;
//	int eliminate=0;
        eliminate=0;
	float squre;
	positout=0;
//	number=*innum;
        number = positin;
	if(number==0) return;
	//new code
	int j;
//	voxel tnv;
        Point3i tnv = new Point3i();
        tnv.x = 0;
        tnv.y = 0;
        tnv.z = 0;
	for(i=0;i<number;i++)
	{
//		if(positout>=(*allocout)-6)
//		{
//			(*allocout)=int(1.2*(*allocout));
//			if(*allocout>totalinnervox) *allocout=totalinnervox;
//			outarray=(voxel2 *)realloc(outarray,(*allocout)*sizeof(voxel2));
//		}
//		tx=inarray[i].ix;
//        ty=inarray[i].iy;
//		tz=inarray[i].iz;
               if(positout >= allocout - 6)
	       {
			int outLength = allocout;
                        allocout = (int) 1.2*allocout;
			if(allocout>totalinnervox) allocout=totalinnervox;
//			outarray=(voxel2 *)realloc(outarray,(*allocout)*sizeof(voxel2));
                        System.arraycopy(outarray, 0, outarray, 0, allocout);
                        for (int ii = outLength; ii < allocout; ii++) {
                            outarray[ii] = new Point3i();
                        }
		}
		tx=inarray[i].x;
                ty=inarray[i].y;
		tz=inarray[i].z;
		for(j=0;j<6;j++)
		{
			tnv.x=tx+nb[j][0];
			tnv.y=ty+nb[j][1];
			tnv.z=tz+nb[j][2];
			if( tnv.x<plength && tnv.x>-1 &&
				tnv.y<pwidth && tnv.y>-1 &&
				tnv.z<pheight && tnv.z>-1 &&
				vp[tnv.x][tnv.y][tnv.z].inout &&
				!vp[tnv.x][tnv.y][tnv.z].isdone)
			{
				boundpoint[tnv.x][tnv.y][tz+nb[j][2]].x=boundpoint[tx][ty][tz].x;
				boundpoint[tnv.x][tnv.y][tz+nb[j][2]].y=boundpoint[tx][ty][tz].y;
				boundpoint[tnv.x][tnv.y][tz+nb[j][2]].z=boundpoint[tx][ty][tz].z;
				dx=tnv.x-boundpoint[tx][ty][tz].x;
				dy=tnv.y-boundpoint[tx][ty][tz].y;
				dz=tnv.z-boundpoint[tx][ty][tz].z;
				squre=(float)(dx*dx+dy*dy+dz*dz);
				vp[tnv.x][tnv.y][tnv.z].distance=(float)(Math.sqrt(squre));
				vp[tnv.x][tnv.y][tnv.z].isdone=true;
				vp[tnv.x][tnv.y][tnv.z].isbound=true;
				outarray[positout].x=tnv.x;
				outarray[positout].y=tnv.y;
				outarray[positout].z=tnv.z;
				positout++;eliminate++;
			}
			else if( tnv.x<plength && tnv.x>-1 &&
				tnv.y<pwidth && tnv.y>-1 &&
				tnv.z<pheight && tnv.z>-1 &&
				vp[tnv.x][tnv.y][tnv.z].inout &&
				vp[tnv.x][tnv.y][tnv.z].isdone)
			{

				dx=tnv.x-boundpoint[tx][ty][tz].x;
				dy=tnv.y-boundpoint[tx][ty][tz].y;
				dz=tnv.z-boundpoint[tx][ty][tz].z;
				squre=(float)(dx*dx+dy*dy+dz*dz);
				squre=(float)(Math.sqrt(squre));
				if(squre<vp[tnv.x][tnv.y][tnv.z].distance)
				{
					boundpoint[tnv.x][tnv.y][tnv.z].x=boundpoint[tx][ty][tz].x;
					boundpoint[tnv.x][tnv.y][tnv.z].y=boundpoint[tx][ty][tz].y;
					boundpoint[tnv.x][tnv.y][tnv.z].z=boundpoint[tx][ty][tz].z;
					vp[tnv.x][tnv.y][tnv.z].distance=(float)(squre);
					if(!vp[tnv.x][tnv.y][tnv.z].isbound)
					{
						vp[tnv.x][tnv.y][tnv.z].isbound=true;
						outarray[positout].x=tnv.x;
						outarray[positout].y=tnv.y;
						outarray[positout].z=tnv.z;
						positout++;
					}
				}

			}
		}
	}
	for(i=0;i<number;i++)
	{
//		if(positout>=(*allocout)-12)
//		{
//			(*allocout)=int(1.2*(*allocout));
//			if(*allocout>totalinnervox) *allocout=totalinnervox;
//			outarray=(voxel2 *)realloc(outarray,(*allocout)*sizeof(voxel2));
//		}
                if(positout >= allocout - 12)
		{
			int outLength = allocout;
                        allocout = (int)1.2*allocout;
			if(allocout > totalinnervox) allocout=totalinnervox;
                        System.arraycopy(outarray, 0, outarray, 0, allocout);
                        for (int ii = outLength; ii < allocout; ii++) {
                            outarray[ii] = new Point3i();
                        }
		}
		tx=inarray[i].x;
                ty=inarray[i].y;
		tz=inarray[i].z;
		for(j=6;j<18;j++)
		{
			tnv.x=tx+nb[j][0];
			tnv.y=ty+nb[j][1];
			tnv.z=tz+nb[j][2];
			if( tnv.x<plength && tnv.x>-1 &&
				tnv.y<pwidth && tnv.y>-1 &&
				tnv.z<pheight && tnv.z>-1 &&
				vp[tnv.x][tnv.y][tnv.z].inout &&
				!vp[tnv.x][tnv.y][tnv.z].isdone)
			{
				boundpoint[tnv.x][tnv.y][tz+nb[j][2]].x=boundpoint[tx][ty][tz].x;
				boundpoint[tnv.x][tnv.y][tz+nb[j][2]].y=boundpoint[tx][ty][tz].y;
				boundpoint[tnv.x][tnv.y][tz+nb[j][2]].z=boundpoint[tx][ty][tz].z;
				dx=tnv.x-boundpoint[tx][ty][tz].x;
				dy=tnv.y-boundpoint[tx][ty][tz].y;
				dz=tnv.z-boundpoint[tx][ty][tz].z;
				squre=(float)(dx*dx+dy*dy+dz*dz);
				vp[tnv.x][tnv.y][tnv.z].distance=(float)(Math.sqrt(squre));
				vp[tnv.x][tnv.y][tnv.z].isdone=true;
				vp[tnv.x][tnv.y][tnv.z].isbound=true;
				outarray[positout].x=tnv.x;
				outarray[positout].y=tnv.y;
				outarray[positout].z=tnv.z;
				positout++;eliminate++;
			}
			else if( tnv.x<plength && tnv.x>-1 &&
				tnv.y<pwidth && tnv.y>-1 &&
				tnv.z<pheight && tnv.z>-1 &&
				vp[tnv.x][tnv.y][tnv.z].inout &&
				vp[tnv.x][tnv.y][tnv.z].isdone)
			{
				dx=tnv.x-boundpoint[tx][ty][tz].x;
				dy=tnv.y-boundpoint[tx][ty][tz].y;
				dz=tnv.z-boundpoint[tx][ty][tz].z;
				squre=(float)(dx*dx+dy*dy+dz*dz);
				squre=(float)(Math.sqrt(squre));
				if(squre<vp[tnv.x][tnv.y][tnv.z].distance)
				{
					boundpoint[tnv.x][tnv.y][tnv.z].x=boundpoint[tx][ty][tz].x;
					boundpoint[tnv.x][tnv.y][tnv.z].y=boundpoint[tx][ty][tz].y;
					boundpoint[tnv.x][tnv.y][tnv.z].z=boundpoint[tx][ty][tz].z;
					vp[tnv.x][tnv.y][tnv.z].distance=(float)(squre);
					if(!vp[tnv.x][tnv.y][tnv.z].isbound)
					{
						vp[tnv.x][tnv.y][tnv.z].isbound=true;
						outarray[positout].x=tnv.x;
						outarray[positout].y=tnv.y;
						outarray[positout].z=tnv.z;
						positout++;
					}
				}

			}
		}
	}
	for(i=0;i<number;i++)
	{
//		if(positout>=(*allocout)-9)
//		{
//			(*allocout)=int(1.2*(*allocout));
//			if(*allocout>totalinnervox) *allocout=totalinnervox;
//			outarray=(voxel2 *)realloc(outarray,(*allocout)*sizeof(voxel2));
//		}
                if(positout >= allocout -9)
		{
                        int outLength = allocout;
			allocout = (int)(1.2*allocout);
			if(allocout>totalinnervox) allocout=totalinnervox;
                        System.arraycopy(outarray, 0, outarray, 0, allocout);
                        for (int ii = outLength; ii < allocout; ii++) {
                            outarray[ii] = new Point3i();
                        }
		}
		tx=inarray[i].x;
                ty=inarray[i].y;
		tz=inarray[i].z;
		for(j=18;j<26;j++)
		{
			tnv.x=tx+nb[j][0];
			tnv.y=ty+nb[j][1];
			tnv.z=tz+nb[j][2];
			if( tnv.x<plength && tnv.x>-1 &&
				tnv.y<pwidth && tnv.y>-1 &&
				tnv.z<pheight && tnv.z>-1 &&
				vp[tnv.x][tnv.y][tnv.z].inout &&
				!vp[tnv.x][tnv.y][tnv.z].isdone)
			{
				boundpoint[tnv.x][tnv.y][tz+nb[j][2]].x=boundpoint[tx][ty][tz].x;
				boundpoint[tnv.x][tnv.y][tz+nb[j][2]].y=boundpoint[tx][ty][tz].y;
				boundpoint[tnv.x][tnv.y][tz+nb[j][2]].z=boundpoint[tx][ty][tz].z;
				dx=tnv.x-boundpoint[tx][ty][tz].x;
				dy=tnv.y-boundpoint[tx][ty][tz].y;
				dz=tnv.z-boundpoint[tx][ty][tz].z;
				squre=(float)(dx*dx+dy*dy+dz*dz);
				vp[tnv.x][tnv.y][tnv.z].distance=(float)(Math.sqrt(squre));
				vp[tnv.x][tnv.y][tnv.z].isdone=true;
				vp[tnv.x][tnv.y][tnv.z].isbound=true;
				outarray[positout].x=tnv.x;
				outarray[positout].y=tnv.y;
				outarray[positout].z=tnv.z;
				positout++;eliminate++;
			}
			else if( tnv.x<plength && tnv.x>-1 &&
				tnv.y<pwidth && tnv.y>-1 &&
				tnv.z<pheight && tnv.z>-1 &&
				vp[tnv.x][tnv.y][tnv.z].inout &&
				vp[tnv.x][tnv.y][tnv.z].isdone)
			{

				dx=tnv.x-boundpoint[tx][ty][tz].x;
				dy=tnv.y-boundpoint[tx][ty][tz].y;
				dz=tnv.z-boundpoint[tx][ty][tz].z;
				squre=(float)(dx*dx+dy*dy+dz*dz);
				squre=(float)(Math.sqrt(squre));
				if(squre<vp[tnv.x][tnv.y][tnv.z].distance)
				{
					boundpoint[tnv.x][tnv.y][tnv.z].x=boundpoint[tx][ty][tz].x;
					boundpoint[tnv.x][tnv.y][tnv.z].y=boundpoint[tx][ty][tz].y;
					boundpoint[tnv.x][tnv.y][tnv.z].z=boundpoint[tx][ty][tz].z;
					vp[tnv.x][tnv.y][tnv.z].distance=(float)(squre);
					if(!vp[tnv.x][tnv.y][tnv.z].isbound)
					{
						vp[tnv.x][tnv.y][tnv.z].isbound=true;
						outarray[positout].x=tnv.x;
						outarray[positout].y=tnv.y;
						outarray[positout].z=tnv.z;
						positout++;
					}
				}

			}
		}
	}

//	outnum=positout;
//	elimi=eliminate;

}
}
