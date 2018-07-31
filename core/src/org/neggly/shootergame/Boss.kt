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
     * DELAY: ショット開始前の待機
     * SHOOT: ショットを撃つ
     */
    private enum class State
    { ENTER, SHOOT, BACK }

    /** 動作状態 */
    private var state = State.ENTER

    /** HP */
    var hp = 0
    /** HPの上限値 */
    private val maxHp = 300
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

    override fun activate(x: Float, y: Float)
    {
        super.activate(x, y)

        state = State.ENTER

        hp = maxHp
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
                shoot()
                shootCounter++
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

    private fun shoot()
    {
        if (shootCounter % 3 == 0)
        {
            mgr.newShot(x, y, shootAngle)
            mgr.newShot(x, y, shootAngle - 60)
            mgr.newShot(x, y, shootAngle - 120)
            mgr.newShot(x, y, shootAngle + 60)
            mgr.newShot(x, y, shootAngle + 120)
            mgr.newShot(x, y, shootAngle + 180)
            shootAngle += 34
        }
        if (shootCounter > 900)
        {
            addAction(back)
            state = State.BACK
        }
    }
}
