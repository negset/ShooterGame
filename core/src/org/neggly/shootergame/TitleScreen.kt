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

    private val font by lazy { game.assets.get<BitmapFont>("font.ttf") }

    private val bg = Bg()

    private val playBtn = TextButton()
    private val creditsBtn = TextButton()

    private var isAssetsUnset = true

    override fun show()
    {
        Gdx.input.inputProcessor = stage
        batch.projectionMatrix = stage.camera.combined

        bg.loadAssets(game.assets)
        stage.addActor(bg)

        game.assets.load("button_bg.png", Texture::class.java)
        stage.addActor(playBtn)
        stage.addActor(creditsBtn)
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

        if (playBtn.isClicked())
            game.nextScreen = PlayScreen(game)
        else if (creditsBtn.isClicked())
            game.nextScreen = CreditsScreen(game)
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
        game.assets.unload("button_bg.png")
    }

    private fun setAssets()
    {
        val btnBgTex: Texture = game.assets.get("button_bg.png")
        btnBgTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val btnBg = NinePatch(btnBgTex, 64, 64, 64, 64)

        playBtn.apply {
            bg = btnBg
            font = game.assets.get("font.ttf")
            text = "はじめる"
            offset = 30f
            setPosition(WIDTH / 2, 1050f)
            setSize(500f, 180f)
        }

        creditsBtn.apply {
            bg = btnBg
            font = game.assets.get("font.ttf")
            text = "クレジット"
            offset = 30f
            setPosition(WIDTH / 2, 750f)
            setSize(500f, 180f)
        }

        isAssetsUnset = false
    }
}
