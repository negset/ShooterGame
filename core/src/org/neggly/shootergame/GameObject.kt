package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * ゲームオブジェクトクラス.
 * すべてのゲームオブジェクトがこのクラスを継承する.
 *
 * @author negset
 */
open class GameObject(private val texture: Texture) : Actor()
{
    init
    {
        /* 拡大・縮小時も滑らかにする. */
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear)

        width = texture.width.toFloat()
        height = texture.height.toFloat()
    }

    /**
     * オブジェクトの初期化処理を行う.
     * 必要に応じてオーバーライドし,処理を増やす.
     *
     * @param x X座標
     * @param y Y座標
     */
    open fun activate(x: Float, y: Float)
    {
        clear()
        setPosition(x, y)
    }

    override fun draw(batch: Batch, parentAlpha: Float)
    {
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        batch.draw(texture, x - width / 2, y - height / 2)
    }

    /**
     * オブジェクトを無効化する.
     */
    open fun deactivate()
    {
        remove()
    }

    /**
     * 描画時の透明度を設定する.
     *
     * @param alpha 透明度(0f~1f)
     */
    fun setAlpha(alpha: Float)
    {
        color.a = alpha
    }
}
