package org.rcsb.mbt.model.util;

/**
 *
 * @author Peter
 */
public class FastScanner {
    private String string;
    private String nextString;
    private int nextInt;
    private float nextFloat;
    private int index;
    private boolean empty;
    
    private static final char singleQuote = '\'';
    private static final char doubleQuote = '\"';
    
    /** Creates a new instance of FastScanner */
    public FastScanner() {
    }
    
    public FastScanner(String string) {
        this.setString(string);
    }
    
    public void setString(String string) {
        this.string = string;
        index = 0;
        empty = true;
    }
    
    public boolean hasNext() {
        boolean hasNext = true;
        if (empty)
            hasNext = getNext();
        
        return hasNext;
    }
    
    public String next() {
        if (empty)
            getNext();
        empty = true;
        return nextString;
    }
    
    public boolean hasNextInt() {
        if (! hasNext())
            return false;
        
        try {
            nextInt = Integer.parseInt(nextString);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
    
    public int nextInt() {
        if (empty)
            getNext();
        empty = true;
        return nextInt;
    }
    
    public boolean hasNextFloat() {
        if (! hasNext())
            return false;
        
        try {
            nextFloat = Float.parseFloat(nextString);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
    
    public float nextFloat() {
        if (empty)
            getNext();
        empty = true;
        return nextFloat;
    }
    
    private boolean getNext() {
        empty = false;
        int length = string.length();
        
        // find begining of next item
        for (;index < length && string.charAt(index) == ' '; index++) {
        }
        int beginIndex = index;
        
        int endIndex = beginIndex;
        
        if (index < length && string.charAt(index) == singleQuote) {
            if (index+1 < length) {
                // skipp past beginning quote character
                index++;
                beginIndex = index;
            } else {
                return false;
            }
            
            // find end of quoted string
            for (;index < length && string.charAt(index) != singleQuote; index++) {
            }
            endIndex = index;
            
            // if there are no white spaces, skipp to the end of this string
            // Example cif file: '2'-DEOXYGUANOSINE-5'-MONOPHOSPHATE'
            int bIndex = index;
            for (;bIndex < length && string.charAt(bIndex) != ' '; bIndex++) {
            }
            if (bIndex > endIndex) {
                index = bIndex;
            }
            // skipp multiple single quotes such as in 'O''' and 'O5'' and 'O3''
            // found in .cif files
//            for (;index < length && string.charAt(index) == singleQuote; index++);
            endIndex = index - 1;
//            System.out.println("quoted string:" + string.substring(beginIndex, endIndex));
        } else if (index < length && string.charAt(index) == doubleQuote) {
            if (index+1 < length) {
                // skipp past beginning quote character
                index++;
                beginIndex = index;
            } else {
                return false;
            }
            // find end of quoted string
            // Example: from cif file: HEM "N A" NA   N  0
            for (;index < length && string.charAt(index) != doubleQuote; index++) {
            }
            endIndex = index;
            
            // skipp multiple single quotes
            // found in .cif files
            for (;index < length && string.charAt(index) == doubleQuote; index++) {
            }
            endIndex = index - 1;
//            System.out.println("quoted string:" + string.substring(beginIndex, endIndex));
        } else {
            // find end of next item
            for (;index < length && string.charAt(index) != ' '; index++) {
            }
            endIndex = index;
        }
        
        if (beginIndex < endIndex) {
            nextString = string.substring(beginIndex, endIndex);
            return true;
        } else {
            return false;
        }
    }
}
