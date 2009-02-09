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
 * Created on 2009/02/07
 *
 */
package org.rcsb.mbt.structLoader;

import java.util.ArrayList;
import java.util.List;

import org.rcsb.mbt.model.geometry.ModelTransformationList;
import org.rcsb.mbt.model.geometry.ModelTransformationMatrix;
import org.rcsb.mbt.model.util.OrderedPair;

/**
 * Represents the data items for the generation of macromolecular assemblies, 
 * such as the biological units and generates the transformation matrices to
 * to build biological units. The used data from the pdbx_struct_assembly_gen 
 * pdbx_struct_oper_list categories in an mmCIF or PDBML file.
 * 
 * @author Peter Rose
 *
 */
public class BioUnitStructureAssembly {
	List<StructAssemblyGenItem> structAssemblyGenItems = new ArrayList<StructAssemblyGenItem>();
	List<ModelTransformationMatrix> modelTransformations = new ArrayList<ModelTransformationMatrix>();

	/**
	 * Add the data item from a pdbx_struct_oper_list category.
	 * 
	 * @param structOperList item representing pdbx_struct_oper_list
	 */
	public void addStructAssemblyGenItem(StructAssemblyGenItem structAssemblyGenItem) {
		structAssemblyGenItems.add(structAssemblyGenItem);
	}

	/**
	 * Add a model transformation matrix from the pdbx_struct_oper_list category
	 * @param modelTransformationMatrix transformation matrix from pdbx_struct_oper_list
	 */
	public void addModelTransformationMatrix(ModelTransformationMatrix modelTransformationMatrix) {
		modelTransformations.add(modelTransformationMatrix);
	}
	
	/**
	 * Returns a list of transformation matrices for the generation of a macromolecular
	 * assembly for the specified assembly Id. 
	 * 
	 * @param assemblyId Id of the macromolecular assembly to be generated
	 * @return list of transformation matrices to generate macromolecular assembly
	 */
	public ModelTransformationList getBioUnitTransformationList(String assemblyId) {
		ModelTransformationList transformations = getBioUnitTransformationsListUnaryOperators(assemblyId);
		transformations.addAll(getBioUnitTransformationsListBinaryOperators(assemblyId));
		transformations.trimToSize();
		return transformations;
	}
	
    private ModelTransformationList getBioUnitTransformationsListUnaryOperators(String assemblyId) {
    	ModelTransformationList transformations = new ModelTransformationList();
    	
    	for (StructAssemblyGenItem item: structAssemblyGenItems) {
			if (item.getAssemblyId().equals(assemblyId)) {
				
				List<String> asymIds = item.getAsymIdList();
				List<String> operators = item.getUnaryOperators();

				// apply unary operators to the specified chains
				for (String chainId : asymIds) {
					for (String operator : operators) {
						ModelTransformationMatrix original = getModelTransformationMatrix(operator);
						ModelTransformationMatrix transform = new ModelTransformationMatrix(original);
						transform.ndbChainId = chainId;
						transform.id = operator;
						transformations.add(transform);
					}
				}
			}
		}
    	
    	return transformations;
    }
    
    private ModelTransformationList getBioUnitTransformationsListBinaryOperators(String assemblyId) {
    	ModelTransformationList transformations = new ModelTransformationList();
    	
    	for (StructAssemblyGenItem item: structAssemblyGenItems) {
			if (item.getAssemblyId().equals(assemblyId)) {
				
				List<String> asymIds = item.getAsymIdList();
				List<OrderedPair<String>> operators = item.getBinaryOperators();
				
				// apply binary operators to the specified chains
				// Example 1M4X: generates all products of transformation matrices (1-60)(61-88)
				for (String chainId : asymIds) {
					for (OrderedPair<String> operator : operators) {
						ModelTransformationMatrix original1 = getModelTransformationMatrix(operator.getElement1());
						ModelTransformationMatrix original2 = getModelTransformationMatrix(operator.getElement2());
						ModelTransformationMatrix transform = ModelTransformationMatrix.multiply4square_x_4square2(original1, original2);
						transform.ndbChainId = chainId;
						transform.id = original1.id + "x" + original2.id;
						transformations.add(transform);
					}
				}
			}
		}
    	return transformations;
    }
    	
	private ModelTransformationMatrix getModelTransformationMatrix(String operator) {
		for (ModelTransformationMatrix transform: modelTransformations) {
			if (transform.id.equals(operator)) {
				return transform;
			}
		}
		return new ModelTransformationMatrix();
	}
	
	public String toString() {
		return "structAssemblyItems: " + structAssemblyGenItems.size() + 
		", modelTransformations: " + modelTransformations.size();
	}
}
