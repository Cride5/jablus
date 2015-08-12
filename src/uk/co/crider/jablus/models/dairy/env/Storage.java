package uk.co.crider.jablus.models.dairy.env;

import uk.co.crider.jablus.data.CompoundData;
import uk.co.crider.jablus.data.Data0D;
import uk.co.crider.jablus.data.DoubleData;
import uk.co.crider.jablus.data.IntegerData;
import uk.co.crider.jablus.models.dairy.Constants;
import uk.co.crider.jablus.models.dairy.Parameters;

import java.util.Hashtable;
import java.util.Map;

/** Represents a number of stores, each accepting a set of designated item types */
public class Storage extends CompoundData {
	
//	private Parameters params;
	
	private Livestock livestock;
	private Store[] stores;
	
	public static final int OFFSET        = 1400;
	public static final int COW_STEADING  = OFFSET + 0;
	public static final int SILAGE_CLAMP  = OFFSET + 1;
	public static final int FEED_BARN     = OFFSET + 2;
	public static final int SLURRY_PIT    = OFFSET + 3;
//	public static final int SLURRY_LAGOON = OFFSET + 4;

	// Store ids (initialised to lowest ID to be stored, allowing counting from 0)
//	public static final int COW_STEADING  = Livestock.HEIFERS01;
//	public static final int FEED_BARN     = Market.HAY;
//	public static final int SILAGE_CLAMP  = Market.SILAGE_GRASS;
//	public static final int SLURRY_PIT    = Livestock.MANURE_DRY;
//	public static final int SLURRY_LAGOON = Livestock.MANURE_WET;
	
	// Item ids within stores
/*	public static final int HAY        = 0;
	public static final int WHEAT      = 1;
	public static final int MAIZE      = 2;
	public static final int FERTILISER = 3;
	public static final int SILAGE     = 0;
	public static final int DRY_MANURE = 0;
	public static final int SLURRY     = 0;
*/
	
	/** Mapping from store item id to designated store ID */
	private static final Map<Integer, Integer> STORE_FOR = new Hashtable<Integer, Integer>();
	/** Mapping from store item id to base ID for items belonging to this store */
	public static final Map<Integer, Integer> STORE_BASE = new Hashtable<Integer, Integer>();
	/** Mapping from store item id to its storage density (usually tonnes/m³) */
	public static final Map<Integer, Double> DENSITY = new Hashtable<Integer, Double>();
	static{
		// Associate each store with its purchasable 
		STORE_FOR.put(Market.STORAGE_LIVESTOCK, COW_STEADING);
		STORE_FOR.put(Market.STORAGE_SILAGE, SILAGE_CLAMP);
		STORE_FOR.put(Market.STORAGE_BARN, FEED_BARN);
		STORE_FOR.put(Market.STORAGE_SLURRY, SLURRY_PIT);
		// normal storage associations
		STORE_FOR.put(Livestock.HEIFERS01,  COW_STEADING);
		STORE_FOR.put(Livestock.HEIFERS1P,  COW_STEADING);
		STORE_FOR.put(Livestock.COWS1ST,    COW_STEADING);
		STORE_FOR.put(Livestock.COWS2ND,    COW_STEADING);
		STORE_FOR.put(Livestock.COWS3PL,    COW_STEADING);
		STORE_FOR.put(Market.SILAGE_GRASS,  SILAGE_CLAMP);
		STORE_FOR.put(Market.SILAGE_WHEAT,  SILAGE_CLAMP);
		STORE_FOR.put(Market.SILAGE_MAIZE,  SILAGE_CLAMP);
		STORE_FOR.put(Market.FERTILISER,    FEED_BARN);
		STORE_FOR.put(Market.CONCENTRATES,  FEED_BARN);
		STORE_FOR.put(Market.HAY,           FEED_BARN);
//		STORE_FOR.put(Market.STRAW,         FEED_BARN);
		STORE_FOR.put(Livestock.MANURE_WET, SLURRY_PIT);
//		STORE_FOR.put(Livestock.MANURE_WET, SLURRY_LAGOON);
		// Base index for store arrays
		STORE_BASE.put(COW_STEADING,  Livestock.HEIFERS01);
		STORE_BASE.put(SILAGE_CLAMP,  Market.SILAGE_GRASS);
		STORE_BASE.put(FEED_BARN,     Market.FERTILISER);
		STORE_BASE.put(SLURRY_PIT,    Livestock.MANURE_WET);
//		STORE_BASE.put(SLURRY_LAGOON, Livestock.MANURE_WET);
		// Amount of space products occupy
		DENSITY.put(Livestock.HEIFERS01 , 1.0);
		DENSITY.put(Livestock.HEIFERS1P , 1.0);
		DENSITY.put(Livestock.COWS1ST   , 1.0);
		DENSITY.put(Livestock.COWS2ND   , 1.0);
		DENSITY.put(Livestock.COWS3PL   , 1.0);
		DENSITY.put(Market.FERTILISER   , 1.0); // t/m³
		DENSITY.put(Market.CONCENTRATES , 1.0); // t/m³
		DENSITY.put(Market.HAY          , 0.5); // t/m³
//		DENSITY.put(Market.STRAW        , 0.4); // t/m³
		DENSITY.put(Market.SILAGE_GRASS , 0.75); // t/m³
		DENSITY.put(Market.SILAGE_WHEAT , 0.75); // t/m³
		DENSITY.put(Market.SILAGE_MAIZE , 0.75); // t/m³
		DENSITY.put(Livestock.MANURE_WET, 1.0); // m³/m³
	}
	
