package io.github.trueangle.cocktail

import android.app.Application
import io.github.trueangle.cocktail.di.AppComponent
import io.github.trueangle.cocktail.di.AppComponentDependencies

class CocktailApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = AppComponent.init(AppComponentDependencies(this))
    }
}