package org.odk.cersgis.audioclips

data class PlaybackFailedException(val uRI: String, val exceptionMsg: Int) : Exception()
