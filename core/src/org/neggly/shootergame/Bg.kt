package org.neggly.shootergame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.graphics.Pixmap


/**
 * 背景クラス.
 *
 * @author negset
 */
class Bg(asset: AssetLoader) : Group()
{
    private val bg: Texture

    private val stars = Array(30) {
        Star(asset.get("bg_star.png") as Texture,
                MathUtils.random(WIDTH), MathUtils.random(HEIGHT))
    }

    init
    {
        Pixmap(1, 1, Pixmap.Format.RGBA8888).run {
            setColor(Color(0f, 0f, 0.30f, 1f))
            fill()
            bg = Texture(this)
            dispose()
        }

        for (star in stars)
            addActor(star)
    }

    override fun draw(batch: Batch, parentAlpha: Float)
    {
        batch.draw(bg, 0f, 0f, WIDTH, HEIGHT)

        super.draw(batch, parentAlpha)
    }

    fun dispose()
    {
        clearChildren()
        remove()
    }

    /**
     * 背景に流れる星のクラス.
     *
     * @author negset
     */
    private class Star(val texture: Texture, x: Float, y: Float) : Actor()
    {
        init
        {
            this.x = x
            this.y = y

            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)

            width = texture.width.toFloat()
            height = texture.height.toFloat()

            val down = Actions.moveBy(0f, -(HEIGHT + height),
                    15 + 10 * Math.random().toFloat())
            addAction(Actions.forever(down))
        }

        override fun draw(batch: Batch, parentAlpha: Float)
        {
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
            batch.draw(texture, x - width / 2, y - height / 2)
        }

        override fun act(delta: Float)
        {
            super.act(delta)

            if (y < 0 - height / 2)
                y = HEIGHT + height / 2
        }
    }
}
