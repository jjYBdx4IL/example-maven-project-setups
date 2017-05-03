package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.rpcdemo;

import com.google.gwt.user.client.Timer;
import static com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.rpcdemo.ResourceBundle.RES;
import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.gwtgraphics.client.shape.path.CurveTo;

public class Animation 
        extends DrawingArea 
{

    private static final float HZ = 60;
    private static final int ivalMillis = (int) (1000.0 / HZ);
    private static final int minIvalMillis = ivalMillis / 10;
    private long nextDesiredExecTime = 0;
    private long lastExecTime = 0;
    private long lastExecTimeTmp = 0;
    private Timer timer = null;

    private long frameCounter = 0;
    private long frameCounterStarted = 0;

    private Circle circle;
    private float circleX;

    private Path leftArm;
    private Path rightArm;
    private Path leftLeg;
    private Path rightLeg;
    private Path body;

    public Animation(int width, int height) {
        super(width, height);
        setStylePrimaryName(RES.getStyle().svgCanvas());
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        circleX = 100;
        circle = new Circle((int) circleX, 100, 50);
        circle.setFillColor("red");
        add(circle);

        leftArm = new Path(200, 120);
        leftArm.setFillOpacity(0);
        leftArm.setStrokeWidth(2);
        leftArm.curveRelativelyTo(10, 10, 100, 100, 100, 20);
        add(leftArm);

    }

    public void start() {
        if (timer != null) {
            return;
        }
        frameCounter = 0;
        frameCounterStarted = System.currentTimeMillis();
        timer = new Timer() {
            @Override
            public void run() {
                // always use start of update as last exec time.
                lastExecTimeTmp = System.currentTimeMillis();
                animate((int) (lastExecTimeTmp - lastExecTime));
                frameCounter++;
                scheduleNextExecution();
            }
        };
        lastExecTime = System.currentTimeMillis();
        nextDesiredExecTime = lastExecTime + minIvalMillis;
        timer.schedule(minIvalMillis);
    }

    /**
     * 
     * @return frame rate (fames per second)
     */
    public float stop() {
        timer.cancel();
        timer = null;
        long duration = System.currentTimeMillis() - frameCounterStarted;
        return (float) (frameCounter * 1000.0 / (duration != 0.0 ? duration : -1.0));
    }

    @Override
    protected void onDetach() {
        timer.cancel();
        super.onDetach();
    }

    protected void scheduleNextExecution() {
        long currentTime = System.currentTimeMillis();
        // try to execute at fixed intervals:
        nextDesiredExecTime += ivalMillis;
        // but do not consume all cycles:
        if (nextDesiredExecTime - currentTime < minIvalMillis) {
            nextDesiredExecTime = currentTime + minIvalMillis;
        }
        timer.schedule((int) (nextDesiredExecTime - currentTime));
        lastExecTime = lastExecTimeTmp;
    }

    /**
     *
     * @param step milliseconds since last update
     */
    private void animate(int step) {
        circleX += step * 80.0 / 1000.0;
        circle.setX((int) circleX);
        CurveTo curveTo = (CurveTo) leftArm.getStep(1);
        curveTo.setX((int) (curveTo.getX() - 0.12 * step));
        curveTo.setY((int) (curveTo.getY() + 0.09 * step));
        leftArm.issueRedraw(false);
    }
}
