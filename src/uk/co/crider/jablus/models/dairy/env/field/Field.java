package uk.co.crider.jablus.models.dairy.env.field;

import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.DataSet;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.data.VectorData;
import uk.co.crider.jablus.env.DriverEndogeneous;
import uk.co.crider.jablus.env.Time;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.Parameters;
import uk.co.crider.jablus.models.dairy.env.CowDigestion;
import uk.co.crider.jablus.models.dairy.env.Livestock;
import uk.co.crider.jablus.models.dairy.env.Market;

/** Class to represent a farmer's field, includes crop/soil models */
public class Field extends CompoundData implements DriverEndogeneous{
	
	public static final float FERTILISER_N = 0.3f; // % Ammonium nitrate fertiliser has 30% N
	
	private Parameters params;
	
	// External drivers
	private Time time;
	private VectorData fieldMap;
	
	// Field Map Attributes
	private IntegerData id;
	private DoubleData area; // Field area in hectares
	private DoubleData soilWater;
	private DoubleData soilTemp;
	private DoubleData soilNitrate;
	private DoubleData soilLeaching;
	private DoubleData cropMass;
	private DoubleData cropLAI;
	private DoubleData cropYield;
	
	// Not added to the field map
	private Soil soil;
	private Crop crop;
//	private Livestock livestock; // All cows
	private double propHerd; // Proportion of herd being grazed on this field
	private double nToAdd; // Fertiliser N to add to field (tonnes)
	public Field(Parameters params,
			     int id,
			     DoubleData area){
		super();
		this.params = params;
		this.id = new IntegerData(Constants.STATIC_FIELD_ID, id);
		addItem(this.id);
		this.area         = area; // Not added since it is static
		this.soilWater    = new DoubleData(Constants.TEMPORAL_SOIL_WATER,    Constants.UNITS_PERCENT,       0, 100); addItem(soilWater);
		this.soilTemp     = new DoubleData(Constants.TEMPORAL_SOIL_TEMP,     Constants.UNITS_TEMPERATURE, -20,  40); addItem(soilTemp);
		this.soilNitrate  = new DoubleData(Constants.TEMPORAL_SOIL_NITRATE,  Constants.UNITS_NITROGEN,      0, 100); addItem(soilNitrate);
		this.soilLeaching = new DoubleData(Constants.TEMPORAL_SOIL_LEACHING, Constants.UNITS_NITROGEN,      0, 100); addItem(soilLeaching);
		this.cropMass     = new DoubleData(Constants.TEMPORAL_CROP_DM,       Constants.UNITS_YIELD,         0,  15); addItem(cropMass);
		this.cropLAI      = new DoubleData(Constants.TEMPORAL_CROP_LAI,                                     0,  15); addItem(cropLAI);
		this.cropYield    = new DoubleData(Constants.TEMPORAL_CROP_YIELD,    Constants.UNITS_YIELD,         0,  15); addItem(cropYield);
		// Initialise soil model
		this.soil = new Soil(params);
		this.nToAdd = 0;
	}
	
	/** @inheritDoc */
	public void init(DataSet drivers) {
		time = (Time)drivers.getItem(Constants.TEMPORAL_TIME);
//		livestock = (Livestock)drivers.getItem(Constants.DRIVER_LIVESTOCK);
		fieldMap = (VectorData)drivers.getItem(Constants.STATIC_FIELD_MAP);
//System.out.println("Setting field attribute:" + Constants.INPUT_FIELD_CROP + "\tfor field id:" + id.getValue() + "\tvalue:" + (crop == null ? Crop.FALLOW : crop.getType()));
		
//System.out.println("set attr:" + fieldMap.getAttribute(Constants.INPUT_FIELD_CROP, id.getValue()));		
	}
	
	/** Causes this field to add it's data items to the fieldMap's attribute table */
	public void addMapAttributes(){
		// Add visualised attributes to field map
		fieldMap.addAttribute(new IntegerData(Constants.INPUT_FIELD_CROP, Crop.FALLOW));
		fieldMap.addAttribute(soilWater);
		fieldMap.addAttribute(soilTemp);
		fieldMap.addAttribute(soilNitrate);
		fieldMap.addAttribute((DoubleData)soilLeaching.clone());
		fieldMap.addAttribute(cropMass);
		fieldMap.addAttribute(cropLAI);
		fieldMap.addAttribute(cropYield);
		fieldMap.setVisibleAttribute(Constants.INPUT_FIELD_CROP);
	}
	
