package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
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

    private val font: BitmapFont = game.asset.get("font.ttf")

    private val bg = Bg(game.asset)
    private val mgr = ObjectMgr(game.asset)

    private var counter = 0
    private var level = 1
    private var enemyCount = 0
    private var enemyNumUntilNextBoss = 11

    private val bgm: Music = game.asset.get("bgm.mp3")

    private val titleBtn = TextButton()

    override fun show()
    {
        Gdx.input.inputProcessor = stage
        batch.projectionMatrix = stage.camera.combined

        bgm.isLooping = true
        bgm.play()

        stage.addActor(bg)
        stage.addActor(mgr)

        setupButtons()
    }

    override fun render(delta: Float)
    {
        draw()
        update(delta)
    }

    private fun draw()
    {
        stage.draw()

        batch.begin()
        font.draw(batch, "スコア: ${mgr.score}", 30f, 100f, WIDTH - 60, Align.left, true)
        font.draw(batch, "ライフ: ${mgr.life}", 30f, 100f, WIDTH - 60, Align.right, true)
        if (mgr.isGameOver)
            font.draw(batch, "ゲームオーバー", 0f, 1480f, WIDTH, Align.center, true)
        batch.end()
    }

    private fun update(delta: Float)
    {
        stage.act(delta)

        if (!mgr.bossBattle && !mgr.waitingBoss)
        {
            if (counter % (150 - level * 10) == 0)
            {
                mgr.newEnemy(MathUtils.random(100f, WIDTH - 100), HEIGHT + 200)

                if (++enemyCount >= enemyNumUntilNextBoss)
                {
                    mgr.waitingBoss = true
                    if (level < 10)
                    {
                        level++
                        enemyNumUntilNextBoss = level * (level + 21) / 2
                    }
                    else
                        enemyNumUntilNextBoss += 20
                }
            }
        }

        if (mgr.isGameOver && !titleBtn.hasParent())
            stage.addActor(titleBtn)

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

    private fun setupButtons()
    {
        val btnBg = game.asset.get<Texture>("button_bg.png").run {
            setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            NinePatch(this, 64, 64, 64, 64)
        }
        titleBtn.let {
            it.bg = btnBg
            it.font = font
            it.text = "タイトルへ"
            it.offset = 30f
            it.clickEvent = {
                Gdx.input.vibrate(30)
                game.nextScreen = ScreenId.TITLE
            }
            it.setPosition(WIDTH / 2, 1080f)
            it.setSize(500f, 180f)
        }

        val texture: Texture = game.asset.get("exit.png")
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
