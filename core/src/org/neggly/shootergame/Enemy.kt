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

    private enum class State
    { ENTER, SHOOT, BACK }

    private var state = State.ENTER

    var hp = 0
    private var shootPattern = 0
    private var shootAngle = 0f

    private var shootCounter = 0

    private lateinit var enter: SequenceAction
    private lateinit var back: SequenceAction

    /** あたり判定用の枠 */
    val bounds = Circle(x, y, 64f)

    override fun activate(x: Float, y: Float)
    {
        super.activate(x, y)

        state = State.ENTER

        hp = 30
        shootPattern = MathUtils.random(2)

        shootCounter = 0

        val dy = MathUtils.random(500f, 1000f)
        enter = Actions.sequence().apply {
            addAction(Actions.moveBy(0f, -dy, 1.5f, Interpolation.fade))
            addAction(Actions.delay(.5f))
        }
        back = Actions.sequence().apply {
            addAction(Actions.delay(.5f))
            addAction(Actions.moveBy(0f, dy, 1.5f, Interpolation.fade))
        }
        addAction(enter)
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
                    state = State.SHOOT
            }

            State.SHOOT ->
            {
                when (shootPattern)
                {
                    0 -> shoot0()
                    1 -> shoot1()
                    2 -> shoot2()
                }
                shootCounter++
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
        if (shootCounter == 0)
        {
            shootAngle = mgr.getAngleToPlayer(this)
        }

        if (shootCounter % 10 == 0)
        {
            mgr.newShot(x, y, shootAngle - 20f)
            mgr.newShot(x, y, shootAngle)
            mgr.newShot(x, y, shootAngle + 20f)
            if (shootCounter % 20 == 0)
                mgr.shotSe.play()
        }

        if (shootCounter > 45)
        {
            addAction(back)
            state = State.BACK
        }
    }

    private fun shoot1()
    {
        if (shootCounter == 0)
        {
            shootAngle = 180f
        }

        if (shootCounter % 5 == 0)
        {
            mgr.newShot(x, y, shootAngle - 120)
            mgr.newShot(x, y, shootAngle)
            mgr.newShot(x, y, shootAngle + 120)
            if (shootCounter % 10 == 0)
                mgr.shotSe.play()
            shootAngle += 20
        }

        if (shootCounter > 24)
        {
            addAction(back)
            state = State.BACK
        }
    }

    private fun shoot2()
    {
        if (shootCounter == 0)
        {
            shootAngle = mgr.getAngleToPlayer(this) - 30
            mgr.shotSe.play()
        }
        if (shootCounter % 4 == 0 && shootCounter % 24 != 20)
        {
            mgr.newShot(x, y, shootAngle)
            if (shootCounter % 24 == 0)
                mgr.shotSe.play()
            shootAngle += if (shootCounter / 24 % 2 == 0) 12 else -12
        }

        if (shootCounter > 64)
        {
            addAction(back)
            state = State.BACK
        }
    }
}
