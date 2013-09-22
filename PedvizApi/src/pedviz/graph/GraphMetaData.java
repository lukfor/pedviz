package pedviz.graph;

import java.util.*;

public class GraphMetaData {

    public static int FAM = 1;
    public static int PID = 2;
    public static int DAD = 3;
    public static int MOM = 4;
    public static int SEX = 5;
    
    private Object male;
    private Object female;

    private HashMap<Integer, String> data;
    private Vector<String> userdata;

    public GraphMetaData(String[] traits, String fam, String pid, String dad,
	    String mom, String sex) {
	this(traits, fam, pid, dad, mom, sex, null, null);
    }
    
    public GraphMetaData(String[] traits, String fam, String pid, String dad,
	    String mom, String sex, Object male, Object female) {
	data = new HashMap<Integer, String>();
	userdata = new Vector<String>();
	for (String trait : traits) {
	    if (trait.equals(fam)) {
		data.put(FAM, trait.toUpperCase());
	    } else if (trait.equals(pid)) {
		data.put(PID, trait.toUpperCase());
	    } else if (trait.equals(dad)) {
		data.put(DAD, trait.toUpperCase());
	    } else if (trait.equals(mom)) {
		data.put(MOM, trait.toUpperCase());
	    } else if (trait.equals(sex)) {
		data.put(SEX, trait.toUpperCase());
	    } else {
		userdata.add(trait.toUpperCase());
	    }
	}
	this.female = female;
	this.male = male;
    }

    public String get(int id) {
	return data.get(id);
    }

    public Vector<String> getUserTraits() {
	return userdata;
    }
    
    public Vector<String> getTraits() {
	Vector<String> traits = new Vector<String>();
	traits.addAll(data.values());
	traits.addAll(userdata);
	Collections.sort(traits);
	return traits;
    }

    public void setTrait(int id, String trait) {
	data.put(id, trait.toUpperCase());
    }

    public void addTrait(String trait) {
	userdata.add(trait.toUpperCase());
    }

    public Object getMale() {
        return male;
    }

    public void setMale(Object male) {
        this.male = male;
    }

    public Object getFemale() {
        return female;
    }

    public void setFemale(Object female) {
        this.female = female;
    }

}
