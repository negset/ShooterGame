package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

class ObjectMgr(private val assets: AssetManager) : Group()
{
    lateinit var player: Player
    lateinit var boss: Boss
    private lateinit var enemies: Array<Enemy>
    private lateinit var bullets: Array<Bullet>
    private lateinit var shots: Array<Shot>
    private lateinit var items: Array<Item>

    var score = 0
    var life = 3
    var isGameOver = false
    var bossTime = false

    init
    {
        assets.load("player.png", Texture::class.java)
        assets.load("boss.png", Texture::class.java)
        assets.load("enemy.png", Texture::class.java)
        assets.load("bullet.png", Texture::class.java)
        assets.load("shot.png", Texture::class.java)
        assets.load("item_2.png", Texture::class.java)
    }

    fun setAssets()
    {
        player = Player(this, assets.get("player.png"))
        boss = Boss(this, assets.get("boss.png"))
        enemies = Array(20, { Enemy(this, assets.get("enemy.png")) })
        bullets = Array(50, { Bullet(this, assets.get("bullet.png")) })
        shots = Array(500, { Shot(this, assets.get("shot.png")) })
        items = Array(20, { Item(this, assets.get("item_2.png")) })

        addActor(player)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (bossTime)
        {
            if (!hasEnemy() && !boss.hasParent())
            {
                boss.activate(WIDTH / 2, HEIGHT + 200)
                addActor(boss)
            }
            collisionDetectBulletAndBoss()
            collisionDetectionPlayerAndBoss()
        }

        collisionDetectionBulletAndEnemy()
        collisionDetectionPlayerAndEnemy()
        collisionDetectionShotAndPlayer()
        collisionDetectionItemToPlayer()
    }

    private fun collisionDetectBulletAndBoss()
    {
        if (!boss.hasParent()) return
        val rect = Rectangle(boss.x - 64, boss.y - 64,
                128f, 128f)
        for (bullet in bullets.filter { it.hasParent() })
        {
            if (rect.contains(bullet.x, bullet.y))
            {
                boss.hp--
                if (boss.hp <= 0)
                    bossTime = false
                bullet.deactivate()
            }
        }
    }

    private fun collisionDetectionBulletAndEnemy()
    {
        for (enemy in enemies.filter { it.hasParent() })
        {
            val rect = Rectangle(enemy.x - 64, enemy.y - 64,
                    128f, 128f)
            for (bullet in bullets.filter { it.hasParent() })
            {
                if (rect.contains(bullet.x, bullet.y))
                {
                    enemy.hp--
                    bullet.deactivate()
                }
            }
        }
    }

    private fun collisionDetectionPlayerAndBoss()
    {
        if (player.isInvincible) return
        if (!boss.hasParent()) return
        val playerCircle = Circle(player.x, player.y, 30f)
        val bossCircle = Circle(boss.x, boss.y, 80f)
        if (playerCircle.overlaps(bossCircle))
        {
            addLife(-1)
            player.isInvincible = true
        }
    }

    private fun collisionDetectionShotAndPlayer()
    {
        if (player.isInvincible) return
        val circle = Circle(player.x, player.y, 30f)
        for (shot in shots.filter { it.hasParent() })
        {
            if (circle.contains(shot.x, shot.y))
            {
                addLife(-1)
                player.isInvincible = true
                shot.deactivate()
            }
        }
    }

    private fun collisionDetectionPlayerAndEnemy()
    {
        if (player.isInvincible) return
        val playerCircle = Circle(player.x, player.y, 30f)
        for (enemy in enemies.filter { it.hasParent() })
        {
            val enemyCircle = Circle(enemy.x, enemy.y, 80f)
            if (playerCircle.overlaps(enemyCircle))
            {
                addLife(-1)
                player.isInvincible = true
            }
        }
    }

    private fun collisionDetectionItemToPlayer()
    {
        val circle1 = Circle(player.x, player.y, 20f)
        val circle2 = Circle(player.x, player.y, 200f)
        for (item in items.filter { it.hasParent() })
        {
            if (circle1.contains(item.x, item.y))
            {
                score += 100
                item.deactivate()
            }
            else if (!item.approaching && circle2.contains(item.x, item.y))
            {
                item.approaching = true
            }
        }
    }

    fun newBullet(x: Float, y: Float)
    {
        for (bullet in bullets.filter { !it.hasParent() })
        {
            bullet.activate(x, y)
            addActor(bullet)
            return
        }
        Gdx.app.error("ObjectMgr", "no free bullet")
    }

    fun newShot(x: Float, y: Float, deg: Float)
    {
        for (shot in shots.filter { !it.hasParent() })
        {
            shot.activate(x, y, deg)
            addActor(shot)
            return
        }
        Gdx.app.error("ObjectMgr", "no free shot")
    }

    fun newEnemy(x: Float, y: Float)
    {
        for (enemy in enemies.filter { !it.hasParent() })
        {
            enemy.activate(x, y)
            addActor(enemy)
            return
        }
        Gdx.app.error("ObjectMgr", "no free enemy")
    }

    fun newItem(x: Float, y: Float)
    {
        for (item in items.filter { !it.hasParent() })
        {
            item.activate(x, y)
            addActor(item)
            return
        }
        Gdx.app.error("ObjectMgr", "no free item")
    }

    fun getAngle(obj1: GameObject, obj2: GameObject): Float
    {
        val v1 = Vector2(obj1.x, obj1.y)
        val v2 = Vector2(obj2.x, obj2.y)
        val v3 = v2.sub(v1)
        return 180 - Math.toDegrees(MathUtils.atan2(v3.x, v3.y).toDouble()).toFloat()
    }

    fun getAngleToPlayer(obj: GameObject): Float
    {
        return getAngle(obj, player)
    }

    fun addLife(value: Int)
    {
        life += value
        if (life <= 0)
        {
            player.deactivate()
            isGameOver = true
        }
    }

    fun hasEnemy(): Boolean
    {
        return enemies.filter { it.hasParent() }.isNotEmpty()
    }

    fun dispose()
    {
        assets.unload("player.png")
        assets.unload("boss.png")
        assets.unload("enemy.png")
        assets.unload("bullet.png")
        assets.unload("shot.png")
        assets.unload("item_2.png")
    }
}