	public Storage(Parameters params, Livestock livestock){
		super(Constants.DATASET_STORE);
//		this.params = params;
		this.livestock = livestock;
		stores = new Store[]{
				new Store(
						new IntegerData(Constants.INPUT_STEADING_CAPACITY, params.INIT_STEADING_CAPACITY),
						new Data0D[]{
							new IntegerData(Livestock.HEIFERS01),
							new IntegerData(Livestock.HEIFERS1P),
							new IntegerData(Livestock.COWS1ST),
							new IntegerData(Livestock.COWS2ND),
							new IntegerData(Livestock.COWS3PL)
						}
				),
				new Store(
						new DoubleData(0, params.INIT_SILAGE_CAPACITY, Constants.UNITS_VOLUME_HIGH),
						new Data0D[]{
							new DoubleData(Market.SILAGE_GRASS, Constants.UNITS_WEIGHTFW_HIGH),
							new DoubleData(Market.SILAGE_WHEAT, Constants.UNITS_WEIGHTFW_HIGH),
							new DoubleData(Market.SILAGE_MAIZE, Constants.UNITS_WEIGHTFW_HIGH)
						}
				),
				new Store(
						new DoubleData(Constants.INPUT_BARN_CAPACITY, params.INIT_BARN_CAPACITY, Constants.UNITS_VOLUME_HIGH),
						new Data0D[]{
							new DoubleData(Market.FERTILISER,   Constants.UNITS_WEIGHT_HIGH),
							new DoubleData(Market.CONCENTRATES, Constants.UNITS_WEIGHTFW_HIGH),
							new DoubleData(Market.HAY,          Constants.UNITS_WEIGHTFW_HIGH)
//							new DoubleData(Market.STRAW,        Constants.UNITS_WEIGHTFW_HIGH)
						}						
				),
				new Store(
						new DoubleData(0, params.INIT_SLURRY_PIT_CAPACITY, Constants.UNITS_VOLUME_HIGH),
						new Data0D[]{
							new DoubleData(Livestock.MANURE_WET, Constants.UNITS_VOLUME_HIGH)
						}
				),
/*				new Store(
						new DoubleData(0, params.INIT_MANURE_LAGOON_CAPACITY, Constants.UNITS_WEIGHT_HIGH),
						new Data0D[]{
							new DoubleData(Livestock.MANURE_WET, Constants.UNITS_WEIGHT_HIGH)
						}
				)
*/		};
	}
	
	/** Returns all store items */
	public Store[] getStores(){
		return stores;
	}
	
	public static boolean hasStore(int id){
		return !Market.isStorageBuilding(id) && (Market.isTradeLivestock(id) || STORE_FOR.containsKey(id));
	}
	
	public double getCapacity(int store){
		return stores[store - OFFSET].getCapacity().doubleValue();
	}
	
	public void addCapacity(int store, double space){
		int st = STORE_FOR.get(store) - OFFSET;
		stores[st].capacity.setValue(stores[st].capacity.getValue().doubleValue() + space);
	}
	
