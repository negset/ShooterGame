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

    private lateinit var font: BitmapFont

    private val bg = Bg()

    private val titleBtn = TextButton()

    private var isAssetsUnset = true

    override fun show()
    {
        Gdx.input.inputProcessor = stage
        batch.projectionMatrix = stage.camera.combined

        bg.loadAssets(game.assets)
        stage.addActor(bg)

        game.assets.load("button_bg.png", Texture::class.java)
        stage.addActor(titleBtn)
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

        if (titleBtn.isClicked())
            game.nextScreen = TitleScreen(game)
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
        game.assets.unload("button_bg.png")
    }

    private fun setAssets()
    {
        font = game.assets.get("font.ttf")

        val btnBgTex: Texture = game.assets.get("button_bg.png")
        btnBgTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val btnBg = NinePatch(btnBgTex, 64, 64, 64, 64)
        titleBtn.apply {
            bg = btnBg
            font = game.assets.get("font.ttf")
            text = "もどる"
            offset = 30f
            setPosition(WIDTH / 2, 450f)
            setSize(500f, 180f)
        }

        isAssetsUnset = false
    }
}
