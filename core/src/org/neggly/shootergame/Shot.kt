package org.neggly.shootergame

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class Shot(asset: AssetLoader) : GameObject()
{
    init
    {
        texture = asset.get("shot.png")
    }

    fun activate(x: Float, y: Float, deg: Float, speed: Float)
    {
        super.activate(x, y)

        val sx = speed * MathUtils.cosDeg(deg - 90)
        val sy = speed * MathUtils.sinDeg(deg - 90)

        val ac1 = Actions.moveBy(50 * sx, 50 * sy, .5f, Interpolation.exp5)
        val ac2 = Actions.forever(Actions.moveBy(2560 * sx, 2560 * sy, 5f))

        addAction(ac1)
        addAction(ac2)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (x !in 0 - width / 2..WIDTH + width / 2 ||
                y !in 0 - height / 2..HEIGHT + height / 2)
            deactivate()
    }
}
