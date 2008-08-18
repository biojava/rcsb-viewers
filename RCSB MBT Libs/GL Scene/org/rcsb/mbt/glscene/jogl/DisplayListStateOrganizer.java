package org.rcsb.mbt.glscene.jogl;

import java.util.Map;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.model.StructureComponentRegistry;

import com.sun.opengl.util.GLUT;


public class DisplayListStateOrganizer {
	/*
	 * VectorMap listsByMutableColorType { key: Integer openGlMaterial //
	 * GL.GL_EMISSION, etc. value: VectorMap listsByAmbientColor { key: float[]
	 * ambientColor value: VectorMap listsByDiffuseColor { key: float[]
	 * diffuseColor value: VectorMap listsByEmissiveColor { key: float[]
	 * emissiveColor value: VectorMap listsByShininess { key: float[] shininess
	 * value: VectorMap listsBySpecularColor { key: float[] specularColor value:
	 * VectorMap[2{false,true}] listsByIsLightingDisabled { []VectorMap key:
	 * ArrayLists lists []VectorMap value: null } } } } } } }
	 */
	private final VectorMap listsByMutableColorType = new VectorMap();

	
	private static final class VectorMap extends Vector {
		/*
		 *  Data structure: each item in the underlying Vector contains Object[key,value].
		 */
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6318408136416226177L;

		public Object get(final Object key) {
			final int size = super.size();
			
			for(int i = 0; i < size; i++) {
				final Object[] tmp = (Object[])super.get(i);
				if((tmp[0] == null && key == null) || (tmp[0] != null && tmp[0].equals(key))) {
					return tmp[1];
				}
			}
			
			return null;
		}

		public Object put(final Object key, final Object value) {
			
			super.add(new Object[] {key,value });
			
			return null;
		}

		
		public boolean remove(final Object key) {
			final int size = super.size();
			
			for(int i = 0; i < size; i++) {
				final Object[] tmp = (Object[])super.get(i);
				if((tmp[0] == null && key == null) || (tmp[0] != null && tmp[0].equals(key))) {
					super.remove(i);
					return true;
				}
			}
			
			return false;
		}
		
	}
	
	public synchronized void clearData() {
		this.listsByMutableColorType.clear();
	}

	private final Object existsObject = new Object();

	public synchronized void addArrayLists(final DisplayLists lists) {

		if (lists.structureComponent.getStructureComponentType() == StructureComponentRegistry.TYPE_FRAGMENT
				|| lists.structureComponent.getStructureComponentType() == StructureComponentRegistry.TYPE_CHAIN
				|| lists.structureComponent.getStructureComponentType() == StructureComponentRegistry.TYPE_RESIDUE) {
			System.err.print("");
		}

		Integer mutableColorType = null;
		if (lists.mutableColorType >= 0) {
			mutableColorType = new Integer(lists.mutableColorType);
		}
		VectorMap listsByAmbientColor = (VectorMap) this.listsByMutableColorType
				.get(mutableColorType);
		if (listsByAmbientColor == null) {
			listsByAmbientColor = new VectorMap();
			this.listsByMutableColorType.put(mutableColorType,
					listsByAmbientColor);
		}

		VectorMap listsByDiffuseColor = (VectorMap) listsByAmbientColor
				.get(lists.ambientColor);
		if (listsByDiffuseColor == null) {
			listsByDiffuseColor = new VectorMap();
			listsByAmbientColor.put(lists.ambientColor, listsByDiffuseColor);
		}

		VectorMap listsByEmissiveColor = (VectorMap) listsByDiffuseColor
				.get(lists.diffuseColor);
		if (listsByEmissiveColor == null) {
			listsByEmissiveColor = new VectorMap();
			listsByDiffuseColor.put(lists.diffuseColor, listsByEmissiveColor);
		}

		VectorMap listsByShininess = (VectorMap) listsByEmissiveColor
				.get(lists.emissiveColor);
		if (listsByShininess == null) {
			listsByShininess = new VectorMap();
			listsByEmissiveColor.put(lists.emissiveColor, listsByShininess);
		}

		VectorMap listsBySpecularColor = (VectorMap) listsByShininess
				.get(lists.shininess);
		if (listsBySpecularColor == null) {
			listsBySpecularColor = new VectorMap();
			listsByShininess.put(lists.shininess, listsBySpecularColor);
		}

		VectorMap[] listsByIsLightingDisabled = (VectorMap[]) listsBySpecularColor
				.get(lists.specularColor);
		if (listsByIsLightingDisabled == null) {
			listsByIsLightingDisabled = new VectorMap[2];
			listsBySpecularColor.put(lists.specularColor,
					listsByIsLightingDisabled);
		}

		final int lightingIndex = lists.disableLigting ? 1 : 0;
		VectorMap listsMap = listsByIsLightingDisabled[lightingIndex];
		if (listsMap == null) {
			listsMap = new VectorMap();
			listsByIsLightingDisabled[lightingIndex] = listsMap;
		}

		listsMap.put(lists, this.existsObject);
	}

