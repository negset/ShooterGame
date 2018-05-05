package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport

class Loading
{
    private var uiCamera: OrthographicCamera? = null
    private var viewport: Viewport? = null
    private var batch: SpriteBatch? = null

    private val texture = Texture("loading.png")

    init
    {
        uiCamera = OrthographicCamera()
        uiCamera?.setToOrtho(false, WIDTH, HEIGHT)
        viewport = FitViewport(WIDTH, HEIGHT, uiCamera)
        batch = SpriteBatch()

        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
    }

    fun render()
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        uiCamera?.update()
        batch?.projectionMatrix = uiCamera?.combined

        batch?.begin()
        batch?.draw(texture, 0f, 0f)
        batch?.end()
    }

    fun dispose()
    {
        texture.dispose()
    }
}
