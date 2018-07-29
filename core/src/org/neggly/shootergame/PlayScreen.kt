package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport

/**
 * プレイ画面クラス.
 *
 * @author negset
 */
class PlayScreen(game: ShooterGame) : ScreenAdapter(game)
{
    private val stage = Stage(FitViewport(WIDTH, HEIGHT))
    private val batch = SpriteBatch()

    private val font = game.asset.get("font.ttf") as BitmapFont

    private val bg = Bg(game.asset)
    private val mgr = ObjectMgr(game.asset)

    private var counter = 0
    private var enemyCount = 0

    private var level = 1

    private val bulletSe = game.asset.get("bullet_se.wav") as Sound
    private val bgm = game.asset.get("bgm.mp3") as Music

    override fun show()
    {
        Gdx.input.inputProcessor = stage
        batch.projectionMatrix = stage.camera.combined

        bgm.isLooping = true
        bgm.play()

        stage.addActor(bg)
        stage.addActor(mgr)

        createExitButton()
    }

    override fun render(delta: Float)
    {
        draw()
        update(delta)
    }

    private fun draw()
    {
        batch.begin()
        font.draw(batch, "スコア: ${mgr.score}", 30f, 100f, WIDTH - 60, Align.left, true)
        font.draw(batch, "ライフ: ${mgr.life}", 30f, 100f, WIDTH - 60, Align.right, true)
        if (mgr.isGameOver)
            font.draw(batch, "ゲームオーバー", 0f, 1280f, WIDTH, Align.center, true)
        batch.end()

        stage.draw()
    }

    private fun update(delta: Float)
    {
        stage.act(delta)

        if (!mgr.isGameOver && Gdx.input.isTouched && counter % 5 == 0)
        {
            mgr.newBullet(mgr.player.x, mgr.player.y + 128)
            mgr.newBullet(mgr.player.x - 25, mgr.player.y + 108)
            mgr.newBullet(mgr.player.x + 25, mgr.player.y + 108)
            bulletSe.play()
        }

        if (!mgr.bossBattle && !mgr.waitForBoss)
        {
            if (counter % (150 - level * 10) == 0)
            {
                mgr.newEnemy(MathUtils.random(100f, WIDTH - 100), HEIGHT + 200)

                if (++enemyCount >= level * (level + 21) / 2)
                {
                    mgr.waitForBoss = true
                    if (level < 10) level++
                }
            }
        }

        counter++
    }

    override fun resize(width: Int, height: Int)
    {
        stage.viewport.update(width, height)
    }

    override fun hide()
    {
        dispose()
    }

    override fun dispose()
    {
        stage.dispose()
        batch.dispose()
        bg.dispose()
        mgr.dispose()
        game.asset.unload("play")
    }

    /**
     * タイトル画面に戻るボタンを作成する。
     */
    private fun createExitButton()
    {
        val texture = game.asset.get("exit.png") as Texture
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val image = Image(texture)
        image.setPosition(WIDTH - 100, HEIGHT - 100)
        val listener = object : ClickListener()
        {
            override fun clicked(event: InputEvent, x: Float, y: Float)
            {
                Gdx.input.vibrate(30)
                game.nextScreen = ScreenId.TITLE
            }
        }
        image.addListener(listener)
        stage.addActor(image)
    }
}
