package core;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;

public class ControllerAction extends AbstractAction {

	protected String methodName;

	protected Object[] args;

	public ControllerAction(String methodName, Object... args) {
		this.methodName = methodName;
		this.args = args;
	}

	public void actionPerformed(ActionEvent e) {
		Class klass = Application.getInstance().getController().getClass();
		try {
			Class[] argClasses = null;
			if (args != null && args.length > 0) {
				argClasses = new Class[args.length];
				for (int i = 0; i < args.length; i++) {
					argClasses[i] = args[i].getClass();
				}
			}
			Method mid = klass.getMethod(methodName, argClasses);
			if (mid != null) {
				if (args != null) {
					mid.invoke(Application.getInstance().getController(), args);
				} else {
					mid.invoke(Application.getInstance().getController());
				}
			}
		} catch (Exception exception) {
			Application.getInstance().handleException(exception);
		}
	}
}
