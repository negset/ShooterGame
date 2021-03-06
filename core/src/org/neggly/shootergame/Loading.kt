package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.FitViewport

class Loading
{
    private val stage = Stage(FitViewport(WIDTH, HEIGHT))

    init
    {
        val texture = Texture("loading.png")
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val image = Image(texture)
        image.setPosition(30f, 30f)
        stage.addActor(image)
    }

    fun render()
    {
        stage.act()
        stage.draw()
    }

    fun dispose()
    {
        stage.dispose()
    }
}
