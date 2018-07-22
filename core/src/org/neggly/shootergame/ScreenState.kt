package org.neggly.shootergame

enum class ScreenState
{
    TITLE
    {
        override val assetsSceneId = "title"
        override fun newScreen(game: ShooterGame) = TitleScreen(game)
    },
    PLAY
    {
        override val assetsSceneId = "play"
        override fun newScreen(game: ShooterGame) = PlayScreen(game)
    },
    CREDITS
    {
        override val assetsSceneId = "credits"
        override fun newScreen(game: ShooterGame) = CreditsScreen(game)
    };

    abstract val assetsSceneId: String

    abstract fun newScreen(game: ShooterGame): ScreenAdapter
}
