import {IconButton, TextInput} from "react-native-paper";
import {useAppContent} from "./AppContex";
import React, {useEffect} from "react";
import {Keyboard, View} from "react-native";
import {useTheme} from "react-native-paper";
import {apiSend, makeApiCall} from "../utils/Api";
import {getRoom} from "../utils/Storage";

export default function RokuKeyboard() {
    const appContext = useAppContent();
    const theme = useTheme()

    useEffect(() => {
        const subscription = Keyboard.addListener('keyboardDidHide', () => {
            appContext.setKeyboardView(undefined);
        });
        return () => {
            subscription.remove();
        };
    }, []);

    return <View style={{
        position: 'absolute',
        width: '100%',
        flexDirection: 'row',
        bottom: 0,
    }}
    ><TextInput
            autoFocus={true}
            style={{
                width: "100%"
            }}
            onKeyPress={({nativeEvent}) => {
                if (nativeEvent.key === 'Enter') {
                    apiSend(appContext, `Enter_In_Cable`);
                } else if (nativeEvent.key === 'Backspace') {
                    apiSend(appContext, `Backspace_In_Cable`);
                } else {
                    try {
                        makeApiCall(appContext, `input/${getRoom()}/${encodeURIComponent(nativeEvent.key)}`)
                    } catch (e) {
                    }
                }
            }}
            onBlur={() => {appContext.setKeyboardView(undefined)}}
            placeholderTextColor='white'
            underlineColorAndroid='transparent'
        />
        <IconButton style={{position: 'absolute', right: 0, top: 0, backgroundColor: theme.colors.surface}}
                    icon={"close"} mode={"outlined"} onPress={() => {
            appContext.setKeyboardView(undefined);
        }}/>
    </View>
}
