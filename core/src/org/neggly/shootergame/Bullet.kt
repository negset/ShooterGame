package org.neggly.shootergame

class Bullet(asset: AssetLoader) : GameObject()
{
    init
    {
        texture = asset.get("bullet.png")
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        y += 50

        if (y > HEIGHT + height / 2)
        {
            deactivate()
        }
    }
}
