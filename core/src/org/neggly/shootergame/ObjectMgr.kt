package org.neggly.shootergame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * すべてのゲームオブジェクトのインスタンスを保有・管理するクラス.
 *
 * @author negset
 */
class ObjectMgr(asset: AssetLoader) : Group()
{
    val player = Player(asset.get("player.png") as Texture)
    private val boss = Boss(asset.get("boss.png") as Texture)
    private val enemies = Array(20) { Enemy(asset.get("enemy.png") as Texture) }
    private val bullets = Array(50) { Bullet(asset.get("bullet.png") as Texture) }
    private val shots = Array(500) { Shot(asset.get("shot.png") as Texture) }
    private val items = Array(20) { Item(asset.get("item.png") as Texture) }

    /** スコア */
    var score = 0
    /** 残機 */
    var life = 3
    /** ゲームオーバーか否か */
    var isGameOver = false
    /** ボス戦中か否か */
    var bossBattle = false
        private set
    /** ボス戦待ち状態か否か */
    var waitForBoss = false

    init
    {
        addActor(player)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (bossBattle)
        {
            collisionDetectBulletAndBoss()
            collisionDetectionPlayerAndBoss()
        }
        else
        {
            collisionDetectionBulletAndEnemy()
            collisionDetectionPlayerAndEnemy()

            if (waitForBoss && !hasEnemy())
            {
                boss.activate(WIDTH / 2, HEIGHT + 200)
                addActor(boss)
                bossBattle = true
                waitForBoss = false
            }
        }

        collisionDetectionShotAndPlayer()
        collisionDetectionItemAndPlayer()
    }

    /**
     * 自機弾とボスの衝突判定を行う.
     */
    private fun collisionDetectBulletAndBoss()
    {
        if (!boss.hasParent()) return
        for (bullet in bullets.filter { it.hasParent() })
        {
            if (boss.bounds.contains(bullet.x, bullet.y))
            {
                if (--boss.hp <= 0)
                    bossBattle = false
                score += 10
                bullet.deactivate()
            }
        }
    }

    /**
     * 自機弾と敵機の衝突判定を行う.
     */
    private fun collisionDetectionBulletAndEnemy()
    {
        for (enemy in enemies.filter { it.hasParent() })
        {
            for (bullet in bullets.filter { it.hasParent() })
            {
                if (enemy.bounds.contains(bullet.x, bullet.y))
                {
                    enemy.hp--
                    score += 10
                    bullet.deactivate()
                }
            }
        }
    }

    /**
     * 自機とボスの衝突判定を行う.
     */
    private fun collisionDetectionPlayerAndBoss()
    {
        if (isGameOver) return
        if (player.isInvincible) return
        if (!boss.hasParent()) return
        if (player.bounds.overlaps(boss.bounds))
        {
            addLife(-1)
            player.isInvincible = true
        }
    }

    /**
     * 敵機弾と自機の衝突判定を行う.
     */
    private fun collisionDetectionShotAndPlayer()
    {
        if (isGameOver) return
        if (player.isInvincible) return
        for (shot in shots.filter { it.hasParent() })
        {
            if (player.bounds.contains(shot.x, shot.y))
            {
                addLife(-1)
                player.isInvincible = true
                shot.deactivate()
            }
        }
    }

    /**
     * 自機と敵機の衝突判定を行う.
     */
    private fun collisionDetectionPlayerAndEnemy()
    {
        if (isGameOver) return
        if (player.isInvincible) return
        for (enemy in enemies.filter { it.hasParent() })
        {
            if (player.bounds.overlaps(enemy.bounds))
            {
                addLife(-1)
                player.isInvincible = true
            }
        }
    }

    /**
     * 自機とアイテムの衝突判定を行う.
     */
    private fun collisionDetectionItemAndPlayer()
    {
        if (isGameOver) return
        for (item in items.filter { it.hasParent() })
        {
            if (player.bounds.contains(item.x, item.y))
            {
                score += 100
                item.deactivate()
            }
            else if (!item.approaching
                    && player.itemApproachBounds.contains(item.x, item.y))
            {
                item.approaching = true
            }
        }
    }

    /**
     * 新規に自機弾を生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newBullet(x: Float, y: Float)
    {
        val bullet = bullets.find { !it.hasParent() } ?: return
        bullet.activate(x, y)
        addActor(bullet)
    }

    /**
     * 新規に敵機弾を生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newShot(x: Float, y: Float, deg: Float)
    {
        val shot = shots.find { !it.hasParent() } ?: return
        shot.activate(x, y, deg)
        addActor(shot)
    }

    /**
     * 新規に敵機を生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newEnemy(x: Float, y: Float)
    {
        val enemy = enemies.find { !it.hasParent() } ?: return
        enemy.activate(x, y)
        addActor(enemy)
    }

    /**
     * 新規にアイテムを生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newItem(x: Float, y: Float)
    {
        val item = items.find { !it.hasParent() } ?: return
        item.activate(x, y)
        addActor(item)
    }

    private fun getAngle(obj1: GameObject, obj2: GameObject): Float
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

    private fun addLife(value: Int)
    {
        life += value
        if (life <= 0)
        {
            player.deactivate()
            isGameOver = true
        }
    }

    private fun hasEnemy(): Boolean
    {
        return enemies.any { it.hasParent() }
    }

    /**
     * リソースの解放処理.
     * 子Actorの削除やアセットの解放を行う.
     */
    fun dispose()
    {
        clearChildren()
        remove()
    }
}
