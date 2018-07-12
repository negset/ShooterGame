package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.FitViewport

class Loading
{
    private var stage: Stage = Stage(FitViewport(WIDTH, HEIGHT))

    init
    {
        val tex = Texture("loading.png")
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val image = Image(tex)
        image.setPosition(100f, 100f)
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
