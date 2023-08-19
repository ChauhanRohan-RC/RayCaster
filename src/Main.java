import math.RMath;
import math.Vector;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.util.*;
import java.util.List;

// TODO: 2. Vertical and Horizontal FOV by mouse, 4. Use JPanel (swing), 3. Wall height and multiple walls in a column

public class Main extends PApplet {

    public static final boolean FULLSCREEN = false;      // fullscreen not working with P2D renderer

    public static final float RAY_MAX_DISTANCE_FACTOR = 0f;     // <=0  for infinite long rays (relative to ray distance)
    public static final float RENDER_DISTANCE_FACTOR = RAY_MAX_DISTANCE_FACTOR > 0? RAY_MAX_DISTANCE_FACTOR * 2: 1.5f;        // (relative to ray distance)

    public static final boolean FOG_EFFECT_ENABLED = true;
    public static final float FOG_EFFECT_START_FACTOR = 0.5f;              // Relative to RENDER_DISTANCE

    public static final WallI BOUNDARY = new Wall(true, BaseWallStyle.strokeOnly(4, null), new PointF(0, 0), new PointF(1, 0), new PointF(1, 1), new PointF(0, 1));
    public static final boolean BOUNDARY_ENABLED = true;

    public static final float MOVEMENT_SPEED = 2.0f;

    public static final float CAMERA_H_WIDTH_SCALE = 0.9f;
    public static final float CAMERA_V_HEIGHT_SCALE = 0.88f;
    public static final float CAMERA_H_SPEED = 1.0f;
    public static final float CAMERA_V_SPEED = 1.0f;
    public static final boolean CAMERA_H_INVERTED = true;
    public static final boolean CAMERA_V_INVERTED = false;
    public static final boolean CAMERA_BY_MOUSE_ENABLED = false;


    public static final String DES_CONTROLS_MOVEMENT = "W: Move Forward\n" +
            "S: Move Backward\n" +
            "A: Move Left\n" +
            "D: Move Right";

    public static final String DES_CONTROLS_CAMERA = "UP: Camera Up\n" +
            "DOWN: Camera Down\n" +
            "LEFT: Camera Left\n" +
            "RIGHT: Camera Right";

    public static final String DES_CONTROLS_OTHERS = "CTRL-R: Change Scene";

    public static final String DES_CONTROLS = "\n\t\tCONTROLS\n\n" + DES_CONTROLS_MOVEMENT + "\n\n" + DES_CONTROLS_CAMERA + "\n\n" + DES_CONTROLS_OTHERS;


    @NotNull
    public static Dimension windowSize(int displayW, int displayH) {
        return new Dimension(Math.round(displayW / 1.4f), Math.round(displayH / 1.4f));
    }


    private float _w, _h;
    @NotNull
    private final Keyboard keyboard = new Keyboard();


    private Player player;
    private Collection<WallI> walls;


//    @NotNull
//    private Vector drawOrigin() {
//        return new Vector((width - xMax - xMin) / 2, (height - yMax - yMin) / 2, -(zMax + zMin) / 2);
//    }


    @Override
    public void settings() {
        if (FULLSCREEN) {
            fullScreen(P2D);
        } else {
            final Dimension s = windowSize(displayWidth, displayHeight);
            size(s.width, s.height, P2D);
        }

        _w = width; _h = height;
        smooth(4);
    }

    @Override
    public void setup() {
        surface.setTitle("RayCaster");
        surface.setResizable(true);
//        surface.hideCursor();
//        surface.setLocation(0, 0);
//        surface.setSize(displayWidth, displayHeight);
//        frameRate(120);

        player = new Player(new Vector(0.5f, 0.5f), width);       // center

        walls = new ArrayList<>();
        randomiseWalls();


//        setFreeCamInternal(mFreeCam);

    }



    protected void onResized(int w, int h) {
//        if (bgImage != null) {
//            bgImage.resize(w, h);
//        }

//        considerReCreateCam();
        player.setRaysCountPreference(w);
    }


    /* Mini map */

    @NotNull
    public SizeF miniMapSize() {
        return new SizeF(width / 5f, height / 5f);
//        return new SizeF(width, height);
    }

