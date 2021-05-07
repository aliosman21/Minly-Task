# Minly task

**Notice:** As I don't own a Mac ios native development was quite difficult so in order not to deliver an incomplete  task I opted to use React Native for the ios and tested it on an IPhone and it worked as well as the android

That being said let's start with installation instructions

Clone this repository and for each directory I will explain how to start step by step

## NodeJS Server

- Open a terminal in the directory and run ```npm install```
- run ```node server.js```
- If you like to make any changes do it in server.ts then run ```tsc server.ts``` then run ```node server.js``` again


## IOS (React Native)

Since the server is not hosted you have to manually type the local IP address in the app.js of the machine running the NodeJS server

In line 6 in [App.js](./ios/App.js)

```javascript
const serverURL = "http://<IP ADDRESS HERE>:3000/"; 
```
After that follow these steps

- Open a terminal in the directory and run npm install
- run `expo start` this will open a browser window where you can scan the QR code using any phone (Android/IOS)


## Android Native
- Clone the repository
- Open android studio
- Go to File -> New -> Import project
- Choose the project directory 
- Then build & Run 


# Endpoints

If you wanted to test it using postman these are the endpoints

- (GET) Getting the images names `http://localhost:3000/` response is an array of strings where you can locate the file names served statically by the server
- (POST) on `http://localhost:3000/api/upload` using `FormData` with `KEY`: photo and `value` [The image being uploaded]
- The server connects with each client using websocket so when a client uploads an image it sends a signal for all clients to call again to get the new images