package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2

class Player(texture: Texture) : GameObject(texture)
{
    private val mgr by lazy { parent as ObjectMgr }

    private var counter = 0

    private var baseX = 0f
    private var baseY = 0f

    private var touchPoint = Vector2()
    private var touchOrigin = Vector2()

    private var touching = false

    private var shooting = false

    var isInvincible = false
    private var invincibleCounter = 0

    /** あたり判定用の枠 */
    val bounds = Circle(x, y, 30f)
    val itemApproachBounds = Circle(x, y, 100f)

    init
    {
        x = WIDTH / 2
        y = 500f
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (moveByTouch() || moveByKeyboard())
        {
            if (x < 0) x = 0f
            else if (x > WIDTH) x = WIDTH
            if (y < 0) y = 0f
            else if (y > HEIGHT) y = HEIGHT
        }

        if (shooting && counter % 5 == 0)
        {
            mgr.newBullet(x, y + 128)
            mgr.newBullet(x - 25, y + 108)
            mgr.newBullet(x + 25, y + 108)
            mgr.bulletSe.play()
        }

        if (isInvincible)
        {
            color.a = if (invincibleCounter % 10 < 5) 0f else 1f
            if (++invincibleCounter > 200)
            {
                color.a = 1f
                invincibleCounter = 0
                isInvincible = false
            }
        }

        counter++
    }

    private fun moveByTouch(): Boolean
    {
        if (Gdx.input.isTouched)
        {
            touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            stage.screenToStageCoordinates(touchPoint)

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
            }
            shooting = true
            return true
        }

        touching = false
        shooting = false
        return false
    }

    private fun moveByKeyboard(): Boolean
    {
        shooting = Gdx.input.isKeyPressed(Input.Keys.Z)

        val speed =
                when
                {
                    Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) -> 4f
                    Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) -> 24f
                    else -> 14f
                }

        val vec = Vector2()
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            vec.x = 1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
        {
            vec.x = -1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
        {
            vec.y = 1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            vec.y = -1f
        }
        return if (vec.len2() > 0)
        {
            vec.setLength(speed)
            x += vec.x
            y += vec.y
            true
        }
        else false
    }

    override fun positionChanged()
    {
        bounds.setPosition(x, y)
        itemApproachBounds.setPosition(x, y)
    }
}
