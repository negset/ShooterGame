package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture

class ItemIndicator(texture: Texture) : GameObject(texture)
{
    private lateinit var item: Item

    fun activate(item: Item)
    {
        super.activate(item.x, HEIGHT - height / 2)

        this.item = item
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        x = item.x

        if (item.y < HEIGHT + item.height / 2)
            deactivate()
    }
}
