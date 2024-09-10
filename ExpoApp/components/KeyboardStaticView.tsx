import React, {useEffect, useState} from 'react';
import {Dimensions, StatusBar, View} from 'react-native';
import RokuKeyboard from "./RokuKeyboard";
import {useAppContent} from "./AppContex";
import {Snackbar} from "react-native-paper";
import {getScreenHeight, getScreenWidth} from "../utils/Layout";

export function KeyboardStaticView(props: {
    children: React.ReactNode
}) {
    const appContext = useAppContent();

    return (
        <>
            <View
                style={{
                    height: getScreenHeight(),
                    width: getScreenWidth()
                }}
                {...props}>
            </View>
            { appContext.keyboardView === "roku" &&
                <RokuKeyboard/>
            }
            <Snackbar
                visible={!!appContext.notification}
                onDismiss={() => appContext.setNotification(undefined)}
                action={{
                    label: 'OK',
                    onPress: () => {
                        appContext.setNotification(undefined)
                    },
                }}>
                {appContext.notification??""}
            </Snackbar>
        </>
    );
};

// ────────────────────────────────────────────────────────────────────────────────
// usage

// <ContainerWithDimensions>
//   {({width, height}) => {
//     console.log('width', width);
//     console.log('height', height);
//     return <View />;
//   }}
// </ContainerWithDimensions>;