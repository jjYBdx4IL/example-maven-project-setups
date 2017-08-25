package a.b.c;

import static org.stjs.javascript.Global.alert;
import static org.stjs.javascript.Global.window;

import org.stjs.javascript.dom.DOMEvent;
import org.stjs.javascript.functions.Callback1;

/**
 *
 * @author jjYBdx4IL
 */
public class Main {

	private int counter = 0;

	public Main() {
	}

	public static void main(String[] args) {
		new Main().run();
	}

	public void run() {
		counter++;

		DTO dto = new DTO();
		dto.setId(1);
		dto.setText("test print output");

		// XMLHttpRequest req = new XMLHttpRequest();
		Global2.print(dto.getText());

		window.onload = new Callback1<DOMEvent>() {
			@Override
			public void $invoke(DOMEvent ev) {
				alert("test log alert");
			}
		};
	}
}
