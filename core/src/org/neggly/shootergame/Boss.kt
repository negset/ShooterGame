package org.neggly.shootergame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction

/**
 * ボスクラス.
 */
class Boss(texture: Texture) : GameObject(texture)
{
    private val mgr by lazy { parent as ObjectMgr }

    /**
     * 動作状態を表す列挙型.
     * ENTER: 登場
     * SHOOT: ショットを撃つ
     * BACK: 退却
     */
    private enum class State
    { ENTER, SHOOT, BACK }

    /** 動作状態 */
    private var state = State.ENTER

    /** HP */
    var hp = 0
    /** HPの上限値 */
    private val maxHp = 300
    private var shootPattern = 0
    /** 弾の射出方向 */
    private var shootAngle = 0f

    private var shootCounter = 0

    /** HPゲージ描画用 */
    private val hpPixmap = Pixmap(1024, 32, Pixmap.Format.RGBA8888)
    private lateinit var hpTexture: Texture

    private lateinit var enter: SequenceAction
    private lateinit var back: SequenceAction

    /** あたり判定用の枠 */
    val bounds = Circle(x, y, 64f)

    fun activate(x: Float, y: Float, shootPattern: Int)
    {
        super.activate(x, y)

        state = State.ENTER

        hp = maxHp
        this.shootPattern = shootPattern
        shootAngle = 0f

        shootCounter = 0

        hpPixmap.setColor(Color.GREEN)
        hpPixmap.fillRectangle(0, 0, 1024, 32)
        hpTexture = Texture(hpPixmap)

        enter = Actions.sequence().apply {
            addAction(Actions.moveBy(0f, -800f, 1.5f, Interpolation.fade))
            addAction(Actions.delay(1f))
        }
        back = Actions.sequence().apply {
            addAction(Actions.delay(1f))
            addAction(Actions.moveBy(0f, 800f, 1.5f, Interpolation.fade))
        }
        addAction(enter)
    }

    /**
     * HPゲージの描画を行うためにオーバーライドする.
     */
    override fun draw(batch: Batch, parentAlpha: Float)
    {
        super.draw(batch, parentAlpha)

        hpPixmap.setColor(Color.DARK_GRAY)
        hpPixmap.fill()
        hpPixmap.setColor(Color.GREEN)
        hpPixmap.fillRectangle(0, 0, 1024 * hp / maxHp, 32)
        hpTexture.draw(hpPixmap, 0, 0)
        batch.draw(hpTexture, 208f, 2400f)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (hp <= 0)
        {
            mgr.score += 3000
            mgr.newItem(x, y + 36)
            mgr.newItem(x - 48, y - 36)
            mgr.newItem(x + 48, y - 36)
            mgr.explosionSe.play()
            deactivate()
        }

        when (state)
        {
            State.ENTER ->
            {
                if (!hasActions())
                {
                    state = State.SHOOT
                }
            }

            State.SHOOT ->
            {
                if (shootCounter == 0)
                    mgr.bossShotSe.play()
                when (shootPattern)
                {
                    0 -> shoot0()
                    1 -> shoot1()
                    2 -> shoot2()
                }
                if (++shootCounter >= 900)
                {
                    addAction(back)
                    state = State.BACK
                }
            }

            State.BACK ->
            {
                if (!hasActions())
                {
                    deactivate()
                }
            }
        }
    }

    override fun positionChanged()
    {
        bounds.setPosition(x, y)
    }

    private fun shoot0()
    {
        if (shootCounter % 3 == 0)
        {
            for (i in 0 until 4)
            {
                val deg = shootAngle + i * 90
                mgr.newShot(x, y, deg)
                mgr.newShot(x, y, -deg)
            }
            shootAngle += 11
        }
    }

    private fun shoot1()
    {
        val clockwise = shootCounter / 45 % 2 == 0
        if (shootCounter % 45 == 0)
        {
            shootAngle = mgr.getAngleToPlayer(this) +
                    if (clockwise) 45 else -45
        }
        if (shootCounter % 45 < 30 && shootCounter % 3 == 0)
        {
            val speed = .75f + shootCounter % 45 / 90f
            for (i in 0 until 3)
            {
                val deg = shootAngle + i * 120
                mgr.newShot(x, y, deg, speed)
                mgr.newShot(x, y, deg + 16, speed)
                mgr.newShot(x, y, deg - 16, speed)
            }
            shootAngle += if (clockwise) -9 else 9
        }
    }

    private fun shoot2()
    {
        if (shootCounter % 50 == 0)
        {
            for (i in 180 until 540 step 5)
                mgr.newShot(x, y, i.toFloat(), .7f)
            if (shootCounter % 250 == 100)
            {
                shootAngle = mgr.getAngleToPlayer(this)
                for (i in 0 until 10)
                {
                    mgr.newShot(x, y, shootAngle - i, 1.5f - i * .1f)
                    mgr.newShot(x, y, shootAngle + i, 1.5f - i * .1f)
                }
            }
        }
    }
}
