package com.echo.journal

import android.app.Application
import com.echo.journal.data.local.EchoDatabase
import com.echo.journal.data.repository.EchoRepository

class EchoApplication : Application() {
    val repository: EchoRepository by lazy {
        EchoRepository(EchoDatabase.get(this).echoDao())
    }
}
