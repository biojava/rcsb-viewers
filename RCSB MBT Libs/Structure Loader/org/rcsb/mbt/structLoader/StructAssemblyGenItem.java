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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.rcsb.mbt.model.util.CartesianProduct;
import org.rcsb.mbt.model.util.OrderedPair;

/**
 * A StructAssemblyGenItem contains instructions how to create a
 * macromolecular assembly.
 * A StructOperListItem defines the chain ids (asym ids) and list
 * of transformations that need to applied to these chains to
 * create a macromolecular assembly.
 * 
 * @author Peter Rose
 *
 */
public class StructAssemblyGenItem {
	/**
	 * assemblyID is a primary key for the pdbx_struct_oper_list category.
	 * Each assemblyId refers to a specific macromolecular assembly, such 
	 * as a biological unit.
	 */
	private String assemblyId = "";
	/**
	 * detail specifies about this molecular assembly.
	 */
	private String detail = "";
	/**
	 * asymID is the list of chain ids that the operator expression
	 * should be applied to.
	 */
	private List<String> asymIdList = Collections.emptyList();
	/**
	 * Unary operator expressions are parsed stored unary operations.
	 * For example the operator expression "(1,2,3,4)" is stored as a list 1,2,3,4
	 */
	private List<String> unaryOperators = Collections.emptyList();
	/**
	 * Binary Operator expressions are parsed and stored as ordered pairs of
	 * binary operators. For example the operator expression "(1-60)(61-88)"
	 * is saved as a list of pairs {1,61}, {1,62}, .., {1,88}, ... {60,88}.
	 */
	private List<OrderedPair<String>> binaryOperators = Collections.emptyList();


	/**
	 * @return
	 */
	public String getAssemblyId() {
		return assemblyId;
	}

