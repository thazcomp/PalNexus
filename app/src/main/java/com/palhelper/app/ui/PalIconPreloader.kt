package com.palhelper.app.ui

import android.content.Context
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

private const val MAX_CONCURRENT_LOADS = 8
private const val PRELOAD_TIMEOUT_MS = 20_000L

/**
 * Preloads every given Pal icon URL into Coil's memory/disk cache before the UI is shown, so
 * the picker list and result cards render every icon instantly instead of popping images in
 * one by one as the user scrolls or selects Pals.
 *
 * Failures (missing icon, offline, slow host) are swallowed per-URL — a Pal without a loadable
 * icon simply falls back to its placeholder badge later; it never blocks the rest of the batch.
 * The whole preload is capped at [PRELOAD_TIMEOUT_MS] so a stalled connection can't leave the
 * user stuck on the loading screen forever — after the timeout the app opens anyway, and any
 * icon that didn't finish just loads normally (or shows its placeholder) as before.
 *
 * @param onProgress Called after each icon finishes (success or failure) with (loadedSoFar, total).
 */
suspend fun preloadPalIcons(
    context: Context,
    urls: List<String>,
    onProgress: (loaded: Int, total: Int) -> Unit
) {
    if (urls.isEmpty()) {
        onProgress(0, 0)
        return
    }

    val loader = context.imageLoader
    var loaded = 0
    onProgress(loaded, urls.size)

    withTimeoutOrNull(PRELOAD_TIMEOUT_MS.milliseconds) {
        coroutineScope {
            urls.chunked(MAX_CONCURRENT_LOADS).forEach { chunk ->
                val deferredResults = chunk.map { url ->
                    async {
                        runCatching {
                            loader.execute(ImageRequest.Builder(context).data(url).build())
                        }
                    }
                }
                deferredResults.forEach { deferred ->
                    deferred.await()
                    loaded++
                    onProgress(loaded, urls.size)
                }
            }
        }
    }
}
