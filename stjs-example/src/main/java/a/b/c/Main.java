package a.b.c;

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
    }
}
