# Shōnen-API
![alt text](https://i.imgur.com/FR9w3tg.png)

Shōnen is a simple API to create and share anime profiles on Discord! Tokens to access the API are configured manually at the moment (to be automized soon). The user's profile picture can be set manually, if not, it will be a random picture from [waifu.pics](https://waifu.pics).

# Endpoints

## User 

### Example
```
POST /user/166883258200621056/Asuha
POST /user/166883258200621056/profile
POST /user/166883258200621056/anime
GET /user/166883258200621056
GET /user/Asuha
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
```json
{
    "link": "https://i.waifu.pics/ueqBS0o.jpg"
}
```

#### Anime
```json
{
    "name": "One Piece"
}
```