    @NotNull
    public Vector miniMapOrigin(@NotNull SizeF miniMapSize) {
        return new Vector(width - miniMapSize.width - 5, 5);
    }


//    @Nullable
//    private Integer pMouseX = null;



    @Override
    public void mouseMoved(MouseEvent e) {
//        if (pMouseX != null) {
//            final float rotX = (((float) (mouseX - pMouseX) / width)) * player.getFovDeg() * 2;
//            player.rotateHeadingBy(rotX);
//        }
//
//        pMouseX = mouseX;

//        // TOdo; width and height of main 3d scene, vertical FOV
//        final float rotX = (((float) (mouseX - pmouseX) / width) - 0.5f);
//        player.rotateHeadingBy(rotX);
    }



    public void preDraw() {
        if (_w != width || _h != height) {
            _w = width; _h = height;
            onResized(width, height);
        }


        // FW
        if (keyboard.isKeyDown(java.awt.event.KeyEvent.VK_W)) {
            player.moveTowardsHeading(0.001f * MOVEMENT_SPEED, walls);
        }

        // BW
        if (keyboard.isKeyDown(java.awt.event.KeyEvent.VK_S)) {
            player.moveTowardsHeading(-0.001f * MOVEMENT_SPEED, walls);
        }

        // LEFT
        if (keyboard.isKeyDown(java.awt.event.KeyEvent.VK_A)) {
            player.movePerpendicularToHeading(0.001f * MOVEMENT_SPEED, walls);
        }

        // RIGHT
        if (keyboard.isKeyDown(java.awt.event.KeyEvent.VK_D)) {
            player.movePerpendicularToHeading(-0.001f * MOVEMENT_SPEED, walls);
        }



        // CAM_UP
        if (keyboard.isKeyDown(java.awt.event.KeyEvent.VK_UP)) {
            player.rotate_v_head((CAMERA_V_INVERTED ? -1: 1) * RMath.rad(CAMERA_V_SPEED));
        }

        // CAM-DOWN
        if (keyboard.isKeyDown(java.awt.event.KeyEvent.VK_DOWN)) {
            player.rotate_v_head((CAMERA_V_INVERTED ? 1: -1) * RMath.rad(CAMERA_V_SPEED));
        }

        // CAM-LEFT
        if (keyboard.isKeyDown(java.awt.event.KeyEvent.VK_LEFT)) {
            player.rotate_h_head((CAMERA_H_INVERTED ? 1: -1) * RMath.rad(CAMERA_H_SPEED));
        }

        // CAM-RIGHT
        if (keyboard.isKeyDown(java.awt.event.KeyEvent.VK_RIGHT)) {
            player.rotate_h_head((CAMERA_H_INVERTED ? -1: 1) * RMath.rad(CAMERA_H_SPEED));
        }



//        if (keyboard.isKeyDown(java.awt.event.KeyEvent.VK_M)) {
//            player.change_v_fov(RMath.rad(1));
//        } else if (keyboard.isKeyDown(java.awt.event.KeyEvent.VK_N)) {
//            player.change_v_fov(RMath.rad(-1));
//        }


        // Mouse
        if (CAMERA_BY_MOUSE_ENABLED && focused) {
            if (pmouseX > 0) {
                final float dx = mouseX - pmouseX;

                if (Math.abs(dx) > 0) {
                    final float delta = CAMERA_H_SPEED * (CAMERA_H_INVERTED ? -1: 1) * (dx / (width * CAMERA_H_WIDTH_SCALE)) * RMath.TWO_PI;
                    player.rotate_h_head(delta);
                }
            }



            if (pmouseY > 0) {
                final float dy = mouseY - pmouseY;

                if (Math.abs(dy) > 0) {
                    final float delta = CAMERA_V_SPEED * (CAMERA_V_INVERTED ? 1: -1) * (dy / (height * CAMERA_V_HEIGHT_SCALE)) * Player.V_HEAD_RANGE;
                    player.rotate_v_head(delta);
                }
            }


//            final int centerX = floor(width * 0.5f);
//            final int centerY = floor(height * 0.5f);
//            final Point screenL = U.getLocationOnScreen(surface);
//            final int windowCenterX = screenL.x + centerX;
//            final int windowCenterY = screenL.y + centerY;
//
////            final float windowCenterX = screenX(centerX, centerY);
////            final float windowCenterY = screenY(centerX, centerY);
//
//            final PointerInfo pInfo = MouseInfo.getPointerInfo();
//            final Point mouse = pInfo.getLocation();
//
//            final boolean xChanged = mouse.x != windowCenterX && mouse.x > screenL.x && mouse.x < screenL.x + width;
//            final boolean yChanged = mouse.y != windowCenterY && mouse.y > screenL.y && mouse.y < screenL.y + height;
//
//            if (xChanged) {
//                final float rh = ((float) (mouse.x - windowCenterX) / width) * player.h_fov();
//                player.rotate_h_head(rh);
//            }
//
//            if (yChanged) {
//                final float rv = ((float) (mouse.y - windowCenterY) / height) * player.v_fov();
//                player.rotate_v_head(rv);
//            }
//
//            if (xChanged || yChanged) {
//                U.moveMouse(new Point(windowCenterX, windowCenterY));
//            }
        }



    }






