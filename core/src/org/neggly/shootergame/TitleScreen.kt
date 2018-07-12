package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport


class TitleScreen(game: ShooterGame) : ScreenAdapter(game)
{
    private val stage = Stage(FitViewport(WIDTH, HEIGHT))
    private val batch = SpriteBatch()

    private lateinit var font: BitmapFont

    private var isAssetsUnset = true

    override fun show()
    {
        Gdx.input.inputProcessor = stage
        batch.projectionMatrix = stage.camera.combined
    }

    override fun render(delta: Float)
    {
        if (isAssetsUnset)
            setAssets()

        update(delta)
        draw()
    }

    private fun update(delta: Float)
    {
        stage.act(delta)

        if (Gdx.input.justTouched())
        {
            game.nextScreen = PlayScreen(game)
        }
    }

    private fun draw()
    {
        batch.begin()
        font.draw(batch, "シューティングゲーム", 0f, 1900f, WIDTH, Align.center, true)
        font.draw(batch, "タッチで開始", 0f, 1000f, WIDTH, Align.center, true)
        font.draw(batch, "(C) 2018 negset", 0f, 300f, WIDTH, Align.center, true)
        batch.end()
    }

    override fun resize(width: Int, height: Int)
    {
        stage.viewport.update(width, height)
    }

    override fun hide()
    {
        dispose()
    }

    override fun dispose()
    {
        stage.dispose()
        batch.dispose()
    }

    private fun setAssets()
    {
        font = game.assets.get("font.ttf")
        isAssetsUnset = false
    }
}
