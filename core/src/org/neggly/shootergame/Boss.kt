package org.neggly.shootergame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class Boss(private val mgr: ObjectMgr, texture: Texture) : GameObject(texture)
{
    private var counter = 0

    private val maxHp = 300
    var hp = 0

    private enum class State
    { ENTER, DELAY, SHOOT }

    private var state = State.ENTER

    private var shootAngle = 0f

    private val pixmap = Pixmap(1024, 32, Pixmap.Format.RGBA8888)
    private lateinit var hpTexture: Texture

    override fun activate(x: Float, y: Float)
    {
        super.activate(x, y)

        counter = 0
        hp = maxHp
        state = State.ENTER
        shootAngle = 0f

        pixmap.setColor(Color.GREEN)
        pixmap.fillRectangle(0, 0, 1024, 32)
        hpTexture = Texture(pixmap)

        val enter = Actions.moveBy(0f, -800f, 1.5f, Interpolation.fade)
        addAction(enter)
    }

    override fun draw(batch: Batch, parentAlpha: Float)
    {
        super.draw(batch, parentAlpha)

        batch.draw(hpTexture, 208f, 2400f)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        pixmap.setColor(Color.DARK_GRAY)
        pixmap.fill()
        pixmap.setColor(Color.GREEN)
        pixmap.fillRectangle(0, 0, 1024 * hp / maxHp, 32)
        hpTexture.draw(pixmap, 0, 0)

        if (hp <= 0)
        {
            mgr.score += 1000
            mgr.newItem(x, y)
            mgr.newItem(x-36, y-54)
            mgr.newItem(x+36, y-54)
            deactivate()
        }

        when (state)
        {
            State.ENTER ->
            {
                if (!hasActions())
                {
                    val delay = Actions.delay(1f)
                    addAction(delay)
                    state = State.DELAY
                }
            }

            State.DELAY ->
            {
                if (!hasActions())
                {
                    state = State.SHOOT
                }
            }

            State.SHOOT ->
            {
                shoot()
                counter++
            }
        }
    }

    private fun shoot()
    {
        when
        {
            counter % 3 == 0 ->
            {
                mgr.newShot(x, y, shootAngle)
                mgr.newShot(x, y, shootAngle - 60)
                mgr.newShot(x, y, shootAngle - 120)
                mgr.newShot(x, y, shootAngle + 60)
                mgr.newShot(x, y, shootAngle + 120)
                mgr.newShot(x, y, shootAngle + 180)
                shootAngle += 34
            }
        }
    }

}
