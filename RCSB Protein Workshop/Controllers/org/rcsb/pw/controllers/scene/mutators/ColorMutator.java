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
package org.rcsb.pw.controllers.scene.mutators;

import java.awt.Color;
import java.util.Vector;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.AtomColorByRgb;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.IResidueColor;
import org.rcsb.mbt.model.attributes.ResidueColorByRgb;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.attributes.SurfaceStyle;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.options.ColorOptions;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.controllers.scene.SceneController;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;
import org.rcsb.vf.glscene.jogl.DisplayListRenderable;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;




public class ColorMutator extends MutatorBase
{
	private ColorOptions options = null; 
	
	public ColorMutator() {
		super();
		this.options = new ColorOptions();
	}
	
	
	@Override
	public boolean supportsBatchMode() {
		return true;
	}
	
	
	@Override
	public void doMutationSingle(final Object mutee) {
		mutees.clear();
		mutees.add(mutee);
		this.doMutation();
		mutees.clear();
	}

	
	@Override
	public void doMutation()
	{
		SceneController sceneController = VFAppBase.sgetSceneController();
		if(sceneController.isColorSelectorSampleModeEnabled() && mutees.size() > 0) {
			final Object mutee = mutees.iterator().next();
			
			this.performColorSample(mutee);
			
			sceneController.setColorSelectorSampleModeEnabled(false);
		}
		
		else
		{
			for (Object next : mutees)
				if(next instanceof Atom) {
					this.changeColor((Atom)next);
				} else if(next instanceof Bond) {
					this.changeColor((Bond)next);
				} else if(next instanceof Residue) {
					this.changeColor((Residue)next);
				} else if(next instanceof Chain) {
					this.changeColor((Chain)next);
				} else if(next instanceof ExternChain) {
					this.changeColor((ExternChain)next);
				} else if(next instanceof Fragment) {
					this.changeColor((Fragment)next);
				} else if(next instanceof Surface) {
					this.changeColor((Surface)next);
				} else if(next instanceof Structure) {
					final Structure s = (Structure)next;
					this.changeColor(s);
				}
		}
	}
	
	private void performColorSample(final Object mutee) {
		final float[] colorFl = {-1,-1,-1};
		
		if(mutee instanceof StructureComponent) {
			final StructureComponent mutee_ = (StructureComponent)mutee;
			final ComponentType type = mutee_.getStructureComponentType();
			
			if(type == ComponentType.ATOM) {
				final Atom a = (Atom)mutee;
				final AtomStyle style = (AtomStyle)a.structure.getStructureMap().getStructureStyles().getStyle(a);
				
				style.getAtomColor(a, colorFl);
			} else if(type == ComponentType.BOND) {
				final Bond b = (Bond)mutee;
				final BondStyle style = (BondStyle)b.structure.getStructureMap().getStructureStyles().getStyle(b);
				
				style.getBondColor(b, colorFl);
			} else if(type == ComponentType.FRAGMENT) {
				final Fragment f = (Fragment)mutee;
				final Chain c = f.getChain();
				final ChainStyle style = (ChainStyle)c.structure.getStructureMap().getStructureStyles().getStyle(c);
				
				style.getResidueColor(f.getResidue(f.getResidueCount() / 2), colorFl);
			} else if(type == ComponentType.RESIDUE) {
				final Residue r = (Residue)mutee;
				final StructureMap sm = r.structure.getStructureMap();
				
				final Chain c = sm.getChain(r.getChainId());
				final ChainStyle style = (ChainStyle)c.structure.getStructureMap().getStructureStyles().getStyle(c);
				
				style.getResidueColor(r, colorFl);
			} else if(type == ComponentType.CHAIN) {
				final Chain c = (Chain)mutee;
				
				final ChainStyle style = (ChainStyle)c.structure.getStructureMap().getStructureStyles().getStyle(c);
				style.getResidueColor(c.getResidue(c.getResidueCount() / 2), colorFl);
			}
			
			else if (mutee_ instanceof ExternChain)
			{
				final ExternChain c = (ExternChain)mutee;
				
				final ChainStyle style = (ChainStyle)c.structure.getStructureMap().getStructureStyles().getStyle(c.getMbtChainIterator().next());
				style.getResidueColor(c.getResidue(c.getResidueCount() / 2), colorFl);
			}

		} else if(mutee instanceof Structure) {
			final Structure s = (Structure)mutee;
			final StructureMap sm = s.getStructureMap();
			
			final Chain firstChain = sm.getChain(0);
			
			final ChainStyle style = (ChainStyle)sm.getStructureStyles().getStyle(firstChain);
			style.getResidueColor(firstChain.getResidue(firstChain.getResidueCount() / 2), colorFl);
		}
		
		if(colorFl[0] == -1 && colorFl[1] == -1 && colorFl[2] == -1) {
			(new Exception(mutee.getClass().getName() + " not handled.")).printStackTrace();
		} else {
			final Color color = new Color(colorFl[0], colorFl[1], colorFl[2]);
			ProteinWorkshop.sgetActiveFrame().getColorPreviewerPanel().getColorPane().setColor(color);
		}
	}

	public ColorOptions getOptions() {
		return this.options;
	}	
	
	private static final float[] colorFl = new float[] {0,0,0};
	
