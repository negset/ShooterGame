package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

open class GameObject(private val texture: Texture) : Actor()
{
    init
    {
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)
        width = texture.width.toFloat()
        height = texture.height.toFloat()
    }

    open fun activate(x: Float, y: Float)
    {
        clear()
        setPosition(x, y)
    }

    override fun draw(batch: Batch, parentAlpha: Float)
    {
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        batch.draw(texture, x - width / 2, y - height / 2)
    }

    open fun deactivate()
    {
        remove()
    }

    fun setAlpha(alpha: Float)
    {
        color.a = alpha
    }
}
