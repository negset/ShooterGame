package org.neggly.shootergame

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
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
    /**
     * FPS 記録用
     */
    private val fpsLogger = FPSLogger()

    /**
     * 次のゲーム画面.
     * null でないときに render() の最後で画面を移行する.
     */
    var nextScreen: Screen? = null

    /**
     * アセット管理用.
     * ゲーム全体で 1 つのインスタンスを共有する.
     */
    val assets = AssetManager()

    /**
     * ロード画面.
     * アセット読み込み中はこれを描画する.
     */
    private lateinit var loading: Loading

    override fun create()
    {
        loading = Loading()
        createFont()
        setScreen(TitleScreen(this))
    }

    override fun render()
    {
        Gdx.gl.glClearColor(0f, 0f, 0.30f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (assets.update())
            super.render()
        else
            loading.render()

        /* nextScreen がセットされている場合はここで画面を切り替える. */
        if (nextScreen != null)
        {
            super.setScreen(nextScreen)
            nextScreen = null
        }

        //fpsLogger.log()
    }

    override fun dispose()
    {
        super.dispose()
        assets.dispose()
        loading.dispose()
    }

    /**
     * フォントの設定を行い,読み込み命令を出す.
     */
    private fun createFont()
    {
        val resolver = InternalFileHandleResolver()
        assets.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        assets.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
        val params = FreetypeFontLoader.FreeTypeFontLoaderParameter()
        params.fontFileName = "font.ttf"
        params.fontParameters.size = 80
        params.fontParameters.color = Color.WHITE
        params.fontParameters.borderColor = Color(0x2962FF)
        params.fontParameters.borderWidth = 8f
        params.fontParameters.magFilter = Texture.TextureFilter.Linear
        params.fontParameters.minFilter = Texture.TextureFilter.Linear
        params.fontParameters.incremental = true
        assets.load("font.ttf", BitmapFont::class.java, params)
    }
}
