# Shōnen-API
![alt text](https://i.imgur.com/FR9w3tg.png)

Shōnen is a simple API to create and share anime profiles on Discord! Tokens to access the API are configured manually at the moment (manuel.kour@shounen.me to request a token). 

# Endpoints

## User 

### Example
```
POST /user/166883258200621056/Asuha
GET /user/166883258200621056 or /user/Asuha
```

```json
{
   "discordID": "166883258200621056",
   "userName": "Asuha",
   "profilePicture": {
       "link": "https://i.waifu.pics/MHrvoGY.jpg"
   },
   "animeList": 132,
   "malUsername": "Shounen-chan"
}
```

#### Profile Picture 
The user's profile picture can be set manually by sending a POST request with the link to the picture, otherwise a random picture from [waifu.pics](https://waifu.pics) will be generated.
```
POST /user/166883258200621056/profile
```
```json
{
    "link": "https://i.waifu.pics/ueqBS0o.jpg"
}
```

## MAL (MyAnimeList)
User's can sync their MAL profile to their shounen.me profile by using [MAL v2 API's](https://myanimelist.net/apiconfig/references/api/v2) OAuth Flow.

### Example
Initializing the sync process
``` 
GET /166883258200621056/mal/sync/init
```
Where a link to authorize the API's access to the user's MAL account will be generated with the PKCE protocol (the link redirects the user directly to the authorization page as shown below).
``` 
https://api.shounen.me/mal/redirect/$verifier/$requestID
https://api.shounen.me/mal/redirect/B3fyJqEquHIMdx24OcwBZqwbfsWMocmBi-_1S2TA.MgnD-LhfufatQK5OZYHO8AY2EKs6YZ6WOcWgERuPQi_l8LrBddBUMJJ07Pe6nbMPJsSOCFixh4IoYLrNsOhk2_c/43
```

![shounen_mal_auth](https://user-images.githubusercontent.com/19893795/128627567-b8d19f48-1ca0-48d6-81d4-2aa5dcd9ddbc.png)


Once the user has synced their MAL account, the API can handle requests on their behalf, such as:
#### Adding a completed anime to their MAL anime list
```
POST /mal/166883258200621056/anime/21
``` 
#### Fetching the ten latest anime on the user's 'currently watching' list
```
GET /mal/166883258200621056/cw
```
```json
{
  "watching": [
    {"One Piece"},
    {"Owarimonogatari"},
    {"Shiguang Dailiren"},
    {"One Piece"},
    {"Kizumonogatari I"},
    {"Kizumonogatari II"},
    {"Kizumonogatari III"},
    {"Bakemonogatari"},
    {"Jujutsu Kaisen"},
    {"Mobile Suit Gundam: Iron-Blooded Orphans"},
  ]
}
```

##
Built with Ktor, Exposed & PostgreSQL ❤
