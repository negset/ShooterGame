package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport

class TitleScreen(game: ShooterGame) : ScreenAdapter(game)
{
    private val stage = Stage(FitViewport(WIDTH, HEIGHT))
    private val batch = SpriteBatch()

    private val font = game.assets.get("font.ttf") as BitmapFont

    private val bg = Bg(game.assets)

    private val playBtn = TextButton()
    private val creditsBtn = TextButton()

    override fun load()
    {
        game.assets.load("title")
    }

    override fun show()
    {
        Gdx.input.inputProcessor = stage
        batch.projectionMatrix = stage.camera.combined

        val btnBgTex = game.assets.get("button_bg.png") as Texture
        btnBgTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val btnBg = NinePatch(btnBgTex, 64, 64, 64, 64)

        playBtn.also {
            it.bg = btnBg
            it.font = font
            it.text = "はじめる"
            it.offset = 30f
            it.setPosition(WIDTH / 2, 1050f)
            it.setSize(500f, 180f)
        }

        creditsBtn.also {
            it.bg = btnBg
            it.font = font
            it.text = "クレジット"
            it.offset = 30f
            it.setPosition(WIDTH / 2, 750f)
            it.setSize(500f, 180f)
        }

        stage.addActor(bg)
        stage.addActor(playBtn)
        stage.addActor(creditsBtn)
    }

    override fun render(delta: Float)
    {
        update(delta)
        draw()
    }

    private fun update(delta: Float)
    {
        stage.act(delta)

        if (playBtn.isClicked())
        {
            game.nextScreen = ScreenState.PLAY
        }
        else if (creditsBtn.isClicked())
        {
            game.nextScreen = ScreenState.CREDITS
        }
    }

    private fun draw()
    {
        stage.draw()

        batch.begin()
        font.draw(batch, "シューティングゲーム", 0f, 1900f, WIDTH, Align.center, true)
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
        bg.dispose()
        game.assets.unload("title")
    }
}
