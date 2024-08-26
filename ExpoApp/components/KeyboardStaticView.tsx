import React, {useEffect, useState} from 'react';
import {Dimensions, View} from 'react-native';
import RokuKeyboard from "./RokuKeyboard";
import {useAppContent} from "./AppContex";
import {Snackbar} from "react-native-paper";

export function KeyboardStaticView(props: {
    children: React.ReactNode
}) {
    const [dimensions, setDimensions]
        = useState({width: 0, height: 0});

    const appContext = useAppContent();

    useEffect(() => {
        const subscription = Dimensions.addEventListener('change', () => {
            setDimensions({
                width: 0, height: 0
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
                    if (dimensions.width < event.nativeEvent.layout.width || dimensions.height < event.nativeEvent.layout.height) {
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