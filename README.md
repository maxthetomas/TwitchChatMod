# Twitch chat mod
 _"It was probably already done before, but I wanted to try it myself."_

This mod sends messages from twitch chat to minecraft chat. Works entirely on the client.

## Config
Configuration file can be found at `.minecraft/config/twitch-irc.json`. (You cannot put comments in the actual file)
```jsonc
{
  "nickname": "", // twitch username
  "oauth": "",    // oauth token from https://twitchapps.com/tmi/
  "default": ""   // channel to auto-subscribe 
}
```

### Commands
- **/twitch [channel name]** - subscribe to channel (you can only be subscribed to a single channel) 
- **/twitch** - unsubscribe from channel
- **/tc [message]** - send a message to a chat
