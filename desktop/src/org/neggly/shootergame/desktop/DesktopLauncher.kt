package org.neggly.shootergame.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import org.neggly.shootergame.ShooterGame

object DesktopLauncher
{
    @JvmStatic
    fun main(arg: Array<String>)
    {
        val config = LwjglApplicationConfiguration()
        config.title = "シューティングゲーム"
        config.width = 450
        config.height = 800
        config.resizable = false
        LwjglApplication(ShooterGame(), config)
    }
}
