package org.neggly.shootergame

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.FPSLogger
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader

const val WIDTH = 1440f
const val HEIGHT = 2560f

/**
 * メインクラス.
 *
 * @author negset
 */
class ShooterGame : Game()
{
    var nextScreen = Screens.UNSET
        set (value)
        {
            when (value)
            {
                Screens.TITLE -> "title"
                Screens.PLAY -> "play"
                Screens.CREDITS -> "credits"
                Screens.UNSET -> null
            }?.let {
                assets.load(it)
            }
            field = value
        }

    val assets = AssetsLoader("assets.xml")

    /**
     * ロード画面.
     * アセット読み込み中はこれを描画する.
     */
    private val loading by lazy { Loading() }

    /**
     * FPS 記録用
     */
    private val fpsLogger = FPSLogger()

    enum class Screens
    {
        TITLE,
        PLAY,
        CREDITS,
        UNSET
    }

    override fun create()
    {
        loadFont()

        assets.load("common")

        nextScreen = Screens.TITLE
    }

    override fun render()
    {
        Gdx.gl.glClearColor(0f, 0f, 0.30f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (assets.update())
        {
            when (nextScreen)
            {
                Screens.TITLE -> TitleScreen(this)
                Screens.PLAY -> PlayScreen(this)
                Screens.CREDITS -> CreditsScreen(this)
                Screens.UNSET -> null
            }?.let { setScreen(it) }
            nextScreen = Screens.UNSET

            super.render()
        }
        else
            loading.render()

        fpsLogger.log()
    }

    override fun dispose()
    {
        super.dispose()
        assets.unload("common")
        assets.dispose()
        loading.dispose()
    }

    /**
     * フォントの設定を行い,読み込み命令を出す.
     */
    private fun loadFont()
    {
        val resolver = InternalFileHandleResolver()
        val param = FreetypeFontLoader.FreeTypeFontLoaderParameter().apply {
            fontFileName = "font.ttf"
            fontParameters.size = 80
            fontParameters.color = Color.WHITE
            fontParameters.borderColor = Color.DARK_GRAY
            fontParameters.borderWidth = 6f
            fontParameters.magFilter = Texture.TextureFilter.Linear
            fontParameters.minFilter = Texture.TextureFilter.Linear
            fontParameters.incremental = true
        }
        assets.manager.apply {
            setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
            setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
            load("font.ttf", BitmapFont::class.java, param)
        }
    }
}
