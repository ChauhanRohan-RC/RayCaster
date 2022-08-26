
import java.util.IdentityHashMap;
import java.util.Map;

public class Keyboard {

    private final Map<Integer, Boolean> pressedKeys = new IdentityHashMap<>();

    public void onKeyPressed(int keyCode) {
        pressedKeys.put(keyCode, true);
    }

    public void onKeyReleased(int keyCode) {
        pressedKeys.remove(keyCode);
    }

    public boolean isKeyDown(int keyCode) { // Any key code from the KeyEvent class
        return pressedKeys.getOrDefault(keyCode, false);
    }
}