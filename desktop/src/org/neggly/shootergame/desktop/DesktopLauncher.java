package org.neggly.shootergame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.neggly.shootergame.ShooterGame;

public class DesktopLauncher
{
    public static void main(String[] arg)
    {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 540;
        config.height = 960;
        config.resizable = false;
        new LwjglApplication(new ShooterGame(), config);
    }
}
