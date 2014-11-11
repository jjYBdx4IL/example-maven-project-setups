package maven.examples;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

public class Main {

    private final static Logger log = Logger.getLogger(Main.class.getName());
    AtomicBoolean windowClosed = new AtomicBoolean(false);
    JFrame jf;
    JPanel container = new JPanel();
    JLabel label = new JLabel("Hello!");

    protected void openWindow() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                jf = createTestJFrame();
                jf.setVisible(true);
            }
        });
    }

    protected void waitForWindowClosing() throws InterruptedException {
        synchronized (windowClosed) {
            while (!windowClosed.get()) {
                log.info("waiting for window to close");
                windowClosed.wait();
            }
            log.info("window closed");
        }
    }

    protected JFrame createTestJFrame() {
        final JFrame jf = new JFrame("Test JFrame");
        jf.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent arg0) {
                log.info(arg0);
            }
        });
        jf.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent arg0) {
                log.info(arg0);
            }

            @Override
            public void windowIconified(WindowEvent arg0) {
                log.info(arg0);
            }

            @Override
            public void windowDeiconified(WindowEvent arg0) {
                log.info(arg0);
            }

            @Override
            public void windowDeactivated(WindowEvent arg0) {
                log.info(arg0);
                synchronized (windowClosed) {
                    windowClosed.set(true);
                    windowClosed.notifyAll();
                }
                log.info("notified main()");
                jf.dispose();
            }

            @Override
            public void windowClosing(WindowEvent arg0) {
                log.info(arg0);
            }

            @Override
            public void windowClosed(WindowEvent arg0) {
                log.info(arg0);
            }

            @Override
            public void windowActivated(WindowEvent arg0) {
                log.info(arg0);
            }
        });
        jf.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                log.info(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                log.info(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyChar() == 'q' || e.getKeyChar() == 'Q') {
                    jf.setVisible(false);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                log.info(e);
            }
        });

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(200, 255, 200));
        container.add(label);
        JScrollPane jsp = new JScrollPane(container);
        jf.getContentPane().add(jsp);
        jf.pack();
        jf.setLocationRelativeTo(null);
        return jf;
    }

    public static void waitForSwing() {
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                    }
                });
            } catch (InterruptedException e) {
            } catch (InvocationTargetException e) {
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        Main main = new Main();
        if (args.length > 0) {
            main.label.setText(main.label.getText() + " " + args[0]);
        }
        main.openWindow();
        main.waitForWindowClosing();
        log.info("quit");
    }
}