	/** Updates the map attributes */
	public void updateMapAttributes(){

		// TODO: this shouldn't be necessery, but prevents inconsistencies in the interface
//		fieldMap.setAttribute(Constants.INPUT_FIELD_CROP,       id.getValue(), getCropType());
		// Update field map with new data
		fieldMap.setAttribute(Constants.INPUT_FIELD_CROP, id.intValue(), getCropType());
		
		fieldMap.setAttribute(Constants.TEMPORAL_SOIL_NITRATE,  id.intValue(), soil.getNitrogenContent(20));
		fieldMap.setAttribute(Constants.TEMPORAL_SOIL_LEACHING, id.intValue(), soil.getQLES());
		fieldMap.setAttribute(Constants.TEMPORAL_SOIL_TEMP,     id.intValue(), soil.getTSOL());
		fieldMap.setAttribute(Constants.TEMPORAL_SOIL_WATER,    id.intValue(), soil.getWaterContent(20) * 100);
		fieldMap.setAttribute(Constants.TEMPORAL_CROP_DM,       id.intValue(), crop == null ? 0 : crop.getMASEC());
		fieldMap.setAttribute(Constants.TEMPORAL_CROP_LAI,      id.intValue(), crop == null ? 0 : crop.getLAI());
		fieldMap.setAttribute(Constants.TEMPORAL_CROP_YIELD,    id.intValue(),
				crop == null ? 0 :
				crop instanceof Pasture ?
					crop.getMASEC() - (Pasture.BASE_MASS * 1E-2) :
					crop.getMASEC()); // Since whold crop is harvested, then whole mass is yield
//			crop instanceof Pasture ? crop.getMASEC() : ((FodderCrop)crop).getMAGRAIN());
	}
	/** Updates the field map's crop attribute to reflect the field's current crop */
	private void updateCropAttribute(){
	   fieldMap.setAttribute(Constants.INPUT_FIELD_CROP, id.intValue(), getCropType());
//System.out.println("ATTR:" + getCropType());
	}
	
	// Accessor methods
	
	/** Returns the field's area */
	public double getArea(){
		return area.doubleValue();
	}
	
	// Mahagement methods
	
	/** Ploughs the field */
	public void ploughField(){
		soil.plough();
		crop = null;
		updateMapAttributes();
	}
	
	/** Sews the given crop on the field */
	public void sewCrop(int cropType){
		// If a crop already exists then we need to plough the field		
		if(crop != null) ploughField();
		// Carry out action
		if(cropType == Crop.GRASS){
			crop = new Pasture(
					time,
					soil,
					params.GRASS_SPECIES,
					(float)area.doubleValue()
				);
		}
		else if(cropType == Crop.WHEAT)
			crop = new WheatCrop(soil, time.getDayOfYear());
		else if(cropType == Crop.MAIZE)
			crop = new MaizeCrop(soil, time.getDayOfYear());
		soil.sowCrop(crop);
		updateMapAttributes();
	}
	
	/** Spreads the given quanitiy of the given fertiliser type on the field (weight in t for fertiliser, m³ for slurry)*/
	public void spread(int type, double qty){
		// If no n content given then estimate
		double nContent = 0;
		switch(type){
		case Market.FERTILISER :
			nContent = qty * FERTILISER_N;
			break;
		case Livestock.MANURE_WET :
			nContent = qty * 0.001 / CowDigestion.SLURRY_N_RATIO; // 0.0045; (conversion to tonnes)
			break;
		}
		spread(type, qty, nContent);
	}
	
	// TODO! What about extra carbon in humus with manure, also
	// what about different fertiliser types.
	/** Spreads the given fertiliser of given weight/nitrogen (tonnes) content on the field */
	public void spread(int type, double weight, double nContent){
		switch(type){
		case Market.FERTILISER :
			if(crop != null) nToAdd += nContent;
			break;
		case Livestock.MANURE_WET :
			if(crop != null) nToAdd += nContent;
			break;
//		case Storage.SLURRY :
//			break;
		}
	}
	
