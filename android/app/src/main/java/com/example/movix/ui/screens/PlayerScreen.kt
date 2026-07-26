package com.example.movix.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.movix.data.local.WatchProgress
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    navController: NavController, 
    url: String, 
    title: String,
    tmdbId: Int,
    mediaType: String,
    posterPath: String?,
    season: Int? = null,
    episode: Int? = null,
    fileIdent: String? = null,
    viewModel: MediaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val appContext = context.applicationContext

    val exoPlayer = remember<ExoPlayer> {
        val renderersFactory = DefaultRenderersFactory(appContext)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
            .setEnableDecoderFallback(true)

        val trackSelector = DefaultTrackSelector(appContext).apply {
            setParameters(
                buildUponParameters()
                    .setPreferredAudioLanguage("cze")
                    .setMaxAudioChannelCount(2)
            )
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        ExoPlayer.Builder(appContext, renderersFactory)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(audioAttributes, true)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(appContext)
                    .setDataSourceFactory(
                        DefaultHttpDataSource.Factory()
                            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
                            .setAllowCrossProtocolRedirects(true)
                    )
            )
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        android.util.Log.e("PlayerScreen", "Playback error: ${error.errorCodeName}", error)
                    }
                })
                setMediaItem(MediaItem.fromUri(url))
                prepare()
                playWhenReady = true
            }
    }

    LaunchedEffect(exoPlayer) {
        // Seek to saved position
        val progress = viewModel.getWatchProgress(tmdbId)
        if (progress != null && progress.position < progress.duration - 10000) {
            exoPlayer.seekTo(progress.position)
        }

        while (true) {
            delay(5000)
            if (exoPlayer.isPlaying) {
                viewModel.saveWatchProgress(
                    WatchProgress(
                        id = tmdbId,
                        title = title,
                        mediaType = mediaType,
                        posterPath = posterPath,
                        position = exoPlayer.currentPosition,
                        duration = exoPlayer.duration,
                        season = season,
                        episode = episode,
                        fileIdent = fileIdent,
                        fileName = title
                    )
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = {
                PlayerView(appContext).apply {
                    player = exoPlayer
                    useController = true
                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
