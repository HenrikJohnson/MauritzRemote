import {Dimensions, StatusBar} from "react-native";

export function determineTextSizes(size: number) {
    let textSize: "displaySmall" | "labelSmall" | "headlineMedium" | "titleLarge" = "labelSmall";
    let largeText: "displaySmall" | "displayMedium" | "headlineMedium" | "titleLarge" = "titleLarge"
    if (size > 400) {
        textSize = "displaySmall";
        largeText = "displayMedium"
    } else if (size > 320) {
        textSize = "headlineMedium";
        largeText = "displaySmall"
    } else if (size > 280) {
        textSize = "titleLarge";
        largeText = "headlineMedium"
    }
    return {textSize, largeText};
}

export function getScreenHeight() {
    return Dimensions.get('window').height - (StatusBar.currentHeight ?? 0);
}

export function getScreenWidth() {
    return Dimensions.get('window').width;
}
