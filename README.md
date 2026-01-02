# YetAnotherMacspoofer
Very, very simple macspoofing app for android (require root & lsposed). Doesn't even have any gui or anything, you have to dive into the code to change the mac🤣🤣

### Why do I make this
Acts as a proof-of-concept (POC), could be a good starting point if someone wanted to make a full-fledged open source one.
A similar app have existed for a while, but it charges some hefty money and has way too many features for most people's needs

### How does it work?
It hooks to `WifiNative#setMacAddress` method that's usually used to replace current active MAC Address with a new one, mainly to randomize a MAC Address when connecting to new wifi.

After successfully hooking into the method, it injects a predefined MAC Address everytime the function is called.

Though, WifiNative isn't accessible through normal classloader because it's loaded by `com.android.server.wifi.WifiService`. Therefore the whole process consists of 3 steps:
1. Retrieve `WifiService` via `SystemServiceManager`'s classloader
2. Use the classloader to retrieve `WifiNative`
3. Hook `WifiNative#setMacAddress` and do the transformation there
