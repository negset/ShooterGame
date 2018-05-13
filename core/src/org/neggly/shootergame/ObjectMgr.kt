package org.neggly.shootergame

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * すべてのゲームオブジェクトのインスタンスを保有・管理するオブジェクト.
 *
 * @author negset
 */
object ObjectMgr : Group()
{
    private lateinit var assets: AssetManager

    lateinit var player: Player
    lateinit var boss: Boss
    private lateinit var enemies: Array<Enemy>
    private lateinit var bullets: Array<Bullet>
    private lateinit var shots: Array<Shot>
    private lateinit var items: Array<Item>

    /** スコア */
    var score = 0
    /** 残機 */
    var life = 3
    /** ゲームオーバーか否か */
    var isGameOver = false
    /** ボス戦中か否か */
    var bossTime = false

    /**
     * 初期化処理を行う.
     * アセットの読み込み命令も行う.
     *
     * @param assetManager アセットマネージャ
     */
    fun init(assetManager: AssetManager)
    {
        assets = assetManager
        assets.load("player.png", Texture::class.java)
        assets.load("boss.png", Texture::class.java)
        assets.load("enemy.png", Texture::class.java)
        assets.load("bullet.png", Texture::class.java)
        assets.load("shot.png", Texture::class.java)
        assets.load("item_2.png", Texture::class.java)

        score = 0
        life = 3
        isGameOver = false
        bossTime = false
    }

    /**
     *
     */
    fun setAssets()
    {
        player = Player(assets.get("player.png"))
        boss = Boss(assets.get("boss.png"))
        enemies = Array(20, { Enemy(assets.get("enemy.png")) })
        bullets = Array(50, { Bullet(assets.get("bullet.png")) })
        shots = Array(500, { Shot(assets.get("shot.png")) })
        items = Array(20, { Item(assets.get("item_2.png")) })

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

    /**
     * 自機弾とボスの衝突判定を行う.
     */
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

    /**
     * 自機弾と敵機の衝突判定を行う.
     */
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

    /**
     * 自機とボスの衝突判定を行う.
     */
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

    /**
     * 敵機弾と自機の衝突判定を行う.
     */
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

    /**
     * 自機と敵機の衝突判定を行う.
     */
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

    /**
     * 自機とアイテムの衝突判定を行う.
     */
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

    /**
     * 新規に自機弾を生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newBullet(x: Float, y: Float)
    {
        val bullet = bullets.filterNot { it.hasParent() }.first()
        bullet.activate(x, y)
        addActor(bullet)
    }

    /**
     * 新規に敵機弾を生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newShot(x: Float, y: Float, deg: Float)
    {
        val shot = shots.filterNot { it.hasParent() }.first()
        shot.activate(x, y, deg)
        addActor(shot)
    }

    /**
     * 新規に敵機を生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newEnemy(x: Float, y: Float)
    {
        val enemy = enemies.filterNot { it.hasParent() }.first()
        enemy.activate(x, y)
        addActor(enemy)
    }

    /**
     * 新規にアイテムを生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newItem(x: Float, y: Float)
    {
        val item = items.filterNot { it.hasParent() }.first()
        item.activate(x, y)
        addActor(item)
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

    /**
     * リソースの解放処理.
     * 子Actorの削除やアセットの解放を行う.
     */
    fun dispose()
    {
        clearChildren()
        remove()
        assets.unload("player.png")
        assets.unload("boss.png")
        assets.unload("enemy.png")
        assets.unload("bullet.png")
        assets.unload("shot.png")
        assets.unload("item_2.png")
    }
}
