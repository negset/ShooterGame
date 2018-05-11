package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture

class Bullet(texture: Texture) : GameObject(texture)
{
    override fun act(delta: Float)
    {
        super.act(delta)

        y += 50

        if (y > HEIGHT + height / 2)
        {
            deactivate()
        }
    }
}
