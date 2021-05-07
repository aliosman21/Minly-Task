const multer = require("multer");
const cors = require("cors");
const express = require("express");
import { Request, Response } from "express";
const fs = require("fs");
const path = require("path");

const app = express();
const staticDir = "./images";
app.use(cors());
app.use(express.json());
app.use("/images/", express.static(path.join(__dirname, staticDir)));

interface fileStructureForMulter {
   fieldname: string;
   originalname: string;
}

const server = app.listen(process.env.PORT || 3000, () => {
   console.log(`server is running at http://localhost:${process.env.PORT || 3000}`);
});
const io = require("socket.io")(server);

const storage = multer.diskStorage({
   destination(req: Request, file: File, callback: Function) {
      callback(null, "./images");
   },
   filename(req: Request, file: fileStructureForMulter, callback: Function) {
      callback(null, `${file.fieldname}_${Date.now()}_${file.originalname}`);
   },
});
const upload = multer({ storage });

app.get("/", (req: Request, res: Response) => {
   try {
      const responseArray: string[] = [];
      fs.readdirSync(staticDir).forEach((file: string) => responseArray.push(file));

      res.status(200).send(responseArray);
   } catch (error) {
      res.status(500).json({
         message: error,
      });
   }
});

app.post("/api/upload", upload.array("photo", 3), (req: Request, res: Response) => {
   try {
      io.emit("message", "Check for new content");
      res.status(200).json({
         message: "success!",
      });
   } catch (error) {
      res.status(500).json({
         message: error,
      });
   }
});
