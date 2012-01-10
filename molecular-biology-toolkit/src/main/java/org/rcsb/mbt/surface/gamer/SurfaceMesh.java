package org.rcsb.mbt.surface.gamer;

import org.rcsb.mbt.surface.gamer.biom.ATOM;
import org.rcsb.mbt.surface.gamer.biom.FLTVECT;
import org.rcsb.mbt.surface.gamer.biom.INT3VECT;
import org.rcsb.mbt.surface.gamer.biom.NPNT3;
import org.rcsb.mbt.surface.gamer.biom.SurfMesh;

/*
 * ***************************************************************************
 * GAMER = < Geometry-preserving Adaptive MeshER >
 * Copyright (C) 2007-2010 -- Michael Holst and Johan Hake
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * ***************************************************************************
 */

/* ***************************************************************************
 * File:     SurfaceMesh.C    < ... >
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Create and destroy SurfaceMesh data
 * ****************************************************************************
 */

public final class SurfaceMesh {
	public static biom biom = new biom();
/*
 * ***************************************************************************
 * Routine:  SurfaceMesh_ctor
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Create a surface mesh instance
 * ***************************************************************************
 */
 public static SurfMesh SurfaceMesh_ctor(int num_vertices,  int num_faces)
  {
  // Allocate memory and initialize variables
//  SurfaceMesh* surfmesh= (SurfaceMesh*)malloc(sizeof(SurfaceMesh));
  SurfMesh surfmesh= biom.new SurfMesh();
  surfmesh.nv = num_vertices;
  surfmesh.nf = num_faces;
//  surfmesh.vertex = (FLTVECT*)malloc(sizeof(FLTVECT)*surfmesh.nv);
  surfmesh.vertex = new FLTVECT[surfmesh.nv];
//  surfmesh.face = (INT3VECT*)malloc(sizeof(INT3VECT)*surfmesh.nf);
  surfmesh.face = new INT3VECT[surfmesh.nf];
  surfmesh.neighbor = null;
  surfmesh.neighbor_list = null;
  surfmesh.avglen = 0.0f;
  surfmesh.min[0] = 0.0f; surfmesh.min[1] = 0.0f; surfmesh.min[2] = 0.0f;
  surfmesh.max[0] = 0.0f; surfmesh.max[1] = 0.0f; surfmesh.max[2] = 0.0f;
  surfmesh.nvm = 0;
  surfmesh.nfm = 0;
  surfmesh.vertex_markers = null;
  surfmesh.face_markers = null;

  return surfmesh;
}

/*
 * ***************************************************************************
 * Routine:  SurfaceMesh_dtor
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Release memory from a SurfaceMesh instance
 * ***************************************************************************
 */
public static void SurfaceMesh_dtor(SurfMesh surfmesh)
{
  // Relase data memory
  SurfaceMesh_releaseData(surfmesh);

  // Release memory for the SurfaceMesh struct
//  free(surfmesh);
  surfmesh = null;
}

/*
 * ***************************************************************************
 * Routine:  SurfaceMesh_releaseData
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Release data memory from a SurfaceMesh instance
 * ***************************************************************************
 */
public static void SurfaceMesh_releaseData(SurfMesh surfmesh)
{
  // Free allocated memory
//  free(surfmesh.vertex);
	surfmesh.vertex = null;
//  free(surfmesh.face);
	surfmesh.face = null;

  // Destroy neighbor_list
  SurfaceMesh_destroyNeighborlist(surfmesh);
  
  // Destroy marker lists
  SurfaceMesh_destroyVertexMarkers(surfmesh);
  SurfaceMesh_destroyFaceMarkers(surfmesh);
  
}

/*
 * ***************************************************************************
 * SubRoutine:  SurfaceMesh_createVertexMarkers
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Create an array for vertex markers
 * ***************************************************************************
 */              
public static void SurfaceMesh_createVertexMarkers(SurfMesh surfmesh)
{
  if (surfmesh.vertex_markers != null)
    SurfaceMesh_destroyVertexMarkers(surfmesh);
//  surfmesh.vertex_markers = (int*)malloc(sizeof(int)*surfmesh.nv);
  surfmesh.vertex_markers = new int[surfmesh.nv];
  surfmesh.nvm = surfmesh.nv;
}

/*
 * ***************************************************************************
 * SubRoutine:  SurfaceMesh_destroyVertexMarkers
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Release memory for vertex markers
 * ***************************************************************************
 */              
public static void SurfaceMesh_destroyVertexMarkers(SurfMesh surfmesh)
{
  if (surfmesh.vertex_markers != null)
 //   free(surfmesh.vertex_markers);
	  surfmesh.vertex_markers = null;
  surfmesh.nvm = 0;
}

/*
 * ***************************************************************************
 * SubRoutine:  SurfaceMesh_resetVertexMarkers
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Reset vertex markers and initialize them to 0
 * ***************************************************************************
 */
public static void SurfaceMesh_resetVertexMarkers(SurfMesh surfmesh)
{

  // If the number of vertex markers are less than the number of vertices
  // (re)create vertex_markers
  if (surfmesh.nvm < surfmesh.nv)
    SurfaceMesh_createVertexMarkers(surfmesh);

  // Initialize the markers
  for (int n = 0; n < surfmesh.nv; n++)
    surfmesh.vertex_markers[n] = 0;

}

/*
 * ***************************************************************************
 * SubRoutine:  SurfaceMesh_createFaceMarkers
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Create an array for face markers
 * ***************************************************************************
 */              
public static void SurfaceMesh_createFaceMarkers(SurfMesh surfmesh)
{
  if (surfmesh.face_markers != null)
    SurfaceMesh_destroyFaceMarkers(surfmesh);
 // surfmesh.face_markers = (int*)malloc(sizeof(int)*surfmesh.nf);
  surfmesh.face_markers = new int[surfmesh.nf];
  surfmesh.nfm = surfmesh.nf;
}

/*
 * ***************************************************************************
 * SubRoutine:  SurfaceMesh_destroyFaceMarkers
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Release memory for face markers
 * ***************************************************************************
 */              
public static void SurfaceMesh_destroyFaceMarkers(SurfMesh surfmesh)
{
  if (surfmesh.face_markers != null)
//    free(surfmesh.face_markers);
	  surfmesh.face_markers = null;
  surfmesh.nfm = 0;
}

/*
 * ***************************************************************************
 * SubRoutine:  SurfaceMesh_resetFaceMarkers
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Reset face markers and initialize them to 0
 * ***************************************************************************
 */              
public static void SurfaceMesh_resetFaceMarkers(SurfMesh surfmesh)
{
  // If the number of face markers are less than the number of vertices
  // (re)create face_markers
  if (surfmesh.nfm < surfmesh.nf)
    SurfaceMesh_createFaceMarkers(surfmesh);

  // Initialize the markers
  for (int n = 0; n < surfmesh.nf; n++)
    surfmesh.face_markers[n] = 0;

}

/*
 * ***************************************************************************
 * SubRoutine:  SurfaceMesh_createNeighborList
 *
 * Author:   Zeyun Yu (zeyun.yu@gmail.com) and Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Create a neigbor list from a surface mesh
 * ***************************************************************************
 */              
public static void SurfaceMesh_createNeighborlist(SurfMesh surfmesh)
{
  
  int m,n,a0,b0;
  int a,b,c;
  NPNT3 first_ngr,second_ngr,tmp_ngr;
  NPNT3[] neighbor_list;

  // Destroy any exsisting neigborlist
  SurfaceMesh_destroyNeighborlist(surfmesh);

  // Create an array of NPNT3, used to store 
 // neighbor_list = (NPNT3 **)malloc(sizeof(NPNT3 *)*surfmesh.nv);
  neighbor_list = new NPNT3[surfmesh.nv];

  // Initialize the neighbor list
  for (n = 0; n < surfmesh.nv; n++) 
    neighbor_list[n] = null;

  // Iterate over the faces and collect line segments (a, b) and its connection 
  // to a face (c). Save the line segment so it forms a counter clockwise triangle 
  // with the origin vertex 
  for (n = 0; n < surfmesh.nf; n++) {
    a = surfmesh.face[n].a;
    b = surfmesh.face[n].b;
    c = surfmesh.face[n].c;
   
 //   first_ngr = (NPNT3 *)malloc(sizeof(NPNT3));
    first_ngr = biom.new NPNT3();
    first_ngr.a = b;
    first_ngr.b = c;
    first_ngr.c = n;
    first_ngr.next = neighbor_list[a];
    neighbor_list[a] = first_ngr;
    
 //   first_ngr = (NPNT3 *)malloc(sizeof(NPNT3));
    first_ngr = biom.new NPNT3();
    first_ngr.a = c;
    first_ngr.b = a;
    first_ngr.c = n;
    first_ngr.next = neighbor_list[b];
    neighbor_list[b] = first_ngr;
    
//    first_ngr = (NPNT3 *)malloc(sizeof(NPNT3));
    first_ngr = biom.new NPNT3();
    first_ngr.a = a;
    first_ngr.b = b;
    first_ngr.c = n;
    first_ngr.next = neighbor_list[c];
    neighbor_list[c] = first_ngr;
    
  }

  // Order the neighbors so they are connected counter clockwise
  for (n = 0; n < surfmesh.nv; n++) {
    first_ngr = neighbor_list[n];
    c = first_ngr.a;
    while (first_ngr != null) {
      a = first_ngr.a;
      b = first_ngr.b;
      
      second_ngr = first_ngr.next;
      while (second_ngr != null) {
	a0 = second_ngr.a;
	b0 = second_ngr.b;
	if (a0==b && b0!=a) {
	  tmp_ngr = first_ngr;
	  while (tmp_ngr != null) {
	    if (tmp_ngr.next == second_ngr) {
	      tmp_ngr.next = second_ngr.next;
	      break;
	    }
	    tmp_ngr = tmp_ngr.next;
	  }
	  tmp_ngr = first_ngr.next;
 	  first_ngr.next = second_ngr;
	  second_ngr.next = tmp_ngr;
	  break;
	}
	second_ngr = second_ngr.next;
      }
      if (first_ngr.next == null) {
	if (first_ngr.b != c)
	{
	  System.out.printf("some polygons are not closed, Vertices: %d-%d\n ", first_ngr.b, c);
	  System.out.printf("(%f,%f,%f)\n", surfmesh.vertex[first_ngr.b].x,
		 surfmesh.vertex[first_ngr.b].y,
		 surfmesh.vertex[first_ngr.b].z);
	  System.out.printf("(%f,%f,%f)\n", surfmesh.vertex[c].x,
		 surfmesh.vertex[c].y,
		 surfmesh.vertex[c].z);
	}
      }
      
      first_ngr = first_ngr.next;
    }
  }
  
  // Attach the neigborlist to the surfmesh
  surfmesh.neighbor_list = neighbor_list;
}

/*
 * ***************************************************************************
 * SubRoutine:  SurfaceMesh_destroyNeighborlist
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Release memory from a neigborlist
 * ***************************************************************************
 */
public static void SurfaceMesh_destroyNeighborlist(SurfMesh surfmesh)
{
   int n;
  NPNT3 first_ngr = null;
  NPNT3 tmp_ngr = null;

  if (surfmesh.neighbor_list != null)
  {
    // Release the single neighbors 
    for (n = 0; n < surfmesh.nv; n++) {
      first_ngr = surfmesh.neighbor_list[n];
      while (first_ngr != null) 
      {
	tmp_ngr = first_ngr.next;
//	free(first_ngr);
	first_ngr = null;
	first_ngr = tmp_ngr;
      }
    } 

    // Free the array of pointers
 //   free(surfmesh.neighbor_list);
    surfmesh.neighbor_list = null;
  }
}

/*
 * ***************************************************************************
 * SubRoutine: SurfaceMesh_getCenterRadius
 *
 * Author:   Johan Hake (hake.dev@gmail.com)
 *
 * Purpose:  Return the center and radius of SurfaceMesh
 * ***************************************************************************
 */
//TODO data has not been initialized. Is this method used anywhere? Commented out for now.
public static ATOM SurfaceMesh_getCenterRadius(SurfMesh surfmesh)
{
  //TODO data was not been initialized in C source code ?? Is is method used anywhere?
  ATOM data = biom.new ATOM();
  int i;

  // Get the center and radius of the molecular surface mesh
  float mol_center_x = 0;
  float mol_center_y = 0;
  float mol_center_z = 0;
  float distance;
  
  for (i=0; i<surfmesh.nv; i++) {
    mol_center_x += surfmesh.vertex[i].x;
    mol_center_y += surfmesh.vertex[i].y;
    mol_center_z += surfmesh.vertex[i].z;
  }
  if (surfmesh.nv > 0) {
    data.x = (float)(mol_center_x/(double)surfmesh.nv);
    data.y = (float)(mol_center_y/(double)surfmesh.nv);
    data.z = (float)(mol_center_z/(double)surfmesh.nv);
  }
  else {
    System.out.printf("no atoms found ....\n");
    System.exit(0);
  }
  
  data.radius = 0;
  for (i=0; i < surfmesh.nv; i++) {
    distance = (float)Math.sqrt((surfmesh.vertex[i].x-data.x)*(surfmesh.vertex[i].x-data.x)+
		    (surfmesh.vertex[i].y-data.y)*(surfmesh.vertex[i].y-data.y)+
		    (surfmesh.vertex[i].z-data.z)*(surfmesh.vertex[i].z-data.z));
    if (distance > data.radius)
      data.radius = distance;
  }
  
  return data;
}

public static void SurfaceMesh_translate(SurfMesh surfmesh, float dx, float dy, float dz){
  int i;
  for (i=0; i<surfmesh.nv; i++){
    surfmesh.vertex[i].x += dx;
    surfmesh.vertex[i].y += dy;
    surfmesh.vertex[i].z += dz;
  }
}

public static void SurfaceMesh_scale(SurfMesh surfmesh, float scale_x, 
		       float scale_y, float scale_z){
  int i;
  for (i=0; i<surfmesh.nv; i++){
    surfmesh.vertex[i].x *= scale_x;
    surfmesh.vertex[i].y *= scale_y;
    surfmesh.vertex[i].z *= scale_z;
  }
}

public static void SurfaceMesh_scale(SurfMesh surfmesh, float scale){
  SurfaceMesh_scale(surfmesh, scale, scale, scale);
}

public static void SurfaceMesh_centeralize(SurfMesh surfmesh){
  ATOM center = SurfaceMesh_getCenterRadius(surfmesh);
  SurfaceMesh_translate(surfmesh, -center.x, -center.y, -center.z);
}
}
