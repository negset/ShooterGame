package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction

class Enemy(texture: Texture) : GameObject(texture)
{
    private val mgr by lazy { parent as ObjectMgr }

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

        shootPattern = MathUtils.random(2)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (hp <= 0)
        {
            mgr.score += 300
            mgr.newItem(x, y)
            mgr.explosionSe.play()
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
                    2 -> shoot2()
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
            shootAngle = mgr.getAngleToPlayer(this)

        if (stayCounter % 10 == 0)
        {
            mgr.newShot(x, y, shootAngle - 20f)
            mgr.newShot(x, y, shootAngle)
            mgr.newShot(x, y, shootAngle + 20f)
            mgr.shotSe.play()
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
            mgr.newShot(x, y, shootAngle - 120)
            mgr.newShot(x, y, shootAngle)
            mgr.newShot(x, y, shootAngle + 120)
            mgr.shotSe.play()
            shootAngle += 20
            shootCount++
        }

        if (shootCount >= 6)
        {
            addAction(backSeq)
            state = State.BACK
        }
    }

    private fun shoot2()
    {
        if (shootCount == 0)
            shootAngle = mgr.getAngleToPlayer(this) - 30

        if (stayCounter % 4 == 0 && stayCounter % 24 != 20)
        {
            mgr.newShot(x, y, shootAngle)
            mgr.shotSe.play()
            shootAngle += if (stayCounter / 24 % 2 == 0) 12 else -12
            shootCount++
        }

        if (shootCount >= 15)
        {
            addAction(backSeq)
            state = State.BACK
        }
    }
}
