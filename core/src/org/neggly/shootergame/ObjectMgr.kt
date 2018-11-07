package org.neggly.shootergame

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group

/**
 * すべてのゲームオブジェクトのインスタンスを保有・管理するクラス.
 *
 * @author negset
 */
class ObjectMgr(asset: AssetLoader) : Group()
{
    val player = Player(asset.get("player.png"))
    private val boss = Boss(asset.get("boss.png"))
    private val enemies = Array(20) { Enemy(asset.get("enemy.png")) }
    private val bullets = Array(50) { Bullet(asset.get("bullet.png")) }
    private val shots = Array(500) { Shot(asset.get("shot.png")) }
    private val items = Array(20) { Item(asset.get("item.png")) }
    private val itemIndicators = Array(10) { ItemIndicator(asset.get("item_indicator.png")) }

    val bulletSe: Sound = asset.get("bullet_se.wav")
    val bossShotSe: Sound = asset.get("boss_shot_se.wav")
    //val enemyDamageSe: Sound = asset.get("enemy_damage_se.wav")
    val enemyShotSe: Sound = asset.get("enemy_shot_se.wav")
    val explosionSe: Sound = asset.get("explosion_se.wav")
    val itemCatchSe: Sound = asset.get("item_catch_se.wav")
    val playerDamageSe: Sound = asset.get("player_damage_se.wav")

    /** スコア */
    var score = 0
    /** 残機 */
    var life = 3
        private set
    /** ゲームオーバーか否か */
    var isGameOver = false
        private set
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
                boss.hp--
                score += 10
                //enemyDamageSe.play()
                bullet.deactivate()
            }
        }
    }

    /**
     * 自機弾と敵機の衝突判定を行う.
     */
    private fun collisionBulletAndEnemy()
    {
        for (enemy in enemies.filter { it.hasParent() })
        {
            for (bullet in bullets.filter { it.hasParent() })
            {
                if (enemy.bounds.contains(bullet.x, bullet.y))
                {
                    enemy.hp--
                    score += 10
                    //enemyDamageSe.play()
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
            damagePlayer()
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
                damagePlayer()
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
                damagePlayer()
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
                score += 100
                itemCatchSe.play()
                item.deactivate()
            }
            else if (!item.approaching
                    && player.itemApproachBounds.contains(item.x, item.y))
            {
                item.approaching = true
            }
        }
    }

    private fun damagePlayer()
    {
        if (--life > 0)
        {
            playerDamageSe.play()
            player.isInvincible = true
        }
        else
        {
            explosionSe.play()
            player.deactivate()
            isGameOver = true
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
    fun newShot(x: Float, y: Float, deg: Float, speed: Float = 1f)
    {
        val shot = shots.find { !it.hasParent() } ?: return
        shot.activate(x, y, deg, speed)
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

    /**
     * 新規にアイテムインジケータを生成する.
     * 実際は配列から無効なものを探して有効化する.
     */
    fun newItemIndicator(item: Item)
    {
        val itemIndicator = itemIndicators.find { !it.hasParent() } ?: return
        itemIndicator.activate(item)
        addActor(itemIndicator)
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
