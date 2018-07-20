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

    private val font = game.assets.get("font.ttf") as BitmapFont

    private val bg = Bg(game.assets)

    private var counter = 0
    private var enemyCount = 0

    private var level = 1

    private val bulletSe = game.assets.get("bullet_se.wav") as Sound
    private val bgm = game.assets.get("bgm.mp3") as Music

    override fun show()
    {
        Gdx.input.inputProcessor = stage
        batch.projectionMatrix = stage.camera.combined

        bgm.isLooping = true
        bgm.play()

        ObjectMgr.assets = game.assets
        ObjectMgr.init()

        stage.addActor(bg)
        stage.addActor(ObjectMgr)

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
        font.draw(batch, "スコア: ${ObjectMgr.score}", 30f, 100f, WIDTH - 60, Align.left, true)
        font.draw(batch, "ライフ: ${ObjectMgr.life}", 30f, 100f, WIDTH - 60, Align.right, true)
        if (ObjectMgr.isGameOver)
            font.draw(batch, "ゲームオーバー", 0f, 1280f, WIDTH, Align.center, true)
        batch.end()

        stage.draw()
    }

    private fun update(delta: Float)
    {
        stage.act(delta)

        if (!ObjectMgr.isGameOver && Gdx.input.isTouched && counter % 5 == 0)
        {
            ObjectMgr.newBullet(ObjectMgr.player.x, ObjectMgr.player.y + 128)
            ObjectMgr.newBullet(ObjectMgr.player.x - 25, ObjectMgr.player.y + 108)
            ObjectMgr.newBullet(ObjectMgr.player.x + 25, ObjectMgr.player.y + 108)
            bulletSe.play()
        }

        if (!ObjectMgr.bossBattle)
        {
            if (enemyCount > level * 10)
            {
                ObjectMgr.bossBattle = true
                level++
                println("level: $level")
            }
            else if (counter % maxOf(150 - level * 10, 50) == 0)
            {
                ObjectMgr.newEnemy(MathUtils.random(100, (WIDTH - 100).toInt()).toFloat(), HEIGHT + 200)
                enemyCount++
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
        ObjectMgr.dispose()
        game.assets.unload("play")
    }

    /**
     * タイトル画面に戻るボタンを作成する。
     */
    private fun createExitButton()
    {
        val texture = Texture("exit.png")
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val image = Image(texture)
        image.setPosition(WIDTH - 100, HEIGHT - 100)
        val listener = object : ClickListener()
        {
            override fun clicked(event: InputEvent, x: Float, y: Float)
            {
                game.nextScreen = ShooterGame.Screens.TITLE
            }
        }
        image.addListener(listener)
        stage.addActor(image)
    }
}