    @Override
    public void draw() {
        preDraw();

        background(0);

        final Player.RayCast[] casts = player.cast(walls, RAY_MAX_DISTANCE_FACTOR, true);
        final float floorLevel = 0.5f * height * (1 + (RMath.tan(player.v_head()) / RMath.tan(player.v_fov() * 0.5f)));

        pushMatrix();
        pushStyle();
        fill(Gl.COLOR_SKY.getRGB());     // sky
        rect(0, 0, width, floorLevel);
        fill(Gl.COLOR_GROUND.getRGB());      // ground
        rect(0, floorLevel, width, height - floorLevel);

//        float colW = (float) width / castInfos.length;
        float u, d, wallH;      // TODO wall height
        float colH;
        float fogLevel;

//        Float prevX = null;

//        rectMode(CENTER);
        noStroke();
        translate(0, 0);

        float prevColumn = -1;
        final float projectionDistance = player.getProjectionDistance(WallI.STANDARD_HEIGHT);

        for (int i=0; i < casts.length; i++) {
//            List<Ray.CastInfo> info = castInfos.get(i);
//            Ray ray = player.rayAt(i);

            final Player.RayCast cast = casts[i];

            final float rayAngle = cast.ray.getAngle();
            final float curColumn = width * player.getRayProjectionPosition(rayAngle);

            if (prevColumn >= curColumn)
                continue;

            float nextColumn = width;
            if (i < casts.length - 1) {
                nextColumn = width * player.getRayProjectionPosition(i + 1);
            }

            final float a = rayAngle - player.h_head();


            if (cast.castInfos != null) {
                Ray.CastInfo info = cast.castInfos.get(cast.castInfos.size() - 1);
                u = info.tu.u;
                d = u * RMath.cos(a);
//                wallH = info.getWallHeightOrDefault(WallI.DEFAULT_HEIGHT);

//                wallH = info.wall.getHeight();
                wallH = 1f;

//                final float dSq = d * d;
//                colH = map(d, 0, maxD, height, 0);
                colH = height * (projectionDistance / d);
                fogLevel = FOG_EFFECT_ENABLED? RMath.constraint(0, 1, (d / (RENDER_DISTANCE_FACTOR * FOG_EFFECT_START_FACTOR)) - 1): 0;
            } else {
                u = d = RENDER_DISTANCE_FACTOR;
                colH = 0;
                fogLevel = 1;
                wallH = 1;
            }

            final float colW = Math.abs(nextColumn - curColumn);

            float centerY = floorLevel - (colH * 0.5f);
            float scaledColH = colH * wallH;
            float y = centerY + (colH - scaledColH);

            // drawing wall. TODO texture
            fill(ColorU.withLuminance(Gl.DEFAULT_COLOR_WALLS.getRGB(), abs(colH / height)));
            rect(curColumn, y, colW, scaledColH);

            // Overlay with fog effect
            if (FOG_EFFECT_ENABLED) {
                fill(Gl.fogColor(fogLevel).getRGB());
                rect(curColumn, y, colW, scaledColH);
            }

            prevColumn = curColumn;
        }

        popStyle();
        popMatrix();

        /* ...............................  Mini Map ................................ */
        final SizeF miniMapSize = miniMapSize();
        final float miniMapMinDim = miniMapSize.minDimension();
        final Vector miniMapO = miniMapOrigin(miniMapSize);


//        player.setPosition(map(mouseX, 0, width, 0, 1), map(mouseY, 0, height, 0, 1));

        pushMatrix();
        translate(miniMapO.x, miniMapO.y);

        // bg
        pushStyle();
        noStroke();
        fill(Gl.MINIMAP_BG.getRGB());
        rect(0, 0, miniMapSize.width, miniMapSize.height);
        popStyle();

        // walls
        if (Gl.MINIMAP_DEFAULT_DRAW_WALLS) {
            for (WallI wall: walls) {
                wall.draw(this, miniMapSize);
            }
        }

        // Player
        final Vector pPos = Gl.abs(player.getPosition(), miniMapSize);
        pushStyle();
        noStroke();
        fill(Gl.MINIMAP_DEFAULT_PLAYER_COLOR.getRGB());

        final float playerDia = miniMapMinDim * Gl.MINIMAP_PLAYER_DIAMETER_FRACTION;
        ellipse(pPos.x, pPos.y, playerDia, playerDia);
        popStyle();

        final int rayC = Gl.MINIMAP_DEFAULT_RAY_COLOR.getRGB(), pointC = Gl.MINIMAP_DEFAULT_INTERSECTION_POINT_COLOR.getRGB();

        for (int i=0; i < casts.length; i++) {
//            Ray ray = player.rayAt(i);
//            Ray.CastInfo info = casts[i];
            final Player.RayCast rayCast = casts[i];

//            Ray.CastInfo info = rayCast.castInfos != null;


            if (rayCast.castInfos != null) {     // collided
                Ray.CastInfo info = rayCast.castInfos.get(rayCast.castInfos.size() - 1);

                final Vector itp = Gl.abs(info.intersectionPoint, miniMapSize);
                if (Gl.MINIMAP_DEFAULT_DRAW_RAYS) {
                    stroke(rayC);
                    strokeWeight(Gl.MINIMAP_RAY_STROKE_WEIGHT);
                    line(pPos.x, pPos.y, itp.x, itp.y);
                }

                if (Gl.MINIMAP_DEFAULT_DRAW_INTERSECTION_POINTS) {
                    stroke(pointC);
                    strokeWeight(Gl.MINIMAP_INTERSECTION_POINT_STROKE_WEIGHT);
                    point(itp.x, itp.y);
                }
            } else {        // consider ray to end at infinite
                if (Gl.MINIMAP_DEFAULT_DRAW_RAYS && Gl.MINIMAP_DEFAULT_DRAW_NON_COLLIDING_RAYS && RAY_MAX_DISTANCE_FACTOR > 0) {
                    final Vector itp = Gl.abs(rayCast.ray.lerp(RAY_MAX_DISTANCE_FACTOR), miniMapSize);
                    stroke(rayC);
                    strokeWeight(Gl.MINIMAP_RAY_STROKE_WEIGHT);
                    line(pPos.x, pPos.y, itp.x, itp.y);
                }
            }
        }

        popMatrix();

        /* .................................HUD........................... */


//        mLastDrawMs = now;
        postDraw();
    }


