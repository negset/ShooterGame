package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction

class Enemy(texture: Texture) : GameObject(texture)
{
    var hp = 0
    private var stayCounter = 0

    private enum class State
    { ENTER, STAY, BACK }

    private var state = State.ENTER

    private lateinit var enterSeq: SequenceAction
    private lateinit var backSeq: SequenceAction

    private var shootCount = 0

    private var shootAngle = 0f

    private var shootPattern = 0

    /** あたり判定用の枠 */
    val bounds = Circle(x, y, 64f)

    override fun activate(x: Float, y: Float)
    {
        super.activate(x, y)

        hp = 30
        stayCounter = 0

        val amountY = MathUtils.random(500, 1000)
        val enter = Actions.moveBy(0f, -amountY.toFloat(), 1.5f, Interpolation.fade)
        val stay1 = Actions.delay(.5f)
        val stay2 = Actions.delay(.5f)
        val back = Actions.moveBy(0f, amountY.toFloat(), 1.5f, Interpolation.fade)

        enterSeq = Actions.sequence()
        enterSeq.addAction(enter)
        enterSeq.addAction(stay1)

        backSeq = Actions.sequence()
        backSeq.addAction(stay2)
        backSeq.addAction(back)

        addAction(enterSeq)

        state = State.ENTER

        shootCount = 0

        shootPattern = MathUtils.random(1)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (hp <= 0)
        {
            ObjectMgr.score += 300
            ObjectMgr.newItem(x, y)
            deactivate()
        }

        when (state)
        {
            State.ENTER ->
            {
                if (!hasActions())
                    state = State.STAY
            }

            State.STAY ->
            {
                when (shootPattern)
                {
                    0 -> shoot0()
                    1 -> shoot1()
                }
                stayCounter++
            }

            State.BACK ->
            {
                if (!hasActions())
                    deactivate()
            }
        }
    }

    override fun positionChanged()
    {
        bounds.setPosition(x, y)
    }

    private fun shoot0()
    {
        if (shootCount == 0)
            shootAngle = ObjectMgr.getAngleToPlayer(this)

        if (stayCounter % 10 == 0)
        {
            ObjectMgr.newShot(x, y, shootAngle - 20f)
            ObjectMgr.newShot(x, y, shootAngle)
            ObjectMgr.newShot(x, y, shootAngle + 20f)
            shootCount++
        }

        if (shootCount >= 5)
        {
            addAction(backSeq)

            state = State.BACK
        }
    }

    private fun shoot1()
    {
        if (shootCount == 0)
            shootAngle = 180f

        if (stayCounter % 4 == 0)
        {
            ObjectMgr.newShot(x, y, shootAngle - 120)
            ObjectMgr.newShot(x, y, shootAngle)
            ObjectMgr.newShot(x, y, shootAngle + 120)
            shootAngle += 20
            shootCount++
        }

        if (shootCount >= 6)
        {
            addAction(backSeq)

            state = State.BACK
        }
    }
}
