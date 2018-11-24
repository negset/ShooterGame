package org.neggly.shootergame

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * すべてのゲームオブジェクトのインスタンスを保有・管理するクラス.
 *
 * @author negset
 */
class ObjectMgr(val asset: AssetLoader) : Group()
{
    val player = Player(this)
    private val boss = Boss(this)
    private val enemies = Array(20) { Enemy(this) }
    private val bullets = Array(50) { Bullet(this) }
    private val shots = Array(500) { Shot(this) }
    private val items = Array(20) { Item(this) }
    private val itemIndicators = Array(10) { ItemIndicator(this) }

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
    var waitingBoss = false

    private var bossCount = 0

    init
    {
        addActor(player)
    }

    override fun act(delta: Float)
    {
        super.act(delta)

        if (bossBattle)
        {
            if (!boss.hasParent())
                bossBattle = false

            collisionBulletAndBoss()
            collisionPlayerAndBoss()
        }
        else
        {
            collisionBulletAndEnemy()
            collisionPlayerAndEnemy()

            if (waitingBoss && !hasEnemy())
            {
                boss.activate(WIDTH / 2, HEIGHT + 200, bossCount % 3)
                addActor(boss)
                bossBattle = true
                waitingBoss = false
                bossCount++
            }
        }

        collisionShotAndPlayer()
        collisionItemAndPlayer()
    }

    /**
     * 自機弾とボスの衝突判定を行う.
     */
    private fun collisionBulletAndBoss()
    {
        if (!boss.hasParent()) return
        for (bullet in bullets.filter { it.hasParent() })
        {
            if (boss.bounds.contains(bullet.x, bullet.y))
            {
                boss.damaged()
                bullet.deactivate()
                if (!boss.hasParent()) return
            }
        }
    }

    /**
     * 自機弾と敵機の衝突判定を行う.
     */
    private fun collisionBulletAndEnemy()
    {
        for (bullet in bullets.filter { it.hasParent() })
        {
            for (enemy in enemies.filter { it.hasParent() })
            {
                if (enemy.bounds.contains(bullet.x, bullet.y))
                {
                    enemy.damaged()
                    bullet.deactivate()
                }
            }
        }
    }

    /**
     * 自機とボスの衝突判定を行う.
     */
    private fun collisionPlayerAndBoss()
    {
        if (isGameOver) return
        if (player.isInvincible) return
        if (!boss.hasParent()) return
        if (player.bounds.overlaps(boss.bounds))
        {
            player.damaged()
        }
    }

    /**
     * 敵機弾と自機の衝突判定を行う.
     */
    private fun collisionShotAndPlayer()
    {
        if (isGameOver) return
        if (player.isInvincible) return
        for (shot in shots.filter { it.hasParent() })
        {
            if (player.bounds.contains(shot.x, shot.y))
            {
                player.damaged()
                shot.deactivate()
                return
            }
        }
    }

    /**
     * 自機と敵機の衝突判定を行う.
     */
    private fun collisionPlayerAndEnemy()
    {
        if (isGameOver) return
        if (player.isInvincible) return
        for (enemy in enemies.filter { it.hasParent() })
        {
            if (player.bounds.overlaps(enemy.bounds))
            {
                player.damaged()
                return
            }
        }
    }

    /**
     * 自機とアイテムの衝突判定を行う.
     */
    private fun collisionItemAndPlayer()
    {
        if (isGameOver) return
        for (item in items.filter { it.hasParent() })
        {
            if (player.bounds.contains(item.x, item.y))
            {
                item.caught()
            }
            else if (!item.approaching &&
                    player.itemApproachBounds.contains(item.x, item.y))
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
        bullets.find { !it.hasParent() }?.run {
            activate(x, y)
            addActor(this)
        }
    }

    /**
     * 新規に敵機弾を生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newShot(x: Float, y: Float, deg: Float, speed: Float = 1f)
    {
        shots.find { !it.hasParent() }?.run {
            activate(x, y, deg, speed)
            addActor(this)
        }
    }

    /**
     * 新規に敵機を生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newEnemy(x: Float, y: Float)
    {
        enemies.find { !it.hasParent() }?.run {
            activate(x, y)
            addActor(this)
        }
    }

    /**
     * 新規にアイテムを生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newItem(x: Float, y: Float)
    {
        items.find { !it.hasParent() }?.run {
            activate(x, y)
            addActor(this)
        }
    }

    /**
     * 新規にアイテムインジケータを生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newItemIndicator(item: Item)
    {
        itemIndicators.find { !it.hasParent() }?.run {
            activate(item)
            addActor(this)
        }
    }

    private fun getAngle(obj1: GameObject, obj2: GameObject): Float
    {
        val dx = obj2.x - obj1.x
        val dy = obj2.y - obj1.y
        return 180 - MathUtils.atan2(dx, dy) * MathUtils.radDeg
    }

    fun getAngleToPlayer(obj: GameObject): Float
    {
        return getAngle(obj, player)
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