	/** Returns true if the store can accept items of the supplied id */
	public boolean canAdd(int id){
		if(Market.isTradeLivestock(id))
			return stores[COW_STEADING - OFFSET].canAdd();
		if(Market.isStorageBuilding(id)) return true;
//System.out.println("can add:" + id);
		Integer st = STORE_FOR.get(id);
		if(st == null) return false;
		return stores[st.intValue() - OFFSET].canAdd();		
	}
	/** Returns true if the store can have the given items removed */
	public boolean canRemove(int id){
		if(Market.isTradeLivestock(id))
			return livestock.getTotal(id) > 0;
//System.out.println("can add:" + id);
//System.out.println("Looking forf:" + id + " (" + Constants.getName(id) + "), in " + STORE_FOR);
		int st = STORE_FOR.get(id);
		int sb = STORE_BASE.get(st);
		return stores[st - OFFSET].canRemove(id - sb);
	}
	/** Returns the quantity of the item type which the store can accept */
	public double spaceAvailable(int id){
		if(Market.isTradeLivestock(id))
			return stores[COW_STEADING - OFFSET].spaceAvailable();
		if(Market.isStorageBuilding(id))
			return 100000;
		int st = STORE_FOR.get(id);
		return stores[st - OFFSET].spaceAvailable() * DENSITY.get(id);
	}
	/** Returns the quantity of the given item type which is currently stored */
	public double quantityStored(int id){
		if(Market.isTradeLivestock(id))
			return livestock.getTotal(id);
//System.out.println("Looking for:" + id + " (" + Constants.getName(id) + "), in " + STORE_FOR);
		int st = STORE_FOR.get(id);
		int sb = STORE_BASE.get(st);
		return stores[st - OFFSET].quantityStored(id - sb);
	}
	/** Adds the item to storage, returns the quantity stored */
	public double add(int id, double qty){
		int st = STORE_FOR.get(id);
		int sb = STORE_BASE.get(st);
		return stores[st - OFFSET].add(id - sb, qty);
	}
	/** Removes the item from storage, returns the quantity removed */
	public double remove(int id, double qty){
		int st = STORE_FOR.get(id);
		int sb = STORE_BASE.get(st);
		return stores[st - OFFSET].remove(id - sb, qty);
	}
	/** Sets the quantity of the given item to the given quantity, returns the quanitity stored */
	public double set(int id, double qty){
		int st = STORE_FOR.get(id);
		int sb = STORE_BASE.get(st);
		return stores[st - OFFSET].set(id - sb, qty);
	}
	
	/** Class to represent an individual store */
	public class Store{
		public final Data0D capacity;
		public final Data0D[] items;
		public Store(Data0D capacity, Data0D[] items){
			this.capacity = capacity;
			this.items = items;
		}
		private Number getCapacity(){
			return capacity.getValue();
		}
		private double set(int i, double qty){
			if(qty < 0) return 0;
			items[i].setValue(0);
			double qtyAvail = spaceAvailable() * DENSITY.get(items[i].getId());			
			items[i].setValue(qty < qtyAvail ? qty : qtyAvail);
			return qty < qtyAvail ? qty : qtyAvail;
		}
		private double add(int i, double qty){
			if(qty <= 0) return 0;
			double qtyAvail = spaceAvailable() * DENSITY.get(items[i].getId());
			items[i].setValue(items[i].getValue().doubleValue() + (qty <= qtyAvail ? qty : qtyAvail));
			return qty > qtyAvail ? qtyAvail : qty;
		}
		private double remove(int i, double qty){
			if(qty <= 0) return 0;
			double qtyLeft = items[i].getValue().doubleValue();
			items[i].setValue(qtyLeft - (qty > qtyLeft ? qtyLeft : qty));
			return qty > qtyLeft ? qtyLeft : qty;
		}
		private boolean canAdd(){
			return spaceUsed() < capacity.getValue().doubleValue();
		}
		private boolean canRemove(int i){
			return items[i].getValue().doubleValue() > 0;
		}
		private double spaceAvailable(){
			return capacity.getValue().doubleValue() - spaceUsed();
		}
		private double spaceUsed(){
			double qtyStored = 0;
			for(Data0D item : items) qtyStored += item.getValue().doubleValue() / DENSITY.get(item.getId());
			return qtyStored;
		}
		private double quantityStored(int i){
			return items[i].getValue().doubleValue();
		}
	}
}
