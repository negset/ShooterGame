package org.neggly.shootergame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.GL20
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

class PlayScreen(game: ShooterGame) : ScreenAdapter(game)
{
    private val stage = Stage(FitViewport(WIDTH, HEIGHT))
    private val batch = SpriteBatch()

    private lateinit var font: BitmapFont

    private var counter = 0
    private var enemyCount = 0

    private var isAssetsUnset = true

    private var level = 1

    private val sound = Gdx.audio.newSound(Gdx.files.internal("shot.wav"))
    private lateinit var music: Music

    init
    {
        Gdx.input.inputProcessor = stage
        batch.projectionMatrix = stage.viewport.camera.combined
    }

    override fun show()
    {
        ObjectMgr.setAssetManager(game.assets)
        stage.addActor(ObjectMgr)

        game.assets.load("bgm.mp3", Music::class.java)

        /* 仮実装 */
        val texture = Texture("exit.png")
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val image = Image(texture)
        image.setPosition(WIDTH - 100, HEIGHT - 100)
        val listener = object : ClickListener()
        {
            override fun clicked(event: InputEvent, x: Float, y: Float)
            {
                game.nextScreen = TitleScreen(game)
            }
        }
        image.addListener(listener)
        stage.addActor(image)
    }

    override fun render(delta: Float)
    {
        if (isAssetsUnset) setAssets()
        draw()
        update(delta)
    }

    private fun draw()
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        font.draw(batch, "Score: ${ObjectMgr.score}", 30f, 100f, WIDTH - 60, Align.left, true)
        font.draw(batch, "Life: ${ObjectMgr.life}", 30f, 100f, WIDTH - 60, Align.right, true)
        if (ObjectMgr.isGameOver)
            font.draw(batch, "Game Over", 0f, 1280f, WIDTH, Align.center, true)
        batch.end()

        stage.draw()
    }

    private fun update(delta: Float)
    {
        stage.act(delta)

        if (!ObjectMgr.isGameOver && Gdx.input.isTouched && counter % 5 == 0)
        {
            ObjectMgr.newBullet(ObjectMgr.player.x, ObjectMgr.player.y)
            ObjectMgr.newBullet(ObjectMgr.player.x - 20, ObjectMgr.player.y - 10)
            ObjectMgr.newBullet(ObjectMgr.player.x + 20, ObjectMgr.player.y - 10)
            sound.play()
        }

        if (!ObjectMgr.bossTime)
        {
            if (enemyCount > level * 10)
            {
                ObjectMgr.bossTime = true
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
        ObjectMgr.dispose()
        sound.dispose()
        game.assets.unload("bgm.mp3")
    }

    private fun setAssets()
    {
        font = game.assets.get("default.otf")
        ObjectMgr.setAssets()
        isAssetsUnset = false

        music = game.assets.get("bgm.mp3")
        music.isLooping = true
        music.play()
    }
}
