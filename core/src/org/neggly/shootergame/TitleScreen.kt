package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.utils.Align


class TitleScreen(game: ShooterGame) : ScreenAdapter(game)
{
    private var uiCamera: OrthographicCamera? = null
    private var viewport: Viewport? = null
    private var batch: SpriteBatch? = null

    private var font: BitmapFont? = null

    private var assetHasSet = false

    override fun show()
    {
        uiCamera = OrthographicCamera()
        uiCamera?.setToOrtho(false, WIDTH, HEIGHT)
        viewport = FitViewport(WIDTH, HEIGHT, uiCamera)
        batch = SpriteBatch()
    }

    override fun render(delta: Float)
    {
        if (!assetHasSet) setAsset()
        update(delta)
        draw()
    }

    private fun update(delta: Float)
    {
        if (Gdx.input.justTouched())
        {
            game.nextScreen = PlayScreen(game)
        }
    }

    private fun draw()
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        uiCamera?.update()
        batch?.projectionMatrix = uiCamera?.combined

        batch?.begin()
        font?.draw(batch, "シューティング", 0f, 1900f, WIDTH, Align.center, true)
        font?.draw(batch, "ゲーム", 0f, 1750f, WIDTH, Align.center, true)
        font?.draw(batch, "タッチで開始", 0f, 1000f, WIDTH, Align.center, true)
        font?.draw(batch, "© 2018 negset", 0f, 300f, WIDTH, Align.center, true)
        batch?.end()
    }

    override fun resize(width: Int, height: Int)
    {
        viewport?.update(width, height)
    }

    override fun hide()
    {
        dispose()
    }

    override fun dispose()
    {
        batch?.dispose()
    }

    private fun setAsset()
    {
        font = game.assets.get("default.otf")
        assetHasSet = true
    }
}
