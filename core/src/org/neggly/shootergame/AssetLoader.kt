package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.XmlReader

class AssetLoader(private val xmlPath: String)
{
    val manager = AssetManager()

    private val root by lazy { XmlReader().parse(Gdx.files.internal(xmlPath)) }

    fun update() = manager.update()

    fun dispose() = manager.dispose()

    fun get(name: String): Any = manager.get(name)

    fun load(sceneId: String)
    {
        val scene = root.getChildrenByName("scene")
                .find { it.getAttribute("id") == sceneId } ?: return
        for (asset in scene.getChildrenByName("asset"))
        {
            if (!asset.getBooleanAttribute("manual-load", false))
            {
                val path = asset.getAttribute("path")
                val className = asset.getAttribute("class")
                manager.load(path, Class.forName(className))
            }
        }
    }

    fun unload(sceneId: String)
    {
        val scene = root.getChildrenByName("scene")
                .find { it.getAttribute("id") == sceneId } ?: return
        for (asset in scene.getChildrenByName("asset"))
        {
            val path = asset.getAttribute("path")
            manager.unload(path)
        }
    }
}