	public void setAssemblyId(String assemblyID) {
		this.assemblyId = assemblyID;
	}

	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}

	/**
	 * Returns a list of asym Ids (chain ids) for this
	 * macromolecular assembly.
	 * @return list of asym Ids
	 */
	public List<String> getAsymIdList() {
		return asymIdList;
	}

	/**
	 * Returns a list of operators for this assembly. The operators
	 * refer to the transformations that should be applied to
	 * the asym ids to generate this macromolecular assembly.
	 * @return the unary operators for this assembly
	 */
	public List<String> getUnaryOperators() {
		return unaryOperators;
	}

	/**
	 * Returns a list of operators for this assembly. The operators
	 * refer to the transformations that should be applied to
	 * the asym ids to generate this macromolecular assembly. 
	 * Each ordered pair refers to the multiplication 
	 * of the two transformation matrices in the
	 * pdbx_structure_oper_list category.
	 * @return the binary operators for this assembly
	 */
	public List<OrderedPair<String>> getBinaryOperators() {
		return binaryOperators;
	}

	/**
	 * Parses the asym_id_list from a mmCIF or PDBML file and
	 * sets the asymIdList.
	 * 
	 * @param asymIdString the asym_id_list from a mmCIF or PDBML file
	 * @throws IllegalArgumentException
	 */
	public void parseAsymIdString(String asymIdString) throws IllegalArgumentException {
		String tmp = asymIdString.trim();
		try {
			asymIdList = Arrays.asList(tmp.split(","));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid asym_id_list: " + asymIdString);
		}
	}

	/**
	 * Parses the operator expression and save the operators as a list
	 * of unary or binary operators (i.e. matrix multiplication, see below).
	 * Operation expressions are given in a compact notation and specify
	 * matrices from the operations list.
	 * An operation expression can be a comma-separated list 1, 5, 9,
	 * a dash-delimited range 1-60 or a matrix multiplication involving two
	 * or more lists or ranges. For instance, (X0)(1-20) specifies the 
	 * portion of the X174 procapsid crystal asymmetric unit belonging to 
	 * the first independent virus particle and corresponds
	 * to the 20 transformations [X0][1], [X0][2], ... , [X0][20].
	 * See C. Lawson, Acta Cryst., D64, 874-882, 2008.
	 *   
	 * @param operatorExpression the operator expression to be parsed
	 */
	public void parseOperatorExpressionString(String operatorExpression) throws IllegalArgumentException {
		String expression = operatorExpression.trim();
		
		// remove single quotes, i.e. '(1-49)' in 1CGM
		expression = expression.replaceAll("'", "");

		if (isUnaryExpression(expression)) {
			unaryOperators = parseUnaryOperatorExpression(expression);
		} else {
			binaryOperators = parseBinaryOperatorExpression(expression);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("assemblyId: " + assemblyId + "\n");
		sb.append("detail: " + detail + "\n");
		sb.append("asym_id_list: " + asymIdList + "\n");
		sb.append("unary operators: " + unaryOperators + "\n");
		sb.append("binary operators: " + binaryOperators + "\n");
		return sb.toString();
	}
	
	/**
	 * Checks if the passed in expression is a unary operator expression
	 * Example: (1,2,3) or (1-60) are unary operator expressions
	 *          (1-60)(61-88) is a binary operator expression, representing
	 *          a cartesian product of the two parenthesised lists
	 *          
	 * @param expression
	 * @return true if expression is a unary operator expression
	 */
	private static boolean isUnaryExpression(String expression) {
		int first = expression.indexOf("(");
		int last = expression.lastIndexOf("(");
		if (first < 0 || last < 0) {
			return true;
		}
		return ! (first == 0 && last > first);
	}

	private List<String> parseUnaryOperatorExpression(String operatorExpression) throws IllegalArgumentException {
		return parseSubExpression(operatorExpression);
	}

	private List<OrderedPair<String>> parseBinaryOperatorExpression(String expression) 
	throws IllegalArgumentException {
		// split operator expression, i.e. (1,2,3)(4,5) into two subexpressions
		String[] subExpressions = null;
		try {
			subExpressions = expression.split("\\)\\(");
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid oper_expression: " + expression);
		}
		if (subExpressions.length != 2) {
			throw new IllegalArgumentException("Invalid oper_expression: " + expression);
		}
		List<String> leftSide = parseSubExpression(subExpressions[0]);
		List<String> rightSide = parseSubExpression(subExpressions[1]);

		// form the cartesian product of the two lists
		CartesianProduct<String> product = new CartesianProduct<String>(leftSide, rightSide);
		return product.getOrderedPairs();
	}

	private List<String> parseSubExpression(String expression) throws IllegalArgumentException {
		// remove parenthesis, if any
		String tmp = expression.replace("(", "");
		tmp = tmp.replace(")", "");

		// separate the operators
		List<String> components = null;
		try {
			components = Arrays.asList(tmp.split(","));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid oper_expression: " + expression);
		}

		// expand ranges if present, i.e. 1-60 -> 1, 2, 3, ..., 60
		List<String> operators = new ArrayList<String>();
		for (String component : components) {
			if (component.contains("-")) {
				operators.addAll(expandRange(component));
			} else {
				operators.add(component);
			}
		}
		return operators;
	}

	/**
	 * Expands a range expression, i.e. (1-6) to a list 1,2,3,4,5,6
	 * @param expression the expression to be expanded
	 * @return list of items in range
	 * @throws IllegalArgumentException
	 */
	private static List<String> expandRange(String expression) throws IllegalArgumentException {
		int first = 0;
		int last = 0;
		try {
			String[] range = expression.split("-");
			first = Integer.parseInt(range[0]);
			last = Integer.parseInt(range[1]);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid range specification in oper_expression: " + expression);
		}
		
		List<String> expandedExpression = new ArrayList<String>(last-first+1);
		for (int i = first; i <= last; i++) {
			expandedExpression.add(String.valueOf(i));
		}
		return expandedExpression;
	}
}
