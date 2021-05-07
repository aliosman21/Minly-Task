"use strict";
exports.__esModule = true;
var multer = require("multer");
var cors = require("cors");
var express = require("express");
var fs = require("fs");
var path = require("path");
var app = express();
var staticDir = "./images";
app.use(cors());
app.use(express.json());
app.use("/images/", express.static(path.join(__dirname, staticDir)));
var server = app.listen(process.env.PORT || 3000, function () {
    console.log("server is running at http://localhost:" + (process.env.PORT || 3000));
});
var io = require("socket.io")(server);
var storage = multer.diskStorage({
    destination: function (req, file, callback) {
        callback(null, "./images");
    },
    filename: function (req, file, callback) {
        callback(null, file.fieldname + "_" + Date.now() + "_" + file.originalname);
    }
});
var upload = multer({ storage: storage });
app.get("/", function (req, res) {
    try {
        var responseArray_1 = [];
        fs.readdirSync(staticDir).forEach(function (file) { return responseArray_1.push(file); });
        res.status(200).send(responseArray_1);
    }
    catch (error) {
        res.status(500).json({
            message: error
        });
    }
});
app.post("/api/upload", upload.array("photo", 3), function (req, res) {
    try {
        io.emit("message", "Check for new content");
        res.status(200).json({
            message: "success!"
        });
    }
    catch (error) {
        res.status(500).json({
            message: error
        });
    }
});
