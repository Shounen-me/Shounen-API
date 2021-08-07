# Shōnen-API
![alt text](https://i.imgur.com/FR9w3tg.png)

Shōnen is a simple API built with to create and share anime profiles on Discord! Tokens to access the API are configured manually at the moment (manuel.kour@shounen.me to request a token). The user's profile picture can be set manually, otherwise it will generate a random picture from [waifu.pics](https://waifu.pics).

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
Profile pictures need to be send as a JSON object, which will be handled internally. 
```
POST /user/166883258200621056/profile
```
```json
{
    "link": "https://i.waifu.pics/ueqBS0o.jpg"
}
```

## MAL (MyAnimeList)
User's can sync their MAL profile to their shounen.me profile by using the [MAL v2 API](https://myanimelist.net/apiconfig/references/api/v2).

### Example
Initializing the sync process
``` 
GET /166883258200621056/mal/sync/init
```
Where a link to authorize the API's access to the user's MAL account will be generated with the PKCE protocol.
``` 
https://myanimelist.net/v1/oauth2/authorize?response_type=code&client_id=6FWSlxOLz1dc-lkPZ7ursqFuOAOVgx93tt-r928o.-ClZ_00lm~qAGlSOO~hXtqIawysBp3CfiQlrTo5gqgIX8Yi_sb0NGbGY890x5PgpXD8MlVcldvlDWMhnqAOdpqF&code_challenge=6FWSlxOLz1dc-lkPZ7ursqFuOAOVgx93tt-r928o.-ClZ_00lm~qAGlSOO~hXtqIawysBp3CfiQlrTo5gqgIX8Yi_sb0NGbGY890x5PgpXD8MlVcldvlDWMhnqAOdpqF&state=RequestIDkotlin.random.XorWowRandom@71318ec4 
```

![BIzD6ofwgrBYJ7SsM7ZyVmyp4K5getR0aW8U3EwdruV7I_yEiMBczy21-Thirwb7v0GRzjSD7hmyL9R7VVSK9H0hGfwNfXUgk-zouEBHlQUxZnVh7nYcFFs8FG9MoK0FRYYCA-fM4jlGp7eg4ZTq3Sl9FPaV3gIMkrW3ZqIw3St8dO7kdYg2Q2sriPbbLTh-dn7UayADecUf5AyhT9Yqig](https://user-images.githubusercontent.com/19893795/128597349-e0a106a3-aa2a-4ada-a899-a8f1ebe7cfbe.png)

After the user authorized the access, their data in the DB will be updated with his data from the MAL account (-> user name on MAL, # of completed anime, access & refresh tokens).
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
