package uk.co.crider.jablus.models.dairy.gui;

import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.gui.JablusWindow;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.utils.Utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.font.TextLayout;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import uk.co.crider.models.dairy.RandomGenerator;


/** A JPanel for viewing weather data as a series of media forecast style glyphs */
public class WeatherView extends JPanel{
	
	/** Unique class ID */
	private static final long serialVersionUID = -6962318828606819279L;
	private static final Font TEMP_FONT = new Font("Dialog", Font.BOLD, 9);
	private static final Font DAY_FONT = new Font("Dialog", Font.PLAIN, 11);
	public static final String BASE_DIR = "/" + uk.co.crider.jablus.Constants.JABLUS_GRAPHICS_DIR + "/" + "weather_icons" + "/";
	public Image[] GLYPH = new Image[]{
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "sun.png"             )),//  0
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud1.png"          )),//  1
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud1_sun.png"      )),//  2
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud1_rain1.png"    )),//  3
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud1_rain1_sun.png")),//  4
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud1_rain2.png"    )),//  5
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud1_rain2_sun.png")),//  6
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud1_snow1.png"    )),//  7
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud1_snow1_sun.png")),//  8
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud1_snow2.png"    )),//  9
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud1_snow2_sun.png")),// 10
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2.png"          )),// 11
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_sun.png"      )),// 12
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_rain1.png"    )),// 13
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_rain1_sun.png")),// 14
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_rain2.png"    )),// 15
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_rain2_sun.png")),// 16
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_rain3.png"    )),// 17
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_rain3_sun.png")),// 18
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_snow1.png"    )),// 19
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_snow1_sun.png")),// 20
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_snow2.png"    )),// 21
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_snow2_sun.png")),// 22
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_snow3.png"    )),// 23
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(BASE_DIR + "cloud2_snow3_sun.png")),// 24
	};
	