    private void changeColor(final Atom a) {
    	final Structure struc = a.structure;
        final StructureMap sm = struc.getStructureMap();
        final StructureStyles ss = sm.getStructureStyles();
        
        ActivationType pickLevel = MutatorBase.getActivationType();
        switch(pickLevel)
        {
        case ATOMS_AND_BONDS:
        	this.options.getCurrentColor().getColorComponents(ColorMutator.colorFl);
        	
            final DisplayListRenderable renderable = ((JoglSceneNode)sm.getUData()).getRenderable(a);
            if(renderable != null) {
            	final AtomStyle oldStyle = (AtomStyle)renderable.style;
            	final AtomStyle style = new AtomStyle();
                style.setAtomColor(new AtomColorByRgb(ColorMutator.colorFl));
                if(oldStyle != null) {
                	style.setAtomLabel(oldStyle.getAtomLabel());
                	style.setAtomRadius(oldStyle.getAtomRadius());
                }
                ss.setStyle(a, style);
                
            	renderable.style = style;
            }
            break;
        case RIBBONS:
            final Residue r = sm.getResidue(a);
            this.changeColor(r.getFragment());
            break;
        default:
            (new Exception("Invalid option: " + pickLevel)).printStackTrace();            
        }
    }
    
    private void changeColor(final Bond b) {
        final Structure struc = b.structure;
        final StructureMap sm = struc.getStructureMap();
        
        ActivationType pickLevel = MutatorBase.getActivationType();
        switch(pickLevel)
        {
       case ATOMS_AND_BONDS:
        	/*this.options.getCurrentColor().getColorComponents(colorFl);
            BondColorByRgb bondColor = new BondColorByRgb(colorFl);
            
            GlGeometryViewer viewer = Model.getSingleton().getViewer();
            DisplayListRenderable renderable = viewer.getRenderable(b);
            if(renderable != null) {
            	BondStyle oldStyle = (BondStyle)renderable.style;
            	BondStyle style = new BondStyle();
                style.setBondColor(bondColor);
                if(oldStyle != null) {
	                style.setBondForm(oldStyle.getBondForm());
	                style.setBondLabel(oldStyle.getBondLabel());
	                style.setBondRadius(oldStyle.getBondRadius());
                }
                ss.setStyle(b, style);
            	
            	renderable.style = style;
//            	renderable.setDirty();
            }*/
        	
        	// delegate to atoms...
        	this.changeColor(b.getAtom(0));
        	this.changeColor(b.getAtom(1));
            break;
        case RIBBONS:
            // even if this is between fragments, just change one of them arbitrarily.
            final Residue r = sm.getResidue(b.getAtom(0));
            this.changeColor(r.getFragment());
            break;
        default:
            (new Exception("Invalid option: " + pickLevel)).printStackTrace();            
        }
    }
    
    private void changeColor(final Residue r) {
        final Structure struc = r.structure;
        final StructureMap sm = struc.getStructureMap();
        final StructureStyles ss = sm.getStructureStyles();
        
        ActivationType pickLevel = MutatorBase.getActivationType();
        switch(pickLevel)
        {
        case ATOMS_AND_BONDS:
        	Vector<Atom> atoms = r.getAtoms();
            for (Atom a : atoms)
                this.changeColor(a);
            
            for (Bond b : sm.getBonds(atoms))
            	this.changeColor(b);
            break;
            
        case RIBBONS:
        	final Chain c = sm.getChain(r.getChainId());
            
            final DisplayListRenderable renderable = ((JoglSceneNode)sm.getUData()).getRenderable(c);
            if(renderable != null) {
            	this.options.getCurrentColor().getColorComponents(ColorMutator.colorFl);
            	
            	ChainStyle style = (ChainStyle)ss.getStyle(c);
                final IResidueColor residueColor = style.getResidueColor();
                ResidueColorByRgb residueColorByRgb = null;
                if(residueColor != null && residueColor instanceof ResidueColorByRgb) {
                	residueColorByRgb = (ResidueColorByRgb)residueColor;
                } else {
                	residueColorByRgb = new ResidueColorByRgb(residueColor);
                	style = new ChainStyle();
                	style.setResidueColor(residueColorByRgb);
                	ss.setStyle(c, style);
                }
                residueColorByRgb.setColor(r, ColorMutator.colorFl);               
            	renderable.style = style;
            }
            break;
            
        default:
            (new Exception("Invalid option: " + pickLevel)).printStackTrace();            
       }
    }
    
    private void changeColor(final Fragment f) {
    	for(int i = 0; i < f.getResidueCount(); i++) {
            final Residue r = f.getResidue(i);
            this.changeColor(r);
        }
    }
    
    private void changeColor(final Chain c) {
    	final int resCount = c.getResidueCount();
    	for(int i = 0; i < resCount; i++) {
    		final Residue r = c.getResidue(i);
    		this.changeColor(r);
    	}
    }
    
    private void changeColor(final ExternChain c)
    {
    	for (Residue r : c.getResiduesVec())
    		this.changeColor(r);
    }
    
    private void changeColor(final Structure s)
    {
		for (Chain c : s.getStructureMap().getChains())
			this.changeColor(c);
    }
    
    private void changeColor(final Surface s) {
        final StructureMap sm = s.getStructure().getStructureMap();
        final StructureStyles ss = sm.getStructureStyles();
        
        this.options.getCurrentColor().getColorComponents(ColorMutator.colorFl);
        	
        final DisplayListRenderable renderable = ((JoglSceneNode)sm.getUData()).getRenderable(s);
        if(renderable != null) {
        	final SurfaceStyle style = new SurfaceStyle();
        	style.setSurfaceColor(s, ColorMutator.colorFl);
        	ss.setStyle(s, style);
        	renderable.style = style;
        }
    }
}
