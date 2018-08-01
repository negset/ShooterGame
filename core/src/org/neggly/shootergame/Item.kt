package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class Item(texture: Texture) : GameObject(texture)
{
    private val mgr by lazy { parent as ObjectMgr }

    private var pos = Vector2()

    var approaching = false

    private var hasIndicator = false

    override fun activate(x: Float, y: Float)
    {
        super.activate(x, y)

        approaching = false

        hasIndicator = false

        val up = Actions.moveBy(0f, 600f, 2f, Interpolation.circleOut)
        val down = Actions.moveBy(0f, -600f, 1.5f)
        val seq = Actions.sequence()
        seq.addAction(up)
        seq.addAction(Actions.forever(down))
        addAction(seq)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (!mgr.isGameOver && mgr.player.y > 2000)
            approaching = true

        if (approaching)
        {
            if (mgr.isGameOver)
            {
                approaching = false
                val down = Actions.moveBy(0f, -600f, 1.5f)
                addAction(Actions.forever(down))
            }
            else
                approach()
        }

        if (!hasIndicator && y > HEIGHT + height / 2)
        {
            mgr.newItemIndicator(this)
            hasIndicator = true
        }

        if (y < 0) deactivate()
    }

    private fun approach()
    {
        if (hasActions())
        {
            clearActions()
            pos.x = x
            pos.y = y
        }
        pos.lerp(Vector2(mgr.player.x, mgr.player.y), .25f)
        setPosition(pos.x, pos.y)
    }
}
