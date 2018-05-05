package org.neggly.shootergame

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.FPSLogger
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader

const val WIDTH = 1440f
const val HEIGHT = 2560f

class ShooterGame : Game()
{
    private val fpsLogger = FPSLogger()

    var nextScreen: Screen? = null

    val assets = AssetManager()

    private var font: BitmapFont? = null

    private var loading: Loading? = null

    override fun create()
    {
        createFont()
        setScreen(TitleScreen(this))

        font = BitmapFont()

        loading = Loading()
    }

    override fun render()
    {
        if (assets.update())
        {
            super.render()
        }
        else
        {
            loading?.render()
        }

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
        loading?.dispose()
        font?.dispose()
    }

    /**
     * フォントの生成および設定
     */
    private fun createFont()
    {
        val resolver = InternalFileHandleResolver()
        assets.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        assets.setLoader(BitmapFont::class.java, ".otf", FreetypeFontLoader(resolver))
        val params = FreetypeFontLoader.FreeTypeFontLoaderParameter()
        params.fontFileName = "default.otf"
        params.fontParameters.size = 80
        params.fontParameters.color = Color.WHITE
        params.fontParameters.borderColor = Color.DARK_GRAY
        params.fontParameters.borderWidth = 8f
        params.fontParameters.magFilter = Texture.TextureFilter.Linear
        params.fontParameters.minFilter = Texture.TextureFilter.Linear
        params.fontParameters.incremental = true
        assets.load("default.otf", BitmapFont::class.java, params)
    }
}
