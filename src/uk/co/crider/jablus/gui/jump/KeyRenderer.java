/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package uk.co.crider.jablus.gui.jump;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.utils.ColourMixer;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.renderer.SimpleRenderer;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.Style;

public class KeyRenderer extends SimpleRenderer {
//    public final static String CONTENT_ID = "SCALE_BAR";
    public final static String CONTENT_ID = "KEY";
    private Style keyScale;
    private static final int NOTCHES = 5;
 
    /**
     *  Height of the increment boxes, in view-space units.
     */
//    private final static int BAR_HEIGHT = 10;
 //   private final static Color FILL2 = new Color(255, 204, 204);
 //   private final static Color FILL1 = Color.white;

    /**
     *  Distance from the right edge, in view-space units.
     */
 //   private final static int HORIZONTAL_MARGIN = 3;

    /**
     *  In view-space units; the actual increment may be a bit larger or smaller
     *  than this amount.
     */
  //  private final static int IDEAL_INCREMENT = 75;
   // private final static Color LINE_COLOR = Color.black;
    //private final static int TEXT_BOTTOM_MARGIN = 1;
   // private final static int UNIT_TEXT_BOTTOM_MARGIN = 1;
   // private final static Color TEXT_COLOR = Color.black;
   // private final static Color UNIT_TEXT_COLOR = Color.blue;

    /**
     *  Distance from the bottom edge, in view-space units.
     */
   // private final static int VERTICAL_MARGIN = 3;
   private final static String ENABLED_KEY = KeyRenderer.class +" - ENABLED";
  //  private final static int INCREMENT_COUNT = 5;
  private Font FONT = new Font("Dialog", Font.PLAIN, 10);
  private Font UNIT_FONT = new Font("Dialog", Font.BOLD, 11);

    public KeyRenderer(LayerViewPanel panel) {
        super(CONTENT_ID, panel);
    }

    public static boolean isEnabled(LayerViewPanel panel) {
        return panel.getBlackboard().get(ENABLED_KEY, false);
    }

    public static void setEnabled(boolean enabled, LayerViewPanel panel) {
        panel.getBlackboard().put(ENABLED_KEY, enabled);
    }
    
//    private Stroke stroke = new BasicStroke();
    
    protected void paint(Graphics2D g) {
        if(!isEnabled(panel) && keyScale == null) return;
        if(keyScale instanceof ColourRampStyle){
            int x = panel.getWidth() - 20;
            int y = panel.getHeight() - 125;
        	g.setColor(Color.BLACK);
        	g.drawRect(x + 5, y, 10, 100);
        	ColourRampStyle cRamp = (ColourRampStyle)keyScale;
        	TextLayout units = createTextLayout(cRamp.getUnits(), UNIT_FONT, g);
        	units.draw(g, (float)(x + 15 - units.getBounds().getWidth()), y + 120);
        	double min = cRamp.getMin();
        	double max = cRamp.getMax();
        	for(int i = 0; i <= NOTCHES;  i++){
        		g.drawLine(x + 3, y + (i * 100 / NOTCHES), x + 5, y + (i * 100 / NOTCHES));
        		TextLayout t = createTextLayout(Utils.roundString(
        				min + ((NOTCHES - i) * (max - min) / NOTCHES), 0
        		), FONT, g);
        		t.draw(g, (float)(x - t.getBounds().getWidth()), y + 5 + (i * 100 / NOTCHES));
        	}
        	int sy = y + 1;
        	for(int i = 0; i < 99; i++){
        		g.setColor(ColourMixer.interpolate((double)(98 - i) / 100, cRamp.getCol()));
        		g.drawLine(x + 6, sy + i, x + 14, sy + i);
        	}
        }
        else if(keyScale instanceof ColourTableStyle){
            int x = panel.getWidth() - 15;
            int y = panel.getHeight() - 20;
        	ColourTableStyle cTable = (ColourTableStyle)keyScale;
        	Map<Object, BasicStyle> table = cTable.getTable();
        	Iterator<Entry<Object, BasicStyle>> it = table.entrySet().iterator();
        	for(int i = 0; i < table.size(); i++){
        		Entry<Object, BasicStyle> e = it.next();
            	g.setColor(Color.BLACK);
            	TextLayout cat = createTextLayout(Constants.getName((Integer)e.getKey()), FONT, g);
            	cat.draw(g, (float)(x - 5 - cat.getBounds().getWidth()), y + 12 - i * 20);
        		g.drawRect(x, y - i * 20, 10, 15);
        		g.setColor(e.getValue().getFillColor());
        		g.fillRect(x + 1, y + 1 - i * 20, 9, 14);
        	}
        }
 
//        return panel.getHeight() - VERTICAL_MARGIN;
   }

    public void setStyle(Style s){
    	keyScale = s;
    }

    private TextLayout createTextLayout(String text, Font font, Graphics2D g) {
        return new TextLayout(text, font, g.getFontRenderContext());
    }

 /*   private void paintIncrement(int i, RoundQuantity increment, int incrementCount, Graphics2D g, double scale) {
        Rectangle2D.Double shape =
            new Rectangle2D.Double(
                x(i, increment, incrementCount, scale),
                barTop(),
                x(i + 1, increment, incrementCount, scale) - x(i, increment, incrementCount, scale),
                barBottom() - barTop());
        g.setColor(((i % 2) == 0) ? FILL1 : FILL2);
        g.fill(shape);
        g.setColor(LINE_COLOR);
        g.draw(shape);
    }

    private void paintIncrements(RoundQuantity increment, int incrementCount, Graphics2D g, double scale) {
        for (int i = 0; i < incrementCount; i++) {
            paintIncrement(i, increment, incrementCount, g, scale);
            paintLabel(i, increment, incrementCount, g, scale);
        }
    }

    private void paintLabel(int i, RoundQuantity increment, int incrementCount, Graphics2D g, double scale) {
        String text =
                new RoundQuantity(
                    increment.getMantissa() * (i + 1),
                    increment.getExponent(),
                    increment.getUnit()).getAmountString();
        Font font = FONT;
        g.setColor(TEXT_COLOR);

        int textBottomMargin = TEXT_BOTTOM_MARGIN;

        if (i == (incrementCount - 1)) {
            text = increment.getUnit().getName();
            font = UNIT_FONT;
            g.setColor(UNIT_TEXT_COLOR);
            textBottomMargin = UNIT_TEXT_BOTTOM_MARGIN;
        }

        TextLayout layout = createTextLayout(text, font, g);
        double center =
            MathUtil.avg(x(i, increment, incrementCount, scale), x(i + 1, increment, incrementCount, scale));
        layout.draw(
            g,
            (float) (center - (layout.getAdvance() / 2)),
            (float) (barBottom() - textBottomMargin));
    }

    private double x(int i, RoundQuantity increment, int incrementCount, double scale) {
        return HORIZONTAL_MARGIN + (i * increment.getModelValue() * scale);
    }
    
    */
}
