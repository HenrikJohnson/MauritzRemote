import React, {useEffect, useState} from 'react';
import {Dimensions, Platform, View} from 'react-native';
import RokuKeyboard from "./RokuKeyboard";
import {useAppContent} from "./AppContex";
import {Snackbar} from "react-native-paper";
import {getScreenHeight, getScreenWidth} from "../utils/Layout";

export function KeyboardStaticView(props: {
    children: React.ReactNode
}) {
    const [dimensions, setDimensions]
        = useState(() => {
            if (Platform.OS !== "ios") {
                return {width: 0, height: 0}
            }
            return {width: getScreenWidth(), height: getScreenHeight()}
    });

    const appContext = useAppContent();

    useEffect(() => {
        const subscription = Dimensions.addEventListener('change', () => {
            setDimensions({
                width: Platform.OS !== "ios" ? 0 : getScreenWidth(),
                height: Platform.OS !== "ios"? 0 : getScreenHeight()
            });
        });

        return () => {
            subscription.remove();
        }
    }, []);


    return (
        <>
            <View
                onLayout={event => {
                    if (Platform.OS !== "ios" && (dimensions.width < event.nativeEvent.layout.width || dimensions.height < event.nativeEvent.layout.height)) {
                        setDimensions({
                            width: event.nativeEvent.layout.width,
                            height: event.nativeEvent.layout.height,
                        })
                    }
                }}
                style={{
                    height: dimensions.height > 0 ? dimensions.height : "100%",
                    width: dimensions.width > 0 ? dimensions.width : "100%"
                }}
                {...props}>
            </View>
            {appContext.keyboardView === "roku" &&
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
                {appContext.notification ?? ""}
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