	/** Sets whether the field may be used for grazing */
	public void setGrazing(boolean graze){
		if(graze && getCropType() == Crop.GRASS)
			((Pasture)crop).execAction(Pasture.ACTION_START_GRAZE);
		if(!graze && getCropType() == Crop.PASTURE)
			((Pasture)crop).execAction(Pasture.ACTION_STOP_GRAZE);
		updateCropAttribute();
	}
	
	/** Sets the proportion of the herd grazing on this field */
	public void setPropHerd(double propHerd){
		this.propHerd = propHerd;
	}
	
	/** Returns the proportion of the herd grazing on this field */
	public double getPropHerd(){
		return propHerd;
	}
	
	/** Returns the pasture crop object if pasture is currenlty growing */
	public Pasture getPasture(){
		return crop instanceof Pasture ? (Pasture)crop : null;
	}
	
	/** Harvests the current crop, return the quantity of DM harvested */
	public double harvestCrop(){
		if(crop == null) return 0;
		double yield = crop.harvest();
		updateMapAttributes();
		if(crop instanceof FodderCrop)
			crop = null;
		updateCropAttribute();
		return yield;
	}
	
	/** Returns the type the product produced when the crop is harvested */
	public int getCropProduct(){
		int cType = getCropType();
		switch(cType){
		case Crop.GRASS : return Market.SILAGE_GRASS;
		case Crop.PASTURE : return Market.SILAGE_GRASS;
		case Crop.WHEAT : return Market.SILAGE_WHEAT;
		case Crop.MAIZE : return Market.SILAGE_MAIZE;
		}
		return 0;
	}
	
	/** Returns the type of the crop currently growing in this field */
	public int getCropType(){
		// No crop is fallow
		if(crop == null) return Crop.FALLOW;
		int cType = crop.getType();
		// Grass with cows on it is pasature
		if(cType == Crop.GRASS && ((Pasture)crop).isGrazing())
			return Crop.PASTURE;
		return cType;
	}
	
	public DoubleData getLeaching(){
		return soilLeaching;
	}
	
	/** Returns whether the given action is possible */
/*	public boolean isActionPossible(String action){
		if(action.equals(DairyAgent.ACTION_SEW_GRASS)
		|| action.equals(DairyAgent.ACTION_SEW_WHEAT)
		|| action.equals(DairyAgent.ACTION_SEW_MAIZE)){
			if(crop == null) return false;
		}
		return true;
	}
*/
	
/*	public static Style getFieldsStyle(){
		BasicStyle green = new BasicStyle(Color.GREEN);
		BasicStyle yellow = new BasicStyle(Color.YELLOW);
		BasicStyle blue = new BasicStyle(Color.BLUE);
		Hashtable<Integer, BasicStyle> map = new Hashtable<Integer, BasicStyle>();
		map.put(1, green);
		map.put(2, green);
		map.put(3, yellow);
		ColorThemingStyle style = new ColorThemingStyle("OBJECTID", map, blue);
		return style;
	}
*/
	/** Executes 1 day in this field's current crop and soil models */
	public void cropDay(double rain, double temp, double tmin, double rad){
//System.out.println("EXEC FIELD area:" + fieldMap.getAttribute(Constants.STATIC_FIELD_AREA, id.getValue()));
		double anit = nToAdd * 1E3 / area.doubleValue(); // Convert t to kg/ha
//if(id.intValue() == 0) System.out.println("nToAdd=" + nToAdd + " anit=" + anit);
	    nToAdd = 0;
		soil.execDay(rain, 0, anit, rad, temp, tmin);
		// Print soil model state for a specific field
		if(params.LOGGING_SOIL && id.intValue() == 0)
			soil.printState();
		soilLeaching.setValue(soilLeaching.doubleValue() + soil.getQLES() * area.doubleValue());
	    if(crop != null){
	    	if(crop instanceof Pasture){
	    		((Pasture)crop).execDay(
					rad, // Daily incident radiation                     MJ⋅m–2
					temp // Average daily temperature                      °C
				);
	   		}
	   		if(crop instanceof FodderCrop){
	   			((FodderCrop)crop).execDay(temp, tmin, rain, rad);
	   		}
			// Print state if logging set and for a specific field id
		    if(params.LOGGING_CROPS && id.intValue() == 0){
		    	crop.printState();
		    }
	    }
    }


}
