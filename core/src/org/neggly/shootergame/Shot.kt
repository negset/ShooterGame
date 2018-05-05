package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class Shot(private val mgr: ObjectMgr, texture: Texture) : GameObject(texture)
{
    fun activate(x: Float, y: Float, deg: Float)
    {
        super.activate(x, y)

        val v1 = Vector2(50f, 0f).setAngle(deg - 90)
        val v2 = Vector2(2560f, 0f).setAngle(deg - 90)

        val ac1 = Actions.moveBy(v1.x, v1.y, .5f, Interpolation.exp5)
        val ac2 = Actions.moveBy(v2.x, v2.y, 5f)

        val forever = Actions.forever(ac2)

        addAction(ac1)
        addAction(forever)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT)
            deactivate()
    }
}
