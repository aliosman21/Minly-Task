import React, { useState, useEffect } from "react";
import { TouchableOpacity, Image, Text, Platform, ScrollView } from "react-native";
import * as ImagePicker from "expo-image-picker";
import axios from "axios";
import { io } from "socket.io-client";
const serverURL = "http://192.168.1.7:3000/";

export default function App() {
   const [image, setImage] = useState(null);
   const [photos, setPhotos] = useState([""]);
   const socket = io(serverURL);

   const createFormData = (photo, body = {}) => {
      const data = new FormData();
      data.append("photo", {
         uri: Platform.OS === "ios" ? photo.uri.replace("file://", "") : photo.uri,
         type: "image/jpeg",
         name: "imagename.jpg",
      });

      return data;
   };

   socket.on("message", () => {
      console.log("Update received will update local photos");
      getNewPhotos();
   });

   useEffect(() => {
      (async () => {
         if (Platform.OS !== "web") {
            const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();
            getNewPhotos();
            if (status !== "granted") {
               alert("Sorry, we need camera roll permissions to make this work!");
            }
         }
      })();
   }, []);

   const getNewPhotos = async () => {
      try {
         const pics = await axios({
            url: serverURL,
            method: "GET",
            headers: {
               Accept: "application/json",
            },
         });
         setPhotos(pics.data);
      } catch (err) {
         console.log(err);
      }
   };

   const pickImage = async () => {
      let imageToSend = await ImagePicker.launchImageLibraryAsync({
         mediaTypes: ImagePicker.MediaTypeOptions.All,
         allowsEditing: true,
         aspect: [4, 3],
         quality: 1,
      });
      let formDataToBeSent = createFormData(imageToSend);
      handlePhotoUpload(formDataToBeSent);

      if (!imageToSend.cancelled) {
         setImage(imageToSend.uri);
      }
   };

   const handlePhotoUpload = async (FormData) => {
      try {
         await axios({
            url: serverURL + "api/upload",
            method: "POST",
            data: FormData,
            headers: {
               Accept: "application/json",
               "Content-Type": "multipart/form-data",
            },
         });
      } catch (err) {
         console.log("Something Happened");
      }
   };
   return (
      <ScrollView style={{ marginTop: "20%" }}>
         <TouchableOpacity
            onPress={pickImage}
            style={{
               marginBottom: "20%",
               backgroundColor: "#2e6bba",
               height: 50,
               display: "flex",
               justifyContent: "center",
               alignItems: "center",
            }}>
            <Text style={{ fontSize: 15, fontWeight: "bold" }}>Pick an image from camera roll</Text>
         </TouchableOpacity>
         {photos
            ?.slice(0)
            .reverse()
            .map((pic, index) => {
               return (
                  <Image
                     source={{ uri: serverURL + "images/" + pic }}
                     key={index}
                     style={{ height: "auto", aspectRatio: 1, width: "auto" }}
                  />
               );
            })}
      </ScrollView>
   );
}
