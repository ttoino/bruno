# Bruno

A terminal based multiplayer card game that was definitely not inspired by another wildly popular card game.

I wrote this mostly as a way to learn how to make a multiplayer game back in 2019 (before I really used GitHub), 
but also so I could play a fun card game with my stingy friends.

This uses a framework I also wrote at the time ([WebSocketNet](https://github.com/ttoino/websocketnet))
for the actual game communication between players.
Authentication, lobby codes and profile picture storage are handled with Firebase.
Currently active lobbies have the server's IP stored in a database and the game port forwarded
using [WaifUPnP](https://github.com/adolfintel/WaifUPnP).
