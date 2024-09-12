import {
    useColorScheme,
} from 'react-native';
import {adaptNavigationTheme, MD3DarkTheme, MD3LightTheme, PaperProvider} from "react-native-paper";
import {KeyboardStaticView} from "./components/KeyboardStaticView";

import React from "react";
import {AppNavigator} from "./components/AppNavigator";
import {AppContextProvider} from "./components/AppContex";
import {DefaultTheme, NavigationContainer} from "@react-navigation/native";
import {StatusBar} from "expo-status-bar";
import {RequireAuth} from "./components/RequireAuth";

export default function App() {

    const colorScheme = useColorScheme();

    const paperTheme =
        colorScheme === 'dark'
            ? MD3DarkTheme
            : MD3LightTheme;

    const navigationTheme = adaptNavigationTheme({
        reactNavigationLight: DefaultTheme,
        materialLight: paperTheme
    });

    return <AppContextProvider>
        <PaperProvider theme={paperTheme}>
            <RequireAuth>
                <KeyboardStaticView>
                    <NavigationContainer theme={navigationTheme.LightTheme}>
                        <AppNavigator/>
                    </NavigationContainer>
                </KeyboardStaticView>
            </RequireAuth>
            <StatusBar style="auto"/>
        </PaperProvider>
    </AppContextProvider>
}