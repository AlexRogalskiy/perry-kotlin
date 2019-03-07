package com.beust.perry

import com.hubspot.dropwizard.guice.GuiceBundle
import io.dropwizard.Application
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

class PerryApp : Application<DemoConfig>() {
    private lateinit var guiceBundle: GuiceBundle<DemoConfig>

    override fun initialize(configuration: Bootstrap<DemoConfig>) {
        configuration.addBundle(AssetsBundle("/assets", "/", "index.html", "static"));
        guiceBundle = GuiceBundle.newBuilder<DemoConfig>()
                .addModule(PerryModule())
                .setConfigClass(DemoConfig::class.java)
                .build()

        configuration.addBundle(guiceBundle)
    }

    override fun run(config: DemoConfig, env: Environment) {
        listOf(PerryService::class.java).forEach {
            env.jersey().register(it)
        }

        env.healthChecks().register("template", DemoCheck(config.version))
    }
}