package com.maestrovs.radiocar.service.player

import com.maestrovs.radiocar.enums.radio.PlayAction

interface AudioPlayerListener {
   fun  onPlayEvent(playAction: PlayAction)
   fun onSetAudioPlayerSessionId(sessionId: Int)
   fun onSongMetadata(metadata: String)
}