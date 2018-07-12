package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class Item(texture: Texture) : GameObject(texture)
{
    var pos = Vector2()

    var approaching = false
    var gathering = false

    override fun activate(x: Float, y: Float)
    {
        super.activate(x, y)

        approaching = false
        gathering = false

        val up = Actions.moveBy(0f, 600f, 1.5f)
        val down = Actions.moveBy(0f, -600f, 1.5f)
        val forever = Actions.forever(down)
        val seq = Actions.sequence()
        seq.addAction(up)
        seq.addAction(forever)
        addAction(seq)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (ObjectMgr.player.y > 2000)
            approaching = true

        if (approaching)
            approach()

        if (y < 0) deactivate()
    }

    private fun approach()
    {
        if (ObjectMgr.isGameOver)
        {
            approaching = false
            return
        }
        if (hasActions())
        {
            clearActions()
            pos.x = x
            pos.y = y
        }
        pos.lerp(Vector2(ObjectMgr.player.x, ObjectMgr.player.y), .25f)
        setPosition(pos.x, pos.y)
    }
}