//	private DoubleData[] rain;
//	private DoubleData[] tmpAvg;
//	private DoubleData[] tmpMin;
//	private DoubleData[] rad;
//	private DoubleData[] radAvg;
	
	private WeatherGlyph[] glyphs;
	
	public WeatherView(
			DoubleData[] rain,
			DoubleData[] tmpAvg,
//			DoubleData[] tmpMin,
			DoubleData[] rad,
			DoubleData[] radAvg){
		super(new FlowLayout(FlowLayout.RIGHT, 5, 10));
//		this.rain   = rain;
//		this.tmpAvg = tmpAvg;
//		this.tmpMin = tmpMin;
//		this.rad    = rad;
//		this.radAvg = radAvg;
		glyphs = new WeatherGlyph[Time.DAYS_WEEK];
		for(int i = 0; i < glyphs.length; i++){
	//		JPanel p = new JPanel(new BorderLayout());
	//		p.setBorder(new EmptyBorder(2,7,2,7));
			glyphs[i] = new WeatherGlyph(
					i,
					rain[i],
					tmpAvg[i],
//					tmpMin[i],
					rad[i],
					radAvg[i]
				);
			add(glyphs[i]);
//			p.add(glyphs[i], BorderLayout.CENTER);
//			JLabel day = new JLabel(Time.DAY_NAMES[i]);
//			day.setHorizontalAlignment(JLabel.CENTER);
//			p.add(day, BorderLayout.SOUTH);
//			add(p);
		}
	}
	
	/** Re-draws the weather view to reflect updated weather data */
	public void redisplay(){
		for(int i = 0; i < glyphs.length; i++){
			glyphs[i].redisplay();
		}
	}
	
	/** Represents an individual weather glyph */
	class WeatherGlyph extends JLabel{
		/** Unique class ID */
        private static final long serialVersionUID = 5622513774105457454L;
		private DoubleData rain;
		private DoubleData tmpAvg;
//		private DoubleData tmpMin;
		private DoubleData rad;
		private DoubleData radAvg;
		private int day;
		public WeatherGlyph(
				int day,
				DoubleData rain,
				DoubleData tmpAvg,
//				DoubleData tmpMin,
				DoubleData rad,
				DoubleData radAvg){
			this.day = day;
			this.rain = rain;
			this.tmpAvg = tmpAvg;
//			this.tmpMin = tmpMin;
			this.rad = rad;
			this.radAvg = radAvg;
			setHorizontalAlignment(JLabel.CENTER);
			setVerticalAlignment(JLabel.CENTER);
			setBorder(new EtchedBorder());
			setBorder(new BevelBorder(BevelBorder.RAISED));
			setBorder(new LineBorder(Color.LIGHT_GRAY));
			setPreferredSize(new Dimension(45, 55));
			redisplay();
		}
		/** re-displays this glyph to represent the current weather data */
		public void redisplay(){
			setIcon(new ImageIcon(getGlyphFile()));
			setToolTipText(Time.DAY_NAMES[day] + " forecast:" +
					" rain " + Utils.roundString(rain.doubleValue(), 0) + "" + rain.getUnits() +
					", temp " + Utils.roundString(tmpAvg.doubleValue(), 0) + "" + tmpAvg.getUnits() +
					", sun " + Utils.roundString(rad.doubleValue(), 1) + "" + rad.getUnits()
				);
		}
		/** paints this glyph */
		public void paint(Graphics gr){
			super.paint(gr);
			Graphics2D g = (Graphics2D)gr;
    		TextLayout d = new TextLayout(
    				Time.DAY_NAMES[day],
    				DAY_FONT, g.getFontRenderContext());
    		g.setColor(Color.BLACK);
    		d.draw(g, 3f, (float)(3 + d.getBounds().getHeight()));			
    		TextLayout t = new TextLayout(
    				Utils.roundString(tmpAvg.doubleValue(), 1),
    				TEMP_FONT, g.getFontRenderContext());
    		//g.setColor(Color.BLACK);
    		t.draw(g, (float)(getBounds().getWidth() - 5 - t.getBounds().getWidth()), (float)(getBounds().getHeight() + 2 - t.getBounds().getHeight()));			
		}
		/** Returns the glyph file representing the current weather data */
		public Image getGlyphFile(){
			int c = 0, s = 0, r = 0, t = 0;
			// What level of precipitation to show
			r = rain.doubleValue() <= 0 ? 0 : rain.doubleValue() <= 5 ? 1 : rain.doubleValue() <= 20 ? 2 : 3;
			// Calculate cloud level
			c = rad.doubleValue() > radAvg.doubleValue() * 1.25 ? 0 :
				rad.doubleValue() < radAvg.doubleValue() * 0.75 ? 2 : 1;
			// Wheather to show sun
			s = rad.doubleValue() > 6 ? 1 : 0;
			// What kind of precipitation to show
			t = tmpAvg.doubleValue() < 2 ? 1 : 0;
//System.out.println("c:" + c + ", s:" + s + ", r:" + r + ", t:" + t);
			if(r == 0){ // No precipitation
				if(c == 0)     return GLYPH[0]; // Sun
				if(c == 1){
					if(s == 0) return GLYPH[1]; // Cloud
					           return GLYPH[2]; // Cloud + Sun
				}else{
					if(s == 0) return GLYPH[11]; // Cloud2
					           return GLYPH[12]; // Cloud2 + Sun
				}
			}
			if(r == 1){ // Precipitation level 1
				if(c <= 1){ // Cloud level 1
					if(t == 0){
						if(s == 0) return GLYPH[3]; // Cloud + Rain
						           return GLYPH[4]; // Cloud + Rain + Sun
					}
					else{
						if(s == 0) return GLYPH[7]; // Cloud + Snow
						           return GLYPH[8]; // Cloud + Snow + Sun
					}
				}else{     // Cloud level 2
					if(t == 0){
						if(s == 0) return GLYPH[13]; // Cloud + Rain
						           return GLYPH[14]; // Cloud + Rain + Sun
					}
					else{
						if(s == 0) return GLYPH[19]; // Cloud + Snow
						           return GLYPH[20]; // Cloud + Snow + Sun
					}					
				}
			}
			if(r == 2){ // Precipitation level 2
				if(c <= 1){ // Cloud level 1
					if(t == 0){
						if(s == 0) return GLYPH[5]; // Cloud + Rain
						           return GLYPH[6]; // Cloud + Rain + Sun
					}
					else{
						if(s == 0) return GLYPH[9]; // Cloud + Snow
						           return GLYPH[10]; // Cloud + Snow + Sun
					}
				}else{     // Cloud level 2
					if(t == 0){
						if(s == 0) return GLYPH[15]; // Cloud + Rain
						           return GLYPH[16]; // Cloud + Rain + Sun
					}
					else{
						if(s == 0) return GLYPH[21]; // Cloud + Snow
						           return GLYPH[22]; // Cloud + Snow + Sun
					}					
				}				
			}
			else{ // Precipitation level 3
				// Only cloud level 2 possible here
				if(t == 0){
					if(s == 0) return GLYPH[17]; // Cloud + Rain
					           return GLYPH[18]; // Cloud + Rain + Sun
				}
				else{
					if(s == 0) return GLYPH[23]; // Cloud + Snow
					           return GLYPH[24]; // Cloud + Snow + Sun
				}				
			}
		}
	}
	

	/** For testing */
	public static boolean running = true;
	/** For testing */
	public static void main(String[] args){
		DoubleData[] rain   = new DoubleData[Time.DAYS_WEEK];
		DoubleData[] tmpAvg = new DoubleData[Time.DAYS_WEEK];
//		DoubleData[] tmpMin = new DoubleData[Time.DAYS_WEEK];
		DoubleData[] rad    = new DoubleData[Time.DAYS_WEEK];
		DoubleData[] radAvg = new DoubleData[Time.DAYS_WEEK];
		for(int i = 0; i < Time.DAYS_WEEK; i++){
			rain[i]   = new DoubleData(Constants.INPUT_RAIN);
			tmpAvg[i] = new DoubleData(Constants.INPUT_TEMP);
//			tmpMin[i] = new DoubleData(Constants.INPUT_TEMP_MIN);
			rad[i]    = new DoubleData(Constants.INPUT_RAD);
			radAvg[i] = new DoubleData(Constants.INPUT_RAD_MEAN);
		}

		WeatherView weather = new WeatherView(rain, tmpAvg, rad, radAvg);
		JablusWindow f = new JablusWindow("Weather View", false, false, true){
            private static final long serialVersionUID = 1L;
			public void dispose(){
				running = false;
				super.dispose();
			}
		};
		f.add(weather);
		f.pack();
//		f.setLocationRelativeTo(parent);
//		redisplay();
		f.setVisible(true);
		int doy = 0;
		// Initialise weather generators
		Random rand = new Random(0);
		RandomGenerator genRain    = new RandomGenerator(RandomGenerator.RAINFALL_DAILY       , rand.nextLong());
		RandomGenerator genTemp    = new RandomGenerator(RandomGenerator.TEMPERATURE_DAILY    , rand.nextLong());
		RandomGenerator genTmin    = new RandomGenerator(RandomGenerator.TEMPERATURE_MIN_DAILY, rand.nextLong());
		RandomGenerator genRad     = new RandomGenerator(RandomGenerator.SOLAR_RADIATION_DAILY, rand.nextLong());
		
		double temp = 5;
		double radi = 5;
		while(running){
			try{ Thread.sleep(4000); } catch(Exception e){}
			weather.redisplay();
			for(int d = 0; d < Time.DAYS_WEEK; d++){
				rain[d].setValue(genRain.next());
				temp = genTemp.next();
				tmpAvg[d].setValue(temp);
				radi = genRad.next();
				rad[d].setValue(radi);
				radAvg[d].setValue(genRad.getMean());
				doy++;
			}
		}
		
	}

}