	// returns: was it removed?
	public synchronized boolean removeArrayLists(final DisplayLists lists) {
		final Map listsByAmbientColor = (Map) this.listsByMutableColorType
				.get(new Integer(lists.mutableColorType));
		if (listsByAmbientColor != null) {
			final Map listsByDiffuseColor = (Map) listsByAmbientColor
					.get(lists.ambientColor);
			if (listsByDiffuseColor != null) {
				final Map listsByEmissiveColor = (Map) listsByDiffuseColor
						.get(lists.diffuseColor);
				if (listsByEmissiveColor != null) {
					final Map listsByShininess = (Map) listsByEmissiveColor
							.get(lists.emissiveColor);
					if (listsByShininess != null) {
						final Map listsBySpecularColor = (Map) listsByShininess
								.get(lists.shininess);
						if (listsBySpecularColor != null) {
							final VectorMap[] listsByIsLightingDisabled = (VectorMap[]) listsBySpecularColor
									.get(lists.specularColor);
							if (listsByIsLightingDisabled != null) {
								final int lightingIndex = lists.disableLigting ? 1
										: 0;
								final VectorMap listsMap = listsByIsLightingDisabled[lightingIndex];
								if (listsMap != null) {
									return listsMap.remove(lists);
								}
							}
						}
					}
				}
			}
		}

		return false;
	}

	public static boolean isColorMaterialEnabled = false;

	public static Integer currentColorMaterialType = null;

	public static float[] currentColorMaterial = null;

	public static float[] currentAmbientColor = null;

	public static float[] currentDiffuseColor = null;

	public static float[] currentEmissiveColor = null;

	public static float[] currentSpecularColor = null;

	public static float[] currentShininess = null;

	public static boolean isLightingDisabled = false;

