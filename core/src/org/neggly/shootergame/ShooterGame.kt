package org.neggly.shootergame

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
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
    var nextScreen: ScreenId? = null
        set (value)
        {
            field = value
            field?.let { asset.load(it.assetSceneId) }
        }

    val asset = AssetLoader("assets.xml")

    /**
     * ロード画面.
     * アセット読み込み中はこれを描画する.
     */
    private val loading by lazy { Loading() }

    /**
     * FPS 記録用
     */
    private val fpsLogger = FPSLogger()

    override fun create()
    {
        loadFont()

        asset.load("common")

        nextScreen = ScreenId.TITLE
    }

    override fun render()
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (asset.update())
        {
            /* nextScreenがセットされていたら,スクリーンを切り替え,nullに戻す. */
            nextScreen?.let {
                setScreen(it.newScreen(this))
                nextScreen = null
            }

            super.render()
        }
        else
            loading.render()

        when
        {
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) ->
                Gdx.graphics.setWindowedMode(360, 640)
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) ->
                Gdx.graphics.setWindowedMode(450, 800)
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) ->
                Gdx.graphics.setWindowedMode(540, 960)
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) ->
                Gdx.graphics.setWindowedMode(630, 1120)
        }

        //fpsLogger.log()
    }

    override fun dispose()
    {
        super.dispose()
        asset.unload("common")
        asset.dispose()
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
        asset.manager.apply {
            setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
            setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
            load("font.ttf", BitmapFont::class.java, param)
        }
    }
}
