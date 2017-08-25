package a.b.c;

import org.stjs.javascript.Global;

/**
 *
 * @author jjYBdx4IL
 */
public class Main {
    
    private int counter = 0;
    
    public Main() {}
    
    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        counter++;
        
        DTO dto = new DTO();
        dto.setId(1);
        dto.setText("test");
        
        //XMLHttpRequest req = new XMLHttpRequest();
        Global.console.log("test log output");
    }
}
