package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align

class TextButton : Actor()
{
    var bg: NinePatch? = null
    var offset = 0f
    var font: BitmapFont? = null
        set(value)
        {
            field = value
            offset = (field?.xHeight ?: 0f) / 2
        }
    var text: String = ""

    private val bounds = Rectangle()

    private var touchPoint = Vector2()

    override fun draw(batch: Batch, parentAlpha: Float)
    {
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        bg?.draw(batch, x - width / 2, y - height / 2, width, height)
        font?.draw(batch, text, 0f, y + offset, WIDTH, Align.center, true)
    }

    fun isClicked(): Boolean
    {
        if (Gdx.input.justTouched())
        {
            touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            stage.screenToStageCoordinates(touchPoint)
            return bounds.contains(touchPoint)
        }
        return false
    }

    override fun positionChanged()
    {
        bounds.setPosition(x - width / 2, y - height / 2)
    }

    override fun sizeChanged()
    {
        bounds.set(x - width / 2, y - height / 2, width, height)
    }
}
