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
