package a.b.c;

/**
 *
 * @author jjYBdx4IL
 */
public class Main {
    
    private int counter = 0;
    
    public Main() {}
    
    public static void main(String[] args) {
        // this line is clearly too long:
        new                                                                                                               Main().run();
    }

    public void run() {
        counter++;
    }
}