	// draws the encapsulated array lists, attempting to keep OpenGL state
	// changes to a minimum.
	// also cleans/compacts the data structure automatically. May take several
	// draws to completely clean the data structure.
	public synchronized void draw(final GL gl, final GLU glu, final GLUT glut, final boolean isSelectionMode) {
		for (int mutableIndex = 0; mutableIndex < this.listsByMutableColorType.size(); mutableIndex++) {
			final Object[] tmp = (Object[])this.listsByMutableColorType.get(mutableIndex);
			final Integer mutableColorType =  tmp[0] == null ? (Integer)null : (Integer)tmp[0];
			final VectorMap listsByAmbientColor = tmp[1] == null ? (VectorMap)null : (VectorMap)tmp[1];

			if (listsByAmbientColor == null || listsByAmbientColor.size() == 0) {
//				mutableKeyIt.remove();
				continue;
			}

			if (mutableColorType != null) {
				if (DisplayListStateOrganizer.currentColorMaterialType == null || DisplayListStateOrganizer.currentColorMaterialType.intValue() != mutableColorType.intValue()) {
					gl.glColorMaterial(GL.GL_FRONT, mutableColorType.intValue());
					if (DisplayListStateOrganizer.isColorMaterialEnabled) {
						gl.glDisable(GL.GL_COLOR_MATERIAL);
						DisplayListStateOrganizer.isColorMaterialEnabled = false;
					}
					DisplayListStateOrganizer.currentColorMaterialType = mutableColorType;
				}

				if (!DisplayListStateOrganizer.isColorMaterialEnabled) {
					gl.glEnable(GL.GL_COLOR_MATERIAL);
					DisplayListStateOrganizer.isColorMaterialEnabled = true;
				}
			}

//			Iterator ambientKeyIt = listsByAmbientColor.iterator();
//			while (ambientKeyIt.hasNext()) {
			for(int ambientIndex = 0; ambientIndex < listsByAmbientColor.size(); ambientIndex++) {
				final Object[] tmp2 = (Object[])listsByAmbientColor.get(ambientIndex);
				final float[] ambientColor = tmp2[0] == null ? (float[])null : (float[])tmp2[0];
				final VectorMap listsByDiffuseColor =  tmp2[1] == null ? (VectorMap)null : (VectorMap)tmp2[1];

				if (listsByDiffuseColor == null || listsByDiffuseColor.size() == 0) {
//					ambientKeyIt.remove();
					continue;
				}

				if (ambientColor != null && (DisplayListStateOrganizer.currentAmbientColor == null || ambientColor != DisplayListStateOrganizer.currentAmbientColor)) {
					gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, ambientColor, 0);
				}
				DisplayListStateOrganizer.currentAmbientColor = ambientColor;

//				Iterator diffuseKeyIt = listsByDiffuseColor.iterator();
//				while (diffuseKeyIt.hasNext()) {
				for(int diffuseIndex = 0; diffuseIndex < listsByDiffuseColor.size(); diffuseIndex++) {
					final Object[] tmp3 = (Object[])listsByDiffuseColor.get(diffuseIndex);
					final float[] diffuseColor = tmp3[0] == null ? (float[])null : (float[])tmp3[0];
					final VectorMap listsByEmissiveColor =  tmp3[1] == null ? (VectorMap)null : (VectorMap)tmp3[1];

					if (listsByEmissiveColor == null || listsByEmissiveColor.size() == 0) {
//						diffuseKeyIt.remove();
						continue;
					}

					if (diffuseColor != null && (DisplayListStateOrganizer.currentDiffuseColor == null || diffuseColor != DisplayListStateOrganizer.currentDiffuseColor)) {
						gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuseColor, 0);
					}
					DisplayListStateOrganizer.currentDiffuseColor = diffuseColor;

//					Iterator emissiveKeyIt = listsByEmissiveColor.iterator();
//					while (emissiveKeyIt.hasNext()) {
					for(int emmissiveIndex = 0; emmissiveIndex < listsByEmissiveColor.size(); emmissiveIndex++) {
						final Object[] tmp5 = (Object[])listsByEmissiveColor.get(emmissiveIndex);
						final float[] emissiveColor = tmp5[0] == null ? (float[])null : (float[])tmp5[0];
						final VectorMap listsByShininess = tmp5[1] == null ? (VectorMap)null : (VectorMap)tmp5[1];

						if (listsByShininess == null || listsByShininess.size() == 0) {
//							emissiveKeyIt.remove();
							continue;
						}

						if (emissiveColor != null && (DisplayListStateOrganizer.currentEmissiveColor == null || emissiveColor != DisplayListStateOrganizer.currentEmissiveColor)) {
							gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, emissiveColor, 0);
						}
						DisplayListStateOrganizer.currentEmissiveColor = emissiveColor;

//						Iterator shininessKeyIt = listsByShininess.iterator();
//						while (shininessKeyIt.hasNext()) {
						for(int shininessIndex = 0; shininessIndex < listsByShininess.size(); shininessIndex++) {
							final Object[] tmp6 = (Object[])listsByShininess.get(shininessIndex);
							final float[] shininess = tmp6[0] == null ? (float[])null : (float[])tmp6[0];
							final VectorMap listsBySpecularColor = tmp6[1] == null ? (VectorMap)null : (VectorMap)tmp6[1];

							if (listsBySpecularColor == null || listsBySpecularColor.size() == 0) {
//								shininessKeyIt.remove();
								continue;
							}

							if (shininess != null && (DisplayListStateOrganizer.currentShininess == null || shininess != DisplayListStateOrganizer.currentShininess)) {
								gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, shininess, 0);
							}
							DisplayListStateOrganizer.currentShininess = shininess;

//							Iterator specularKeyIt = listsBySpecularColor.iterator();
//							while (specularKeyIt.hasNext()) {
							for(int specularIndex = 0; specularIndex < listsBySpecularColor.size(); specularIndex++) {
								final Object[] tmp4 = (Object[])listsBySpecularColor.get(specularIndex);
								final float[] specularColor = (float[]) tmp4[0] == null ? (float[])null : (float[])tmp4[0];
								final VectorMap[] listsByIsLightingDisabled =  tmp4[1] == null ? (VectorMap[])null : (VectorMap[])tmp4[1];

								if (listsByIsLightingDisabled == null) {
//									specularKeyIt.remove();
									continue;
								}

								if (specularColor != null && (DisplayListStateOrganizer.currentSpecularColor == null || specularColor != DisplayListStateOrganizer.currentSpecularColor)) {
									gl.glMaterialfv(GL.GL_FRONT,GL.GL_SPECULAR, specularColor, 0);
								}
								DisplayListStateOrganizer.currentSpecularColor = specularColor;

								boolean childrenExistLighting = false;
								for (int i = 0; i < listsByIsLightingDisabled.length; i++) {
									final VectorMap listsMap = listsByIsLightingDisabled[i];

									if (listsMap == null) {
										continue;
									}
									childrenExistLighting = true;

									if (i == 0 && DisplayListStateOrganizer.isLightingDisabled) {
										gl.glEnable(GL.GL_LIGHTING);
										DisplayListStateOrganizer.isLightingDisabled = false;
									} else if (i == 1 && !DisplayListStateOrganizer.isLightingDisabled) {
										gl.glDisable(GL.GL_LIGHTING);
										DisplayListStateOrganizer.isLightingDisabled = true;
									}

//									Iterator listsKeyIt = listsMap.iterator();
//									while (listsKeyIt.hasNext()) {
									for(int listsIndex = 0; listsIndex < listsMap.size(); listsIndex++) {
										final Object[] tmp8 = (Object[])listsMap.get(listsIndex);
										final DisplayLists lists = tmp8[0] == null ? (DisplayLists)null : (DisplayLists)tmp8[0];
										
										if (lists == null) { // just in case...
//											listsKeyIt.remove();
											continue;
										}

										//**JB disabled due to errors.
//										if (isSelectionMode) {
//											gl.glLoadName(lists.getName());
//										}
//
//										lists.drawSimple(gl, glu, glut);
									}
								}

								if (!childrenExistLighting) {
//									specularKeyIt.remove();
								}
							}
						}
					}
				}
			}
		}
	}
}
