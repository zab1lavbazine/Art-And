package art.example

import android.app.Application
import art.example.modules.appModules
import com.example.arthub.api.RetrofitInstance
import cz.fit.cvut.sdk.core.TolgeeSdkInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitInstance.initialize(this)
        TolgeeSdkInitializer.initializeFromMetadata(this)
        startKoin {
            androidContext(this@App)
            modules(appModules)
        }
    }
}