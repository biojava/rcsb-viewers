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
package org.rcsb.vf.controllers.scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.AtomColorByBFactor;
import org.rcsb.mbt.model.attributes.AtomColorByElement;
import org.rcsb.mbt.model.attributes.AtomColorByRandom;
import org.rcsb.mbt.model.attributes.AtomColorByResidueColor;
import org.rcsb.mbt.model.attributes.AtomColorByRgb;
import org.rcsb.mbt.model.attributes.AtomLabelByAtomCompound;
import org.rcsb.mbt.model.attributes.AtomLabelByAtomElement;
import org.rcsb.mbt.model.attributes.AtomLabelByAtomName;
import org.rcsb.mbt.model.attributes.AtomLabelByChainId;
import org.rcsb.mbt.model.attributes.AtomLabelNone;
import org.rcsb.mbt.model.attributes.AtomRadiusByCpk;
import org.rcsb.mbt.model.attributes.AtomRadiusByScaledCpk;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondColorByAtomColor;
import org.rcsb.mbt.model.attributes.BondRadiusByAtomRadius;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.IAtomColor;
import org.rcsb.mbt.model.attributes.IAtomLabel;
import org.rcsb.mbt.model.attributes.IAtomRadius;
import org.rcsb.mbt.model.attributes.IResidueColor;
import org.rcsb.mbt.model.attributes.IResidueLabel;
import org.rcsb.mbt.model.attributes.ResidueColorByFragmentType;
import org.rcsb.mbt.model.attributes.ResidueColorByHydrophobicity;
import org.rcsb.mbt.model.attributes.ResidueColorByRandom;
import org.rcsb.mbt.model.attributes.ResidueColorByRandomFragmentColor;
import org.rcsb.mbt.model.attributes.ResidueColorByResidueCompound;
import org.rcsb.mbt.model.attributes.ResidueColorByResidueIndex;
import org.rcsb.mbt.model.attributes.ResidueColorByResidueIndexDna;
import org.rcsb.mbt.model.attributes.ResidueColorByRgb;
import org.rcsb.mbt.model.attributes.ResidueLabelCustom;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.jogl.AtomGeometry;
import org.rcsb.vf.glscene.jogl.BondGeometry;
import org.rcsb.vf.glscene.jogl.ChainGeometry;
import org.rcsb.vf.glscene.jogl.Constants;
import org.rcsb.vf.glscene.jogl.CustomAtomLabel;
import org.rcsb.vf.glscene.jogl.DisplayListRenderable;
import org.rcsb.vf.glscene.jogl.Geometry;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;
import org.rcsb.vf.glscene.jogl.ChainGeometry.RibbonForm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SceneState
{
	public class AtomStyleMap
	{
		public class TreeBelowColors
		{
			public class TreeBelowLabel
			{
				private final HashMap<Class<Object>, TreeBelowRadius> byRadiusClass = new HashMap<Class<Object>, TreeBelowRadius>(); // key: Class atomRadius. value: TreeBelowRadius.
				
				public class TreeBelowRadius {
					public class TreeBelowForm {
						public HashMap<Float, RangeMap2> byQuality = new HashMap<Float, RangeMap2>();	// key: Float quality. value: RangeMap2 atomIndices.						
						public RangeMap2 getSubtree(final Float quality) {
							RangeMap2 atomIndices = (RangeMap2)byQuality.get(quality);
							if(atomIndices == null) {
								atomIndices = new RangeMap2();
								byQuality.put(quality, atomIndices);
							}
							
							return atomIndices;
						}
						
						public void appendEntries(final Vector entries, final Entry sampleEntry)
						{
							for (Float quality : byQuality.keySet())
							{
								final RangeMap2 subtree = byQuality.get(quality);
								
								subtree.collapse();
								
								sampleEntry.geometryQuality = quality.floatValue();
								
								final int rangeCount = subtree.getRangeCount();
								for(int i = 0; i < rangeCount; i++) {
									final int[] range = subtree.getRange(i);
									
									final Entry entry = sampleEntry.createCopy();
									entry.range = range;
									entries.add(entry);
								}
							}
						}
					}
					
					public HashMap<Integer, TreeBelowForm> byForm = new HashMap<Integer, TreeBelowForm>();	// key: Integer form. value: TreeBelowForm.
					
					public TreeBelowForm getSubtree(final Integer form)
					{
						TreeBelowForm subtree = (TreeBelowForm)byForm.get(form);
						if(subtree == null)
						{
							subtree = new TreeBelowForm();
							byForm.put(form, subtree);
						}
						
						return subtree;
					}
					
					public void appendEntries(final Vector entries, final Entry sampleEntry)
					{
						for (Integer form : byForm.keySet())
						{
							final TreeBelowForm subtree = byForm.get(form);
							
							sampleEntry.geometryForm = form.intValue();
							
							subtree.appendEntries(entries, sampleEntry);
						}
					}
				}
				
				public TreeBelowRadius getSubtree(final IAtomRadius radius) {
					final Class radiusClass = radius.getClass();
					
					TreeBelowRadius subtree = (TreeBelowRadius)byRadiusClass.get(radiusClass);
					if(subtree == null) {
						subtree = new TreeBelowRadius();
						byRadiusClass.put(radiusClass, subtree);
					}
					
					return subtree;
				}
				
				public void appendEntries(final Vector entries, final Entry sampleEntry)
				{
					for (Class radiusClass : byRadiusClass.keySet())
					{
						final TreeBelowRadius subtree = byRadiusClass.get(radiusClass);
						
						sampleEntry.atomRadiusClass = radiusClass;
						
						subtree.appendEntries(entries, sampleEntry);
					}
				}
			}
			
			private final HashMap<String, TreeBelowLabel> byCustomLabel = new HashMap<String, TreeBelowLabel>();	// key: String label. value: TreeBelowLabel.
			private final HashMap<Class, TreeBelowLabel> byLabelClass = new HashMap<Class, TreeBelowLabel>();	// key: Class AtomLabel. value: TreeBelowLabel.
			
			private TreeBelowLabel getSubtree(final String label) {
				TreeBelowLabel subtree = (TreeBelowLabel)byCustomLabel.get(label);
				if(subtree == null) {
					subtree = new TreeBelowLabel();
					byCustomLabel.put(label, subtree);
				}
				
				return subtree;
			}
			
			public TreeBelowLabel getSubtree(final IAtomLabel label, final Atom a) {
				TreeBelowLabel subtree;
				if(label instanceof CustomAtomLabel) {
					subtree = getSubtree(label.getAtomLabel(a));
				} else {
					final Class labelClass = label.getClass();
					subtree = (TreeBelowLabel)byLabelClass.get(labelClass);
					if(subtree == null) {
						subtree = new TreeBelowLabel();
						byLabelClass.put(labelClass, subtree);
					}
				}
				
				return subtree;
			}
			
			public void appendEntries(final Vector entries, final Entry sampleEntry)
			{
				for (String label : byCustomLabel.keySet())
				{
					final TreeBelowLabel subtree = byCustomLabel.get(label);
					
					sampleEntry.atomLabelClass = CustomAtomLabel.class;
					sampleEntry.customLabel = label;
					
					subtree.appendEntries(entries, sampleEntry);
				}
				
				sampleEntry.customLabel = null;
				
				for (Class labelClass : byLabelClass.keySet())
				{
					final TreeBelowLabel subtree = byLabelClass.get(labelClass);
					
					sampleEntry.atomLabelClass = labelClass;
					
					subtree.appendEntries(entries, sampleEntry);
				}
			}
		}
		
		public class TreeBelowRed {
			public class TreeBelowGreen {
				private final HashMap<Float, TreeBelowColors> byBlue = new HashMap<Float, TreeBelowColors>();	// key: Float blue. value: TreeBelowColors.
				
				public TreeBelowColors getSubtree(final Float blue) {
					TreeBelowColors subtree = (TreeBelowColors)byBlue.get(blue);
					if(subtree == null) {
						subtree = new TreeBelowColors();
						byBlue.put(blue, subtree);
					}
					
					return subtree;
				}
				
				public void appendEntries(final Vector entries, final Entry sampleEntry)
				{
					for (Float blue : byBlue.keySet())
					{
						final TreeBelowColors subtree = byBlue.get(blue);
						
						sampleEntry.rgb[2] = blue.floatValue();
						
						subtree.appendEntries(entries, sampleEntry);
					}
				}
			}
			
			private final HashMap<Float, TreeBelowGreen> byGreen = new HashMap<Float, TreeBelowGreen>();	// key: Float green. value: TreeBelowBlue.
			
			public TreeBelowGreen getSubtree(final Float green) {
				TreeBelowGreen subtree = (TreeBelowGreen)byGreen.get(green);
				if(subtree == null) {
					subtree = new TreeBelowGreen();
					byGreen.put(green, subtree);
				}
				
				return subtree;
			}
			
			public void appendEntries(final Vector entries, final Entry sampleEntry)
			{
				for (Float green : byGreen.keySet())
				{
					final TreeBelowGreen subtree = byGreen.get(green);
					
					sampleEntry.rgb[1] = green.floatValue();
					
					subtree.appendEntries(entries, sampleEntry);
				}
			}
		}
		
		private final HashMap<Class, TreeBelowColors> byAtomColor = new HashMap<Class, TreeBelowColors>();	// key: AtomColor.getClass() (not AtomColorByRgb). value: TreeBelowColors. 
		private final HashMap<Float, TreeBelowRed> byRed = new HashMap<Float, TreeBelowRed>();	// if AtomColor is AtomColorByRgb, this is used instead... value: TreeBelowRed.
		
		private TreeBelowRed getSubtree(final Float red) {
			TreeBelowRed subtree = (TreeBelowRed)byRed.get(red);
			if(subtree == null) {
				subtree = new TreeBelowRed();
				byRed.put(red, subtree);
			}
			
			return subtree;
		}
		
		public TreeBelowColors getSubtree(final IAtomColor atomColor, final Atom atom) {
			TreeBelowColors subtree = null;
			
			if(atomColor instanceof AtomColorByRgb) {
				final AtomColorByRgb rgb = (AtomColorByRgb)atomColor;
				
				rgb.getAtomColor(atom, Constants.colorTemp);
				final Float red = new Float(Constants.colorTemp[0]);
				final Float green = new Float(Constants.colorTemp[1]);
				final Float blue = new Float(Constants.colorTemp[2]);
				
				final TreeBelowRed redSubtree = getSubtree(red);
				final TreeBelowRed.TreeBelowGreen greenSubtree = redSubtree.getSubtree(green);
				subtree = greenSubtree.getSubtree(blue);
			} else {
				final Class colorClass = atomColor.getClass();
				subtree = (TreeBelowColors)byAtomColor.get(colorClass);
				if(subtree == null) {
					subtree = new TreeBelowColors();
					byAtomColor.put(colorClass, subtree);
				}
			}
			
			return subtree;
		}
		
		public class Entry {
			public int[] range = null;
			public Class atomColorClass = null;
			public float[] rgb = null;	// used when atomColorClass == AtomColorByRgb.class.
			public Class atomLabelClass = null;
			public String customLabel = null;	// used when atomLabelClass == CustomAtomLabel.class.
			public Class atomRadiusClass = null;
			public float geometryQuality = -1;
			public int geometryForm = -1;
			
			public Entry createCopy() {
				final Entry copy = new Entry();
				
				if(range != null) {
					copy.range = new int[] {range[0], range[1]};
				}
				copy.atomColorClass = atomColorClass;
				if(rgb != null) {
					copy.rgb = new float[] {rgb[0], rgb[1], rgb[2]};
				}
				copy.atomLabelClass = atomLabelClass;
				copy.customLabel = customLabel;
				copy.atomRadiusClass = atomRadiusClass;
				copy.geometryQuality = geometryQuality;
				copy.geometryForm = geometryForm;
				
				return copy;
			}
		}
		
		/**
		 * Returns a vector ef Entries. 
		 */
		public Vector getEntries() {
			final Vector entries = new Vector();
			final Entry sampleEntry = new Entry();
			
			for (Float red : byRed.keySet())
			{
				final TreeBelowRed subtree = byRed.get(red);
				
				sampleEntry.atomColorClass = AtomColorByRgb.class;
				if(sampleEntry.rgb == null) {
					sampleEntry.rgb = new float[3];
				}
				sampleEntry.rgb[0] = red.floatValue();
				
				subtree.appendEntries(entries, sampleEntry);
			}
			
			sampleEntry.rgb = null;
			
			for (Class atomColorClass : byAtomColor.keySet())
			{
				final TreeBelowColors subtree = byAtomColor.get(atomColorClass);
				
				sampleEntry.atomColorClass = atomColorClass; 
				
				subtree.appendEntries(entries, sampleEntry);
			}
			
			return entries;
		}
	}
	
	public class ResidueStyleMap {
		public class TreeBelowColors {
			public class TreeBelowForm {
				public class TreeBelowQuality {
					public HashMap<Class, RangeMap2> byLabelClass = new HashMap<Class, RangeMap2>();	// key: Class labelClass. value: RangeMap2 residueIndices.
					public HashMap<String, RangeMap2> byLabel = new HashMap<String, RangeMap2>();	// key: String label. value: RangeMap2 residueIndices.
					
					public RangeMap2 getSubtree(final IResidueLabel label, final Residue residue) {
						final Class labelClass = label.getClass();
						RangeMap2 residueIndices = null;
						
						if(labelClass == ResidueLabelCustom.class) {	// treat this class specially
							String labelS = label.getResidueLabel(residue);
							if(labelS == null) {
								labelS = "";
							}
							residueIndices = (RangeMap2)byLabel.get(labelS);
							if(residueIndices == null) {
								residueIndices = new RangeMap2();
								byLabel.put(labelS, residueIndices);
							}
						} else {
							residueIndices = (RangeMap2)byLabelClass.get(labelClass);
							if(residueIndices == null) {
								residueIndices = new RangeMap2();
								byLabelClass.put(labelClass, residueIndices);
							}
						}
						
						return residueIndices;
					}
					
					public void appendEntries(final Vector entries, final Entry sampleEntry)
					{
						for (Class labelClass : byLabelClass.keySet())
						{
							final RangeMap2 subtree = byLabelClass.get(labelClass);
							
							subtree.collapse();
							
							sampleEntry.residueLabelClass = labelClass;
							
							final int rangeCount = subtree.getRangeCount();
							for(int i = 0; i < rangeCount; i++)
							{
								final int[] range = subtree.getRange(i);
								
								final Entry entry = sampleEntry.createCopy();
								entry.range = range;
								entries.add(entry);
							}
						}
						
						for (String label : byLabel.keySet())
						{
							final RangeMap2 subtree = byLabel.get(label);
							
							subtree.collapse();
							
							sampleEntry.residueLabelClass = ResidueLabelCustom.class;
							sampleEntry.customLabel = label;
							
							final int rangeCount = subtree.getRangeCount();
							for(int i = 0; i < rangeCount; i++) {
								final int[] range = subtree.getRange(i);
								
								final Entry entry = sampleEntry.createCopy();
								entry.range = range;
								entries.add(entry);
							}
						}
					}
				}
				public HashMap<Float, TreeBelowQuality> byQuality = new HashMap<Float, TreeBelowQuality>();
				
				public TreeBelowQuality getSubtree(final Float quality) {
					TreeBelowQuality subtree = (TreeBelowQuality)byQuality.get(quality);
					if(subtree == null) {
						subtree = new TreeBelowQuality();
						byQuality.put(quality, subtree);
					}
					
					return subtree;
				}
				
				public void appendEntries(final Vector entries, final Entry sampleEntry)
				{
					for (Float quality : byQuality.keySet())
					{
						final TreeBelowQuality subtree = byQuality.get(quality);
						
						sampleEntry.geometryQuality = quality.floatValue();
						
						subtree.appendEntries(entries, sampleEntry);
					}
				}
			}
			
			public HashMap<RibbonForm, TreeBelowForm> byForm = new HashMap<RibbonForm, TreeBelowForm>();	// key: Integer form. value: TreeBelowForm.
			
			public TreeBelowForm getSubtree(final RibbonForm form) {
				TreeBelowForm subtree = (TreeBelowForm)byForm.get(form);
				if(subtree == null) {
					subtree = new TreeBelowForm();
					byForm.put(form, subtree);
				}
				
				return subtree;
			}
			
			public void appendEntries(final Vector entries, final Entry sampleEntry)
			{
				for (RibbonForm form : byForm.keySet())
				{
					final TreeBelowForm subtree = byForm.get(form);
					
					sampleEntry.geometryForm = form;
					
					subtree.appendEntries(entries, sampleEntry);
				}
			}
		}
		
		public class TreeBelowRed
		{
			public class TreeBelowGreen
			{
				private final HashMap<Float, TreeBelowColors> byBlue = new HashMap<Float, TreeBelowColors>();	// key: Float blue. value: TreeBelowColors.
				
				public TreeBelowColors getSubtree(final Float blue) {
					TreeBelowColors subtree = (TreeBelowColors)byBlue.get(blue);
					if(subtree == null) {
						subtree = new TreeBelowColors();
						byBlue.put(blue, subtree);
					}
					
					return subtree;
				}
				
				public void appendEntries(final Vector entries, final Entry sampleEntry)
				{
					for (Float blue : byBlue.keySet())
					{
						final TreeBelowColors subtree = byBlue.get(blue);
						
						sampleEntry.rgb[2] = blue.floatValue();
						
						subtree.appendEntries(entries, sampleEntry);
					}
				}
			}
			
			private final HashMap<Float, TreeBelowGreen> byGreen = new HashMap<Float, TreeBelowGreen>();	// key: Float green. value: TreeBelowBlue.
			
			public TreeBelowGreen getSubtree(final Float green) {
				TreeBelowGreen subtree = (TreeBelowGreen)byGreen.get(green);
				if(subtree == null) {
					subtree = new TreeBelowGreen();
					byGreen.put(green, subtree);
				}
				
				return subtree;
			}
			
			public void appendEntries(final Vector entries, final Entry sampleEntry)
			{
				for (Float green : byGreen.keySet())
				{
					final TreeBelowGreen subtree = byGreen.get(green);
					
					sampleEntry.rgb[1] = green.floatValue();
					
					subtree.appendEntries(entries, sampleEntry);
				}
			}
		}
		
		private final HashMap<Class, TreeBelowColors> byResidueColor = new HashMap<Class, TreeBelowColors>();	// key: ResidueColor.getClass() (not ResidueColorByRgb). value: TreeBelowColors. 
		private final HashMap<Float, TreeBelowRed> byRed = new HashMap<Float, TreeBelowRed>();	// if ResidueColor is ResidueColorByRgb, this is used instead... value: TreeBelowRed.
		
		private TreeBelowRed getSubtree(final Float red) {
			TreeBelowRed subtree = (TreeBelowRed)byRed.get(red);
			if(subtree == null) {
				subtree = new TreeBelowRed();
				byRed.put(red, subtree);
			}
			
			return subtree;
		}
		
		public TreeBelowColors getSubtree(final IResidueColor chainColor_, final Residue residue) {
			boolean isResidueColorCustom = false;
			IResidueColor chainColor = chainColor_;
			
			TreeBelowColors subtree = null;
			if(chainColor instanceof ResidueColorByRgb) {
				final ResidueColorByRgb rgb = (ResidueColorByRgb)chainColor;
				
				if(rgb.usesDefaultColorGenerator(residue)) {
					chainColor = rgb.getDefaultColorGenerator();
				} else {
					rgb.getResidueColor(residue, Constants.colorTemp);
					final Float red = new Float(Constants.colorTemp[0]);
					final Float green = new Float(Constants.colorTemp[1]);
					final Float blue = new Float(Constants.colorTemp[2]);
					
					final TreeBelowRed redSubtree = getSubtree(red);
					final TreeBelowRed.TreeBelowGreen greenSubtree = redSubtree.getSubtree(green);
					subtree = greenSubtree.getSubtree(blue);
					
					isResidueColorCustom = true;
				}
			}
			
			if(!isResidueColorCustom && chainColor != null) {
				final Class colorClass = chainColor.getClass();
				subtree = (TreeBelowColors)byResidueColor.get(colorClass);
				if(subtree == null) {
					subtree = new TreeBelowColors();
					byResidueColor.put(colorClass, subtree);
				}
			}
			
			return subtree;
		}
		
		/**
		 * Returns a vector ef Entries. 
		 */
		public Vector getEntries() {
			final Vector entries = new Vector();
			final Entry sampleEntry = new Entry();
			
			for (Float red : byRed.keySet())
			{
				final TreeBelowRed subtree = byRed.get(red);
				
				sampleEntry.residueColorClass = ResidueColorByRgb.class;
				if(sampleEntry.rgb == null) {
					sampleEntry.rgb = new float[3];
				}
				sampleEntry.rgb[0] = red.floatValue();
				
				subtree.appendEntries(entries, sampleEntry);
			}
			
			sampleEntry.rgb = null;
			
			for (Class residueColorClass : byResidueColor.keySet())
			{
				final TreeBelowColors subtree = byResidueColor.get(residueColorClass);
				
				sampleEntry.residueColorClass = residueColorClass;
				
				subtree.appendEntries(entries, sampleEntry);
			}
			
			return entries;
		}
		
		public class Entry {
			public int[] range = null;
			public Class residueColorClass = null;
			public float[] rgb = null;	// used only when residueColor == ResidueColorByRgb.class.
			public RibbonForm geometryForm = null;
			public float geometryQuality = -1;
			public Class residueLabelClass = null;
			public String customLabel = null;	// used when residueLabelClass == ResidueLabelCustom.class.
			
			public Entry createCopy() {
				final Entry copy = new Entry();
				
				if(range != null) {
					copy.range = new int[] {range[0], range[1]};
				}
				
				copy.residueColorClass = residueColorClass;
				copy.geometryForm = geometryForm;
				copy.geometryQuality = geometryQuality;
				copy.residueLabelClass = residueLabelClass;
				copy.customLabel = customLabel;
				
				if(rgb != null) {
					copy.rgb = new float[] {rgb[0], rgb[1], rgb[2]};
				}
				
				return copy;
			}
		}
	}
	
	protected Document document;
	protected String title;
	
	protected Element root;
	protected Element structureDocument;
	protected Element structure;
	protected Element structureStyles;
	protected Element visibility;
	protected Element styles;
	protected Element viewer;
	protected Element structureViewer;
	protected Element camera;
	protected Element viewpoint;
	protected Element environment;
	protected Element background;
	protected Element titleElement;
	protected Element fog;
	

	
	public SceneState()
	{
		document = null;
		title = "null";
		System.err.flush();
	}
	
	public String toString()
	{
		return title;
	}

	protected Document createDocument() throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
				// apparently a 'gotcha' when reading - the writer/reader
				// can error out if this not set (according to jdk1.6 doc.)
		
		Document doc = factory.newDocumentBuilder().newDocument();
		return doc;
	}
	
	protected Element appendRoot(final Document doc) {
		final Element root = doc.createElement("MBTRoot");
		root.setAttribute("version", "1.0");
		doc.appendChild(root);
		
		return root;
	}
	
	protected Element appendChild(final Document doc, final Element parent, final String title) {
		final Element child = doc.createElement(title);
		parent.appendChild(child);
		
		return child;
	}
	
	public void captureCurrentState(final String title)
	{
		try {
	    	document = createDocument();
			root = appendRoot(document);
			structureDocument = appendChild(document, root, "StructureDocument");
			structure = appendChild(document, structureDocument, "Structure");
			structureStyles = appendChild(document, structure, "StructureStyles");
			visibility = appendChild(document, structureStyles, "Visibility");
			styles = appendChild(document, structureStyles, "Styles");
			viewer = appendChild(document, structureDocument, "Viewer");
			structureViewer = appendChild(document, viewer, "StructureViewer");
			camera = appendChild(document, structureViewer, "Camera");
			viewpoint = appendChild(document, camera, "Viewpoint");
			environment = appendChild(document, structureViewer, "Environment");
			background = appendChild(document, environment, "Background");
			titleElement = appendChild(document, root, "Title");
			fog = appendChild(document, environment, "Fog");		
			
			titleElement.setAttribute("value", title);
			
			appendDocElements();
			
			final StructureModel model = AppBase.sgetModel();
			structure.setAttribute("url", model.getStructures().get(0).getUrlString());
			
			setVisibilityInfo(document, visibility);
			setStyleInfo(document, styles);
			setViewerInfo();
			
			this.title = title;
			
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		} catch (final TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
		
		Status.output(Status.LEVEL_REMARK, "State save complete.");
	}
	
	/**
	 * Override this if you need to add more elements to the document
	 */
	protected void appendDocElements()
	{
	}
	
	public boolean loadState(final File file)
	{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			document = factory.newDocumentBuilder().parse(new FileInputStream(file));
			
			// find the title.
			NodeList nodes = document.getElementsByTagName("Structure");
			Structure activeStruct = AppBase.sgetModel().getStructures().get(0);
			String nodeUrl = nodes.item(0).getAttributes().getNamedItem("url").getTextContent();
			if (activeStruct.getUrlString().equalsIgnoreCase(nodeUrl))
			{
				nodes = document.getElementsByTagName("Title");
				if(nodes.getLength() > 0)
				{
					final Element titleElement = (Element)nodes.item(0);
					title = titleElement.getAttribute("value");
					return true;
				}
			}
			
			else
				Status.output(Status.LEVEL_ERROR, "State file does not match active structure URL");

		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	protected void setViewerInfo()
	{
		final GlGeometryViewer glViewer = VFAppBase.sgetGlGeometryViewer();
		setViewerInfo(glViewer.getEye(), glViewer.getCenter(), glViewer.getUp());	
		fog.setAttribute("is_enabled", "false");  // glViewer.isFogEnabled ? "true" : "false");
		fog.setAttribute("start", "-1.0"); // glViewer.fogStart + "");
		fog.setAttribute("end", "-1.0"); // glViewer.fogEnd + "");		
	}	
	
	protected void setViewerInfo(final double[] eye, final double[] center, final double[] up)
	{
		final GlGeometryViewer glViewer = VFAppBase.sgetGlGeometryViewer();
		viewpoint.setAttribute("name", "Default");
		viewpoint.setAttribute("position", center[0] + " " + center[1] + " " + center[2]);
		viewpoint.setAttribute("orientation", eye[0] + " " + eye[1] + " " + eye[2]);
		viewpoint.setAttribute("up", up[0] + " " + up[1] + " " + up[2]);
		
		glViewer.getBackgroundColor(Constants.colorTemp);
		background.setAttribute("color", Constants.colorTemp[0] + " " + Constants.colorTemp[1] + " " + Constants.colorTemp[2]);		
	}
	
	protected void setVisibilityInfo(final Document doc, final Element visibility)
	{
		StructureModel model = AppBase.sgetModel();
		final JoglSceneNode node = (JoglSceneNode)model.getStructures().get(0).getStructureMap().getUData();
		final Structure struc = model.getStructures().get(0);
		final StructureMap sm = struc.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		final RangeMap2 atoms = new RangeMap2();
//		RangeMap2 bonds = new RangeMap2();
		final RangeMap2 residues = new RangeMap2();
		
		final Vector chains = new Vector();
		
		// visibility for atoms and bonds is handled via renderables in the GlGeometryViewer...
		JoglSceneNode.RenderablesMap renderables = node.getRenderablesMap();
		for (StructureComponent sc : renderables.keySet())
		{			
			if(sc.getStructureComponentType() == ComponentType.ATOM) {
				final Atom a = (Atom)sc;
				final int atomIndex = sm.getAtomIndex(a);
				atoms.addValue(atomIndex);
//			} else if(sc.getStructureComponentType() == ComponentType.BOND) {
//				Bond b = (Bond)sc;
//				int bondIndex = sm.getBondIndex(b);
//				bonds.addValue(bondIndex);
			} else if(sc.getStructureComponentType() == ComponentType.CHAIN) {
				chains.add(sc);
			}
		}
		
		// visibility for ribbons is handled via StructureStyles...
		final int chainSize = chains.size();
		for(int i = 0; i < chainSize; i++) {
			final Chain c = (Chain)chains.get(i);
			
			final int resCount = c.getResidueCount();
			for(int j = 0; j < resCount; j++) {
				final Residue r = c.getResidue(j);
				
				if(ss.isVisible(r)) {
					final int resIndex = sm.getResidueIndex(r);
					residues.addValue(resIndex);
				}
			}
		}
		
		// cause the maps to collapse so we can retreive the ranges from them.
//		residues.setCollapseOn(true);
		residues.collapse();
//		atoms.setCollapseOn(true);
		atoms.collapse();
//		bonds.setCollapseOn(true);
//		bonds.collapse();
		
		final int atomRangeCount = atoms.getRangeCount();
		for(int i = 0; i < atomRangeCount; i++) {
			final int[] range = atoms.getRange(i);
			
			final Element selector = appendChild(doc, visibility, "Selector");
			selector.setAttribute("scType", Atom.class.getName());
			selector.setAttribute("indexRange", range[0] + "-" + range[1]);
		}
		
//		final int bondRangeCount = bonds.getRangeCount();
//		for(int i = 0; i < bondRangeCount; i++) {
//			int[] range = bonds.getRange(i);
//			
//			Element selector = appendChild(doc, visibility, "Selector");
//			selector.setAttribute("scType", "org.rcsb.mbt.model.Bond");
//			selector.setAttribute("indexRange", range[0] + "-" + range[1]);
//		}
		
		final int residueRangeCount = residues.getRangeCount();
		for(int i = 0; i < residueRangeCount; i++) {
			final int[] range = residues.getRange(i);
			
			final Element selector = appendChild(doc, visibility, "Selector");
			selector.setAttribute("scType", Residue.class.getName());
			selector.setAttribute("indexRange", range[0] + "-" + range[1]);
		}
	}
	
	public void writeState(final File outFile) {
		// Prepare the DOM document for writing
        final Source source = new DOMSource(document);

        // Prepare the output file
        final Result result = new StreamResult(outFile);

        // Write the DOM document to the file
        Transformer xformer;
		try
		{
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (final TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (final TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (final TransformerException e) {
			e.printStackTrace();
		}
	}

	
	protected void setStyleInfo(final Document doc, final Element styles) {
		StructureModel model = AppBase.sgetModel();
		final JoglSceneNode node = (JoglSceneNode)model.getStructures().get(0).getStructureMap().getUData();
//		final GlGeometryViewer glViewer = VFAppBase.sgetGlGeometryViewer();
		final Structure struc = model.getStructures().get(0);
		final StructureMap sm = struc.getStructureMap();
//		final StructureStyles ss = sm.getStructureStyles();
		
		final AtomStyleMap atomStyleMap = new AtomStyleMap();
		final ResidueStyleMap residueStyleMap = new ResidueStyleMap();
		
		// index atoms and (ribbon) residues by their style
		JoglSceneNode.RenderablesMap renderables = node.getRenderablesMap();
		for (StructureComponent sc : renderables.keySet())
		{
			final DisplayListRenderable renderable = renderables.get(sc);
			
			if(sc.getStructureComponentType() == ComponentType.ATOM) {
				final Atom a = (Atom)sc;
				final AtomStyle style = (AtomStyle)renderable.style;
				final AtomGeometry geometry = (AtomGeometry)renderable.geometry;
				final AtomStyleMap.TreeBelowColors colorSubtree = atomStyleMap.getSubtree(style.getAtomColor(), a);
				final AtomStyleMap.TreeBelowColors.TreeBelowLabel labelSubtree = colorSubtree.getSubtree(style.getAtomLabel(), a);
				final AtomStyleMap.TreeBelowColors.TreeBelowLabel.TreeBelowRadius radiusSubtree = labelSubtree.getSubtree(style.getAtomRadius());
				final AtomStyleMap.TreeBelowColors.TreeBelowLabel.TreeBelowRadius.TreeBelowForm formSubtree = radiusSubtree.getSubtree(new Integer(geometry.getForm()));
				final RangeMap2 indices = formSubtree.getSubtree(new Float(geometry.getQuality()));
				indices.addValue(sm.getAtomIndex(a));
			} else if(sc.getStructureComponentType() == ComponentType.BOND) {
				// don't record bond styles...
			} else if(sc.getStructureComponentType() == ComponentType.CHAIN) {
				final Chain c = (Chain)sc;
				final ChainStyle style = (ChainStyle)renderable.style;
				final ChainGeometry geometry = (ChainGeometry)renderable.geometry;
				
				final int resCount = c.getResidueCount();
				for(int i = 0; i < resCount; i++) {
					final Residue r = c.getResidue(i);
//					if(ss.isVisible(r)) {
						final ResidueStyleMap.TreeBelowColors colorSubtree = residueStyleMap.getSubtree(style.getResidueColor(), r);
						if(colorSubtree != null) {
							final ResidueStyleMap.TreeBelowColors.TreeBelowForm formSubtree = colorSubtree.getSubtree(geometry.getRibbonForm());
							if(formSubtree != null) {
								final ResidueStyleMap.TreeBelowColors.TreeBelowForm.TreeBelowQuality qualitySubtree = formSubtree.getSubtree(new Float(geometry.getQuality()));
								if(qualitySubtree != null) {
									final RangeMap2 indices = qualitySubtree.getSubtree(style.getResidueLabel(), r);
									
									if(indices != null) {
										indices.addValue(sm.getResidueIndex(r));
									}
								}
							}
						}
//					}
				}
			}
		}
		
		// set atom styles...
		final Vector entries = atomStyleMap.getEntries();
		final int entriesCount = entries.size();
		for(int i = 0; i < entriesCount; i++) {
			final AtomStyleMap.Entry entry = (AtomStyleMap.Entry)entries.get(i);
			
			final Element selector = appendChild(doc, styles, "Selector");
			final Element style = appendChild(doc, selector, "Style");
			final Element colorClassElement = appendChild(doc, style, "Class");
			final Element radiusClassElement = appendChild(doc, style, "Class");
			final Element labelClassElement = appendChild(doc, style, "Class");
			final Element geometryClassElement = appendChild(doc, style, "Class");
//			Element qualityAttribute = appendChild(doc, geometryClassElement, "Attribute");
			final Element formAttrubute = appendChild(doc, geometryClassElement, "Attribute");
			
			selector.setAttribute("scType", Atom.class.getName());
			selector.setAttribute("indexRange", entry.range[0] + "-" + entry.range[1]);
			
			style.setAttribute("type", AtomStyle.class.getName());
			
			colorClassElement.setAttribute("type", IAtomColor.class.getName());
			colorClassElement.setAttribute("value", entry.atomColorClass.getName());
			
			if(entry.rgb != null) {
				final Element attribute = appendChild(doc, colorClassElement, "Attribute");
				attribute.setAttribute("name", "color");
				attribute.setAttribute("value", entry.rgb[0] + " " + entry.rgb[1] + " " + entry.rgb[2]);
			}
			
			labelClassElement.setAttribute("type", IAtomLabel.class.getName());
			labelClassElement.setAttribute("value", entry.atomLabelClass.getName());
			
			if(entry.customLabel != null) {
				final Element attribute = appendChild(doc, labelClassElement, "Attribute");
				attribute.setAttribute("name", "label");
				attribute.setAttribute("value", entry.customLabel);
			}
			
			radiusClassElement.setAttribute("type", IAtomRadius.class.getName());
			radiusClassElement.setAttribute("value", entry.atomRadiusClass.getName());
			
			geometryClassElement.setAttribute("type", Geometry.class.getName());
			formAttrubute.setAttribute("name", "form");
			String formString = null;
			if(entry.geometryForm == Geometry.FORM_FLAT) {
				formString = "FORM_FLAT";
			} else if(entry.geometryForm == Geometry.FORM_LINES) {
				formString = "FORM_LINES";
			} else if(entry.geometryForm == Geometry.FORM_POINTS) {
				formString = "FORM_POINTS";
			} else if(entry.geometryForm == Geometry.FORM_THICK) {
				formString = "FORM_THICK";
			}
			formAttrubute.setAttribute("value", formString);
		}
		
		// set (ribbon) residue styles...
		final Vector ribbonEntries = residueStyleMap.getEntries();
		final int ribbonEntriesCount = ribbonEntries.size();
		for(int i = 0; i < ribbonEntriesCount; i++) {
			final ResidueStyleMap.Entry entry = (ResidueStyleMap.Entry)ribbonEntries.get(i);
			
			final Element selector = appendChild(doc, styles, "Selector");
			final Element style = appendChild(doc, selector, "Style");
			final Element classElement = appendChild(doc, style, "Class");
			final Element geometryClassElement = appendChild(doc, style, "Class");
			final Element ribbonFormElement = appendChild(doc, geometryClassElement, "Attribute");
			
			selector.setAttribute("scType", Residue.class.getName());
			selector.setAttribute("indexRange", entry.range[0] + "-" + entry.range[1]);
			
			style.setAttribute("type", ChainStyle.class.getName());
			
			classElement.setAttribute("type", IResidueColor.class.getName());
			classElement.setAttribute("value", entry.residueColorClass.getName());
			
			if(entry.rgb != null) {
				final Element attribute = appendChild(doc, classElement, "Attribute");
				attribute.setAttribute("name", "color");
				attribute.setAttribute("value", entry.rgb[0] + " " + entry.rgb[1] + " " + entry.rgb[2]);
			}
			
			geometryClassElement.setAttribute("type", ChainGeometry.class.getName());
			ribbonFormElement.setAttribute("name", "ribbonForm");

			ribbonFormElement.setAttribute("value", entry.geometryForm.toString());
			
			// all residues have a ResidueLabelCustom class associated with them. Only put a label if there is one.
			if(entry.residueLabelClass != ResidueLabelCustom.class || (entry.customLabel != null && entry.customLabel.length() != 0)) {
				final Element labelClassElement = appendChild(doc, style, "Class");
				labelClassElement.setAttribute("type", IResidueLabel.class.getName());
				labelClassElement.setAttribute("value", entry.residueLabelClass.getName());
				
				if(entry.customLabel != null) {
					final Element attribute = appendChild(doc, labelClassElement, "Attribute");
					attribute.setAttribute("name", "label");
					attribute.setAttribute("value", entry.customLabel);
				}
			}
		}
	}
	
	
	/**
	 * Causes a mutation in the application so it matches this state.
	 */
	public void enact()
	{
		final GlGeometryViewer glViewer = VFAppBase.sgetGlGeometryViewer();
		
		enactVisibility();
		enactStyles();
		enactViewerOptions();
		
		glViewer.requestRepaint();
		
		Status.output(Status.LEVEL_REMARK, "State " + title + " restored.");
	}
	

	protected static final Pattern dash = Pattern.compile("\\-");
	
	protected void enactVisibility()
	{
		StructureModel model = AppBase.sgetModel();
		final JoglSceneNode node = (JoglSceneNode)model.getStructures().get(0).getStructureMap().getUData();
		final GlGeometryViewer glViewer = VFAppBase.sgetGlGeometryViewer();
		final Structure struc = model.getStructures().get(0);
		final StructureMap sm = struc.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();
		
		// determine which atoms and residues have been flagged as visible
		final NodeList visibilityList = document.getElementsByTagName("Visibility");
		if(visibilityList != null && visibilityList.getLength() > 0) {
			// there should only be one Visibility tag.
			final Element visibilityTag = (Element)visibilityList.item(0);
			
			final Object exists = new Object();	// something to put into the maps so the value isn't null.
			final HashMap visibleAtoms = new HashMap();
			final HashMap visibleResidues = new HashMap();
			
			final NodeList selectors = visibilityTag.getElementsByTagName("Selector");
			if(selectors == null) {
				return;
			}
			final int selectorSize = selectors.getLength();
			for(int i = 0; i < selectorSize; i++) {
				final Element selector = (Element)selectors.item(i);
				String indexRange = selector.getAttribute("indexRange");
				String type = selector.getAttribute("scType");
				
				if(indexRange == null || type == null) {
					continue;
				}
				type = type.trim();
				indexRange = indexRange.trim();
				
				final String[] indexPieces = dash.split(indexRange);
				if(indexPieces.length != 2 || indexPieces[0] == null || indexPieces[1] == null) {
					continue;
				}
				
				int startIndex = -1, endIndex = -1;
				try {
					startIndex = Integer.parseInt(indexPieces[0]);
					endIndex = Integer.parseInt(indexPieces[1]);
				} catch(final Exception e) {
					continue;
				}
				
				
				boolean isAtom = false;
				boolean isResidue = false;
				if(type.endsWith("Atom")) {
					isAtom = true;
				} else if(type.endsWith("Residue")) {
					isResidue = true;
				} else {
					continue;
				}
				
				for(int j = startIndex; j <= endIndex; j++)
				{
					if (isAtom) 
					{
						final Atom a = sm.getAtom(j);
						visibleAtoms.put(a, exists);
					}
					
					else if (isResidue)
					{
						final Residue r = sm.getResidue(j);
//						System.out.println("SceneState: visible residue: " + r.getCompoundCode());
						visibleResidues.put(r, exists);
					}
				}
			}
			
			// find all bonds for visible atoms...
			Iterator atomIt = visibleAtoms.keySet().iterator();
			final HashMap visibleBonds = new HashMap();
			while(atomIt.hasNext())
			{
				final Atom a = (Atom)atomIt.next();
				final Vector bondsVec = sm.getBonds(a);
				if(bondsVec != null) {
					final int bondsVecCount = bondsVec.size();
					for(int i = 0; i < bondsVecCount; i++) {
						// make sure both atoms are visible in the bond.
						final Bond b = (Bond)bondsVec.get(i);
						final Atom a0 = b.getAtom(0);
						final Atom a1 = b.getAtom(1);
						if((a0 == a || visibleAtoms.containsKey(a0)) && (a1 == a || visibleAtoms.containsKey(a1))) {
							visibleBonds.put(bondsVec.get(i), exists);
						}
					}
				}
			}
			
			// perform a delta on the visibility of the components so that not too much component generation needs to be done.
			JoglSceneNode.RenderablesMap renderables = node.getRenderablesMap();
			synchronized(renderables)
			{
				synchronized(glViewer.renderablesToDestroy)
				{
					Set<StructureComponent> renderablesKeys = new HashSet<StructureComponent>(renderables.keySet());
								// clone this, because the following loop expects to be able to
								// modify the 'renderables' collection
					
					for (StructureComponent comp : renderablesKeys)
					{
						final DisplayListRenderable renderable = renderables.get(comp);
						
						if(comp.getStructureComponentType() == ComponentType.ATOM)
						{
							final Atom a = (Atom)comp;
							if(visibleAtoms.remove(a) != exists)
							{
								glViewer.renderablesToDestroy.add(renderable);
								renderables.remove(comp);
							}
							
						}
						
						else if(comp.getStructureComponentType() == ComponentType.BOND)
						{
							final Bond b = (Bond)comp;
							if (visibleBonds.remove(b) != exists)
							{
								glViewer.renderablesToDestroy.add(renderable);
								renderables.remove(comp);
							}
						}
						
						else if(comp.getStructureComponentType() == ComponentType.CHAIN)
						{
							final Chain c = (Chain)comp;
							
							final int residueCount = c.getResidueCount();
							for(int i = 0; i < residueCount; i++) {
								final Residue r = c.getResidue(i);
								if(visibleResidues.remove(r) == exists) {
									ss.setVisible(r, true);
								} else {
									ss.setVisible(r, false);
								}
							}
						}
					}
				}
				
				atomIt = visibleAtoms.keySet().iterator();
				final AtomStyle dummyStyle = new AtomStyle();
				final AtomGeometry dummyGeometry = new AtomGeometry();
				while(atomIt.hasNext()) {
					final Atom a = (Atom)atomIt.next();
					// add a renderable with a dummy style and geometry. These will be replaced in the enactStyles() function.
					final DisplayListRenderable renderable = new DisplayListRenderable(a, dummyStyle, dummyGeometry);
					renderables.put(a, renderable);
				}
				
				final Iterator bondIt = visibleBonds.keySet().iterator();
				final BondStyle dummyBondStyle = new BondStyle();
				final BondGeometry dummyBondGeometry = new BondGeometry();
				while(bondIt.hasNext()) {
					final Bond b = (Bond)bondIt.next();
					// add a renderable with a dummy style and geometry. These will be replaced in the enactStyles() function.
					final DisplayListRenderable renderable = new DisplayListRenderable(b, dummyBondStyle, dummyBondGeometry);
					renderables.put(b, renderable);
				}
				
				// silently ignore any residues which have a visible delta. This shouldn't occur, but other viewers could interpret things differently.
			}
		}
	}
	
	protected final Pattern spaces = Pattern.compile("\\ ++");
	protected void enactStyles() {
		StructureModel model = AppBase.sgetModel();
		final JoglSceneNode node = (JoglSceneNode)model.getStructures().get(0).getStructureMap().getUData();
//		final GlGeometryViewer viewer = VFAppBase.sgetGlGeometryViewer();
		final Structure struc = model.getStructures().get(0);
		final StructureMap sm = struc.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles(); 
		
		JoglSceneNode.RenderablesMap renderables = node.getRenderablesMap();
		
		final NodeList styles = document.getElementsByTagName("Styles");
		if(styles == null || styles.getLength() == 0) {	// should be at least one Styles tag
			return;
		}
		
//		final HashMap dirtyRenderables = new HashMap();
		
		// reset residue coloring...
		final int chainCount = sm.getChainCount();
		for(int j = 0; j < chainCount; j++) {
			final Chain c = sm.getChain(j);
			final ChainStyle style = (ChainStyle)ss.getStyle(c);
			final ResidueColorByRgb rgb = new ResidueColorByRgb();
			style.setResidueColor(rgb);
		}
		
		// remove all labels...
		node.clearLabels();
		
		final Element stylesElement = (Element)styles.item(0);
		final NodeList selectors = stylesElement.getElementsByTagName("Selector");
		final int selectorsSize = selectors.getLength();
		for(int i = 0; i < selectorsSize; i++) {
			final Element selector = (Element)selectors.item(i);
			
			String indexRange = selector.getAttribute("indexRange");
			String type = selector.getAttribute("scType");
			
			if(indexRange == null || type == null) {
				continue;
			}
			type = type.trim();
			indexRange = indexRange.trim();
			
			final String[] indexPieces = dash.split(indexRange);
			if(indexPieces.length != 2 || indexPieces[0] == null || indexPieces[1] == null) {
				continue;
			}
			
			int startIndex = -1, endIndex = -1;
			try {
				startIndex = Integer.parseInt(indexPieces[0]);
				endIndex = Integer.parseInt(indexPieces[1]);
			} catch(final Exception e) {
				continue;
			}
			
			
			boolean isAtom = false;
			boolean isResidue = false;
			if(type.endsWith("Atom")) {
				isAtom = true;
			} else if(type.endsWith("Residue")) {
				isResidue = true;
			} else {
				continue;
			}
			
			final NodeList classList = selector.getElementsByTagName("Class");
			if(classList == null) {
				continue;
			}
			
			AtomStyle atomStyle = null;
			BondStyle bondStyle = null;
			AtomGeometry atomGeometry = null;
			BondGeometry bondGeometry = null;
			if(isAtom) {
				atomStyle = new AtomStyle();
				bondStyle = new BondStyle();
				atomGeometry = new AtomGeometry();
				bondGeometry = new BondGeometry();
				
				bondStyle.setBondColor(BondColorByAtomColor.create());
				bondStyle.setBondRadius(BondRadiusByAtomRadius.create());
				bondGeometry.setShowOrder(true);
			}
			
			final int classSize = classList.getLength();
			for(int j = 0; j < classSize; j++) {
				final Element classElement = (Element)classList.item(j);
				final String typeAttribute = classElement.getAttribute("type");
				final String valueAttribute = classElement.getAttribute("value");
				
				try {
					if(isAtom) {
						if(typeAttribute.endsWith("AtomColor"))
						{
							IAtomColor color = null;
							
							if(valueAttribute.equals(AtomColorByRandom.class.getName()))
							{
								color = AtomColorByRandom.create();
							} else if(valueAttribute.equals(AtomColorByRgb.class.getName())) {
								// should have an Attribute child.
								final NodeList attributes = classElement.getElementsByTagName("Attribute");
								if(attributes == null || attributes.getLength() == 0) {
									continue;
								}
								
								final Element attribute = (Element)attributes.item(0);
								final String name = attribute.getAttribute("name");
								final String value = attribute.getAttribute("value");
								if(name == null || value == null || !name.equals("color")) {
									continue;
								}
								
								final String[] split = spaces.split(value);
								if(split.length < 3) {
									continue;
								}
								
								float red = -1, green = -1, blue = -1;
								try {
									red = Float.parseFloat(split[0]);
									green = Float.parseFloat(split[1]);
									blue = Float.parseFloat(split[2]);
								} catch(final Exception e) {
									continue;
								}
								
								color = new AtomColorByRgb(red, green, blue);
							} else if(valueAttribute.equals(AtomColorByBFactor.class.getName())) {
								color = AtomColorByBFactor.create();
							} else if(valueAttribute.equals(AtomColorByElement.class.getName())) {
								color = AtomColorByElement.create();
							} else if(valueAttribute.equals(AtomColorByResidueColor.class.getName())) {
								color = AtomColorByResidueColor.create();
							}
							
							atomStyle.setAtomColor(color);
						} else if(typeAttribute.endsWith("AtomRadius")) {
							IAtomRadius radius = null;
							if(valueAttribute.equals(AtomRadiusByCpk.class.getName())) {
								radius = AtomRadiusByCpk.create();
							} else if(valueAttribute.equals(AtomRadiusByScaledCpk.class.getName())) {
								radius = AtomRadiusByScaledCpk.create();
							}
							atomStyle.setAtomRadius(radius);
						} else if(typeAttribute.endsWith("AtomLabel")) {
							IAtomLabel label = null;
							if(valueAttribute.equals(AtomLabelByAtomCompound.class.getName())) {
								label = AtomLabelByAtomCompound.create();
							} else if(valueAttribute.equals(AtomLabelByAtomElement.class.getName())) {
								label = AtomLabelByAtomElement.create();
							} else if(valueAttribute.equals(AtomLabelByAtomName.class.getName())) {
								label = AtomLabelByAtomName.create();
							} else if(valueAttribute.equals(AtomLabelByChainId.class.getName())) {
								label = AtomLabelByChainId.create();
							} else if(valueAttribute.equals(CustomAtomLabel.class.getName())) {
								final NodeList attributes = classElement.getElementsByTagName("Attribute");
								if(attributes == null || attributes.getLength() == 0) {
									continue;
								}
								
								final Element attribute = (Element)attributes.item(0);
//								String name = attribute.getAttribute("name");
								final String value = attribute.getAttribute("value");
								if(value == null) {
									continue;
								}
								
								label = new CustomAtomLabel(value);
							} else if(valueAttribute.equals(AtomLabelNone.class.getName())) {
								label = AtomLabelNone.create();
							}
							if(label != null) {
								atomStyle.setAtomLabel(label);
							}
						} else if(typeAttribute.endsWith("Geometry")) {
							final NodeList attributes = classElement.getElementsByTagName("Attribute");
							if(attributes == null) {
								continue;
							}
							
							final int attributesLength = attributes.getLength();
							for(int k = 0; k < attributesLength; k++) {
								final Element attribute = (Element)attributes.item(k);
								final String name = attribute.getAttribute("name");
								final String value = attribute.getAttribute("value");
								if(value == null || name == null) {
									continue;
								}
								
								int form = -1;
								if(value.equals("FORM_THICK")) {
									form = Geometry.FORM_THICK;
								} else if(value.equals("FORM_FLAT")) {
									form = Geometry.FORM_FLAT;
								} else if(value.equals("FORM_LINES")) {
									form = Geometry.FORM_LINES;
								} else if(value.equals("FORM_POINTS")) {
									form = Geometry.FORM_POINTS;
								}
								
								if(name.equals("form")) {
									atomGeometry.setForm(form);
									bondGeometry.setForm(form);
								}
							}
						}
					} else if(isResidue) {
						// get unique chains and corresponding ResidueColorByRgb objects...
						final HashMap<Chain, DisplayListRenderable> uniqueChains = new HashMap<Chain, DisplayListRenderable>();
						for(int k = startIndex; k <= endIndex; k++) {
							final Residue r = sm.getResidue(k);
							final Chain chain = r.getFragment().getChain();
//							DisplayListRenderable renderable = (DisplayListRenderable)viewer.renderables.get(chain);
							
							uniqueChains.put(chain, null);
						}
						
						if(typeAttribute.endsWith("ResidueColor")) {
							
							if(valueAttribute.equals(ResidueColorByRandom.class.getName()))
							{
								for (Chain c : uniqueChains.keySet())
								{
									final DisplayListRenderable renderable = renderables.get(c);
									final ChainStyle style = (ChainStyle)renderable.style;
									IResidueColor color_ = style.getResidueColor();
									if(!(color_ instanceof ResidueColorByRgb)) {
										color_ = new ResidueColorByRgb();
									}
									final ResidueColorByRgb color = (ResidueColorByRgb)color_;
									color.setDefaultColorGenerator(ResidueColorByRandom.create());
								}
								
							}
							
							else if(valueAttribute.equals(ResidueColorByRgb.class.getName()))
							{
								// should have an Attribute child.
								final NodeList attributes = classElement.getElementsByTagName("Attribute");
								if(attributes == null || attributes.getLength() == 0) {
									continue;
								}
								
								final Element attribute = (Element)attributes.item(0);
								final String name = attribute.getAttribute("name");
								final String value = attribute.getAttribute("value");
								if(name == null || value == null || !name.equals("color")) {
									continue;
								}
								
								final String[] split = spaces.split(value);
								if(split.length < 3) {
									continue;
								}
								
								final float[] rgb = {0,0,0};
								try {
									rgb[0] = Float.parseFloat(split[0]);
									rgb[1] = Float.parseFloat(split[1]);
									rgb[2] = Float.parseFloat(split[2]);
								} catch(final Exception e) {
									continue;
								}
								
								for(int k = startIndex; k <= endIndex; k++) {
									final Residue r = sm.getResidue(k);
									final Chain c = r.getFragment().getChain();
									
									final DisplayListRenderable renderable = renderables.get(c);
									final ChainStyle style = (ChainStyle)renderable.style;
									IResidueColor color_ = style.getResidueColor();
									if(!(color_ instanceof ResidueColorByRgb)) {
										color_ = new ResidueColorByRgb();
									}
									final ResidueColorByRgb color = (ResidueColorByRgb)color_;
									color.setColor(r, rgb);
								}
							}
							
							else if(valueAttribute.equals(ResidueColorByRandomFragmentColor.class.getName()))
							{
								for (Chain c : uniqueChains.keySet())
								{
									final DisplayListRenderable renderable = renderables.get(c);
									final ChainStyle style = (ChainStyle)renderable.style;
									IResidueColor color_ = style.getResidueColor();
									if(!(color_ instanceof ResidueColorByRgb)) {
										color_ = new ResidueColorByRgb();
									}
									final ResidueColorByRgb color = (ResidueColorByRgb)color_;
									color.setDefaultColorGenerator(ResidueColorByRandomFragmentColor.create(struc));
								}
							}
							
							else if(valueAttribute.equals(ResidueColorByResidueCompound.class.getName()))
							{
								for (Chain c : uniqueChains.keySet())
								{
									final DisplayListRenderable renderable = renderables.get(c);
									final ChainStyle style = (ChainStyle)renderable.style;
									IResidueColor color_ = style.getResidueColor();
									if(!(color_ instanceof ResidueColorByRgb)) {
										color_ = new ResidueColorByRgb();
									}
									final ResidueColorByRgb color = (ResidueColorByRgb)color_;
									color.setDefaultColorGenerator(ResidueColorByResidueCompound.create());
								}
							}
							
							else if(valueAttribute.equals(ResidueColorByResidueIndexDna.class.getName()))
							{
								for (Chain c : uniqueChains.keySet())
								{
									final DisplayListRenderable renderable = renderables.get(c);
									final ChainStyle style = (ChainStyle)renderable.style;
									IResidueColor color_ = style.getResidueColor();
									if(!(color_ instanceof ResidueColorByRgb)) {
										color_ = new ResidueColorByRgb();
									}
									final ResidueColorByRgb color = (ResidueColorByRgb)color_;
									color.setDefaultColorGenerator(ResidueColorByResidueIndexDna.create());
								}
							}
							
							else if(valueAttribute.equals(ResidueColorByFragmentType.class.getName()))
							{
								for (Chain c : uniqueChains.keySet())
								{
									final DisplayListRenderable renderable = renderables.get(c);
									final ChainStyle style = (ChainStyle)renderable.style;
									IResidueColor color_ = style.getResidueColor();
									if(!(color_ instanceof ResidueColorByRgb)) {
										color_ = new ResidueColorByRgb();
									}
									final ResidueColorByRgb color = (ResidueColorByRgb)color_;
									color.setDefaultColorGenerator(ResidueColorByFragmentType.create());
								}
							}
							
							else if(valueAttribute.equals(ResidueColorByHydrophobicity.class.getName()))
							{
								for (Chain c : uniqueChains.keySet())
								{
									final DisplayListRenderable renderable = renderables.get(c);
									final ChainStyle style = (ChainStyle)renderable.style;
									IResidueColor color_ = style.getResidueColor();
									if(!(color_ instanceof ResidueColorByRgb)) {
										color_ = new ResidueColorByRgb();
									}
									final ResidueColorByRgb color = (ResidueColorByRgb)color_;
									color.setDefaultColorGenerator(ResidueColorByHydrophobicity.create());
								}
							}
							
							else if(valueAttribute.equals(ResidueColorByResidueIndex.class.getName()))
							{
								for (Chain c : uniqueChains.keySet())
								{
									final DisplayListRenderable renderable = renderables.get(c);
									final ChainStyle style = (ChainStyle)renderable.style;
									IResidueColor color_ = style.getResidueColor();
									if(!(color_ instanceof ResidueColorByRgb)) {
										color_ = new ResidueColorByRgb();
									}
									final ResidueColorByRgb color = (ResidueColorByRgb)color_;
									color.setDefaultColorGenerator(ResidueColorByResidueIndex.create());
								}
							}
						}
						
						else if(typeAttribute.endsWith("ResidueLabel"))
						{
							if(valueAttribute.equals(ResidueLabelCustom.class.getName()))
							{
								final NodeList attributes = classElement.getElementsByTagName("Attribute");
								if(attributes == null || attributes.getLength() == 0) {
									continue;
								}
								
								final Element attribute = (Element)attributes.item(0);
//								String name = attribute.getAttribute("name");
								final String value = attribute.getAttribute("value");
								if(value == null) {
									continue;
								}
								
								final Residue[] resArray = new Residue[endIndex - startIndex + 1];
								for(int k = startIndex, l = 0; k <= endIndex; k++, l++) {
									resArray[l] = sm.getResidue(k);
								}
								node.createAndAddLabel(resArray, value);
							}
						}
						
						else if(typeAttribute.endsWith("ChainGeometry"))
						{
							final NodeList attributes = classElement.getElementsByTagName("Attribute");
							if(attributes == null)
								continue;
							
							final int attributesLength = attributes.getLength();
							for(int k = 0; k < attributesLength; k++) {
								final Element attribute = (Element)attributes.item(k);
								final String name = attribute.getAttribute("name");
								final String value = attribute.getAttribute("value");
								if(value == null || name == null) {
									continue;
								}
								
								if(name.equals("ribbonForm")) {
									RibbonForm form = RibbonForm.valueOf(value);
									if (form == null) continue;
									
									for (Chain c : uniqueChains.keySet())
									{
										final DisplayListRenderable renderable = renderables.get(c);
										final ChainGeometry geom = (ChainGeometry)renderable.geometry;
										
										final RibbonForm oldForm = geom.getRibbonForm();
										boolean oldRibbonsSmoothed = geom.isRibbonsAreSmoothed();
										geom.setRibbonForm(form);
										geom.setRibbonsAreSmoothed(true);
										
										if(oldForm != form || !oldRibbonsSmoothed) {
											renderable.setDirty();
										}
									}
								}
							}
						}
					}
				} catch(final Exception e) {e.printStackTrace();}	// silently ignore any problems.
			}
			
			if(isAtom) {
				final Vector atoms = new Vector();
				for(int j = startIndex; j <= endIndex; j++) {
					final Atom a = sm.getAtom(j);
					atoms.add(a);
					
					final DisplayListRenderable renderable = renderables.get(a);
					if(renderable != null) {
						final AtomStyle oldStyle = (AtomStyle)renderable.style;
						renderable.style = atomStyle;
						ss.setStyle(a, atomStyle);
						final AtomGeometry oldGeometry = (AtomGeometry)renderable.geometry;
						renderable.geometry = atomGeometry;
						
						if(oldGeometry.getForm() != atomGeometry.getForm() || oldGeometry.getQuality() != atomGeometry.getQuality() || oldStyle.getAtomLabel().getClass() != atomStyle.getAtomLabel().getClass() || oldStyle.getAtomRadius().getClass() != atomStyle.getAtomRadius().getClass()) {
							renderable.setDirty();
						} 
						
						if(oldStyle.getAtomLabel() instanceof CustomAtomLabel && atomStyle.getAtomLabel() instanceof CustomAtomLabel) {
							final CustomAtomLabel oldLabel = (CustomAtomLabel)oldStyle.getAtomLabel();
							final CustomAtomLabel newLabel = (CustomAtomLabel)atomStyle.getAtomLabel();
							if(oldLabel.getAtomLabel(a).equals(newLabel.getAtomLabel(a))) {
								renderable.setDirty();
							}
						}
					}
				}
				
				final Vector bonds = sm.getBonds(atoms);
				final int bondsSize = bonds.size();
				for(int j = 0; j < bondsSize; j++) {
					final Bond b = (Bond)bonds.get(j);
					
					final DisplayListRenderable renderable = renderables.get(b);
					if(renderable != null) {
						renderable.style = bondStyle;
						ss.setStyle(b, bondStyle);
						final BondGeometry oldGeometry = (BondGeometry)renderable.geometry;
						renderable.geometry = bondGeometry;
						
						if(oldGeometry.getForm() != bondGeometry.getForm() || oldGeometry.getQuality() != bondGeometry.getQuality() || oldGeometry.getShowOrder() != bondGeometry.getShowOrder()) {
							renderable.setDirty();
						}
					}
				}
			}
		}
	}

	
	protected double[] currentOrientation, currentPosition, currentUp;
	protected float curFogStart, curFogEnd;
	protected boolean isFogEnabled;

	protected void enactViewerOptions()
	{	
		try {
			final NodeList viewpoints = document.getElementsByTagName("Viewpoint");
			
			if(viewpoints != null && viewpoints.getLength() != 0 /* && fogs != null && fogs.getLength() != 0 */)
			{
				final Element viewpoint = (Element)viewpoints.item(0);
/* **
// no fog, yet...
				final Element fog = (Element)fogs.item(0);
* **/
				
/* **
// XXX_DEBUG uncomment for debugging
				String name = viewpoint.getAttribute("name");
* **/
				
				final String orientation = viewpoint.getAttribute("orientation");
				final String position = viewpoint.getAttribute("position");
				final String up = viewpoint.getAttribute("up");

/* **
// no fog, yet...
				final String fogEnabledSt = fog.getAttribute("is_enabled");
				final String fogStartSt = fog.getAttribute("start");
				final String fogEndSt = fog.getAttribute("end");
* **/
				
				if (orientation != null && position != null && up != null /* && fogEnabledSt != null && fogEndSt != null && fogStartSt != null */) {
					final String[] orientationSplit = spaces.split(orientation);
					final String[] positionSplit = spaces.split(position);
					final String[] upSplit = spaces.split(up);
					
					if(upSplit.length >= 3 && orientationSplit.length >= 3 && positionSplit.length >= 3) {
						final double[] orientationArray = new double[orientationSplit.length];
						final double[] positionArray = new double[positionSplit.length];
						final double[] upArray = new double[upSplit.length];
						
						for(int i = 0; i < orientationSplit.length; i++) {
							orientationArray[i] = Double.parseDouble(orientationSplit[i]);
							positionArray[i] = Double.parseDouble(positionSplit[i]);
							upArray[i] = Double.parseDouble(upSplit[i]);
						}
						
//						if(movementThread != null && movementThread.isRunning()) {
//							movementThread.stop();
//						}
						
//						final boolean newIsFogEnabled = false;  //// fogEnabledSt.equals("true");
						final float newFogStart = -1.0f;   //// Float.parseFloat(fogStartSt);
						final float newFogEnd = -1.0f;    //// Float.parseFloat(fogEndSt);

/* **
// no fog, yet...
						isFogEnabled = newIsFogEnabled;	// just set this - no reason to transition to the new state, etc.
						final float curFogStart = fogStart;
						final float curFogEnd = fogEnd;
* **/
						
						
						
//						viewer.lookAt(orientationArray, positionArray, upArray);
						
						fillLookInfo();
						
						ViewMovementThread.createMovementThread(currentOrientation, orientationArray,
								currentPosition, positionArray,
								currentUp, upArray,
								curFogStart, newFogStart,
								curFogEnd, newFogEnd).start();
					}
				}
			}
		} catch(final Exception e) {}
		
		try {
							// do we do this if fog is enabled??
			final NodeList backgrounds = document.getElementsByTagName("Background");
			if(backgrounds != null && backgrounds.getLength() != 0) {
				final Element background = (Element)backgrounds.item(0);
				
				final String color = background.getAttribute("color");
				
				if(color != null) {
					final String[] colorSplit = spaces.split(color);
					
					if(colorSplit.length >= 3) {
						final float[] colorArray = new float[colorSplit.length];
						
						for(int i = 0; i < colorSplit.length; i++) {
							colorArray[i] = Float.parseFloat(colorSplit[i]);
						}
						
						VFAppBase.sgetGlGeometryViewer().setBackgroundColor(colorArray[0], colorArray[1], colorArray[2], 1);
					}
				}
			}
		} catch(final Exception e) {}
	}
	
	/*
	 * default implementation.  Note that LigandExplorer overrides this to get the information from the node.
	 */
	protected void fillLookInfo()
	{
		GlGeometryViewer glViewer = VFAppBase.sgetGlGeometryViewer();
		
		currentOrientation = glViewer.getEye();
		currentPosition = glViewer.getCenter();
		currentUp = glViewer.getUp();
		
		curFogStart = -1.0f;
		curFogEnd = -1.0f;
	}
}
