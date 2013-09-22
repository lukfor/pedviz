package core;

import java.util.Vector;

public class Question {

	private String description;

	private int layout;

	private String id;

	private String trait;

	private boolean extern = true;

	public static final int CHECKBOX = 0;

	public static final int TEXTAREA = 1;

	public static final int RADIOBUTTON = 2;

	public static final int COMBOBOX = 3;

	public static final int TEXTLABEL = 4;

	public static final String[] caption = { "Checkbox", "Textarea",
			"Radiobutton", "Combobox", "TextLabel" };

	private String answer;

	private String parentQuestion;

	public Vector<String> values = new Vector<String>();

	public Question(String id, String description) {
		this.id = id;
		this.description = description;
	}

	public Question(String id, String description, int layout) {
		this.id = id;
		this.description = description;
		this.layout = layout;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return layout;
	}

	public void setLayout(int layout) {
		this.layout = layout;
	}

	public String toString() {
		// return getTrait().trim();

		if (!extern) {
			return getTrait().trim();
		} else {
			if (getDescription() == null || getDescription().trim().equals("")) {
				if (getParentQuestion() == null
						|| getParentQuestion().trim().equals("")) {
					return "-";
				} else {
					return getParentQuestion().trim();
				}
			}
			return getDescription().trim();
		}

		/*
		 * if (layout == RADIOBUTTON) { String result = ""; for (String
		 * radiobutton : values) { result += radiobutton; if
		 * (values.lastElement() != radiobutton) { result += ", "; } } return
		 * getParentQuestion() + ": "+ getDescription(); } else { return
		 * getParentQuestion() + ": "+ getDescription(); }
		 */
	}

	public String getId() {
		return id;
	}

	public String getTypename() {
		return caption[layout];
	}

	public void addValue(String description) {
		values.add(description);
	}

	public Vector<String> getValues() {
		return values;
	}

	public String getTrait() {
		return trait;
	}

	public void setTrait(String trait) {
		this.trait = trait;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getParentQuestion() {
		return parentQuestion;
	}

	public void setParentQuestion(String parentQuestion) {
		this.parentQuestion = parentQuestion;
	}

	public boolean isExtern() {
		return extern;
	}

	public void setExtern(boolean extern) {
		this.extern = extern;
	}

}
