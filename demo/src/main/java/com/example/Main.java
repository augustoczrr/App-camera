package com.example;

import React, { useEffect, useState } from "react";
import {
  View,
  StyleSheet,
  TouchableOpacity,
  Text,
  Image,
  Alert,
} from "react-native";

import MapView, { Marker } from "react-native-maps";
import * as Location from "expo-location";
import * as ImagePicker from "expo-image-picker";

export default function App() {
  const [location, setLocation] = useState(null);

  const [photoMarker, setPhotoMarker] = useState(null);

  const [selectedPhoto, setSelectedPhoto] = useState(null);

  useEffect(() => {
    getUserLocation();
  }, []);

  async function getUserLocation() {
    
    const { status } =
      await Location.requestForegroundPermissionsAsync();

    if (status !== "granted") {
      Alert.alert(
        "Permissão negada",
        "Não foi possível acessar a localização."
      );
      return;
    }

    
    const currentLocation =
      await Location.getCurrentPositionAsync({});

    setLocation(currentLocation.coords);
  }

  async function takePhoto() {
    
    const { status } =
      await ImagePicker.requestCameraPermissionsAsync();

    if (status !== "granted") {
      Alert.alert(
        "Permissão negada",
        "Não foi possível acessar a câmera."
      );
      return;
    }

    
    const result = await ImagePicker.launchCameraAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 1,
    });

    
    if (result.canceled) {
      return;
    }

    
    const photoUri = result.assets[0].uri;

    
    setPhotoMarker({
      latitude: location.latitude,
      longitude: location.longitude,
      photoUri,
    });

    setSelectedPhoto(photoUri);
  }

  if (!location) {
    return (
      <View style={styles.loadingContainer}>
        <Text>Carregando mapa...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <MapView
        style={styles.map}
        initialRegion={{
          latitude: location.latitude,
          longitude: location.longitude,
          latitudeDelta: 0.01,
          longitudeDelta: 0.01,
        }}
        showsUserLocation
      >
        {photoMarker && (
          <Marker
            coordinate={{
              latitude: photoMarker.latitude,
              longitude: photoMarker.longitude,
            }}
            onPress={() =>
              setSelectedPhoto(photoMarker.photoUri)
            }
          />
        )}
      </MapView>

      {/* Preview da foto */}
      {selectedPhoto && (
        <View style={styles.previewContainer}>
          <Image
            source={{ uri: selectedPhoto }}
            style={styles.previewImage}
          />
        </View>
      )}

      {/* Botão da câmera */}
      <TouchableOpacity
        style={styles.cameraButton}
        onPress={takePhoto}
      >
        <Text style={styles.buttonText}>Tirar Foto</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },

  map: {
    flex: 1,
  },

  loadingContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },

  cameraButton: {
    position: "absolute",
    bottom: 40,
    alignSelf: "center",

    backgroundColor: "#2196F3",

    paddingHorizontal: 24,
    paddingVertical: 14,

    borderRadius: 12,
  },

  buttonText: {
    color: "#fff",
    fontWeight: "bold",
    fontSize: 16,
  },

  previewContainer: {
    position: "absolute",

    top: 120,
    right: 20,

    width: 140,
    height: 180,

    backgroundColor: "#fff",

    borderRadius: 14,

    overflow: "hidden",

    elevation: 10,
  },

  previewImage: {
    width: "100%",
    height: "100%",
  },
});