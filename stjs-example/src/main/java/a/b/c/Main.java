package a.b.c;

import static org.stjs.javascript.Global.alert;
import static org.stjs.javascript.Global.window;
import static org.stjs.javascript.JSGlobal.stjs;
import static org.stjs.javascript.jquery.GlobalJQuery.$;

import org.stjs.javascript.XMLHttpRequest;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.functions.Callback0;
import org.stjs.javascript.functions.Callback3;
import org.stjs.javascript.jquery.AjaxParams;
import org.stjs.javascript.jquery.Event;
import org.stjs.javascript.jquery.EventHandler;
import org.stjs.javascript.jquery.JQueryXHR;

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

		final Main that = this;
		$(window).ready(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {

				// native ajax
				final XMLHttpRequest request = new XMLHttpRequest();
				request.onreadystatechange = new Callback0() {
					@Override
					public void $invoke() {
						if (request.readyState == 4 && request.status == 200) {
							DTO dto = stjs.parseJSON(request.responseText, DTO.class);
							alert(dto.getText());
						}
					}
				};

				request.open("GET", "/getDtoNative");
				request.send();

				// jquery ajax frontend
				$.ajax(new AjaxParams() {
					{
						url = "/getDtoJquery";
						// dataType = "jsonp";
						dataType = "text";
						success = new Callback3<Object, String, JQueryXHR>() {
							@Override
							public void $invoke(Object data, String status, JQueryXHR xhr) {
								DTO dto = stjs.parseJSON((String)data, DTO.class);
								alert(dto.getText());
							}
						};
						error = new Callback3<JQueryXHR, String, String>() {
							@Override
							public void $invoke(JQueryXHR xhr, String err, String exc) {
								alert(err + " - " + exc);
							}
						};
					}
				});

				return false;
			}
		});
	}

}
