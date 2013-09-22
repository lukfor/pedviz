package core;

import pedviz.algorithms.filter.Condition;
import pedviz.graph.Node;

public class QuestionCondition implements Condition {

	private Question question;

	public QuestionCondition(Question question) {
		this.question = question;
	}

	public boolean check(Node node) {
		Object value = node.getUserData(question.getTrait());
		if (value != null) {
			return value.equals(question.getAnswer());
		} else {
			return false;
		}
	}

}
