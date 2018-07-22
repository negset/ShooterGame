package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport


class CreditsScreen(game: ShooterGame) : ScreenAdapter(game)
{
    private val stage = Stage(FitViewport(WIDTH, HEIGHT))
    private val batch = SpriteBatch()

    private val font = game.assets.get("font.ttf") as BitmapFont

    private val bg = Bg(game.assets)

    private val titleBtn = TextButton()

    override fun show()
    {
        Gdx.input.inputProcessor = stage
        batch.projectionMatrix = stage.camera.combined

        val btnBgTex = game.assets.get("button_bg.png") as Texture
        btnBgTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val btnBg = NinePatch(btnBgTex, 64, 64, 64, 64)
        titleBtn.also {
            it.bg = btnBg
            it.font = font
            it.text = "もどる"
            it.offset = 30f
            it.clickEvent = { game.nextScreen = ScreenState.TITLE }
            it.setPosition(WIDTH / 2, 450f)
            it.setSize(500f, 180f)
        }

        stage.addActor(bg)
        stage.addActor(titleBtn)
    }

    override fun render(delta: Float)
    {
        update(delta)
        draw()
    }

    private fun update(delta: Float)
    {
        stage.act(delta)
    }

    private fun draw()
    {
        stage.draw()

        batch.begin()
        font.draw(batch, "～ クレジット ～", 0f, 2200f, WIDTH, Align.center, true)
        font.draw(batch, "プログラム・イラスト", 0f, 1900f, WIDTH, Align.center, true)
        font.draw(batch, "negset (@negself)", 0f, 1750f, WIDTH, Align.center, true)
        font.draw(batch, "BGM・効果音", 0f, 1450f, WIDTH, Align.center, true)
        font.draw(batch, "魔王魂", 0f, 1300f, WIDTH, Align.center, true)
        font.draw(batch, "フォント", 0f, 1000f, WIDTH, Align.center, true)
        font.draw(batch, "LOGOTYPE.JP", 0f, 850f, WIDTH, Align.center, true)
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
        game.assets.unload("credits")
    }
}
