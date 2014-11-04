package testg;

public class NativeApp
{
    static
    {
        NarSystem.loadLibrary();
    }

    public final native String sayHello();

    public static void main( String[] args )
    {
        NativeApp app = new NativeApp();
        System.out.println( app.sayHello() );
    }
}
