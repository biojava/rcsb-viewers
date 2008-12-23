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
package org.rcsb.lx.ui;

import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.rcsb.lx.ui.FloatLimitField;
import org.rcsb.lx.ui.FloatLimitField.FloatLimitDocument;


/**
 * restict input to a three character float: one number to the right of the
 * decimal, and one to the left.
 * 
 * If as stated, this seems way overkill.  Should be handle-able with some
 * stock IO functions - 08-May-08 rickb
 * 
 * @author John Beaver
 *
 */
public class FloatLimitField extends JTextField
{
	private static final long serialVersionUID = 817696705775173647L;

	public FloatLimitField(final String s)
	{
		super(s);
	}

	protected Document createDefaultModel()
	{
		return new FloatLimitDocument(this);
	}

	class FloatLimitDocument extends PlainDocument
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 326787895128572076L;
		private FloatLimitField parentField;

		// Eclipse suggests that outer class elements not be referenced from
		// inner classes for performance reasons.

		public FloatLimitDocument(final FloatLimitField flf)
		{
			super();
			this.parentField = flf;
		}

		public void insertString(final int offs, final String inputStr,
				final javax.swing.text.AttributeSet a)
				throws BadLocationException
		{
			// if nothing was passed...
			if (inputStr == null || inputStr.length() == 0) {
				return;
			}

			// if more than one character was passed, this is most likely
			// the initial value of the field. Check it, then insert it.
			if (inputStr.length() > 1) {
				switch (inputStr.length()) {
				case 3:
					if (!Character.isDigit(inputStr.charAt(2))) {
						break;
					}
				case 2:
					if (inputStr.charAt(1) == '.'
							&& Character.isDigit(inputStr.charAt(0))) {
						super.insertString(offs, inputStr, a);
					}
				}
				return;
			}

			// the input is only one character. Input it appropriately
			final String oldText = this.parentField.getText();
			final String newText = oldText.substring(0, offs) + inputStr
					+ oldText.substring(offs);
			if (offs < oldText.length()
					&& ((Character.isDigit(inputStr.charAt(0)) && Character
							.isDigit(oldText.charAt(offs))) || (inputStr
							.charAt(0) == '.' && oldText.charAt(offs) == '.'))) {
				super.remove(offs, 1);
				super.insertString(offs, inputStr, a);
			}
			switch (newText.length()) {
			case 3:
				if (Character.isDigit(newText.charAt(0))
						&& newText.charAt(1) == '.'
						&& Character.isDigit(newText.charAt(2))) {
					super.insertString(offs, inputStr, a);
					return;
				}
				break;
			case 2:
				if ((Character.isDigit(newText.charAt(0)) && newText
						.charAt(1) == '.')
						|| (newText.charAt(0) == '.' && Character
								.isDigit(newText.charAt(1)))) {
					super.insertString(offs, inputStr, a);
					return;
				}
				break;
			case 1:
				if (Character.isDigit(newText.charAt(0))
						|| newText.charAt(0) == '.') {
					super.insertString(offs, inputStr, a);
				}
			}
		}

		public void remove(final int offs, final int length)
				throws BadLocationException {
			// there is no way to invalidate the field by a multiple
			// deletion, so let the superclass handle these normally.
			if (length > 1) {
				super.remove(offs, length);
				return;
			}

			final String oldText = this.parentField.getText();
			final String newText = oldText.substring(0, offs)
					+ oldText.substring(offs + 1);
			switch (newText.length()) {
			case 3:
				if (Character.isDigit(newText.charAt(0))
						&& newText.charAt(1) == '.'
						&& Character.isDigit(newText.charAt(2))) {
					super.remove(offs, length);
				}
				break;
			case 2:
				if ((Character.isDigit(newText.charAt(0)) && newText
						.charAt(1) == '.')
						|| (newText.charAt(0) == '.' && Character
								.isDigit(newText.charAt(1)))) {
					super.remove(offs, length);
				}
				break;
			case 1:
				if (Character.isDigit(newText.charAt(0))
						|| newText.charAt(0) == '.') {
					super.remove(offs, length);
				}
				break;
			case 0:
				super.remove(offs, length);
			}

			// if the removal was the dot...
			if (length == 1 && offs == 1) {
				if (this.parentField.getCaretPosition() == 2) {
					// if the removal of the dot was because of a left
					// removal (backspace key)
					super.remove(0, 1);
					this.parentField.setCaretPosition(0);
				} else if (this.parentField.getCaretPosition() == 1) {
					// if the removal of the dot was because of a right
					// removal (delete key)
					super.remove(1, 2);
				}
			}
		}
	}
}
