package com.beust.perry

import io.dropwizard.Configuration

class DemoConfig : Configuration() {
    var version: String = "0.1"
    var dbProvider: String? = null
}
