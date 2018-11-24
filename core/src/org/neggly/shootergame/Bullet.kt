package org.neggly.shootergame

class Bullet(mgr: ObjectMgr) : GameObject(mgr)
{
    init
    {
        texture = mgr.asset.get("bullet.png")
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
