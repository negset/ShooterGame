package org.neggly.shootergame

class ItemIndicator(mgr: ObjectMgr) : GameObject(mgr)
{
    private lateinit var item: Item

    init
    {
        texture = mgr.asset.get("item_indicator.png")
    }

    fun activate(item: Item)
    {
        super.activate(item.x, HEIGHT - height / 2)

        this.item = item
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        x = item.x

        if (item.y < HEIGHT + item.height / 2)
            deactivate()
    }
}
