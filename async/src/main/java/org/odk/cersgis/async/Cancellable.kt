package org.odk.cersgis.async

interface Cancellable {
    fun cancel(): Boolean
}
