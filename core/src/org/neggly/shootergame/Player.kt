package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2

class Player(texture: Texture) : GameObject(texture)
{
    private var baseX = 0f
    private var baseY = 0f

    private var touchPoint = Vector2()
    private var touchOrigin = Vector2()

    private var touching = false

    var isInvincible = false
    private var invincibleCounter = 0

    init
    {
        x = WIDTH / 2
        y = 500f
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (Gdx.input.isTouched)
        {
            touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            stage.viewport.unproject(touchPoint)

            if (!touching)
            {
                baseX = x
                baseY = y
                touchOrigin = touchPoint.cpy()
                touching = true
            }
            else
            {
                val v = touchPoint.sub(touchOrigin).scl(1.5f)
                x = baseX + v.x
                y = baseY + v.y

                if (x < 0) x = 0f
                else if (x > WIDTH) x = WIDTH
                if (y < 0) y = 0f
                else if (y > HEIGHT) y = HEIGHT
            }
        }
        else
        {
            touching = false
        }

        if (isInvincible)
        {
            if (invincibleCounter % 10 < 5)
                setAlpha(0f)
            else
                setAlpha(1f)
            if (++invincibleCounter > 200)
            {
                setAlpha(1f)
                invincibleCounter = 0
                isInvincible = false
            }
        }
    }
}
