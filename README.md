# Shōnen-API
![alt text](https://i.imgur.com/FR9w3tg.png)

Shōnen is a simple API to create and share anime profiles on Discord! Tokens to access the API are configured manually at the moment (manuelkour@asuha.dev to request a token). The user's profile picture can be set manually, otherwise it will generate a random picture from [waifu.pics](https://waifu.pics).

# Endpoints

## User 

### Example
```
POST /user/166883258200621056/Asuha
POST /user/166883258200621056/profile
POST /user/166883258200621056/anime/One+Piece
GET /user/166883258200621056 or /user/Asuha
```

```json
{
    "discordID": "166883258200621056",
    "userName": "Asuha",
    "profilePicture": "https://i.waifu.pics/ueqBS0o.jpg",
    "animeList": ["One Piece"]
}
```

#### Profile Picture 
Profile pictures need to be send as a JSON object, which will be handled internally. 
```json
{
    "link": "https://i.waifu.pics/ueqBS0o.jpg"
}
```

