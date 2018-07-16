package org.neggly.shootergame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions

/**
 * ボスクラス.
 */
class Boss(texture: Texture) : GameObject(texture)
{
    /** フレームカウンタ */
    private var counter = 0

    /** HPの上限値 */
    private val maxHp = 300
    /** HP */
    var hp = 0

    /**
     * 動作状態を表す列挙型.
     * ENTER: 登場
     * DELAY: ショット開始前の待機
     * SHOOT: ショットを撃つ
     */
    private enum class State
    { ENTER, DELAY, SHOOT }

    /** 動作状態 */
    private var state = State.ENTER

    /** 弾の射出方向 */
    private var shootAngle = 0f

    /** HPゲージ描画用 */
    private val hpPixmap = Pixmap(1024, 32, Pixmap.Format.RGBA8888)
    private lateinit var hpTexture: Texture

    /** あたり判定用の枠 */
    val bounds = Circle(x, y, 64f)

    override fun activate(x: Float, y: Float)
    {
        super.activate(x, y)

        counter = 0
        hp = maxHp
        state = State.ENTER
        shootAngle = 0f

        hpPixmap.setColor(Color.GREEN)
        hpPixmap.fillRectangle(0, 0, 1024, 32)
        hpTexture = Texture(hpPixmap)

        val enter = Actions.moveBy(0f, -800f, 1.5f, Interpolation.fade)
        addAction(enter)
    }

    /**
     * HPゲージの描画を行うためにオーバーライドする.
     */
    override fun draw(batch: Batch, parentAlpha: Float)
    {
        super.draw(batch, parentAlpha)

        batch.draw(hpTexture, 208f, 2400f)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        /* HPゲージの描画 */
        hpPixmap.setColor(Color.DARK_GRAY)
        hpPixmap.fill()
        hpPixmap.setColor(Color.GREEN)
        hpPixmap.fillRectangle(0, 0, 1024 * hp / maxHp, 32)
        hpTexture.draw(hpPixmap, 0, 0)

        if (hp <= 0)
        {
            ObjectMgr.score += 3000
            ObjectMgr.newItem(x, y + 36)
            ObjectMgr.newItem(x - 48, y - 36)
            ObjectMgr.newItem(x + 48, y - 36)
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

        bounds.setPosition(x, y)
    }

    private fun shoot()
    {
        when
        {
            counter % 3 == 0 ->
            {
                ObjectMgr.newShot(x, y, shootAngle)
                ObjectMgr.newShot(x, y, shootAngle - 60)
                ObjectMgr.newShot(x, y, shootAngle - 120)
                ObjectMgr.newShot(x, y, shootAngle + 60)
                ObjectMgr.newShot(x, y, shootAngle + 120)
                ObjectMgr.newShot(x, y, shootAngle + 180)
                shootAngle += 34
            }
        }
    }
}
