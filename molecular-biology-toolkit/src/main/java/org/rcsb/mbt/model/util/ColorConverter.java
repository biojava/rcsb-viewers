package org.rcsb.mbt.model.util;

import javax.vecmath.Color4f;
import java.awt.*;

/**
 * Created by ap3 on 27/01/2015.
 */

public class ColorConverter {

    public static Color4f convertColor4f(Color color) {
        return new Color4f(color);
    }

    public static Color4f[] convertColor4f(Color colors[]) {
        Color4f[] colors4 = new Color4f[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colors4[i] = convertColor4f(colors[i]);
        }
        return colors4;
    }
}