    private void postDraw() {

    }


    @Override
    public void keyPressed(KeyEvent event) {
        keyboard.onKeyPressed(event.getKeyCode());

        final int keyCode = event.getKeyCode();

        switch (keyCode) {
            case java.awt.event.KeyEvent.VK_R -> {
                if (event.isControlDown()) {
                    randomiseWalls();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        keyboard.onKeyReleased(event.getKeyCode());
    }


    @NotNull
    public static List<WallI> generateRandomWalls(int count, int maxVertices) {
        final List<WallI> walls = new LinkedList<>();

        while (count > 0) {
            int verticesCount = 0;
            do {
                verticesCount = U.RANDOM.nextInt(maxVertices);
            } while (verticesCount < 2);

            final PointF[] vertices = new PointF[verticesCount];
            for (int i = 0; i < verticesCount; i++) {
                vertices[i] = PointF.randomRelative();
            }

            walls.add(new Wall(false, vertices).setRigid(U.RANDOM.nextBoolean()));
            count--;
        }

//        walls.add(new Wall(true, PointF.randomRelative(), PointF.randomRelative(), PointF.randomRelative()));
//        walls.add(new Wall(false, PointF.randomRelative(), PointF.randomRelative()));
//        walls.add(new Wall(false, PointF.randomRelative(), PointF.randomRelative()));
//        walls.add(new Wall(false, PointF.randomRelative(), PointF.randomRelative(), PointF.randomRelative(), PointF.randomRelative()));
//        walls.add(new Wall(false, PointF.randomRelative(), PointF.randomRelative(), PointF.randomRelative()));
        return walls;
    }

    @NotNull
    public static List<WallI> generateRandomBlocks(int count, int maxCellsInABlock, float cellW, float cellH) {
        List<WallI> blocks = new LinkedList<>();
        final int cellCountX = (int) (1 / cellW), cellCountY = (int) (1 / cellH);

        while (count > 0) {
            int cells;

            do {
                cells = U.RANDOM.nextInt(maxCellsInABlock);
            } while (cells < 1);

            int posX = U.RANDOM.nextInt(cellCountX);
            int posY = U.RANDOM.nextInt(cellCountY);
            boolean vertical = U.RANDOM.nextBoolean();

            PointF[] vertices = new PointF[4];
            vertices[0] = new PointF(posX * cellW, posY * cellH);

            if (vertical) {
                vertices[1] = new PointF(vertices[0].x, vertices[0].y + (cells * cellH));
                vertices[2] = new PointF(vertices[1].x + cellW, vertices[1].y);
            } else {
                vertices[1] = new PointF(vertices[0].x, vertices[0].y + cellH);
                vertices[2] = new PointF(vertices[1].x + (cells * cellW), vertices[1].y);
            }

            vertices[3] = new PointF(vertices[2].x, vertices[0].y);

            blocks.add(new Wall(true, BaseWallStyle.fillOnly(null), vertices).setHeight(U.RANDOM.nextFloat()));
            count--;
        }

        return blocks;
    }

    public final void randomiseWalls() {
        walls.clear();
        if (BOUNDARY_ENABLED) {
            walls.add(BOUNDARY);
        }

//        walls.addAll(generateRandomWalls(2, 4));
        walls.addAll(generateRandomBlocks(10, 2, 0.05f, 0.05f));
    }



    /* Camera */

//    @NotNull
//    private PeasyCam createCam(@Nullable float[] rotations) {
//        final Vector o = drawOrigin();
//        final PeasyCam cam = new PeasyCam(this, o.x, o.y, o.z, o.y / tan(radians(26)));
//
//        if (rotations == null) {
//            rotations = INITIAL_CAM_ROTATIONS;
//        }
//
//        cam.setRotations(rotations[0], rotations[1], rotations[2]);
//        return cam;
//    }
//
//    @NotNull
//    private PeasyCam createCam() {
//        return createCam(null);
//    }
//
//    private void considerReCreateCam() {
//        if (!mFreeCam)
//            return;
//
//        float[] rotations = null;
//        if (mPeasyCam != null) {
//            mPeasyCam.setActive(false);
//            rotations = mPeasyCam.getRotations();
//        }
//
//        mPeasyCam = createCam(rotations);
//    }
//
//    private void setFreeCamInternal(boolean freeCam) {
//        mFreeCam = freeCam;
//
//        if (freeCam) {
//            considerReCreateCam();
//        } else if (mPeasyCam != null) {
//            mPeasyCam.setActive(false);     // Do not reset or nullify
//        }
//    }
//
//    public void serFreeCam(boolean freeCam) {
//        if (mFreeCam == freeCam)
//            return;
//        setFreeCamInternal(freeCam);
//    }
//
//    public void toggleFreeCam() {
//        setFreeCamInternal(!mFreeCam);
//    }
//
//    public boolean isCamFree() {
//        return mFreeCam;
//    }


    public static void mainTest(String[] args) {
        println(U.contains(new PointF(2, 2), false, new PointF(0, 0), new PointF(4, 4), new PointF(2, 5)));
    }

    public static void mainLaunch(String[] args) {
        final Main app = new Main();
        runSketch(concat(new String[]{app.getClass().getName()}, args), app);
    }

    public static void main(String[] args) {
        print(DES_CONTROLS);
        mainLaunch(args);
//        mainTest(args);
    }
}
