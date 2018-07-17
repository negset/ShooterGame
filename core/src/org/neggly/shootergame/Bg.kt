package org.neggly.shootergame

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions

/**
 * 背景クラス.
 *
 * @author negset
 */
class Bg : Group()
{
    private lateinit var assets: AssetManager

    private lateinit var stars: Array<Star>

    private var isAssetsUnset = true

    override fun act(delta: Float)
    {
        if (isAssetsUnset)
            setAssets()

        super.act(delta)
    }

    fun loadAssets(assets: AssetManager)
    {
        this.assets = assets
        assets.load("bg_star.png", Texture::class.java)
    }

    private fun setAssets()
    {
        stars = Array(30) {
            Star(assets.get("bg_star.png"),
                    WIDTH * Math.random().toFloat(),
                    HEIGHT * Math.random().toFloat())
        }

        for (star in stars)
            addActor(star)

        isAssetsUnset = false
    }

    fun dispose()
    {
        clearChildren()
        remove()
        assets.unload("bg_star.png")
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
