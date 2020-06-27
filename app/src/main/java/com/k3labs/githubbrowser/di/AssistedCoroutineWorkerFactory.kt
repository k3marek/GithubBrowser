package com.k3labs.githubbrowser.di

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

interface AssistedCoroutineWorkerFactory {
    /**
     * Param's names must be as the worker ones
     */
    fun create(context: Context, workerParameters: WorkerParameters): CoroutineWorker
}