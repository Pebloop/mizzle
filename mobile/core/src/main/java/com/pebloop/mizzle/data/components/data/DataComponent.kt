package com.pebloop.mizzle.data.components.data

import java.io.Serializable


class DataComponent<T>(var data: T, val displayId: String): Serializable
