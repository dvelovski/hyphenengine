package org.cstr24.hyphengl.engine;

import com.beust.jcommander.JCommander;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGuiContext;
import org.cstr24.hyphengl.assets.*;
import org.cstr24.hyphengl.backends.GraphicsBackend;
import org.cstr24.hyphengl.entities.ComponentRegistry;
import org.cstr24.hyphengl.entities.components.BoneMergeComponent;
import org.cstr24.hyphengl.entities.components.ModelComponent;
import org.cstr24.hyphengl.entities.components.TransformComponent;
import org.cstr24.hyphengl.geometry.*;
import org.cstr24.hyphengl.input.*;
import org.cstr24.hyphengl.interop.source.*;
import org.cstr24.hyphengl.interop.source.materials.SourceShaders;
import org.cstr24.hyphengl.interop.source.studiomdl.StudioModel;
import org.cstr24.hyphengl.interop.source.studiomdl.StudioModelSequenceInstance;
import org.cstr24.hyphengl.interop.source.studiomdl.structs.mstudiobone_t;
import org.cstr24.hyphengl.interop.source.tf.TFItemDef;
import org.cstr24.hyphengl.interop.source.tf.TFItemLibrary;
import org.cstr24.hyphengl.interop.source.vbsp.BSPLoader;
import org.cstr24.hyphengl.rendering.HyGLBasicRenderer;
import org.cstr24.hyphengl.rendering.camera.HyCameraTest;
import org.cstr24.hyphengl.system.SystemEnvironment;
import org.cstr24.hyphengl.textures.TextureFactory;
import org.cstr24.hyphengl.ui.css.CStyleSheetCache;
import org.cstr24.hyphengl.ui.fonts.FontCache;
import org.cstr24.hyphengl.ui.fonts.HyFont;
import org.cstr24.hyphengl.ui.fonts.FontLoader;
import org.cstr24.hyphengl.ui.windowing.UIWindowManager;
import org.cstr24.hyphengl.ui.css.CStyleSheetLoader;
import org.cstr24.hyphengl.ui.css.HyCStyleSheet;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class Engine {
    private static final Logger LOGGER = Logger.getLogger(Engine.class.getName());

    public static final long NULL = 0L;
    public static final int NO_ID = -1;

    private static Application runningApplication = null;
    private static GraphicsBackend graphicsBackend = null;

    private static Unsafe unsafeReference;

    public static boolean oldMethod = true;

    private static double initializationTime;
    private static long nvgContext;

    private static boolean imGUIEnabled;
    private static ImGuiImplGl3 imGUIImplRef;
    private static ImGuiImplGlfw imGUIGLFWImplRef;

    private static PerformanceCounters performanceCounters;

    private static ComponentRegistry componentRegistry;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafeReference = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public static Unsafe getUnsafe(){
        return unsafeReference;
    }

    public static void main(String[] args) {
        Application testApplication = new TestApplication();
        startApplication(testApplication, args);
    }

    public static void startApplication(Application app, String[] args){
        if (runningApplication == null) {
            if (initializeEngine()){
                parseCommandLineArgs(args, app.startupSettings);

                configureBackend(app);
                createWindowAndContext(app);

                registerInternalResourceLoaders();
                if (app.startupSettings.requiresSourceInterop){
                    SourceInterop.initialize();
                }

                initializeNanoVG();
                initializeImGUI(app.applicationWindow.getWindowHandle());

                //the asset manager is now in a state where we can load objects to the GPU. notify the loaders that they can cache default resources, etc.
                AssetLoader.get().preload();

                initializeUIWindowManager();

                runningApplication = app;
                app.start();

                loop();

                app.shutdown();

                exit(true);
            }
        }else{
            throw new IllegalStateException("There is already an application running.");
        }
    }

    public static void parseCommandLineArgs(String[] args, ApplicationStartupSettings startupSettings){
        //some arguments are going to override information in the app's regular startup settings
        JCommander jCom = JCommander.newBuilder().build();
        jCom.parse(args);

        //System.out.println(jCom.getCommands());
    }

    public static void configureBackend(Application app){
        graphicsBackend = app.startupSettings.backend;

        if (app.startupSettings.graphicsDebug){
            graphicsBackend.enableDebug();
        }

        graphicsBackend.initialize();

        MeshFactory.setInstance(graphicsBackend.getMeshFactory());
        TextureFactory.setInstance(graphicsBackend.getTextureFactory());
    }

    private static void createWindowAndContext(Application app){
        app.applicationWindow = new OSWindow(app);
    }

    private static void registerInternalResourceLoaders(){
        AssetLoader.get().registerLoader(HyFont.RESOURCE_TYPE, new FontLoader());
        AssetCache.get().registerCacheProvider(HyFont.RESOURCE_TYPE, new FontCache());

        AssetLoader.get().registerLoader(HyCStyleSheet.RESOURCE_TYPE, new CStyleSheetLoader());
        AssetCache.get().registerCacheProvider(HyCStyleSheet.RESOURCE_TYPE, new CStyleSheetCache());
    }

    private static void initializeImGUI(long windowHandle){
        if (System.getProperty("os.arch").equalsIgnoreCase("aarch64")) {
            imGUIEnabled = false;
        }else{
            imGUIEnabled = true;

            ImGuiContext imContext = ImGui.createContext();
            imGUIImplRef = new ImGuiImplGl3();
            imGUIGLFWImplRef = new ImGuiImplGlfw();

            imGUIImplRef.init(); //TODO investigate whether I need to amend the GLSL version that's used
            imGUIGLFWImplRef.init(windowHandle, true);
        }

    }
    private static void initializeUIWindowManager(){
        UIWindowManager.get().init();
        UIWindowManager.get().setNVGContext(nvgContext);
        UIWindowManager.get().setImGUILayerEnabled(false);
    }
    private static void initializeNanoVG(){
        long nvgHandle = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS);
        if (nvgHandle == NULL){
            throw new RuntimeException("Could not initialize NanoVG vector library.");
        }
        nvgContext = nvgHandle;
    }
    public static long getNvgContext(){
        return nvgContext;
    }

    /**
     * Returns the current time since the currently running application was launched.
     * @return The current time, in seconds.
     */
    public static double currentTime(){
        return GLFW.glfwGetTime();
    }

    private static boolean initializeEngine(){
        GLFW.glfwSetErrorCallback((error, description) -> {
            System.out.println("GLFW error: " + description + " (" + error + ")");
        });

        if (GLFW.glfwInit()){
            SystemEnvironment.initialize();

            initializationTime = GLFW.glfwGetTime();

            performanceCounters = new PerformanceCounters();

            initializeComponentRegistry();
        }else{
            try (MemoryStack stack = MemoryStack.stackPush()){
                var pBuffer = stack.mallocPointer(1);
                GLFW.glfwGetError(pBuffer);
                throw new RuntimeException("Unable to initialize GLFW: " + MemoryUtil.memUTF8(pBuffer.get()));
            }
        }
        return true;
    }

    private static void initializeComponentRegistry(){
        componentRegistry = new ComponentRegistry();

        componentRegistry.registerComponentType(new ModelComponent());
        componentRegistry.registerComponentType(new TransformComponent());
    }

    public static Application getRunningApplication(){
        return runningApplication;
    }

    static double previousTime;
    static HyGLBasicRenderer basicRenderer;

    private static void loop(){
        previousTime = glfwGetTime();

        //poll events for every window in here, dispatch to handlers
        while (!runningApplication.exitRequested){
            OSWindow aWindow = runningApplication.applicationWindow;

            performanceCounters.updateBegin(glfwGetTime());

            //todo an actually balanced update loop
            InputManager.get().processEvents();
            UIWindowManager.get().update();
            runningApplication.update();

            performanceCounters.updateEnd(glfwGetTime());

            performanceCounters.renderBegin(glfwGetTime());
            aWindow.makeCurrent();
            aWindow.clear();

            GL11.glEnable(GL11.GL_STENCIL_TEST);
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            NanoVG.nvgBeginFrame(nvgContext, aWindow.getWidth(), aWindow.getHeight(), 1);
            UIWindowManager.get().renderHyWindows();
            NanoVG.nvgEndFrame(nvgContext);

            imGuiRender(aWindow);

            /*var app = (TestApplication) runningApplication;

            basicRenderer.model = app.hundrethScale;
            basicRenderer.view = app.camera.getViewMatrix();
            basicRenderer.projection = app.projectionMatrix;
            basicRenderer.boneMatrices = null;
            //GL11.glFrontFace(GL11.GL_CCW);
            //basicRenderer.draw(app.mapMesh2);

            GL11.glFrontFace(GL11.GL_CW);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_CULL_FACE);

            app.mercInstance.animationInstances.forEach(anim -> {
                anim.update((float) (currentTime - previousTime));
            });

            basicRenderer.drawModel(app.mercInstance, app.hundrethScaleRotXZ);

            for (ModelInstance itemInstance : app.modelInstances){
                itemInstance.animationInstances.forEach(a -> {
                    a.update((float) (currentTime - previousTime));
                });
                basicRenderer.drawModel(itemInstance, app.hundrethScaleRotXZ);
            }*/



            performanceCounters.renderEnd(glfwGetTime());
            aWindow.swapBuffers();
                //System.out.println(app.camera.getViewMatrix());

            GLFW.glfwPollEvents();
            
            if (aWindow.isCloseRequested()){
                if (runningApplication.settings.exitOnMainWindowClose){
                    runningApplication.exitRequested = true;
                }
            }
        }
    }

    private static void imGuiRender(OSWindow window){
        if (imGUIEnabled){
            ImGui.getIO().setDisplaySize(window.getWidth(), window.getHeight());

            ImGui.newFrame();
            imGUIGLFWImplRef.newFrame();

            UIWindowManager.get().renderImmediateUI();

            if (ImGui.begin("Counters")){
                if (ImGui.beginListBox("Metrics")){
                    ImGui.selectable("Update time (ms): " + String.format("%.9f", performanceCounters.getLastUpdateTime() * 1000));
                    ImGui.selectable("Frame time (ms): " + String.format("%.9f", performanceCounters.getLastFrameTime() * 1000));
                    ImGui.selectable("FPS: " + performanceCounters.getFPS());
                    ImGui.endListBox();
                }
            }
            ImGui.end();

            ImGui.render();
            imGUIImplRef.renderDrawData(ImGui.getDrawData());
        }
    }

    public static void exit(boolean force){
        boolean canExit = true;
        if (runningApplication != null){
            canExit = runningApplication.canShutdown();
        }
        if (force || canExit){
            Callbacks.glfwFreeCallbacks(runningApplication.applicationWindow.getWindowHandle());
            runningApplication.applicationWindow.close();

            destroyImGUI();
            destroyNanoVG();

            unloadResources();

            if (runningApplication != null){
                runningApplication.shutdown();
            }

            GLFW.glfwTerminate();
            System.exit(0);
        }
    }

    private static void destroyImGUI(){
        if (imGUIEnabled){
            ImGui.destroyContext();

            if (imGUIGLFWImplRef != null){
                imGUIGLFWImplRef.dispose();
            }
        }
    }
    private static void destroyNanoVG(){
        if (nvgContext != NULL){
            NanoVGGL3.nnvgDelete(nvgContext);
        }
    }
    private static void unloadResources(){
        AssetCache.get().destroy();
    }

    public static ComponentRegistry getComponentRegistry(){
        return componentRegistry;
    }

    public static GraphicsBackend getBackend(){
        return graphicsBackend;
    }

    private static class TestApplication extends Application {
        BSPMapFile loadedMap;

        public HyMesh mapMesh2;

        public Matrix4f projectionMatrix;
        public Matrix4f hundrethScale = new Matrix4f().scale(0.01f);
        public Matrix4f hundrethScaleRotXZ = new Matrix4f().rotationXYZ((float) -90, 0, -90).scale(0.01f);
        public HyCameraTest camera;

        public ArrayList<Integer> facesToDraw = new ArrayList<>();
        public HyResHandle<HyModel>[] mercenaryModels = null;

        //currently-'equipped' item library entries
        public ArrayList<TFItemDef> equippedItems = new ArrayList<>();

        public TFItemLibrary itemLibrary = new TFItemLibrary();

        @Override
        public void initialise() {

        }

        public void updateProjMatrix(){

        }

        boolean[] keyStates = new boolean[6];
        float lastX, lastY, pitch, yaw;
        public boolean doFrustumCull = true;

        @Override
        public void update() {
            var queue = InputManager.get().getInputQueue(); //todo make this a bit safer and NOT application specific

            while(!queue.isEmpty()){
                var evt = queue.poll();
                switch (evt.source){
                    case Keyboard -> {
                        var kEvt = (KeyEvent) evt;
                        if (evt.action == KeyEvent.Action.KeyPress){
                            switch (kEvt.keyCode){
                                case GLFW_KEY_W -> {
                                    keyStates[0] = true;
                                }
                                case GLFW_KEY_A -> {
                                    keyStates[1] = true;
                                }
                                case GLFW_KEY_S -> {
                                    keyStates[2] = true;
                                }
                                case GLFW_KEY_D -> {
                                    keyStates[3] = true;
                                }
                                case GLFW_KEY_Q -> {
                                    keyStates[4] = true;
                                }
                                case GLFW_KEY_Z -> {
                                    keyStates[5] = true;
                                }
                                case GLFW_KEY_ESCAPE -> {
                                    applicationWindow.releaseCursor();
                                }
                                case GLFW_KEY_F -> {
                                    doFrustumCull = !doFrustumCull;
                                }
                                case GLFW_KEY_O -> {
                                    oldMethod = !oldMethod;
                                }
                                case GLFW_KEY_SPACE -> { //pause/play
                                    /*mercInstance.animationInstances.get(0).toggle();*/
                                }
                                case GLFW_KEY_RIGHT -> { //step
                                    /*mercInstance.animationInstances.getFirst().step();*/
                                }
                                case GLFW_KEY_M -> { //set animation to midpoint
                                    /*var ani = ((StudioModelSequenceInstance) mercInstance.animationInstances.getFirst());
                                    ani.cycle = 0.5f;
                                    ani.paused = true;*/
                                }
                                case GLFW_KEY_R -> {
                                    /*reroll();*/
                                }
                                /*case GLFW_KEY_T -> {
                                    var mi = ((StudioModelInstance) mercInstance);
                                    mi.setSkinFamily(mi.skinFamily == 0 ? 1 : 0);
                                    for (int i = 0; i < modelInstances.size(); i++) {
                                        ModelInstance sm = modelInstances.get(i);
                                        TFItemDef id = equippedItems.get(i);
                                        if (id.styles.length > 0){
                                            if (mi.skinFamily == 0){
                                                ((StudioModelInstance) sm).setSkinFamily(id.styles[0].skinRed);
                                            }else{
                                                ((StudioModelInstance) sm).setSkinFamily(id.styles[0].skinBlue);
                                            }
                                        }else{
                                            ((StudioModelInstance) sm).setSkinFamily(mi.skinFamily);
                                        }
                                    }
                                }*/
                                case GLFW_KEY_U -> {
                                    AssetCache.get().unloadAllImmediate();
                                }
                                case GLFW_KEY_L -> {
                                    AssetCache.get().reloadAllImmediate();
                                }
                            }
                        }else if (evt.action == InputEvent.Action.KeyRelease){
                            switch (kEvt.keyCode){
                                case GLFW_KEY_W -> {
                                    keyStates[0] = false;
                                }
                                case GLFW_KEY_A -> {
                                    keyStates[1] = false;
                                }
                                case GLFW_KEY_S -> {
                                    keyStates[2] = false;
                                }
                                case GLFW_KEY_D -> {
                                    keyStates[3] = false;
                                }
                                case GLFW_KEY_Q -> {
                                    keyStates[4] = false;
                                }
                                case GLFW_KEY_Z -> {
                                    keyStates[5] = false;
                                }
                            }
                        }
                    }
                    case Char -> {
                        //no.1
                        var cEvt = (CharEvent) evt;
                    }
                    case Mouse -> {
                        //no.0
                        var mEvt = (MouseEvent) evt;
                        if (evt.action == InputEvent.Action.MouseMove){
                            float xOffset = mEvt.mouseX - lastX;
                            float yOffset = lastY - mEvt.mouseY;
                            lastX = mEvt.mouseX;
                            lastY = mEvt.mouseY;

                            xOffset *= 0.1f;
                            yOffset *= 0.1f;

                            yaw += xOffset;
                            pitch += yOffset;
                            if (pitch > 89f){
                                pitch = 89f;
                            }
                            if (pitch < -89f){
                                pitch = -89f;
                            }

                            camera.direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
                            camera.direction.y = (float) Math.sin(Math.toRadians(pitch));
                            camera.direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
                            camera.cameraFront = new Vector3f(camera.direction).normalize();
                        }
                    }
                    case Joystick -> {
                        //no
                    }
                }
            }
            var camFront = new Vector3f(camera.cameraFront).mul(0.05f);
            var camRight = new Vector3f(camera.cameraRight).mul(0.05f);
            if (keyStates[0]){
                camera.position.add(camFront);
            }
            if (keyStates[1]){
                camera.position.sub(camRight);
            }
            if (keyStates[2]){
                camera.position.sub(camFront);
            }
            if (keyStates[3]){
                camera.position.add(camRight);
            }
            if (keyStates[4]){
                camera.position.add(0, 0.05f, 0);
            }
            if (keyStates[5]){
                camera.position.sub(0, 0.05f, 0);
            }
            camera.updateVectors();
            updateProjMatrix();

            var tempPos = new Vector3f(camera.position).mul(100f);

            var frust = new FrustumIntersection(new Matrix4f(projectionMatrix).mul(camera.getViewMatrix()));

            facesToDraw.clear();
            int currentLeaf = loadedMap.findLeaf(tempPos);
            int currentCluster = loadedMap.mapLeaves.get(currentLeaf).cluster;

            for (int i = loadedMap.mapLeaves.size() - 1; i >= 0; i--){
                var theLeaf = loadedMap.mapLeaves.get(i);
                if (loadedMap.clusterVisible(currentCluster, theLeaf.cluster)){
                    float bbMinX, bbMinY, bbMinZ;
                    float bbMaxX, bbMaxY, bbMaxZ;
                    float scale = 0.01f;

                    bbMinX = (theLeaf.mins[0] * scale) - 2f;
                    bbMinY = (theLeaf.mins[2] * scale) - 2f; //swapppy
                    bbMinZ = (-theLeaf.mins[1] * scale) - 2f;

                    bbMaxX = (theLeaf.maxs[0] * scale) + 2f;
                    bbMaxY = (theLeaf.maxs[2] * scale) + 2f; //swapppy
                    bbMaxZ = (-theLeaf.maxs[1] * scale) + 2f;

                    int fCullResult = frust.intersectAab(bbMinX, bbMinY, bbMinZ, bbMaxX, bbMaxY, bbMaxZ,
                     FrustumIntersection.PLANE_MASK_NZ | FrustumIntersection.PLANE_MASK_PZ);
                    if ((fCullResult == FrustumIntersection.INTERSECT || fCullResult == FrustumIntersection.INSIDE) || !doFrustumCull){
                        int fCount = theLeaf.numLeafFaces;
                        //System.out.println("leaf " + i + " faces: " + fCount);
                        while (fCount-- > 0){
                            int faceIndex = loadedMap.mapLeafFaces.get(theLeaf.firstLeafFace + fCount);
                            facesToDraw.add(faceIndex);
                        }
                    }else{
                        //System.out.println("leaf " + i + " needs to be culled");
                    }
                }
            }
            loadedMap.mapDisplacements.forEach(disp -> {
                if (!facesToDraw.contains(disp.mapFace)){
                    facesToDraw.add(disp.mapFace);
                }
            });
            mapMesh2.subMeshes.forEach(mesh -> mesh.enabled = false);
            facesToDraw.forEach(inty -> {
                if (inty < mapMesh2.subMeshes.size()){
                    mapMesh2.subMeshes.get(inty).enabled = true;
                }
            });
            //System.out.println("Current leaf: " + currentLeaf + " | pos * 100 : " + tempPos);
        }

        @Override
        public void render() {

        }



        @Override
        public boolean canShutdown() {
            return applicationWindow.isCloseRequested();
        }

        @Override
        public void shutdown() {
            applicationWindow.destroy();
        }
    }
}
