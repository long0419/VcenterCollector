package com.purvar.warpper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * 
 *
 * @author mr.liang
 */
public class PropertiesWarpper {

	Properties props = null;

	public PropertiesWarpper(Properties props) {
        this.props = props;
    }

	public Properties getUnderlyingProperties() {
		return props;
	}

	/**
	 * Get the trimmed String value of the property with the given
	 * <code>name</code>. If the value the empty String (after trimming), then
	 * it returns null.
	 */
	public String getStringProperty(String name) {
		return getStringProperty(name, null);
	}

	/**
	 * Get the trimmed String value of the property with the given
	 * <code>name</code> or the given default value if the value is null or
	 * empty after trimming.
	 */
	public String getStringProperty(String name, String def) {
		String val = props.getProperty(name, def);
		if (val == null) {
			return def;
		}

		val = val.trim();

		return (val.length() == 0) ? def : val;
	}

	public String[] getStringArrayProperty(String name) {
		return getStringArrayProperty(name, null);
	}

	public String[] getStringArrayProperty(String name, String[] def) {
		String vals = getStringProperty(name);
		if (vals == null) {
			return def;
		}

		StringTokenizer stok = new StringTokenizer(vals, ",");
		ArrayList<String> strs = new ArrayList<String>();
		try {
			while (stok.hasMoreTokens()) {
				strs.add(stok.nextToken().trim());
			}

			return (String[]) strs.toArray(new String[strs.size()]);
		} catch (Exception e) {
			return def;
		}
	}

	public boolean getBooleanProperty(String name) {
		return getBooleanProperty(name, false);
	}

	public boolean getBooleanProperty(String name, boolean def) {
		String val = getStringProperty(name);

		return (val == null) ? def : Boolean.valueOf(val).booleanValue();
	}

	public byte getByteProperty(String name) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			throw new NumberFormatException(" null string");
		}

		try {
			return Byte.parseByte(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	public byte getByteProperty(String name, byte def) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			return def;
		}

		try {
			return Byte.parseByte(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	public char getCharProperty(String name) {
		return getCharProperty(name, '\0');
	}

	public char getCharProperty(String name, char def) {
		String param = getStringProperty(name);
		return (param == null) ? def : param.charAt(0);
	}

	public double getDoubleProperty(String name) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			throw new NumberFormatException(" null string");
		}

		try {
			return Double.parseDouble(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	public double getDoubleProperty(String name, double def) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			return def;
		}

		try {
			return Double.parseDouble(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	public float getFloatProperty(String name) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			throw new NumberFormatException(" null string");
		}

		try {
			return Float.parseFloat(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	public float getFloatProperty(String name, float def) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			return def;
		}

		try {
			return Float.parseFloat(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	public int getIntProperty(String name) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			throw new NumberFormatException(" null string");
		}

		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	public int getIntProperty(String name, int def) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			return def;
		}

		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	public int[] getIntArrayProperty(String name) throws NumberFormatException {
		return getIntArrayProperty(name, null);
	}

	public int[] getIntArrayProperty(String name, int[] def) throws NumberFormatException {
		String vals = getStringProperty(name);
		if (vals == null) {
			return def;
		}

		StringTokenizer stok = new StringTokenizer(vals, ",");
		ArrayList<Integer> ints = new ArrayList<Integer>();
		try {
			while (stok.hasMoreTokens()) {
				try {
					ints.add(new Integer(stok.nextToken().trim()));
				} catch (NumberFormatException nfe) {
					throw new NumberFormatException(" '" + vals + "'");
				}
			}

			int[] outInts = new int[ints.size()];
			for (int i = 0; i < ints.size(); i++) {
				outInts[i] = ((Integer) ints.get(i)).intValue();
			}
			return outInts;
		} catch (Exception e) {
			return def;
		}
	}

	public long getLongProperty(String name) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			throw new NumberFormatException(" null string");
		}

		try {
			return Long.parseLong(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	public long getLongProperty(String name, long def) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			return def;
		}

		try {
			return Long.parseLong(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	public short getShortProperty(String name) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			throw new NumberFormatException(" null string");
		}

		try {
			return Short.parseShort(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	public short getShortProperty(String name, short def) throws NumberFormatException {
		String val = getStringProperty(name);
		if (val == null) {
			return def;
		}

		try {
			return Short.parseShort(val);
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException(" '" + val + "'");
		}
	}

	
	/**
	 * 
	 * sys.address.1  :xxx
	 * sys.username.1 :xxx
	 * sys.password.1 :xxx
	 * 
	 * sys.address.1  :xxx
	 * sys.username.1 :xxx
	 * 
	 * e.g : 
	 * 	1.getGroup("sys.address","sys.password")    
	 * 		==>
	 * 			[{sys.address:xxx,sys.password:xxx},{sys.address:xxx}] 
	 * 	2.getGroup("sys.address","sys.password","sys.username") 
	 * 		==> 
	 * 			[{sys.address:xxx,sys.password:xxx,sys.username:xxx},{sys.address:xxx,sys.username:xxx}] 
	 * 
	 */
	public List<Map<String,String>> getGroup(String... prefixKeys){
		Properties properties = getUnderlyingProperties();
		
		List<Map<String,String>> list = new ArrayList<>();
		
		//加点后查询
		String[] tempPrefixKeys = new String[prefixKeys.length];
		for(int i=0;i<prefixKeys.length;i++){
			String prefixKey = prefixKeys[i];
			if(!prefixKey.endsWith("\\.")){
				prefixKey += ".";
			}
			tempPrefixKeys[i] = prefixKey;
		}
		prefixKeys = tempPrefixKeys;
		
		//
		for(Entry<Object, Object> entry :properties.entrySet()){
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			
			for(String prefixKey :prefixKeys){
				
				if(key.startsWith(prefixKey)){
					int index = Integer.valueOf(key.substring(prefixKey.length()));
					while(list.size() <= index){
						list.add(new HashMap<>());
					}
					Map<String,String> map = list.get(index);
					
					String mapKey = key.substring(0, prefixKey.length()-1);
					
					if(mapKey != null){
						mapKey = mapKey.trim();
					}
					if(value != null){
						value = value.trim();
					}
					map.put(mapKey,value);
				}
			}
		}
		
		return list;
	}

}
