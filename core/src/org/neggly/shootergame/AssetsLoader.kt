package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.XmlReader

class AssetsLoader(private val xmlPath: String)
{
    val manager = AssetManager()

    fun update() = manager.update()

    fun dispose() = manager.dispose()

    fun load(sceneId: String)
    {
        val root = XmlReader().parse(Gdx.files.internal(xmlPath))
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

    fun get(name: String): Any
    {
        return manager.get(name)
    }

    fun unload(sceneId: String)
    {
        val root = XmlReader().parse(Gdx.files.internal(xmlPath))
        val scene = root.getChildrenByName("scene")
                .find { it.getAttribute("id") == sceneId } ?: return
        for (asset in scene.getChildrenByName("asset"))
        {
            val path = asset.getAttribute("path")
            manager.unload(path)
        }
    }
}
