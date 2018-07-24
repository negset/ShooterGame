package org.neggly.shootergame

enum class ScreenId
{
    TITLE
    {
        override val assetSceneId = "title"
        override fun newScreen(game: ShooterGame) = TitleScreen(game)
    },
    PLAY
    {
        override val assetSceneId = "play"
        override fun newScreen(game: ShooterGame) = PlayScreen(game)
    },
    CREDITS
    {
        override val assetSceneId = "credits"
        override fun newScreen(game: ShooterGame) = CreditsScreen(game)
    };

    abstract val assetSceneId: String

    abstract fun newScreen(game: ShooterGame): ScreenAdapter
}
