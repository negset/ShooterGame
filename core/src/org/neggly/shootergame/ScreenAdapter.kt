package org.neggly.shootergame

import com.badlogic.gdx.Screen

/**
 * Screen を空実装するクラス.
 * 各々のゲーム画面はこのクラスを継承する.
 *
 * @author negset
 */
open class ScreenAdapter(val game: ShooterGame) : Screen
{
    override fun show()
    {
    }

    override fun render(delta: Float)
    {
    }

    override fun resize(width: Int, height: Int)
    {
    }

    override fun pause()
    {
    }

    override fun resume()
    {
    }

    override fun hide()
    {
    }

    override fun dispose()
    {
    }
}
